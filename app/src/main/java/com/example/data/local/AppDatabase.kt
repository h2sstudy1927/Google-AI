package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Main Room Database abstract class defining the database configuration and serving as the app's
 * main access point for persisted relational data across ExamCategories, MockTests, UserResults, and Users.
 */
@Database(
  entities = [
    ExamCategoryEntity::class,
    MockTestEntity::class,
    UserResultEntity::class,
    UserEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  /**
   * Provides access to CRUD operations for examination categories.
   */
  abstract fun examCategoryDao(): ExamCategoryDao

  /**
   * Provides access to CRUD operations for mock tests and offline progress.
   */
  abstract fun mockTestDao(): MockTestDao

  /**
   * Provides access to CRUD operations for mock results and score evaluation.
   */
  abstract fun userResultDao(): UserResultDao

  /**
   * Provides access to user authentication and session operations.
   */
  abstract fun userDao(): UserDao

  // Backwards compatibility helper methods
  fun testDao(): MockTestDao = mockTestDao()
  fun attemptDao(): UserResultDao = userResultDao()
}
