package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun syncUserProfile_authenticatedUser_writesProfileSuccessfully() = runBlocking {
    val uid = try {
      signInTestUser("aspirant_alice@test.com")
    } catch (e: Exception) {
      org.junit.Assume.assumeNoException("Firebase emulator not reachable in JVM test environment", e)
      return@runBlocking
    }
    val repository = FirestoreSyncRepository(firestore)

    val result = withTimeout(10000L) {
      repository.syncUserProfile(
        displayName = "Alice Sharma",
        email = "aspirant_alice@test.com",
        targetExam = "OPSC OAS"
      )
    }

    assertTrue("Sync should succeed for authenticated user", result.isSuccess)
  }

  @Test
  fun saveMockResult_authenticatedUser_createsResultSuccessfully() = runBlocking {
    try {
      signInTestUser("aspirant_bob@test.com")
    } catch (e: Exception) {
      org.junit.Assume.assumeNoException("Firebase emulator not reachable in JVM test environment", e)
      return@runBlocking
    }
    val repository = FirestoreSyncRepository(firestore)

    val result = withTimeout(10000L) {
      repository.saveMockResult(
        testId = "mock_opsc_1",
        testTitle = "OPSC General Studies Mock 1",
        category = "OPSC",
        score = 88.5,
        totalQuestions = 100,
        correctAnswers = 90,
        incorrectAnswers = 6,
        timeTakenSeconds = 3400L
      )
    }

    assertTrue("Save mock result should succeed", result.isSuccess)
    val resultId = result.getOrNull()
    assertNotNull(resultId)
    assertTrue(resultId!!.startsWith("res_"))
  }

  @Test
  fun unauthenticatedWrite_failsFast() = runBlocking {
    auth.signOut()
    val repository = FirestoreSyncRepository(firestore)

    val result = withTimeout(10000L) {
      repository.syncUserProfile("Nobody", "nobody@test.com", "OPSC")
    }

    assertTrue("Unauthenticated write should fail", result.isFailure)
  }
}
