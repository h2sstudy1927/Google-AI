package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.ExamCategoryEntity
import com.example.data.local.MockTestEntity
import com.example.data.local.PreloadedData
import com.example.data.local.UserEntity
import com.example.data.local.UserResultEntity
import com.example.data.model.ClassChatMessage
import com.example.data.model.ExamAttemptResult
import com.example.data.model.ExamTest
import com.example.data.model.TeacherApprovalRequest
import com.example.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import com.example.util.SecurityManager

/**
 * Concrete Data Repository implementation that abstracts the Room database operations
 * and provides a clean API for ViewModels to fetch exam categories, list available mock tests,
 * and save user results.
 */
class H2sRepository private constructor(context: Context) : ExamDataRepository {

  private val database = Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "h2s_study_db"
  ).fallbackToDestructiveMigration().build()

  private val categoryDao = database.examCategoryDao()
  private val testDao = database.mockTestDao()
  private val resultDao = database.userResultDao()
  private val userDao = database.userDao()

  // Live in-memory requests and chats (synchronized across views)
  private val _approvalRequests = MutableStateFlow(
    listOf(
      TeacherApprovalRequest(
        id = "req_demo_1",
        name = "Deepak Rout",
        mobile = "9861998877",
        district = "Cuttack",
        roleRequested = "teacher",
        timestamp = "5 mins ago",
        status = "pending"
      )
    )
  )
  val approvalRequests = _approvalRequests.asStateFlow()

  private val _liveChatMessages = MutableStateFlow(
    listOf(
      ClassChatMessage(
        id = "msg_1",
        senderName = "H2S Odisha Exam Moderator",
        senderRole = "moderator",
        message = "Welcome aspirants to today's OPSC/OSSSC live class. Ask your doubts directly to Prof. Mohapatra below.",
        isDoubt = false,
        timeFormatted = "09:25 AM"
      ),
      ClassChatMessage(
        id = "msg_2",
        senderName = "Chinmayee Sahoo",
        senderRole = "candidate",
        district = "Puri",
        message = "Sir, who was the first Prime Minister / Premier of Odisha in 1937?",
        isDoubt = true,
        timeFormatted = "09:26 AM"
      ),
      ClassChatMessage(
        id = "msg_3",
        senderName = "Hemanand Hans (Faculty)",
        senderRole = "faculty",
        district = "Kalahandi",
        message = "Krushna Chandra Gajapati (Maharaja of Paralakhemundi) formed the first ministry in April 1937, followed by Biswanath Das. Note this down for OPSC/OSSSC!",
        isDoubt = false,
        timeFormatted = "09:27 AM"
      )
    )
  )
  val liveChatMessages = _liveChatMessages.asStateFlow()

  // ==================== 1. DATABASE INITIALIZATION ====================

  override suspend fun initializeDatabaseIfEmpty() {
    // 1. Seed Exam Categories
    val categoryCount = categoryDao.getCategoryCount()
    if (categoryCount == 0) {
      categoryDao.insertCategories(PreloadedData.getDefaultCategories())
    }

    // 2. Seed Preloaded Mock Tests
    val count = testDao.getTestCount()
    if (count == 0) {
      val defaultEntities = PreloadedData.getDefaultTests().map {
        MockTestEntity.fromDomain(it, isCustom = false)
      }
      testDao.insertAll(defaultEntities)
    }

    // 3. Developer Master Account (Hemanand Hans) & Demo Student
    val devUser = UserEntity(
      name = "Hemanand Hans",
      mobile = "8327777148",
      district = "Kalahandi",
      role = "developer",
      password = SecurityManager.hashPassword("8327777148"),
      isApproved = true,
      isAdmin = true,
      isCurrentSession = false
    )
    userDao.insertUser(devUser)

    val demoStudent = userDao.getUserByMobile("9861012345")
    if (demoStudent == null) {
      userDao.insertUser(
        UserEntity(
          name = "Aarav Mohanty",
          mobile = "9861012345",
          district = "Cuttack",
          role = "student",
          password = "odisha2026",
          isApproved = true,
          isAdmin = false,
          isCurrentSession = false
        )
      )
    }
  }

  // ==================== 2. EXAM CATEGORIES API ====================

  override fun getExamCategories(): Flow<List<ExamCategoryEntity>> {
    return categoryDao.getAllCategories()
  }

  override fun getActiveCategories(): Flow<List<ExamCategoryEntity>> {
    return categoryDao.getActiveCategories()
  }

  override fun getCategoryById(id: String): Flow<ExamCategoryEntity?> {
    return categoryDao.getCategoryById(id)
  }

  override suspend fun addCategory(category: ExamCategoryEntity): Long {
    return categoryDao.insertCategory(category)
  }

  override suspend fun updateCategory(category: ExamCategoryEntity) {
    categoryDao.updateCategory(category)
  }

  override suspend fun deleteCategory(id: String) {
    categoryDao.deleteCategoryById(id)
  }

  // ==================== 3. MOCK TESTS API ====================

  override fun getMockTests(categoryFilter: String?): Flow<List<ExamTest>> {
    return if (categoryFilter.isNullOrBlank() || categoryFilter == "ALL") {
      testDao.getAllTests().map { list -> list.map { it.toDomain() } }
    } else {
      testDao.getTestsByCategory(categoryFilter).map { list -> list.map { it.toDomain() } }
    }
  }

  // Backward compatibility alias
  fun getTests(categoryFilter: String? = null): Flow<List<ExamTest>> = getMockTests(categoryFilter)

  override fun getMockTestEntities(categoryFilter: String?): Flow<List<MockTestEntity>> {
    return if (categoryFilter.isNullOrBlank() || categoryFilter == "ALL") {
      testDao.getAllTests()
    } else {
      testDao.getTestsByCategory(categoryFilter)
    }
  }

  override fun getMockTestById(id: String): Flow<ExamTest?> {
    return testDao.getTestById(id).map { it?.toDomain() }
  }

  override fun getCustomMockTests(): Flow<List<ExamTest>> {
    return testDao.getCustomTests().map { list -> list.map { it.toDomain() } }
  }

  override suspend fun addMockTest(test: ExamTest) {
    testDao.insertTest(MockTestEntity.fromDomain(test, isCustom = true))
  }

  // Backward compatibility alias
  suspend fun addCustomTest(test: ExamTest) = addMockTest(test)

  override suspend fun deleteMockTest(testId: String) {
    testDao.deleteTestById(testId)
  }

  // ==================== 4. USER RESULTS & OFFLINE PROGRESS API ====================

  override suspend fun saveUserResult(result: ExamAttemptResult): Long {
    // 1. Insert into user_results table
    val attemptId = resultDao.insertResult(UserResultEntity.fromDomain(result))

    // 2. Atomically update MockTestEntity progress (attemptsCount, bestScore, lastAttemptTimestamp)
    testDao.updateTestProgress(
      testId = result.testId,
      score = result.netScore,
      timestamp = result.timestamp
    )

    return attemptId
  }

  // Backward compatibility alias
  suspend fun saveAttempt(result: ExamAttemptResult): Long = saveUserResult(result)

  override fun getAllAttemptResults(): Flow<List<ExamAttemptResult>> {
    return resultDao.getAllResults().map { list -> list.map { it.toDomain() } }
  }

  // Backward compatibility alias
  fun getAllAttempts(): Flow<List<ExamAttemptResult>> = getAllAttemptResults()

  override fun getAllUserResults(): Flow<List<UserResultEntity>> {
    return resultDao.getAllResults()
  }

  override fun getLatestUserResult(): Flow<ExamAttemptResult?> {
    return resultDao.getLatestResult().map { it?.toDomain() }
  }

  // Backward compatibility alias
  fun getLatestAttempt(): Flow<ExamAttemptResult?> = getLatestUserResult()

  override fun getUserResultsForTest(testId: String): Flow<List<UserResultEntity>> {
    return resultDao.getResultsForTest(testId)
  }

  // Backward compatibility alias
  fun getResultsForTest(testId: String): Flow<List<UserResultEntity>> = getUserResultsForTest(testId)

  override fun getUserResultsByCategory(categoryId: String): Flow<List<UserResultEntity>> {
    return resultDao.getResultsByCategory(categoryId)
  }

  override fun getBestResultForTest(testId: String): Flow<UserResultEntity?> {
    return resultDao.getBestResultForTest(testId)
  }

  override fun getTotalTestsAttempted(): Flow<Int> {
    return resultDao.getTotalTestsAttempted()
  }

  override fun getAverageScore(): Flow<Double?> {
    return resultDao.getAverageScore()
  }

  override suspend fun deleteUserResult(id: Long) {
    resultDao.deleteResultById(id)
  }

  suspend fun restoreAttemptResults(results: List<ExamAttemptResult>) {
    if (results.isEmpty()) return
    resultDao.insertResults(results.map { UserResultEntity.fromDomain(it) })
  }

  // ==================== 5. USER SESSIONS & PERMISSIONS ====================

  fun getCurrentSession(): Flow<UserSession?> {
    return userDao.getCurrentSession().map { it?.toDomain() }
  }

  suspend fun authenticateStudent(mobile: String, password: String): Result<UserSession> {
    val user = userDao.getUserByMobile(mobile)
      ?: return Result.failure(Exception("No account found with mobile number +91 $mobile. Please Sign Up first as a 1st time user."))

    if (user.password.isNotBlank() && password.isNotBlank() && !SecurityManager.verifyPassword(password, user.password)) {
      return Result.failure(Exception("Incorrect password for +91 $mobile. Please check your password and try again."))
    }

    var activeUser = user
    if (mobile == "8327777148") {
      if (!activeUser.isAdmin || activeUser.role != "developer") {
        activeUser = activeUser.copy(isAdmin = true, role = "developer", name = "Hemanand Hans", district = "Kalahandi")
        userDao.insertUser(activeUser)
      }
    }

    userDao.clearActiveSessions()
    userDao.setActiveSession(mobile)
    return Result.success(activeUser.toDomain())
  }

  // Backward compatibility alias
  suspend fun signIn(mobile: String): UserSession? {
    val user = userDao.getUserByMobile(mobile) ?: return null
    userDao.clearActiveSessions()
    userDao.setActiveSession(mobile)
    return user.toDomain()
  }

  suspend fun registerStudent(
    name: String,
    mobile: String,
    district: String,
    role: String,
    password: String = ""
  ): Result<UserSession> {
    val existing = userDao.getUserByMobile(mobile)
    if (existing != null) {
      return Result.failure(Exception("Mobile +91 $mobile is already registered. Please go to Sign In and enter your password."))
    }

    val isDeveloper = mobile == "8327777148"
    val isTeacher = role == "teacher"
    val assignedRole = if (isDeveloper) "developer" else "student"
    val isApproved = isDeveloper || !isTeacher
    val isAdmin = isDeveloper
    val storedPassword = if (password.isNotBlank()) SecurityManager.hashPassword(password) else ""

    val userEntity = UserEntity(
      name = if (isDeveloper) "Hemanand Hans" else name,
      mobile = mobile,
      district = if (isDeveloper) "Kalahandi" else district,
      role = assignedRole,
      password = storedPassword,
      isApproved = isApproved,
      isAdmin = isAdmin,
      isCurrentSession = true,
      createdAt = System.currentTimeMillis()
    )
    userDao.clearActiveSessions()
    userDao.insertUser(userEntity)

    if (isTeacher) {
      val newReq = TeacherApprovalRequest(
        id = "req_${System.currentTimeMillis()}",
        name = name,
        mobile = mobile,
        district = district,
        roleRequested = "teacher",
        timestamp = "Just now",
        status = "pending"
      )
      _approvalRequests.value = listOf(newReq) + _approvalRequests.value
    }

    return Result.success(userEntity.toDomain())
  }

  // Backward compatibility alias
  suspend fun register(
    name: String,
    mobile: String,
    district: String,
    role: String
  ): UserSession {
    val res = registerStudent(name, mobile, district, role, "")
    return res.getOrElse {
      userDao.getUserByMobile(mobile)?.toDomain() ?: UserSession(name, mobile, district, role)
    }
  }

  suspend fun recordStudentWork(mobile: String, netScore: Double, weakTopics: List<String>) {
    val user = userDao.getUserByMobile(mobile) ?: return
    val newTotal = user.totalTestsTaken + 1
    val newAvg = if (newTotal > 1) {
      ((user.avgScore * user.totalTestsTaken) + netScore) / newTotal
    } else {
      netScore
    }
    val newHighest = maxOf(user.highestScore, netScore)
    val tier = when {
      newAvg >= 80.0 -> "EXAM_READY"
      newAvg >= 60.0 -> "ADVANCED"
      newAvg >= 40.0 -> "INTERMEDIATE"
      else -> "BEGINNER"
    }

    val existingWeak = try {
      val arr = org.json.JSONArray(user.weakTopicsJson)
      val list = mutableListOf<String>()
      for (i in 0 until arr.length()) list.add(arr.getString(i))
      list
    } catch (_: Exception) {
      mutableListOf<String>()
    }
    val combinedWeak = (existingWeak + weakTopics).distinct().take(10)
    val weakJson = org.json.JSONArray(combinedWeak).toString()

    userDao.updateStudentTelemetry(
      mobile = mobile,
      totalTests = newTotal,
      avgScore = newAvg,
      highestScore = newHighest,
      weakTopicsJson = weakJson,
      studentTier = tier
    )
  }

  suspend fun signOut() {
    userDao.clearActiveSessions()
  }

  suspend fun approveTeacher(requestId: String, mobile: String) {
    userDao.approveTeacher(mobile)
    _approvalRequests.value = _approvalRequests.value.map { req ->
      if (req.id == requestId) req.copy(status = "approved") else req
    }
  }

  fun declineTeacher(requestId: String) {
    _approvalRequests.value = _approvalRequests.value.map { req ->
      if (req.id == requestId) req.copy(status = "declined") else req
    }
  }

  fun addChatMessage(message: ClassChatMessage) {
    _liveChatMessages.value = _liveChatMessages.value + message
  }

  companion object {
    @Volatile
    private var INSTANCE: H2sRepository? = null

    fun getInstance(context: Context): H2sRepository {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: H2sRepository(context).also { INSTANCE = it }
      }
    }
  }
}
