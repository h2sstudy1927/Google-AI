package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.H2sRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

sealed interface Screen {
  data object Auth : Screen
  data object Home : Screen
  data class FolderDetail(val category: String) : Screen
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

class H2sViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = H2sRepository.getInstance(application)

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
      // Auto-sign in default student demo if no session exists yet
      val session = repository.signIn("9861012345")
      if (session == null) {
        repository.register(
          name = "Aarav Mohanty",
          mobile = "9861012345",
          district = "Cuttack",
          role = "student"
        )
      }
    }
  }

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun openFolder(category: String) {
    _selectedCategory.value = category
    _currentScreen.value = Screen.FolderDetail(category)
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

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_secondsRemaining.value > 0) {
        delay(1000)
        _secondsRemaining.value -= 1
      }
      finishExam()
    }

    _currentScreen.value = Screen.ExamHall(test.id)
  }

  fun selectOption(index: Int) {
    if (!_isCurrentQuestionSubmitted.value) {
      _selectedOptionIndex.value = index
    }
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

  private fun finishExam() {
    timerJob?.cancel()
    val test = _activeTest.value ?: return
    val user = currentUser.value
    val totalAnswered = _correctCount.value + _wrongCount.value
    val accuracy = if (totalAnswered > 0) (_correctCount.value * 100) / totalAnswered else 0
    val penalty = _wrongCount.value * 0.25

    val result = ExamAttemptResult(
      testId = test.id,
      testTitle = test.title,
      category = test.category,
      candidateName = user?.name ?: "Odisha Candidate",
      candidateDistrict = user?.district ?: "Kalahandi",
      correctCount = _correctCount.value,
      wrongCount = _wrongCount.value,
      skippedCount = _skippedCount.value,
      penaltyDeducted = penalty,
      netScore = _liveScore.value,
      accuracyPercent = accuracy,
      verificationId = "H2S-OD-${Random.nextInt(1000, 9999)}",
      questionAttempts = _questionAttempts.value
    )

    viewModelScope.launch {
      repository.saveAttempt(result)
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

  // AI Mentor Chat
  fun sendAiUserMessage(query: String) {
    val userMsg = AiChatMessage(sender = currentUser.value?.name ?: "You", text = query, isAi = false)
    _aiChatMessages.value = _aiChatMessages.value + userMsg

    viewModelScope.launch {
      delay(700)
      val qLower = query.lowercase(Locale.ROOT)
      val attempt = latestAttempt.value
      val wrongCount = attempt?.wrongCount ?: 0
      val penalty = attempt?.penaltyDeducted ?: 0.0

      val reply = when {
        qLower.contains("improve") || qLower.contains("score") || qLower.contains("osssc") || qLower.contains("opsc") -> {
          "Strategic Blueprint for Odisha Recruitment Exams:\n" +
          "1. Contain the 1/4th Penalty: In your last test, you lost ${String.format(Locale.ROOT, "%.2f", penalty)} marks to doubtful guesses. Eliminating 2 options gives a 50% probability; otherwise skipping preserves your rank!\n" +
          "2. Daily Odia Byakarana Practice: Devote 25 minutes daily to ସନ୍ଧି, ସମାସ, ଏକପଦୀକରଣ, and ବିପରୀତ ଶବ୍ଦ.\n" +
          "3. Modern Odisha History: Focus on Utkal Sammilani (1903), formation of Odisha (1936), and Salt Satyagraha at Inchudi (1930)."
        }
        qLower.contains("wrong") || qLower.contains("mistake") || qLower.contains("penalty") -> {
          if (wrongCount == 0) {
            "You had 0 incorrect answers in your recent attempt! Your conceptual foundation in Odisha GK is rock solid."
          } else {
            "Analysis of your mistakes:\n" +
            "• You incurred -$penalty marks in negative penalty.\n" +
            "• Most errors in Odisha exams arise from subtle spelling variations in Odia (ଶୁଦ୍ଧ ବନାନ) and historical timelines. Practice elimination technique."
          }
        }
        else -> {
          "In Odisha state examinations (OPSC, OSSC, OSSSC, Teaching), precision in Odia grammar and regional GK separates finalists from the rest. Ask me about any topic or exam strategy!"
        }
      }

      val aiMsg = AiChatMessage(sender = "H2S Odisha Exam Coach", text = reply, isAi = true)
      _aiChatMessages.value = _aiChatMessages.value + aiMsg
    }
  }

  // User Auth & Teacher Requests
  fun signIn(mobile: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val user = repository.signIn(mobile)
      if (user != null) {
        showToast("Welcome back, ${user.name}!")
        onSuccess()
      } else {
        onError("Mobile number not registered. Please sign up.")
      }
    }
  }

  fun register(
    name: String,
    mobile: String,
    district: String,
    role: String,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      val user = repository.register(name, mobile, district, role)
      if (role == "teacher") {
        showToast("Teacher request submitted for admin approval! Candidate access granted.")
      } else {
        showToast("Welcome, ${user.name}! Permanent instant access active.")
      }
      onSuccess()
    }
  }

  fun signOut() {
    viewModelScope.launch {
      repository.signOut()
      _currentScreen.value = Screen.Auth
      showToast("Signed out. Credentials remembered for 1-tap return.")
    }
  }

  fun approveTeacherRequest(req: TeacherApprovalRequest) {
    viewModelScope.launch {
      repository.approveTeacher(req.id, req.mobile)
      showToast("Approved ${req.name} as Teacher/Developer!")
    }
  }

  fun declineTeacherRequest(req: TeacherApprovalRequest) {
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

  fun setCertificateDialogVisible(visible: Boolean) {
    _showCertificateDialog.value = visible
  }

  fun setNotificationsDialogVisible(visible: Boolean) {
    _showNotificationsDialog.value = visible
  }

  fun showToast(msg: String) {
    _toastMessage.value = msg
  }

  fun clearToast() {
    _toastMessage.value = null
  }
}
