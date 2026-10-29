package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing complete CRUD operations for MockTestEntity.
 */
@Dao
interface MockTestDao {

  // --- CREATE ---
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTest(test: MockTestEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tests: List<MockTestEntity>)

  // --- READ ---
  @Query("SELECT * FROM mock_tests ORDER BY createdAt DESC")
  fun getAllTests(): Flow<List<MockTestEntity>>

  @Query("SELECT * FROM mock_tests WHERE categoryId = :cat ORDER BY createdAt DESC")
  fun getTestsByCategory(cat: String): Flow<List<MockTestEntity>>

  @Query("SELECT * FROM mock_tests WHERE id = :id LIMIT 1")
  fun getTestById(id: String): Flow<MockTestEntity?>

  @Query("SELECT * FROM mock_tests WHERE isCustom = 1 ORDER BY createdAt DESC")
  fun getCustomTests(): Flow<List<MockTestEntity>>

  @Query("SELECT COUNT(*) FROM mock_tests")
  suspend fun getTestCount(): Int

  @Query("SELECT COUNT(*) FROM mock_tests WHERE categoryId = :cat")
  suspend fun getTestCountByCategory(cat: String): Int

  // --- UPDATE ---
  @Update
  suspend fun updateTest(test: MockTestEntity)

  @Query("""
    UPDATE mock_tests 
    SET attemptsCount = attemptsCount + 1, 
        bestScore = CASE WHEN bestScore IS NULL OR :score > bestScore THEN :score ELSE bestScore END,
        lastAttemptTimestamp = :timestamp 
    WHERE id = :testId
  """)
  suspend fun updateTestProgress(testId: String, score: Double, timestamp: Long)

  @Query("UPDATE mock_tests SET title = :title, description = :description, durationMinutes = :duration WHERE id = :id")
  suspend fun updateTestDetails(id: String, title: String, description: String, duration: Int)

  // --- DELETE ---
  @Delete
  suspend fun deleteTest(test: MockTestEntity)

  @Query("DELETE FROM mock_tests WHERE id = :id")
  suspend fun deleteTestById(id: String)

  @Query("DELETE FROM mock_tests WHERE isCustom = 1")
  suspend fun deleteCustomTests()

  @Query("DELETE FROM mock_tests")
  suspend fun deleteAllTests()
}
