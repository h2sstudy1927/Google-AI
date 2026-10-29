package com.example.data.repository

import com.example.data.local.ExamCategoryEntity
import com.example.data.local.MockTestEntity
import com.example.data.local.UserResultEntity
import com.example.data.model.ExamAttemptResult
import com.example.data.model.ExamTest
import kotlinx.coroutines.flow.Flow

/**
 * Clean Data Repository interface defining the high-level contract to abstract Room database operations.
 * Exposes reactive Flow streams for ViewModels to fetch exam categories, list available mock tests,
 * and persist user progress and mock examination results.
 */
interface ExamDataRepository {

  // ==================== 1. EXAM CATEGORIES ====================

  /**
   * Reactive stream of all examination categories ordered by displayOrder.
   */
  fun getExamCategories(): Flow<List<ExamCategoryEntity>>

  /**
   * Reactive stream of currently active categories for display in the dashboard.
   */
  fun getActiveCategories(): Flow<List<ExamCategoryEntity>>

  /**
   * Query a single examination category by its unique ID (e.g. "OPSC", "OSSC", "OSSSC").
   */
  fun getCategoryById(id: String): Flow<ExamCategoryEntity?>

  /**
   * Insert or replace an exam category.
   */
  suspend fun addCategory(category: ExamCategoryEntity): Long

  /**
   * Update category properties (title, topics, active status, test count).
   */
  suspend fun updateCategory(category: ExamCategoryEntity)

  /**
   * Remove an examination category by its unique ID.
   */
  suspend fun deleteCategory(id: String)


  // ==================== 2. MOCK TESTS ====================

  /**
   * Fetch all mock tests, optionally filtered by commission category (e.g., "OPSC", "OSSC", "OSSSC").
   * Returns clean domain models (ExamTest) ready for UI consumption.
   */
  fun getMockTests(categoryFilter: String? = null): Flow<List<ExamTest>>

  /**
   * Stream raw MockTestEntity records including offline progress statistics (attemptsCount, bestScore).
   */
  fun getMockTestEntities(categoryFilter: String? = null): Flow<List<MockTestEntity>>

  /**
   * Retrieve a specific mock test with full question data by its unique ID.
   */
  fun getMockTestById(id: String): Flow<ExamTest?>

  /**
   * Fetch all teacher-authored custom mock tests.
   */
  fun getCustomMockTests(): Flow<List<ExamTest>>

  /**
   * Add a new mock test created by teacher or loaded from local bundle.
   */
  suspend fun addMockTest(test: ExamTest)

  /**
   * Delete a mock test by its ID.
   */
  suspend fun deleteMockTest(testId: String)


  // ==================== 3. USER RESULTS & OFFLINE PROGRESS ====================

  /**
   * Persist a completed mock exam attempt: saves the detailed UserResultEntity and
   * automatically updates the MockTestEntity's attemptsCount, bestScore, and lastAttemptTimestamp.
   */
  suspend fun saveUserResult(result: ExamAttemptResult): Long

  /**
   * Stream all user mock exam results mapped to domain models.
   */
  fun getAllAttemptResults(): Flow<List<ExamAttemptResult>>

  /**
   * Stream raw UserResultEntity records for analytical or historical review.
   */
  fun getAllUserResults(): Flow<List<UserResultEntity>>

  /**
   * Retrieve the most recent test attempt for scorecard display and AI diagnostic coaching.
   */
  fun getLatestUserResult(): Flow<ExamAttemptResult?>

  /**
   * Retrieve attempt history for a specific mock test.
   */
  fun getUserResultsForTest(testId: String): Flow<List<UserResultEntity>>

  /**
   * Retrieve all results for a specific category (e.g. all OSSSC attempts).
   */
  fun getUserResultsByCategory(categoryId: String): Flow<List<UserResultEntity>>

  /**
   * Retrieve the candidate's highest scoring result on a specific test.
   */
  fun getBestResultForTest(testId: String): Flow<UserResultEntity?>

  /**
   * Reactive stream of total number of tests attempted offline.
   */
  fun getTotalTestsAttempted(): Flow<Int>

  /**
   * Reactive stream of candidate's average net score across all mock tests.
   */
  fun getAverageScore(): Flow<Double?>

  /**
   * Delete a specific attempt result.
   */
  suspend fun deleteUserResult(id: Long)


  // ==================== 4. DATABASE INITIALIZATION ====================

  /**
   * Seeds the local Room database with default Odisha exam categories, question banks,
   * and admin profile if the database is unpopulated.
   */
  suspend fun initializeDatabaseIfEmpty()
}
