package com.example.data.model

enum class ExamCategory(
  val id: String,
  val title: String,
  val subtitle: String,
  val badge: String,
  val subCategoryTag: String,
  val iconEmoji: String,
  val topics: String
) {
  OPSC(
    id = "OPSC",
    title = "OPSC EXAMS",
    subtitle = "Mocks • Odisha History & Polity",
    badge = "CLASS 1 & 2 GAZETTED",
    subCategoryTag = "OCS / OAS • ASO • CIVIL SERVICES",
    iconEmoji = "🏛️",
    topics = "Odisha History, Polity, Geography, General Studies"
  ),
  OSSC(
    id = "OSSC",
    title = "OSSC EXAMS",
    subtitle = "Mocks • Arithmetic & Reasoning",
    badge = "GROUP B & C CADRE",
    subCategoryTag = "CGL • SUB-INSPECTOR • JUNIOR CLERK",
    iconEmoji = "📋",
    topics = "General Studies, Reasoning, Economy, Indian Constitution"
  ),
  OSSSC(
    id = "OSSSC",
    title = "OSSSC EXAMS",
    subtitle = "Mocks • Odia Grammar & GK",
    badge = "DISTRICT REVENUE CADRE",
    subCategoryTag = "RI • ARI • AMIN • PEO • JA",
    iconEmoji = "🌾",
    topics = "Odisha GK, Odia Byakarana, Arithmetic, District Cadre"
  ),
  OUSSSC(
    id = "OUSSSC",
    title = "OUSSSC EXAMS",
    subtitle = "Mocks • Junior Assistant & Staff",
    badge = "UNIVERSITY & STATE CADRE",
    subCategoryTag = "STATE & UNIVERSITY RECRUITMENT",
    iconEmoji = "🏛️",
    topics = "University Administrative Services, English, Computer Knowledge"
  ),
  TEACHING(
    id = "TEACHING",
    title = "TEACHING EXAM",
    subtitle = "Mocks • Pedagogy & Odia Byakarana",
    badge = "TEACHER RECRUITMENT",
    subCategoryTag = "OTET • OSSTET • RHT • JT • OAVS",
    iconEmoji = "🎓",
    topics = "Child Development, Pedagogy, Odia Literature, Evaluation"
  ),
  LIVE(
    id = "LIVE",
    title = "LIVE CLASSROOM",
    subtitle = "Hemanand Hans • 144p-1080p",
    badge = "WHITEBOARD STUDIO",
    subCategoryTag = "LIVE LECTURES • WHITEBOARD • DOUBTS",
    iconEmoji = "🎥",
    topics = "Daily Interactive Whiteboard Lecture with Low-Data Streaming"
  ),
  AI_MENTOR(
    id = "AI_MENTOR",
    title = "AI MOCK MENTOR",
    subtitle = "Ask AI: What did I do wrong?",
    badge = "AI EXAM DIAGNOSTICS",
    subCategoryTag = "SCORE & NEGATIVE MARK ANALYSIS",
    iconEmoji = "🤖",
    topics = "Negative Penalty Elimination, Topic Weakness Analysis"
  ),
  WHATSAPP(
    id = "WHATSAPP",
    title = "WHATSAPP BATCH",
    subtitle = "Daily PDF Notes & Updates",
    badge = "OFFICIAL COMMUNITY",
    subCategoryTag = "ODISHA ASPIRANTS DOUBT GROUP",
    iconEmoji = "💬",
    topics = "Daily PDF Notes, Answer Keys, Aspirant Community"
  )
}

data class Question(
  val id: Int,
  val topic: String,
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)

data class ExamTest(
  val id: String,
  val title: String,
  val category: String,
  val subject: String,
  val durationMinutes: Int,
  val totalMarks: Int,
  val description: String,
  val questions: List<Question>
)

data class QuestionAttempt(
  val questionNumber: Int,
  val question: String,
  val topic: String,
  val chosenIndex: Int?,
  val correctIndex: Int,
  val isCorrect: Boolean,
  val isSkipped: Boolean,
  val explanation: String
)

data class ExamAttemptResult(
  val id: Long = System.currentTimeMillis(),
  val testId: String,
  val testTitle: String,
  val category: String,
  val candidateName: String,
  val candidateDistrict: String,
  val correctCount: Int,
  val wrongCount: Int,
  val skippedCount: Int,
  val penaltyDeducted: Double,
  val netScore: Double,
  val accuracyPercent: Int,
  val timestamp: Long = System.currentTimeMillis(),
  val verificationId: String,
  val questionAttempts: List<QuestionAttempt> = emptyList()
)

data class UserSession(
  val name: String,
  val mobile: String,
  val district: String,
  val role: String, // "student" or "teacher"
  val isApproved: Boolean = true,
  val isAdmin: Boolean = false
)

data class TeacherApprovalRequest(
  val id: String,
  val name: String,
  val mobile: String,
  val district: String,
  val roleRequested: String = "teacher",
  val timestamp: String,
  val status: String // "pending", "approved", "declined"
)

data class ClassChatMessage(
  val id: String,
  val senderName: String,
  val senderRole: String, // "faculty", "moderator", "candidate"
  val district: String = "",
  val message: String,
  val isDoubt: Boolean = false,
  val timeFormatted: String
)

data class WhiteboardPoint(
  val x: Float,
  val y: Float
)

data class WhiteboardStroke(
  val points: List<WhiteboardPoint>,
  val color: androidx.compose.ui.graphics.Color,
  val strokeWidth: Float,
  val isHighlighter: Boolean = false,
  val isEraser: Boolean = false,
  val shapeType: String = "free" // "free", "line", "rect"
)
