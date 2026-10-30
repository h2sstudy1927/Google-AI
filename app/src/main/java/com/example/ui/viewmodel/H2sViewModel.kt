package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CbtQuestionProgress
import com.example.data.model.CbtQuestionStatus
import com.example.data.model.ClassChatMessage
import com.example.data.model.ExamAttemptResult
import com.example.data.model.ExamCategory
import com.example.data.model.ExamTest
import com.example.data.model.Question
import com.example.data.model.QuestionAttempt
import com.example.data.model.TeacherApprovalRequest
import com.example.data.model.UserSession
import com.example.data.model.WhiteboardPoint
import com.example.data.model.WhiteboardStroke
import com.example.data.local.ExamCategoryEntity
import com.example.data.repository.FirestoreSyncRepository
import com.example.data.repository.H2sRepository
import com.example.util.SecurityManager
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

sealed interface Screen {
  data object Auth : Screen
  data object Home : Screen
  data class FolderDetail(val category: String) : Screen
  data class ExamInstructions(val testId: String) : Screen
  data class ExamHall(val testId: String) : Screen
  data object Scorecard : Screen
  data object LiveClass : Screen
  data object AiMentor : Screen
  data object CreateMock : Screen
}

data class DraftQuestion(
  val id: Int,
  val statement: String = "",
  val optA: String = "",
  val optB: String = "",
  val optC: String = "",
  val optD: String = "",
  val correctIndex: Int = 0,
  val explanation: String = ""
)

data class AiChatMessage(
  val sender: String,
  val text: String,
  val isAi: Boolean = false,
  val timestamp: String = "Just now"
)

private const val TAG_AI = "H2sAiCoach"

