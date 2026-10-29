package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ExamCategoryEntity
import com.example.data.local.MockTestEntity
import com.example.data.local.PreloadedData
import com.example.data.local.UserResultEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("H2S STUDY", appName)
  }

  @Test
  fun `verify Room ExamCategoryDao full CRUD operations`() = runBlocking {
    val categoryDao = db.examCategoryDao()

    // 1. CREATE
    val defaultCategories = PreloadedData.getDefaultCategories()
    categoryDao.insertCategories(defaultCategories)
    assertEquals(5, categoryDao.getCategoryCount())

    // 2. READ
    val opsc = categoryDao.getCategoryById("OPSC").first()
    assertNotNull(opsc)
    assertEquals("OPSC EXAMS", opsc?.title)

    // 3. UPDATE
    categoryDao.updateCategoryTestCount("OPSC", 10)
    val updatedOpsc = categoryDao.getCategoryById("OPSC").first()
    assertEquals(10, updatedOpsc?.totalTestsCount)

    categoryDao.updateCategoryActiveStatus("TEACHING", false)
    val activeCategories = categoryDao.getActiveCategories().first()
    assertEquals(4, activeCategories.size)

    // 4. DELETE
    categoryDao.deleteCategoryById("TEACHING")
    assertEquals(4, categoryDao.getCategoryCount())
  }

  @Test
  fun `verify Room MockTestDao full CRUD operations and progress`() = runBlocking {
    val testDao = db.mockTestDao()

    // 1. CREATE
    val tests = PreloadedData.getDefaultTests().map { MockTestEntity.fromDomain(it) }
    testDao.insertAll(tests)
    assertTrue(testDao.getTestCount() > 0)

    // 2. READ
    val test = testDao.getTestById("mock_osssc_01").first()
    assertNotNull(test)
    assertEquals(0, test?.attemptsCount)

    // 3. UPDATE Progress
    testDao.updateTestProgress("mock_osssc_01", 4.75, 1700000000000L)
    val updated = testDao.getTestById("mock_osssc_01").first()
    assertEquals(1, updated?.attemptsCount)
    assertEquals(4.75, updated?.bestScore ?: 0.0, 0.001)

    // 4. DELETE
    testDao.deleteTestById("mock_osssc_01")
    val deleted = testDao.getTestById("mock_osssc_01").first()
    assertNull(deleted)
  }

  @Test
  fun `verify Room UserResultDao full CRUD operations`() = runBlocking {
    val resultDao = db.userResultDao()

    // 1. CREATE
    val result = UserResultEntity(
      testId = "mock_osssc_01",
      testTitle = "OSSSC Combined Mock Test 2026",
      categoryId = "OSSSC",
      candidateName = "Aarav Mohanty",
      candidateDistrict = "Cuttack",
      correctCount = 4,
      wrongCount = 1,
      skippedCount = 0,
      penaltyDeducted = 0.25,
      netScore = 3.75,
      accuracyPercent = 80,
      timestamp = System.currentTimeMillis(),
      verificationId = "H2S-OD-1234",
      questionAttemptsJson = "[]",
      isPassed = true
    )
    val id = resultDao.insertResult(result)
    assertTrue(id > 0)

    // 2. READ
    val saved = resultDao.getResultById(id).first()
    assertNotNull(saved)
    assertEquals("Aarav Mohanty", saved?.candidateName)
    assertEquals(3.75, saved?.netScore ?: 0.0, 0.001)

    // 3. UPDATE
    resultDao.updateCandidateName(id, "Hemanand Hans")
    val updated = resultDao.getResultById(id).first()
    assertEquals("Hemanand Hans", updated?.candidateName)

    // 4. DELETE
    resultDao.deleteResultById(id)
    val deleted = resultDao.getResultById(id).first()
    assertNull(deleted)
  }

  @Test
  fun `verify odisha negative marking arithmetic`() {
    val correctCount = 4
    val wrongCount = 2
    val penalty = wrongCount * 0.25
    val netScore = (correctCount * 1.0) - penalty
    assertEquals(0.50, penalty, 0.001)
    assertEquals(3.50, netScore, 0.001)
  }

  @Test
  fun `verify ExamDataRepository API methods`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository: com.example.data.repository.ExamDataRepository = com.example.data.repository.H2sRepository.getInstance(context)
    repository.initializeDatabaseIfEmpty()

    val categories = repository.getExamCategories().first()
    assertTrue(categories.isNotEmpty())

    val tests = repository.getMockTests("OSSSC").first()
    assertTrue(tests.isNotEmpty())

    val attempt = com.example.data.model.ExamAttemptResult(
      testId = tests.first().id,
      testTitle = tests.first().title,
      category = "OSSSC",
      candidateName = "Subrat Mohapatra",
      candidateDistrict = "Kalahandi",
      correctCount = 4,
      wrongCount = 1,
      skippedCount = 0,
      penaltyDeducted = 0.25,
      netScore = 3.75,
      accuracyPercent = 80,
      timestamp = System.currentTimeMillis(),
      verificationId = "H2S-OD-REPO",
      questionAttempts = emptyList()
    )
    val savedId = repository.saveUserResult(attempt)
    assertTrue(savedId > 0)

    val latest = repository.getLatestUserResult().first()
    assertNotNull(latest)
    assertEquals("Subrat Mohapatra", latest?.candidateName)
    assertEquals(3.75, latest?.netScore ?: 0.0, 0.001)

    val totalAttempted = repository.getTotalTestsAttempted().first()
    assertTrue(totalAttempted > 0)
  }

  @Test
  fun `verify Whiteboard tools and stroke path properties`() {
    val points = listOf(
      androidx.compose.ui.geometry.Offset(10f, 20f),
      androidx.compose.ui.geometry.Offset(50f, 80f)
    )
    val stroke = com.example.ui.components.DrawnStroke(
      points = points,
      color = androidx.compose.ui.graphics.Color.White,
      strokeWidth = 6f,
      tool = com.example.ui.components.BoardTool.PEN
    )
    assertEquals(2, stroke.points.size)
    assertEquals(6f, stroke.strokeWidth, 0.001f)
    assertEquals(com.example.ui.components.BoardTool.PEN, stroke.tool)
    assertEquals("Graph Grid", com.example.ui.components.GridType.GRAPH.label)
    assertEquals(3, com.example.ui.components.GridType.entries.size)
    assertEquals(6, com.example.ui.components.BoardTool.entries.size)
  }

  @Test
  fun `verify Exam Dashboard Room category and content retrieval`() = runBlocking {
    val categoryDao = db.examCategoryDao()
    val testDao = db.mockTestDao()

    categoryDao.insertCategories(PreloadedData.getDefaultCategories())
    testDao.insertAll(PreloadedData.getDefaultTests().map { MockTestEntity.fromDomain(it) })

    val categories = categoryDao.getAllCategories().first()
    assertEquals(5, categories.size)
    val categoryIds = categories.map { it.id }
    assertTrue(categoryIds.contains("OPSC"))
    assertTrue(categoryIds.contains("OSSC"))
    assertTrue(categoryIds.contains("OSSSC"))
    assertTrue(categoryIds.contains("OUSSSC"))
    assertTrue(categoryIds.contains("TEACHING"))

    val opscTests = testDao.getTestsByCategory("OPSC").first()
    assertEquals(1, opscTests.size)
    assertEquals("OPSC", opscTests.first().categoryId)
    assertTrue(opscTests.first().title.contains("OPSC", ignoreCase = true))

    val osscTests = testDao.getTestsByCategory("OSSC").first()
    assertEquals(1, osscTests.size)
    assertEquals("OSSC", osscTests.first().categoryId)

    val ossscTests = testDao.getTestsByCategory("OSSSC").first()
    assertEquals(1, ossscTests.size)
    assertEquals("OSSSC", ossscTests.first().categoryId)
  }
}
