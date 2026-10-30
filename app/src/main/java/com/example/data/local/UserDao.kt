package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for UserEntity handling local authentication and session state.
 */
@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE isCurrentSession = 1 LIMIT 1")
  fun getCurrentSession(): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
  suspend fun getUserByMobile(mobile: String): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Query("UPDATE users SET isCurrentSession = 0")
  suspend fun clearActiveSessions()

  @Query("UPDATE users SET isCurrentSession = 1 WHERE mobile = :mobile")
  suspend fun setActiveSession(mobile: String)

  @Query("UPDATE users SET role = 'teacher', isApproved = 1 WHERE mobile = :mobile")
  suspend fun approveTeacher(mobile: String)

  @Query("SELECT * FROM users")
  suspend fun getAllUsers(): List<UserEntity>

  @Query("UPDATE users SET totalTestsTaken = :totalTests, avgScore = :avgScore, highestScore = :highestScore, weakTopicsJson = :weakTopicsJson, studentTier = :studentTier, lastActiveTimestamp = :lastActive WHERE mobile = :mobile")
  suspend fun updateStudentTelemetry(
    mobile: String,
    totalTests: Int,
    avgScore: Double,
    highestScore: Double,
    weakTopicsJson: String,
    studentTier: String,
    lastActive: Long = System.currentTimeMillis()
  )
}
