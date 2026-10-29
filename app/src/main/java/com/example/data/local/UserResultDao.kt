package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing complete CRUD operations for UserResultEntity.
 */
@Dao
interface UserResultDao {

  // --- CREATE ---
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResult(result: UserResultEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResults(results: List<UserResultEntity>)

  // --- READ ---
  @Query("SELECT * FROM user_results ORDER BY timestamp DESC")
  fun getAllResults(): Flow<List<UserResultEntity>>

  @Query("SELECT * FROM user_results WHERE id = :id LIMIT 1")
  fun getResultById(id: Long): Flow<UserResultEntity?>

  @Query("SELECT * FROM user_results WHERE testId = :testId ORDER BY timestamp DESC")
  fun getResultsForTest(testId: String): Flow<List<UserResultEntity>>

  @Query("SELECT * FROM user_results WHERE categoryId = :categoryId ORDER BY timestamp DESC")
  fun getResultsByCategory(categoryId: String): Flow<List<UserResultEntity>>

  @Query("SELECT * FROM user_results ORDER BY timestamp DESC LIMIT 1")
  fun getLatestResult(): Flow<UserResultEntity?>

  @Query("SELECT * FROM user_results WHERE testId = :testId ORDER BY netScore DESC LIMIT 1")
  fun getBestResultForTest(testId: String): Flow<UserResultEntity?>

  @Query("SELECT COUNT(*) FROM user_results")
  fun getTotalTestsAttempted(): Flow<Int>

  @Query("SELECT AVG(netScore) FROM user_results")
  fun getAverageScore(): Flow<Double?>

  @Query("SELECT MAX(netScore) FROM user_results")
  fun getHighestScore(): Flow<Double?>

  @Query("SELECT SUM(penaltyDeducted) FROM user_results")
  fun getTotalPenaltyDeducted(): Flow<Double?>

  // --- UPDATE ---
  @Update
  suspend fun updateResult(result: UserResultEntity)

  @Query("UPDATE user_results SET candidateName = :name WHERE id = :id")
  suspend fun updateCandidateName(id: Long, name: String)

  // --- DELETE ---
  @Delete
  suspend fun deleteResult(result: UserResultEntity)

  @Query("DELETE FROM user_results WHERE id = :id")
  suspend fun deleteResultById(id: Long)

  @Query("DELETE FROM user_results WHERE testId = :testId")
  suspend fun deleteResultsByTestId(testId: String)

  @Query("DELETE FROM user_results")
  suspend fun deleteAllResults()
}
