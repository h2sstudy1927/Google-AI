package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing complete CRUD operations for ExamCategoryEntity.
 */
@Dao
interface ExamCategoryDao {

  // --- CREATE ---
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: ExamCategoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<ExamCategoryEntity>)

  // --- READ ---
  @Query("SELECT * FROM exam_categories ORDER BY displayOrder ASC")
  fun getAllCategories(): Flow<List<ExamCategoryEntity>>

  @Query("SELECT * FROM exam_categories WHERE id = :id LIMIT 1")
  fun getCategoryById(id: String): Flow<ExamCategoryEntity?>

  @Query("SELECT * FROM exam_categories WHERE isActive = 1 ORDER BY displayOrder ASC")
  fun getActiveCategories(): Flow<List<ExamCategoryEntity>>

  @Query("SELECT COUNT(*) FROM exam_categories")
  suspend fun getCategoryCount(): Int

  // --- UPDATE ---
  @Update
  suspend fun updateCategory(category: ExamCategoryEntity)

  @Query("UPDATE exam_categories SET totalTestsCount = :count WHERE id = :id")
  suspend fun updateCategoryTestCount(id: String, count: Int)

  @Query("UPDATE exam_categories SET isActive = :isActive WHERE id = :id")
  suspend fun updateCategoryActiveStatus(id: String, isActive: Boolean)

  // --- DELETE ---
  @Delete
  suspend fun deleteCategory(category: ExamCategoryEntity)

  @Query("DELETE FROM exam_categories WHERE id = :id")
  suspend fun deleteCategoryById(id: String)

  @Query("DELETE FROM exam_categories")
  suspend fun deleteAllCategories()
}
