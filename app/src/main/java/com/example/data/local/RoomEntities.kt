package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.ExamAttemptResult
import com.example.data.model.ExamTest
import com.example.data.model.Question
import com.example.data.model.QuestionAttempt
import com.example.data.model.UserSession
import org.json.JSONArray
import org.json.JSONObject

/**
 * Entity representing an Odisha State Examination Category (e.g., OPSC, OSSC, OSSSC, OUSSSC, TEACHING).
 * Supports local offline categorization and metadata storage.
 */
@Entity(tableName = "exam_categories")
data class ExamCategoryEntity(
  @PrimaryKey val id: String, // e.g. "OPSC", "OSSC", "OSSSC", "OUSSSC", "TEACHING"
  val title: String,
  val subtitle: String,
  val badge: String,
  val subCategoryTag: String,
  val iconEmoji: String,
  val topics: String,
  val displayOrder: Int = 0,
  val isActive: Boolean = true,
  val totalTestsCount: Int = 0
)

/**
 * Entity representing a Mock Test under an Exam Category.
 * Stores duration, total marks, questions (in JSON format), and progress stats for offline tracking.
 */
@Entity(
  tableName = "mock_tests",
  indices = [Index(value = ["categoryId"])]
)
data class MockTestEntity(
  @PrimaryKey val id: String,
  val categoryId: String, // References ExamCategoryEntity.id
  val title: String,
  val subject: String,
  val durationMinutes: Int,
  val totalMarks: Int,
  val description: String,
  val questionsJson: String,
  val isCustom: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  // Offline Progress tracking fields
  val attemptsCount: Int = 0,
  val bestScore: Double? = null,
  val lastAttemptTimestamp: Long? = null
) {
  // Domain mapper for category compatibility
  val category: String get() = categoryId

  fun toDomain(): ExamTest {
    val qList = mutableListOf<Question>()
    try {
      val array = JSONArray(questionsJson)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val optsArray = obj.getJSONArray("options")
        val opts = mutableListOf<String>()
        for (j in 0 until optsArray.length()) {
          opts.add(optsArray.getString(j))
        }
        qList.add(
          Question(
            id = obj.optInt("id", i + 1),
            topic = obj.optString("topic", categoryId),
            question = obj.optString("question", ""),
            options = opts,
            correctIndex = obj.optInt("correctIndex", 0),
            explanation = obj.optString("explanation", "")
          )
        )
      }
    } catch (_: Exception) {}

    return ExamTest(
      id = id,
      title = title,
      category = categoryId,
      subject = subject,
      durationMinutes = durationMinutes,
      totalMarks = totalMarks,
      description = description,
      questions = qList
    )
  }

  companion object {
    fun fromDomain(test: ExamTest, isCustom: Boolean = false): MockTestEntity {
      val array = JSONArray()
      test.questions.forEach { q ->
        val obj = JSONObject()
        obj.put("id", q.id)
        obj.put("topic", q.topic)
        obj.put("question", q.question)
        val opts = JSONArray()
        q.options.forEach { opts.put(it) }
        obj.put("options", opts)
        obj.put("correctIndex", q.correctIndex)
        obj.put("explanation", q.explanation)
        array.put(obj)
      }
      return MockTestEntity(
        id = test.id,
        categoryId = test.category,
        title = test.title,
        subject = test.subject,
        durationMinutes = test.durationMinutes,
        totalMarks = test.totalMarks,
        description = test.description,
        questionsJson = array.toString(),
        isCustom = isCustom
      )
    }
  }
}

/**
 * Entity representing a Candidate's Mock Exam Result.
 * Supports complete offline evaluation, +1.00 / -0.25 negative marking breakdown, and verification IDs.
 */
