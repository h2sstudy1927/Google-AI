package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class CloudMockResult(
  val id: String = "",
  val userId: String = "",
  val testId: String = "",
  val testTitle: String = "",
  val category: String = "",
  val score: Double = 0.0,
  val totalQuestions: Int = 0,
  val correctAnswers: Int = 0,
  val incorrectAnswers: Int = 0,
  val timeTakenSeconds: Long = 0L
)

class FirestoreSyncRepository(private val db: FirebaseFirestore) {

  // Secondary constructor resolving named database ID from string resources
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = FirebaseAuth.getInstance()

  private fun requireUserId(): String {
    return auth.currentUser?.uid ?: error("Authentication required for Firestore access.")
  }

  suspend fun syncUserProfile(displayName: String, email: String, targetExam: String): Result<Unit> {
    return try {
      val uid = requireUserId()
      val path = "users/$uid"
      val userDoc = db.collection("users").document(uid)

      val data = mapOf(
        "userId" to uid,
        "displayName" to displayName,
        "email" to email,
        "targetExam" to targetExam,
        "updatedAt" to FieldValue.serverTimestamp()
      )

      userDoc.set(data, SetOptions.merge()).await()
      Result.success(Unit)
    } catch (e: Exception) {
      val uid = auth.currentUser?.uid ?: "anonymous"
      handleFirestoreError(e, OperationType.WRITE, "users/$uid")
      Result.failure(e)
    }
  }

  suspend fun saveMockResult(
    testId: String,
    testTitle: String,
    category: String,
    score: Double,
    totalQuestions: Int,
    correctAnswers: Int,
    incorrectAnswers: Int,
    timeTakenSeconds: Long
  ): Result<String> {
    return try {
      val uid = requireUserId()
      val resultId = "res_${UUID.randomUUID().toString().take(8)}"
      val path = "users/$uid/mock_results/$resultId"
      val docRef = db.collection("users").document(uid).collection("mock_results").document(resultId)

      val data = mapOf(
        "id" to resultId,
        "userId" to uid,
        "testId" to testId,
        "testTitle" to testTitle,
        "category" to category,
        "score" to score,
        "totalQuestions" to totalQuestions,
        "correctAnswers" to correctAnswers,
        "incorrectAnswers" to incorrectAnswers,
        "timeTakenSeconds" to timeTakenSeconds,
        "completedAt" to FieldValue.serverTimestamp()
      )

      docRef.set(data).await()
      Result.success(resultId)
    } catch (e: Exception) {
      val uid = auth.currentUser?.uid ?: "anonymous"
      handleFirestoreError(e, OperationType.CREATE, "users/$uid/mock_results")
      Result.failure(e)
    }
  }

  fun observeUserMockResults(): Flow<List<CloudMockResult>> = callbackFlow {
    val uid = auth.currentUser?.uid
    if (uid == null) {
      trySend(emptyList())
      close()
      return@callbackFlow
    }

    val collectionRef = db.collection("users").document(uid).collection("mock_results")
    var listener: ListenerRegistration? = null

    try {
      listener = collectionRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
          handleFirestoreError(error, OperationType.LIST, "users/$uid/mock_results")
          trySend(emptyList())
          return@addSnapshotListener
        }

        if (snapshot != null) {
          val results = snapshot.documents.mapNotNull { doc ->
            try {
              CloudMockResult(
                id = doc.getString("id") ?: doc.id,
                userId = doc.getString("userId") ?: uid,
                testId = doc.getString("testId") ?: "",
                testTitle = doc.getString("testTitle") ?: "",
                category = doc.getString("category") ?: "",
                score = doc.getDouble("score") ?: 0.0,
                totalQuestions = doc.getLong("totalQuestions")?.toInt() ?: 0,
                correctAnswers = doc.getLong("correctAnswers")?.toInt() ?: 0,
                incorrectAnswers = doc.getLong("incorrectAnswers")?.toInt() ?: 0,
                timeTakenSeconds = doc.getLong("timeTakenSeconds") ?: 0L
              )
            } catch (ex: Exception) {
              null
            }
          }
          trySend(results)
        }
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.LIST, "users/$uid/mock_results")
      trySend(emptyList())
    }

    awaitClose {
      listener?.remove()
    }
  }
}