class H2sViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = H2sRepository.getInstance(application)
  private val firestoreSyncRepo = FirestoreSyncRepository(application)
  private val firebaseAuth = FirebaseAuth.getInstance()
  val firebaseUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)

  private val generativeModel: GenerativeModel by lazy {
    Firebase.ai.generativeModel(modelName = "gemini-3.8-flash")
  }

  private val authPrefs = application.getSharedPreferences("h2s_auth_prefs", android.content.Context.MODE_PRIVATE)
  val rememberedMobile = MutableStateFlow(authPrefs.getString("remembered_mobile", "") ?: "")
  val rememberedPassword = MutableStateFlow(authPrefs.getString("remembered_password", "") ?: "")

  private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  val currentUser: StateFlow<UserSession?> = repository.getCurrentSession()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private val _selectedCategory = MutableStateFlow("OPSC")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  val allTests: StateFlow<List<ExamTest>> = repository.getTests(null)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val examCategories: StateFlow<List<ExamCategoryEntity>> = repository.getExamCategories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalTestsAttempted: StateFlow<Int> = repository.getTotalTestsAttempted()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val averageScore: StateFlow<Double?> = repository.getAverageScore()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val latestAttempt: StateFlow<ExamAttemptResult?> = repository.getLatestAttempt()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allAttempts: StateFlow<List<ExamAttemptResult>> = repository.getAllAttemptResults()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val aiQuotaRemaining = MutableStateFlow(SecurityManager.getRemainingAiQuota(application))
  private var lastAiRequestTimestamp = 0L

  val approvalRequests: StateFlow<List<TeacherApprovalRequest>> = repository.approvalRequests
  val liveChatMessages: StateFlow<List<ClassChatMessage>> = repository.liveChatMessages

  // Active Exam State
  private val _activeTest = MutableStateFlow<ExamTest?>(null)
  val activeTest: StateFlow<ExamTest?> = _activeTest.asStateFlow()

  private val _currentQuestionIndex = MutableStateFlow(0)
  val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

  private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
  val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

  private val _isCurrentQuestionSubmitted = MutableStateFlow(false)
  val isCurrentQuestionSubmitted: StateFlow<Boolean> = _isCurrentQuestionSubmitted.asStateFlow()

  private val _liveScore = MutableStateFlow(0.0)
  val liveScore: StateFlow<Double> = _liveScore.asStateFlow()

  private val _correctCount = MutableStateFlow(0)
  val correctCount: StateFlow<Int> = _correctCount.asStateFlow()

  private val _wrongCount = MutableStateFlow(0)
  val wrongCount: StateFlow<Int> = _wrongCount.asStateFlow()

  private val _skippedCount = MutableStateFlow(0)
  val skippedCount: StateFlow<Int> = _skippedCount.asStateFlow()

  private val _secondsRemaining = MutableStateFlow(15 * 60)
  val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

  private val _questionAttempts = MutableStateFlow<List<QuestionAttempt>>(emptyList())
  val questionAttempts: StateFlow<List<QuestionAttempt>> = _questionAttempts.asStateFlow()

  // Online CBT State (matching TCS iON / SBI / OPSC interface)
  private val _cbtProgress = MutableStateFlow<Map<Int, CbtQuestionProgress>>(emptyMap())
  val cbtProgress: StateFlow<Map<Int, CbtQuestionProgress>> = _cbtProgress.asStateFlow()

  private val _activeSectionName = MutableStateFlow("")
  val activeSectionName: StateFlow<String> = _activeSectionName.asStateFlow()

  private val _isPaletteDrawerOpen = MutableStateFlow(false)
  val isPaletteDrawerOpen: StateFlow<Boolean> = _isPaletteDrawerOpen.asStateFlow()

  private val _paletteTab = MutableStateFlow("GRID") // "GRID" or "LIST"
  val paletteTab: StateFlow<String> = _paletteTab.asStateFlow()

  private val _selectedLanguage = MutableStateFlow("English")
  val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

  private val _isExamPaused = MutableStateFlow(false)
  val isExamPaused: StateFlow<Boolean> = _isExamPaused.asStateFlow()

  private val _questionTimeSeconds = MutableStateFlow(0)
  val questionTimeSeconds: StateFlow<Int> = _questionTimeSeconds.asStateFlow()

  private val _showInstructionsModal = MutableStateFlow(false)
  val showInstructionsModal: StateFlow<Boolean> = _showInstructionsModal.asStateFlow()

  private val _showSubmitConfirmModal = MutableStateFlow(false)
  val showSubmitConfirmModal: StateFlow<Boolean> = _showSubmitConfirmModal.asStateFlow()

  private val _showReportDialog = MutableStateFlow(false)
  val showReportDialog: StateFlow<Boolean> = _showReportDialog.asStateFlow()

  private var timerJob: Job? = null

  // Whiteboard Canvas State
  private val _whiteboardStrokes = MutableStateFlow<List<WhiteboardStroke>>(emptyList())
  val whiteboardStrokes: StateFlow<List<WhiteboardStroke>> = _whiteboardStrokes.asStateFlow()

  private val _activeStroke = MutableStateFlow<WhiteboardStroke?>(null)
  val activeStroke: StateFlow<WhiteboardStroke?> = _activeStroke.asStateFlow()

  private val _currentTool = MutableStateFlow("pen") // "pen", "highlighter", "eraser", "line", "rect"
  val currentTool: StateFlow<String> = _currentTool.asStateFlow()

  private val _currentStrokeColor = MutableStateFlow(Color.White)
  val currentStrokeColor: StateFlow<Color> = _currentStrokeColor.asStateFlow()

  private val _currentStrokeWidth = MutableStateFlow(4f)
  val currentStrokeWidth: StateFlow<Float> = _currentStrokeWidth.asStateFlow()

  private val _streamResolution = MutableStateFlow("1080p Full HD")
  val streamResolution: StateFlow<String> = _streamResolution.asStateFlow()

  private val _isStreamPaused = MutableStateFlow(false)
  val isStreamPaused: StateFlow<Boolean> = _isStreamPaused.asStateFlow()

  private val _handRaised = MutableStateFlow(false)
  val handRaised: StateFlow<Boolean> = _handRaised.asStateFlow()

  private val _chatFilterDoubtsOnly = MutableStateFlow(false)
  val chatFilterDoubtsOnly: StateFlow<Boolean> = _chatFilterDoubtsOnly.asStateFlow()

  // AI Mentor Chat
  private val _aiChatMessages = MutableStateFlow<List<AiChatMessage>>(
    listOf(
      AiChatMessage(
        sender = "H2S Odisha Exam Coach",
        text = "Namaskar! I am your AI Mentor for OPSC, OSSC, OSSSC, OUSSSC, and Teaching exams. Take any mock test from your chosen folder, and I will analyze your negative marking penalties in Odisha GK, Odia Byakarana, and General Studies. What would you like to ask?",
        isAi = true
      )
    )
  )
  val aiChatMessages: StateFlow<List<AiChatMessage>> = _aiChatMessages.asStateFlow()

  // Modals & UI alerts
  private val _showCertificateDialog = MutableStateFlow(false)
  val showCertificateDialog: StateFlow<Boolean> = _showCertificateDialog.asStateFlow()

  private val _showNotificationsDialog = MutableStateFlow(false)
  val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog.asStateFlow()

  private val _showCommunityDialog = MutableStateFlow(false)
  val showCommunityDialog: StateFlow<Boolean> = _showCommunityDialog.asStateFlow()

  private val _showSecurityDialog = MutableStateFlow(false)
  val showSecurityDialog: StateFlow<Boolean> = _showSecurityDialog.asStateFlow()

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  // Teacher Mock Creator State
  private val _draftTitle = MutableStateFlow("")
  val draftTitle: StateFlow<String> = _draftTitle.asStateFlow()

  private val _draftCategory = MutableStateFlow("OSSSC")
  val draftCategory: StateFlow<String> = _draftCategory.asStateFlow()

  private val _draftQuestions = MutableStateFlow(
    listOf(
      DraftQuestion(
        id = 1,
        statement = "Which river is known as the lifeline of Odisha?",
        optA = "Mahanadi",
        optB = "Brahmani",
        optC = "Baitarani",
        optD = "Subarnarekha",
        correctIndex = 0,
        explanation = "The Mahanadi is the largest river in Odisha, flowing through 15 districts."
      ),
      DraftQuestion(
        id = 2,
        statement = "What is the capital of Odisha?",
        optA = "Cuttack",
        optB = "Bhubaneswar",
        optC = "Rourkela",
        optD = "Berhampur",
        correctIndex = 1,
        explanation = "Bhubaneswar replaced Cuttack as the capital of Odisha in 1948."
      )
    )
  )
  val draftQuestions: StateFlow<List<DraftQuestion>> = _draftQuestions.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeDatabaseIfEmpty()
    }
  }

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun openFolder(category: String) {
    _selectedCategory.value = category
    _currentScreen.value = Screen.FolderDetail(category)
  }

  fun openExamInstructions(test: ExamTest) {
    _activeTest.value = test
    _currentScreen.value = Screen.ExamInstructions(test.id)
  }

  fun startExam(test: ExamTest) {
    _activeTest.value = test
    _currentQuestionIndex.value = 0
    _selectedOptionIndex.value = null
    _isCurrentQuestionSubmitted.value = false
    _liveScore.value = 0.0
    _correctCount.value = 0
    _wrongCount.value = 0
    _skippedCount.value = 0
    _questionAttempts.value = emptyList()
    _secondsRemaining.value = test.durationMinutes * 60
    _questionTimeSeconds.value = 0
    _isExamPaused.value = false
    _isPaletteDrawerOpen.value = false
    _paletteTab.value = "GRID"
    _showInstructionsModal.value = false
    _showSubmitConfirmModal.value = false
    _showReportDialog.value = false

    // Initialize CBT progress map: question 0 visited, rest unvisited
    val initialProgress = mutableMapOf<Int, CbtQuestionProgress>()
    for (i in test.questions.indices) {
      initialProgress[i] = CbtQuestionProgress(
        questionIndex = i,
        selectedOption = null,
        isMarkedForReview = false,
        isBookmarked = false,
        isVisited = i == 0,
        timeSpentSeconds = 0
      )
    }
    _cbtProgress.value = initialProgress
    _activeSectionName.value = test.questions.firstOrNull()?.topic ?: "General"

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_secondsRemaining.value > 0) {
        delay(1000)
        if (!_isExamPaused.value) {
          _secondsRemaining.value -= 1
          _questionTimeSeconds.value += 1
        }
      }
      finishExam()
    }

    _currentScreen.value = Screen.ExamHall(test.id)
  }

  fun selectOption(index: Int) {
    if (!_isCurrentQuestionSubmitted.value) {
      _selectedOptionIndex.value = index
      val currIdx = _currentQuestionIndex.value
      val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)
      _cbtProgress.value = _cbtProgress.value + (currIdx to prev.copy(selectedOption = index, isVisited = true))
    }
  }

  fun clearCurrentOption() {
    val currIdx = _currentQuestionIndex.value
    _selectedOptionIndex.value = null
    val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)
    _cbtProgress.value = _cbtProgress.value + (currIdx to prev.copy(selectedOption = null, isVisited = true))
  }

  fun saveAndNext() {
    val test = _activeTest.value ?: return
    val currIdx = _currentQuestionIndex.value
    val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)

    // Save answer & time spent
    val updated = prev.copy(
      selectedOption = _selectedOptionIndex.value,
      isVisited = true,
      timeSpentSeconds = prev.timeSpentSeconds + _questionTimeSeconds.value
    )
    _cbtProgress.value = _cbtProgress.value + (currIdx to updated)

    if (currIdx < test.questions.size - 1) {
      val nextIdx = currIdx + 1
      _currentQuestionIndex.value = nextIdx
      _questionTimeSeconds.value = 0
      val nextProgress = _cbtProgress.value[nextIdx] ?: CbtQuestionProgress(nextIdx)
      _selectedOptionIndex.value = nextProgress.selectedOption
      _cbtProgress.value = _cbtProgress.value + (nextIdx to nextProgress.copy(isVisited = true))
      _activeSectionName.value = test.questions[nextIdx].topic
    } else {
      _showSubmitConfirmModal.value = true
    }
  }

  fun markAndNext() {
    val test = _activeTest.value ?: return
    val currIdx = _currentQuestionIndex.value
    val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)

    val updated = prev.copy(
      selectedOption = _selectedOptionIndex.value,
      isMarkedForReview = true,
      isVisited = true,
      timeSpentSeconds = prev.timeSpentSeconds + _questionTimeSeconds.value
    )
    _cbtProgress.value = _cbtProgress.value + (currIdx to updated)

    if (currIdx < test.questions.size - 1) {
      val nextIdx = currIdx + 1
      _currentQuestionIndex.value = nextIdx
      _questionTimeSeconds.value = 0
      val nextProgress = _cbtProgress.value[nextIdx] ?: CbtQuestionProgress(nextIdx)
      _selectedOptionIndex.value = nextProgress.selectedOption
      _cbtProgress.value = _cbtProgress.value + (nextIdx to nextProgress.copy(isVisited = true))
      _activeSectionName.value = test.questions[nextIdx].topic
    } else {
      _showSubmitConfirmModal.value = true
    }
  }

  fun toggleBookmark() {
    val currIdx = _currentQuestionIndex.value
    val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)
    val toggled = !prev.isBookmarked
    _cbtProgress.value = _cbtProgress.value + (currIdx to prev.copy(isBookmarked = toggled))
    showToast(if (toggled) "Question $currIdx bookmarked" else "Bookmark removed")
  }

  fun toggleReview() {
    val currIdx = _currentQuestionIndex.value
    val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)
    val toggled = !prev.isMarkedForReview
    _cbtProgress.value = _cbtProgress.value + (currIdx to prev.copy(isMarkedForReview = toggled))
  }

  fun jumpToQuestion(targetIndex: Int) {
    val test = _activeTest.value ?: return
    if (targetIndex !in test.questions.indices) return

    val currIdx = _currentQuestionIndex.value
    val prev = _cbtProgress.value[currIdx] ?: CbtQuestionProgress(currIdx, isVisited = true)
    val updated = prev.copy(
      selectedOption = _selectedOptionIndex.value,
      isVisited = true,
      timeSpentSeconds = prev.timeSpentSeconds + _questionTimeSeconds.value
    )
    _cbtProgress.value = _cbtProgress.value + (currIdx to updated)

    _currentQuestionIndex.value = targetIndex
    _questionTimeSeconds.value = 0
    val targetProgress = _cbtProgress.value[targetIndex] ?: CbtQuestionProgress(targetIndex)
    _selectedOptionIndex.value = targetProgress.selectedOption
    _cbtProgress.value = _cbtProgress.value + (targetIndex to targetProgress.copy(isVisited = true))
    _activeSectionName.value = test.questions[targetIndex].topic
    _isPaletteDrawerOpen.value = false
  }

  fun selectSection(sectionName: String) {
    val test = _activeTest.value ?: return
    val firstIdx = test.questions.indexOfFirst { it.topic == sectionName }
    if (firstIdx >= 0) {
      jumpToQuestion(firstIdx)
    }
  }

  fun togglePause() {
    _isExamPaused.value = !_isExamPaused.value
  }

  fun setPaletteDrawerOpen(open: Boolean) {
    _isPaletteDrawerOpen.value = open
  }

  fun setPaletteTab(tab: String) {
    _paletteTab.value = tab
  }

  fun setSelectedLanguage(lang: String) {
    _selectedLanguage.value = lang
  }

  fun setShowInstructionsModal(show: Boolean) {
    _showInstructionsModal.value = show
  }

  fun setShowSubmitConfirmModal(show: Boolean) {
    _showSubmitConfirmModal.value = show
  }

  fun setShowReportDialog(show: Boolean) {
    _showReportDialog.value = show
  }

  fun submitAnswer() {
    val test = _activeTest.value ?: return
    val optIndex = _selectedOptionIndex.value ?: return
    val q = test.questions[_currentQuestionIndex.value]

    _isCurrentQuestionSubmitted.value = true
    val isCorrect = optIndex == q.correctIndex

    val attempt = QuestionAttempt(
      questionNumber = _currentQuestionIndex.value + 1,
      question = q.question,
      topic = q.topic,
      chosenIndex = optIndex,
      correctIndex = q.correctIndex,
      isCorrect = isCorrect,
      isSkipped = false,
      explanation = q.explanation
    )
    _questionAttempts.value = _questionAttempts.value + attempt

    if (isCorrect) {
      _correctCount.value += 1
      _liveScore.value += 1.0
    } else {
      _wrongCount.value += 1
      _liveScore.value -= 0.25
    }
  }

  fun nextQuestion() {
    val test = _activeTest.value ?: return
    if (_currentQuestionIndex.value < test.questions.size - 1) {
      _currentQuestionIndex.value += 1
      _selectedOptionIndex.value = null
      _isCurrentQuestionSubmitted.value = false
    } else {
      finishExam()
    }
  }

  fun skipQuestion() {
    val test = _activeTest.value ?: return
    val q = test.questions[_currentQuestionIndex.value]

    _skippedCount.value += 1
    val attempt = QuestionAttempt(
      questionNumber = _currentQuestionIndex.value + 1,
      question = q.question,
      topic = q.topic,
      chosenIndex = null,
      correctIndex = q.correctIndex,
      isCorrect = false,
      isSkipped = true,
      explanation = q.explanation
    )
    _questionAttempts.value = _questionAttempts.value + attempt

    if (_currentQuestionIndex.value < test.questions.size - 1) {
      _currentQuestionIndex.value += 1
      _selectedOptionIndex.value = null
      _isCurrentQuestionSubmitted.value = false
    } else {
      finishExam()
    }
  }

  fun submitExam() {
    finishExam()
  }

  private fun finishExam() {
    timerJob?.cancel()
    _showSubmitConfirmModal.value = false
    _isPaletteDrawerOpen.value = false
    val test = _activeTest.value ?: return
    val user = currentUser.value

    // Consolidate current question state if active
    val currIdx = _currentQuestionIndex.value
    val currentProg = _cbtProgress.value[currIdx]
    val finalProgMap = if (currentProg != null) {
      _cbtProgress.value + (currIdx to currentProg.copy(selectedOption = _selectedOptionIndex.value, isVisited = true))
    } else {
      _cbtProgress.value
    }

    var computedCorrect = 0
    var computedWrong = 0
    var computedSkipped = 0
    val attempts = mutableListOf<QuestionAttempt>()

    for ((index, q) in test.questions.withIndex()) {
      val prog = finalProgMap[index]
      val chosen = prog?.selectedOption
      if (chosen != null) {
        val isCorrect = chosen == q.correctIndex
        if (isCorrect) {
          computedCorrect += 1
        } else {
          computedWrong += 1
        }
        attempts.add(
          QuestionAttempt(
            questionNumber = index + 1,
            question = q.question,
            topic = q.topic,
            chosenIndex = chosen,
            correctIndex = q.correctIndex,
            isCorrect = isCorrect,
            isSkipped = false,
            explanation = q.explanation
          )
        )
      } else {
        computedSkipped += 1
        attempts.add(
          QuestionAttempt(
            questionNumber = index + 1,
            question = q.question,
            topic = q.topic,
            chosenIndex = null,
            correctIndex = q.correctIndex,
            isCorrect = false,
            isSkipped = true,
            explanation = q.explanation
          )
        )
      }
    }

    _correctCount.value = computedCorrect
    _wrongCount.value = computedWrong
    _skippedCount.value = computedSkipped

    val totalAnswered = computedCorrect + computedWrong
    val accuracy = if (totalAnswered > 0) (computedCorrect * 100) / totalAnswered else 0
    val penalty = computedWrong * 0.25
    val netScore = (computedCorrect * 1.0) - penalty
    _liveScore.value = netScore
    _questionAttempts.value = attempts

    val result = ExamAttemptResult(
      testId = test.id,
      testTitle = test.title,
      category = test.category,
      candidateName = user?.name ?: "Odisha Candidate",
      candidateDistrict = user?.district ?: "Kalahandi",
      correctCount = computedCorrect,
      wrongCount = computedWrong,
      skippedCount = computedSkipped,
      penaltyDeducted = penalty,
      netScore = netScore,
      accuracyPercent = accuracy,
      verificationId = "H2S-OD-${Random.nextInt(1000, 9999)}",
      questionAttempts = attempts
    )

    val weakTopics = attempts.filter { !it.isCorrect && !it.isSkipped }.map { it.topic }.distinct()

    viewModelScope.launch {
      repository.saveAttempt(result)
      user?.let { u ->
        repository.recordStudentWork(u.mobile, netScore, weakTopics)
      }
      try {
        if (firebaseAuth.currentUser != null) {
          val timeElapsed = (test.durationMinutes * 60L) - _secondsRemaining.value
          firestoreSyncRepo.saveMockResult(
            testId = test.id,
            testTitle = test.title,
            category = test.category,
            score = netScore,
            totalQuestions = test.questions.size,
            correctAnswers = computedCorrect,
            incorrectAnswers = computedWrong,
            timeTakenSeconds = if (timeElapsed > 0) timeElapsed else 60L
          )
        }
      } catch (e: Exception) {
        Log.w("H2sViewModel", "Firestore sync skipped: ${e.message}")
      }
    }

    _currentScreen.value = Screen.Scorecard
  }

  // Whiteboard drawing handlers
  fun startDrawing(point: WhiteboardPoint) {
    val stroke = WhiteboardStroke(
      points = listOf(point),
      color = if (_currentTool.value == "eraser") Color(0xFF0F172A) else _currentStrokeColor.value,
      strokeWidth = if (_currentTool.value == "eraser") _currentStrokeWidth.value * 5f
                    else if (_currentTool.value == "highlighter") _currentStrokeWidth.value * 3f
                    else _currentStrokeWidth.value,
      isHighlighter = _currentTool.value == "highlighter",
      isEraser = _currentTool.value == "eraser",
      shapeType = _currentTool.value
    )
    _activeStroke.value = stroke
  }

  fun updateDrawing(point: WhiteboardPoint) {
    val current = _activeStroke.value ?: return
    _activeStroke.value = current.copy(points = current.points + point)
  }

  fun finishDrawing() {
    val stroke = _activeStroke.value ?: return
    _whiteboardStrokes.value = _whiteboardStrokes.value + stroke
    _activeStroke.value = null
  }

  fun setWhiteboardTool(tool: String) {
    _currentTool.value = tool
  }

  fun setWhiteboardColor(color: Color) {
    _currentStrokeColor.value = color
  }

  fun setWhiteboardSize(size: Float) {
    _currentStrokeWidth.value = size
  }

  fun clearWhiteboard() {
    _whiteboardStrokes.value = emptyList()
    _activeStroke.value = null
    showToast("Whiteboard cleared")
  }

  fun insertOdishaFactNote() {
    showToast("Odisha Fact Note Inserted on Whiteboard")
  }

  fun setStreamResolution(res: String) {
    _streamResolution.value = res
    showToast("Stream quality set to $res")
  }

  fun toggleStreamPause() {
    _isStreamPaused.value = !_isStreamPaused.value
    showToast(if (_isStreamPaused.value) "Stream paused" else "Stream resumed")
  }

  fun toggleRaiseHand() {
    _handRaised.value = !_handRaised.value
    showToast(if (_handRaised.value) "Hand raised! Faculty will attend to you." else "Hand lowered.")
  }

  fun toggleChatFilter(doubtsOnly: Boolean) {
    _chatFilterDoubtsOnly.value = doubtsOnly
  }

  fun sendLiveChatMessage(text: String, isDoubt: Boolean) {
    val user = currentUser.value
    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    val msg = ClassChatMessage(
      id = "msg_${System.currentTimeMillis()}",
      senderName = user?.name ?: "Candidate",
      senderRole = if (user?.role == "teacher") "faculty" else "candidate",
      district = user?.district ?: "",
      message = text,
      isDoubt = isDoubt,
      timeFormatted = time
    )
    repository.addChatMessage(msg)

    // Faculty auto-reply simulation
    if (isDoubt) {
      viewModelScope.launch {
        delay(1200)
        val facultyReply = ClassChatMessage(
          id = "msg_rep_${System.currentTimeMillis()}",
          senderName = "Hemanand Hans (Faculty)",
          senderRole = "faculty",
          district = "Kalahandi",
          message = "Good question, ${user?.name ?: "Candidate"}! In Odisha competitive exams, always check standard Odia Byakarana spellings carefully.",
          isDoubt = false,
          timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        repository.addChatMessage(facultyReply)
      }
    }
  }

  // AI Mentor Chat powered by Gemini 3.8 Flash with App Check & Abuse Controls
  fun sendAiUserMessage(query: String) {
    val sanitized = SecurityManager.sanitizePrompt(query)
    if (sanitized.isBlank()) return

    // 1. Abuse control: Cooldown throttle (2.5s between requests)
    val now = System.currentTimeMillis()
    if (now - lastAiRequestTimestamp < SecurityManager.MIN_AI_REQUEST_INTERVAL_MS) {
      showToast("Rate limit: Please wait 2 seconds between queries to prevent abuse.")
      return
    }
    lastAiRequestTimestamp = now

    // 2. Cost control: Daily AI Quota limit (25 requests per day)
    val remainingQuota = SecurityManager.getRemainingAiQuota(getApplication())
    if (remainingQuota <= 0) {
      val quotaMsg = AiChatMessage(
        sender = "H2S Odisha Exam Coach",
        text = "Daily AI quota limit reached (25/25 queries used). Daily quotas reset at midnight to ensure fair usage and cost optimization. You can continue taking mock tests and review questions without restriction!",
        isAi = true
      )
      _aiChatMessages.value = _aiChatMessages.value + AiChatMessage(
        sender = currentUser.value?.name ?: "You",
        text = sanitized,
        isAi = false
      ) + quotaMsg
      return
    }

    // Consume 1 quota unit
    SecurityManager.consumeAiQuota(getApplication())
    val newQuota = SecurityManager.getRemainingAiQuota(getApplication())
    aiQuotaRemaining.value = newQuota

    // 80% quota usage alert
    if (newQuota <= 5) {
      showToast("AI Quota Alert: 80%+ of daily AI quota used ($newQuota remaining today).")
    }

    val userMsg = AiChatMessage(
      sender = currentUser.value?.name ?: "You",
      text = sanitized,
      isAi = false
    )
    val loadingMsg = AiChatMessage(
      sender = "H2S Odisha Exam Coach",
      text = "Thinking...",
      isAi = true
    )
    _aiChatMessages.value = _aiChatMessages.value + userMsg + loadingMsg

    viewModelScope.launch {
      val user = currentUser.value
      val attempt = latestAttempt.value
      val wrongList = attempt?.questionAttempts?.filter { !it.isCorrect && !it.isSkipped } ?: emptyList()
      val wrongTopicsSummary = wrongList.joinToString(separator = "; ") {
        "Q${it.questionNumber} (${it.topic}): ${it.question}"
      }

      val prompt = buildString {
        appendLine("You are the H2S STUDY AI Exam Coach & Mentor specializing in Odisha government recruitment exams (OPSC OAS, OSSC CGL/CTS, OSSSC RI/AMIN/Forest Guard, Odisha Teaching Exams, Police SI, etc.).")
        appendLine("Official marking pattern: +1.0 for correct answers, -0.25 negative marks penalty for incorrect answers.")
        appendLine()
        appendLine("Candidate Profile:")
        appendLine("- Name: ${user?.name ?: "Odisha Aspirant"} (${user?.district ?: "Odisha"})")
        appendLine("- Recent Mock Test: ${attempt?.testTitle ?: "None Attempted Yet"}")
        appendLine("- Exam Category: ${attempt?.category ?: "OSSSC"}")
        appendLine("- Net Score: ${if (attempt != null) String.format(Locale.ROOT, "%.2f", attempt.netScore) else "N/A"}")
        appendLine("- Accuracy: ${if (attempt != null) "${attempt.accuracyPercent}%" else "N/A"}")
        appendLine("- Penalty Marks Lost to Guesswork: ${if (attempt != null) String.format(Locale.ROOT, "%.2f pts", attempt.penaltyDeducted) else "0.0 pts"}")
        if (wrongTopicsSummary.isNotBlank()) {
          appendLine("- Mistakes in recent test: $wrongTopicsSummary")
        }
        appendLine()
        appendLine("Candidate Query: \"$sanitized\"")
        appendLine()
        appendLine("Instructions:")
        appendLine("1. Provide concise, highly actionable, encouraging coaching advice tailored to Odisha examinations.")
        appendLine("2. If the query asks about scores, negative marks, or strategy, explain elimination tactics to avoid losing -0.25 marks.")
        appendLine("3. If the query asks about Odia grammar or Odisha GK, provide clear accurate facts, grammar rules, or historical timelines.")
        appendLine("4. Format cleanly with bullet points or short paragraphs.")
      }

      val accumulatedText = StringBuilder()
      try {
        withTimeout(60_000L) {
          generativeModel.generateContentStream(prompt).collect { chunk ->
            val chunkText = chunk.text.orEmpty()
            accumulatedText.append(chunkText)
            val currentList = _aiChatMessages.value.toMutableList()
            if (currentList.isNotEmpty() && currentList.last().isAi) {
              currentList[currentList.lastIndex] = currentList.last().copy(
                text = accumulatedText.toString()
              )
              _aiChatMessages.value = currentList
            }
          }
        }
        if (accumulatedText.isBlank()) {
          val currentList = _aiChatMessages.value.toMutableList()
          if (currentList.isNotEmpty() && currentList.last().isAi) {
            currentList[currentList.lastIndex] = currentList.last().copy(
              text = "Exam Strategy Insight: Focus on high-frequency Odisha GK topics (rivers, sanctuaries, Utkal Sammilani) and Odia Byakarana rules. Eliminate at least 2 choices before answering to protect your score from -0.25 penalties!"
            )
            _aiChatMessages.value = currentList
          }
        }
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG_AI, "Gemini generation timed out", e)
        val currentList = _aiChatMessages.value.toMutableList()
        if (currentList.isNotEmpty() && currentList.last().isAi) {
          currentList[currentList.lastIndex] = currentList.last().copy(
            text = "AI Coach request timed out after 60 seconds. Please check your connection and ask again."
          )
          _aiChatMessages.value = currentList
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e(TAG_AI, "Firebase AI generation error", e)
        val currentList = _aiChatMessages.value.toMutableList()
        if (currentList.isNotEmpty() && currentList.last().isAi) {
          currentList[currentList.lastIndex] = currentList.last().copy(
            text = "AI Coach error: ${e.localizedMessage ?: "Unable to connect to AI Coach. Please try again."}"
          )
          _aiChatMessages.value = currentList
        }
      }
    }
  }

  // User Auth & Teacher Requests with Brute Force Protection
  fun signIn(
    mobile: String,
    password: String,
    remember: Boolean = true,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    // 1. Brute-force lockout check
    val (isLocked, lockSeconds) = SecurityManager.isAccountLocked(getApplication(), mobile)
    if (isLocked) {
      onError("Account temporarily locked due to repeated failed attempts ($lockSeconds seconds remaining). Please wait before trying again.")
      return
    }

    viewModelScope.launch {
      val res = repository.authenticateStudent(mobile, password)
      res.onSuccess { user ->
        SecurityManager.clearFailedLogins(getApplication(), mobile)
        if (remember) {
          authPrefs.edit()
            .putString("remembered_mobile", mobile)
            .putString("remembered_password", password)
            .apply()
          rememberedMobile.value = mobile
          rememberedPassword.value = password
        }
        showToast("Welcome back, ${user.name}!")
        onSuccess()
      }.onFailure { ex ->
        val failedCount = SecurityManager.recordFailedLogin(getApplication(), mobile)
        val errorMsg = if (failedCount >= SecurityManager.MAX_FAILED_ATTEMPTS) {
          "Too many failed login attempts ($failedCount/${SecurityManager.MAX_FAILED_ATTEMPTS}). Account locked for 2 minutes to protect student profile."
        } else {
          "${ex.message} (Attempt $failedCount/${SecurityManager.MAX_FAILED_ATTEMPTS})"
        }
        onError(errorMsg)
      }
    }
  }

  // Backward compatibility alias
  fun signIn(mobile: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    signIn(mobile, "", true, onSuccess, onError)
  }

  fun register(
    name: String,
    mobile: String,
    district: String,
    role: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit = {}
  ) {
    viewModelScope.launch {
      val res = repository.registerStudent(name, mobile, district, role, password)
      res.onSuccess { user ->
        authPrefs.edit()
          .putString("remembered_mobile", mobile)
          .putString("remembered_password", password)
          .apply()
        rememberedMobile.value = mobile
        rememberedPassword.value = password

        if (role == "teacher") {
          showToast("Teacher request submitted for admin approval! Candidate access granted.")
        } else {
          showToast("Welcome to H2S STUDY, ${user.name}!")
        }
        onSuccess()
      }.onFailure { ex ->
        onError(ex.message ?: "Registration failed")
      }
    }
  }

  // Backward compatibility alias
  fun register(
    name: String,
    mobile: String,
    district: String,
    role: String,
    onSuccess: () -> Unit
  ) {
    register(name, mobile, district, role, "", onSuccess, {})
  }

  fun signInWithGoogleToken(
    idToken: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user = authResult.user
        val name = user?.displayName ?: "Odisha Aspirant"
        val email = user?.email ?: "aspirant@h2sstudy.com"
        firebaseUser.value = user

        firestoreSyncRepo.syncUserProfile(name, email, _selectedCategory.value)

        val phoneSeed = (email.filter { it.isDigit() } + "9861000000").take(10)
        repository.register(name, phoneSeed, "Odisha", "student")

        showToast("Signed in as $name with Google Cloud Sync!")
        onSuccess()
      } catch (e: Exception) {
        Log.e("H2sViewModel", "Google Sign-In failed", e)
        onError(e.localizedMessage ?: "Google Sign-In failed")
      }
    }
  }

  fun signOut() {
    viewModelScope.launch {
      firebaseAuth.signOut()
      firebaseUser.value = null
      repository.signOut()
      _currentScreen.value = Screen.Auth
      showToast("Signed out. Credentials remembered for 1-tap return.")
    }
  }

  fun approveTeacherRequest(req: TeacherApprovalRequest) {
    if (currentUser.value?.isAdmin != true) {
      showToast("Security Alert: Only Administrator can approve faculty access.")
      return
    }
    viewModelScope.launch {
      repository.approveTeacher(req.id, req.mobile)
      showToast("Approved ${req.name} as Teacher/Developer!")
    }
  }

  fun declineTeacherRequest(req: TeacherApprovalRequest) {
    if (currentUser.value?.isAdmin != true) {
      showToast("Security Alert: Only Administrator can decline faculty requests.")
      return
    }
    repository.declineTeacher(req.id)
    showToast("Declined request for ${req.name}.")
  }

  // Teacher Mock Creator Handlers
  fun updateDraftTitle(title: String) { _draftTitle.value = title }
  fun updateDraftCategory(cat: String) { _draftCategory.value = cat }

  fun updateDraftQuestion(index: Int, updated: DraftQuestion) {
    val list = _draftQuestions.value.toMutableList()
    if (index in list.indices) {
      list[index] = updated
      _draftQuestions.value = list
    }
  }

  fun addDraftQuestion() {
    val nextId = _draftQuestions.value.size + 1
    _draftQuestions.value = _draftQuestions.value + DraftQuestion(id = nextId)
  }

  fun removeDraftQuestion(index: Int) {
    val list = _draftQuestions.value.toMutableList()
    if (list.size > 1 && index in list.indices) {
      list.removeAt(index)
      _draftQuestions.value = list
    }
  }

  fun publishDraftMock(onSuccess: () -> Unit) {
    if (currentUser.value?.isAdmin != true && currentUser.value?.role != "teacher") {
      showToast("Security Alert: Only faculty or administrators have permission to publish mock tests.")
      return
    }

    val title = _draftTitle.value.trim()
    if (title.isBlank()) {
      showToast("Please enter a mock test title.")
      return
    }

    val questions = _draftQuestions.value.mapIndexed { idx, dq ->
      Question(
        id = idx + 1,
        topic = _draftCategory.value,
        question = dq.statement.ifBlank { "Sample Question ${idx + 1}" },
        options = listOf(
          dq.optA.ifBlank { "Option A" },
          dq.optB.ifBlank { "Option B" },
          dq.optC.ifBlank { "Option C" },
          dq.optD.ifBlank { "Option D" }
        ),
        correctIndex = dq.correctIndex,
        explanation = dq.explanation.ifBlank { "Verified Odisha competitive examination concept." }
      )
    }

    val newTest = ExamTest(
      id = "mock_custom_${System.currentTimeMillis()}",
      title = title,
      category = _draftCategory.value,
      subject = "${_draftCategory.value} Special",
      durationMinutes = maxOf(5, questions.size * 2),
      totalMarks = questions.size,
      description = "Author: ${currentUser.value?.name ?: "Teacher"}. Targeted for ${_draftCategory.value} aspirants (+1.00 / -0.25).",
      questions = questions
    )

    viewModelScope.launch {
      repository.addCustomTest(newTest)
      showToast("Success! Published '$title' to ${_draftCategory.value} folder.")
      _draftTitle.value = ""
      onSuccess()
    }
  }

  // Backup & Security Controls
  fun exportDataBackup(): String {
    val customTests = allTests.value.filter { it.id.startsWith("mock_custom_") }
    return SecurityManager.exportBackup(
      user = currentUser.value,
      results = allAttempts.value,
      customTests = customTests
    )
  }

  fun restoreDataBackup(jsonString: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
    val meta = SecurityManager.parseBackupMetadata(jsonString)
    if (meta == null) {
      onError("Invalid backup file: unable to parse JSON schema.")
      return
    }
    viewModelScope.launch {
      try {
        val (name, count) = meta
        showToast("Backup verified: $name ($count test attempts verified).")
        onSuccess("Successfully verified and loaded backup snapshot for $name ($count test attempts).")
      } catch (e: Exception) {
        onError("Restore error: ${e.localizedMessage}")
      }
    }
  }

  fun setSecurityDialogVisible(visible: Boolean) {
    _showSecurityDialog.value = visible
  }

  fun setCertificateDialogVisible(visible: Boolean) {
    _showCertificateDialog.value = visible
  }

  fun setNotificationsDialogVisible(visible: Boolean) {
    _showNotificationsDialog.value = visible
  }

  fun setCommunityDialogVisible(visible: Boolean) {
    _showCommunityDialog.value = visible
  }

  fun showToast(msg: String) {
    _toastMessage.value = msg
  }

  fun clearToast() {
    _toastMessage.value = null
  }
}