@Entity(
  tableName = "user_results",
  indices = [
    Index(value = ["testId"]),
    Index(value = ["categoryId"]),
    Index(value = ["timestamp"])
  ]
)
data class UserResultEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val testId: String,
  val testTitle: String,
  val categoryId: String,
  val candidateName: String,
  val candidateDistrict: String,
  val correctCount: Int,
  val wrongCount: Int,
  val skippedCount: Int,
  val penaltyDeducted: Double,
  val netScore: Double,
  val accuracyPercent: Int,
  val timestamp: Long = System.currentTimeMillis(),
  val verificationId: String,
  val questionAttemptsJson: String,
  val timeTakenSeconds: Int = 0,
  val isPassed: Boolean = false
) {
  // Domain mapper for category compatibility
  val category: String get() = categoryId

  fun toDomain(): ExamAttemptResult {
    val list = mutableListOf<QuestionAttempt>()
    try {
      val array = JSONArray(questionAttemptsJson)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val chosen = if (obj.has("chosenIndex") && !obj.isNull("chosenIndex")) obj.getInt("chosenIndex") else null
        list.add(
          QuestionAttempt(
            questionNumber = obj.optInt("questionNumber", i + 1),
            question = obj.optString("question", ""),
            topic = obj.optString("topic", ""),
            chosenIndex = chosen,
            correctIndex = obj.optInt("correctIndex", 0),
            isCorrect = obj.optBoolean("isCorrect", false),
            isSkipped = obj.optBoolean("isSkipped", false),
            explanation = obj.optString("explanation", "")
          )
        )
      }
    } catch (_: Exception) {}

    return ExamAttemptResult(
      id = id,
      testId = testId,
      testTitle = testTitle,
      category = categoryId,
      candidateName = candidateName,
      candidateDistrict = candidateDistrict,
      correctCount = correctCount,
      wrongCount = wrongCount,
      skippedCount = skippedCount,
      penaltyDeducted = penaltyDeducted,
      netScore = netScore,
      accuracyPercent = accuracyPercent,
      timestamp = timestamp,
      verificationId = verificationId,
      questionAttempts = list
    )
  }

  companion object {
    fun fromDomain(res: ExamAttemptResult): UserResultEntity {
      val array = JSONArray()
      res.questionAttempts.forEach { qa ->
        val obj = JSONObject()
        obj.put("questionNumber", qa.questionNumber)
        obj.put("question", qa.question)
        obj.put("topic", qa.topic)
        obj.put("chosenIndex", qa.chosenIndex ?: JSONObject.NULL)
        obj.put("correctIndex", qa.correctIndex)
        obj.put("isCorrect", qa.isCorrect)
        obj.put("isSkipped", qa.isSkipped)
        obj.put("explanation", qa.explanation)
        array.put(obj)
      }
      return UserResultEntity(
        id = res.id,
        testId = res.testId,
        testTitle = res.testTitle,
        categoryId = res.category,
        candidateName = res.candidateName,
        candidateDistrict = res.candidateDistrict,
        correctCount = res.correctCount,
        wrongCount = res.wrongCount,
        skippedCount = res.skippedCount,
        penaltyDeducted = res.penaltyDeducted,
        netScore = res.netScore,
        accuracyPercent = res.accuracyPercent,
        timestamp = res.timestamp,
        verificationId = res.verificationId,
        questionAttemptsJson = array.toString(),
        isPassed = res.netScore > 0.0
      )
    }
  }
}

/**
 * Entity for Local User Session and Roles.
 */
@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val mobile: String,
  val name: String,
  val district: String,
  val role: String,
  val isApproved: Boolean = true,
  val isAdmin: Boolean = false,
  val isCurrentSession: Boolean = false
) {
  fun toDomain(): UserSession = UserSession(
    name = name,
    mobile = mobile,
    district = district,
    role = role,
    isApproved = isApproved,
    isAdmin = isAdmin
  )
}

// Backward-compatibility aliases so existing code works seamlessly
typealias TestEntity = MockTestEntity
typealias AttemptEntity = UserResultEntity
typealias TestDao = MockTestDao
typealias AttemptDao = UserResultDao
