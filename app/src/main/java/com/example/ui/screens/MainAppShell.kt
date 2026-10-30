package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.H2sViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(
  viewModel: H2sViewModel,
  modifier: Modifier = Modifier
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val allTests by viewModel.allTests.collectAsStateWithLifecycle()
  val activeTest by viewModel.activeTest.collectAsStateWithLifecycle()
  val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
  val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
  val isCurrentQuestionSubmitted by viewModel.isCurrentQuestionSubmitted.collectAsStateWithLifecycle()
  val liveScore by viewModel.liveScore.collectAsStateWithLifecycle()
  val secondsRemaining by viewModel.secondsRemaining.collectAsStateWithLifecycle()
  val latestAttempt by viewModel.latestAttempt.collectAsStateWithLifecycle()
  val examCategories by viewModel.examCategories.collectAsStateWithLifecycle()
  val totalTestsAttempted by viewModel.totalTestsAttempted.collectAsStateWithLifecycle()
  val averageScore by viewModel.averageScore.collectAsStateWithLifecycle()

  val whiteboardStrokes by viewModel.whiteboardStrokes.collectAsStateWithLifecycle()
  val activeStroke by viewModel.activeStroke.collectAsStateWithLifecycle()
  val currentTool by viewModel.currentTool.collectAsStateWithLifecycle()
  val currentStrokeColor by viewModel.currentStrokeColor.collectAsStateWithLifecycle()
  val currentStrokeWidth by viewModel.currentStrokeWidth.collectAsStateWithLifecycle()
  val streamResolution by viewModel.streamResolution.collectAsStateWithLifecycle()
  val isStreamPaused by viewModel.isStreamPaused.collectAsStateWithLifecycle()
  val handRaised by viewModel.handRaised.collectAsStateWithLifecycle()
  val chatMessages by viewModel.liveChatMessages.collectAsStateWithLifecycle()
  val chatFilterDoubtsOnly by viewModel.chatFilterDoubtsOnly.collectAsStateWithLifecycle()

  val aiChatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
  val approvalRequests by viewModel.approvalRequests.collectAsStateWithLifecycle()

  val draftTitle by viewModel.draftTitle.collectAsStateWithLifecycle()
  val draftCategory by viewModel.draftCategory.collectAsStateWithLifecycle()
  val draftQuestions by viewModel.draftQuestions.collectAsStateWithLifecycle()

  val showCertificateDialog by viewModel.showCertificateDialog.collectAsStateWithLifecycle()
  val showNotificationsDialog by viewModel.showNotificationsDialog.collectAsStateWithLifecycle()
  val showCommunityDialog by viewModel.showCommunityDialog.collectAsStateWithLifecycle()
  val showSecurityDialog by viewModel.showSecurityDialog.collectAsStateWithLifecycle()
  val aiQuotaRemaining by viewModel.aiQuotaRemaining.collectAsStateWithLifecycle()
  val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

  // CBT Exam State
  val cbtProgress by viewModel.cbtProgress.collectAsStateWithLifecycle()
  val activeSectionName by viewModel.activeSectionName.collectAsStateWithLifecycle()
  val isPaletteDrawerOpen by viewModel.isPaletteDrawerOpen.collectAsStateWithLifecycle()
  val paletteTab by viewModel.paletteTab.collectAsStateWithLifecycle()
  val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
  val isExamPaused by viewModel.isExamPaused.collectAsStateWithLifecycle()
  val questionTimeSeconds by viewModel.questionTimeSeconds.collectAsStateWithLifecycle()
  val showInstructionsModal by viewModel.showInstructionsModal.collectAsStateWithLifecycle()
  val showSubmitConfirmModal by viewModel.showSubmitConfirmModal.collectAsStateWithLifecycle()
  val showReportDialog by viewModel.showReportDialog.collectAsStateWithLifecycle()

  LaunchedEffect(toastMessage) {
    if (toastMessage != null) {
      delay(3000)
      viewModel.clearToast()
    }
  }

  // System Back Navigation Handling
  BackHandler(enabled = currentScreen != Screen.Home && currentScreen != Screen.Auth) {
    when (currentScreen) {
      is Screen.ExamInstructions -> viewModel.navigateTo(Screen.FolderDetail(selectedCategory))
      is Screen.ExamHall -> viewModel.navigateTo(Screen.FolderDetail(selectedCategory))
      is Screen.Scorecard -> viewModel.navigateTo(Screen.Home)
      is Screen.FolderDetail -> viewModel.navigateTo(Screen.Home)
      is Screen.CreateMock -> viewModel.navigateTo(Screen.FolderDetail(selectedCategory))
      else -> viewModel.navigateTo(Screen.Home)
    }
  }

  val isTeacherOrAdmin = currentUser?.role == "teacher" || currentUser?.isAdmin == true
  val pendingReqCount = approvalRequests.count { it.status == "pending" }
  val isExamMode = currentScreen is Screen.ExamHall || currentScreen is Screen.ExamInstructions

  val rememberedMobile by viewModel.rememberedMobile.collectAsStateWithLifecycle()
  val rememberedPassword by viewModel.rememberedPassword.collectAsStateWithLifecycle()

  // Auth gate if signed out
  if (currentUser == null || currentScreen == Screen.Auth) {
    AuthGateScreen(
      initialMobile = rememberedMobile,
      initialPassword = rememberedPassword,
      onSignIn = { mobile, password, remember, onSuccess, onError ->
        viewModel.signIn(mobile, password, remember, onSuccess, onError)
      },
      onSignUp = { name, mobile, district, role, password, onSuccess, onError ->
        viewModel.register(name, mobile, district, role, password, onSuccess, onError)
      },
      onGoogleSignIn = { idToken, onSuccess, onError ->
        viewModel.signInWithGoogleToken(idToken, onSuccess, onError)
      },
      onAuthenticated = {
        viewModel.navigateTo(Screen.Home)
      }
    )
    return
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = if (isExamMode) Color(0xFF14171C) else Slate50,
    contentWindowInsets = if (isExamMode) WindowInsets(0, 0, 0, 0) else WindowInsets.safeDrawing,
    topBar = {
      if (!isExamMode) {
        TopAppBar(
          title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "H2S STUDY",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              letterSpacing = 0.5.sp
            )
            Text(
              text = " • Odisha Govt Exams",
              color = Purple200,
              fontSize = 11.sp,
              fontWeight = FontWeight.Normal,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("top_home_btn")
          ) {
            Text("☰", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
        },
        actions = {
          // Security & Backups Center
          IconButton(
            onClick = { viewModel.setSecurityDialogVisible(true) },
            modifier = Modifier.testTag("top_security_center_btn")
          ) {
            Text("🛡️", fontSize = 18.sp)
          }

          // Notification Bell with Badge
          IconButton(
            onClick = { viewModel.setNotificationsDialogVisible(true) },
            modifier = Modifier.testTag("notif_bell_btn")
          ) {
            BadgedBox(
              badge = {
                if (pendingReqCount > 0) {
                  Badge(containerColor = Rose500) {
                    Text("$pendingReqCount", color = Color.White, fontSize = 9.sp)
                  }
                }
              }
            ) {
              Text("🔔", fontSize = 18.sp)
            }
          }

          // User Name Pill with Active Dot
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Purple950.copy(alpha = 0.7f))
              .border(1.dp, Purple700, RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Emerald400)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = currentUser?.name ?: "Candidate",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Logout Button
          Text(
            text = "Logout",
            color = Purple200,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
              .clickable { viewModel.signOut() }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("logout_btn")
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple900)
      )
    }
  },
  bottomBar = {
    if (!isExamMode) {
      NavigationBar(
        containerColor = Color.White,
        contentColor = Purple900,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        val activeBottomTab = when (currentScreen) {
          Screen.Home -> "home"
          is Screen.FolderDetail, is Screen.ExamInstructions, is Screen.ExamHall, Screen.Scorecard -> "mocks"
          Screen.LiveClass -> "live"
          Screen.AiMentor -> "ai"
          else -> "home"
        }

        NavigationBarItem(
          selected = activeBottomTab == "home",
          onClick = { viewModel.navigateTo(Screen.Home) },
          icon = { Text("🏠", fontSize = 18.sp) },
          label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = Purple200.copy(alpha = 0.5f),
            selectedTextColor = Purple900,
            unselectedTextColor = Slate400
          ),
          modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
          selected = activeBottomTab == "mocks",
          onClick = { viewModel.openFolder("ALL") },
          icon = { Text("📚", fontSize = 18.sp) },
          label = { Text("Mocks", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = Purple200.copy(alpha = 0.5f),
            selectedTextColor = Purple900,
            unselectedTextColor = Slate400
          ),
          modifier = Modifier.testTag("nav_item_mocks")
        )

        NavigationBarItem(
          selected = activeBottomTab == "live",
          onClick = { viewModel.navigateTo(Screen.LiveClass) },
          icon = { Text("🎥", fontSize = 18.sp) },
          label = { Text("Live Class", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = Purple200.copy(alpha = 0.5f),
            selectedTextColor = Purple900,
            unselectedTextColor = Slate400
          ),
          modifier = Modifier.testTag("nav_item_live")
        )

        NavigationBarItem(
          selected = activeBottomTab == "ai",
          onClick = { viewModel.navigateTo(Screen.AiMentor) },
          icon = { Text("🤖", fontSize = 18.sp) },
          label = { Text("AI Mentor", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = Purple200.copy(alpha = 0.5f),
            selectedTextColor = Purple900,
            unselectedTextColor = Slate400
          ),
          modifier = Modifier.testTag("nav_item_ai")
        )

        NavigationBarItem(
          selected = false,
          onClick = {
            viewModel.showToast("Candidate: ${currentUser?.name} • District: ${currentUser?.district} (${currentUser?.role})")
          },
          icon = { Text("👤", fontSize = 18.sp) },
          label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = NavigationBarItemDefaults.colors(
            selectedTextColor = Purple900,
            unselectedTextColor = Slate400
          ),
          modifier = Modifier.testTag("nav_item_profile")
        )
      }
    }
  }
) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (val screen = currentScreen) {
        Screen.Home -> {
          ExamDashboardScreen(
            categories = examCategories,
            tests = allTests,
            currentUser = currentUser,
            latestAttempt = latestAttempt,
            totalAttempted = totalTestsAttempted,
            averageScore = averageScore,
            onSelectCategory = { cat -> viewModel.openFolder(cat) },
            onAttemptTest = { test -> viewModel.openExamInstructions(test) },
            onOpenLiveClass = { viewModel.navigateTo(Screen.LiveClass) },
            onOpenAiMentor = { viewModel.navigateTo(Screen.AiMentor) },
            onReviewAttempt = { viewModel.navigateTo(Screen.Scorecard) },
            onShowToast = { msg -> viewModel.showToast(msg) },
            onOpenCommunity = { viewModel.setCommunityDialogVisible(true) }
          )
        }

        is Screen.FolderDetail -> {
          FolderDetailScreen(
            category = screen.category,
            tests = allTests,
            isTeacher = isTeacherOrAdmin,
            onBack = { viewModel.navigateTo(Screen.Home) },
            onAttemptTest = { test -> viewModel.openExamInstructions(test) },
            onOpenCreateMock = { viewModel.navigateTo(Screen.CreateMock) }
          )
        }

        is Screen.ExamInstructions -> {
          activeTest?.let { test ->
            ExamInstructionsScreen(
              test = test,
              selectedLanguage = selectedLanguage,
              onSelectLanguage = { lang -> viewModel.setSelectedLanguage(lang) },
              onStartExam = { viewModel.startExam(test) },
              onBack = { viewModel.navigateTo(Screen.FolderDetail(selectedCategory)) }
            )
          }
        }

        is Screen.ExamHall -> {
          activeTest?.let { test ->
            ExamHallScreen(
              test = test,
              questionIndex = currentQuestionIndex,
              selectedOptionIndex = selectedOptionIndex,
              secondsRemaining = secondsRemaining,
              questionTimeSeconds = questionTimeSeconds,
              isPaused = isExamPaused,
              cbtProgress = cbtProgress,
              activeSection = activeSectionName,
              isPaletteDrawerOpen = isPaletteDrawerOpen,
              paletteTab = paletteTab,
              showInstructionsModal = showInstructionsModal,
              showSubmitConfirmModal = showSubmitConfirmModal,
              showReportDialog = showReportDialog,
              onSelectOption = { idx -> viewModel.selectOption(idx) },
              onClearOption = { viewModel.clearCurrentOption() },
              onSaveAndNext = { viewModel.saveAndNext() },
              onMarkAndNext = { viewModel.markAndNext() },
              onToggleBookmark = { viewModel.toggleBookmark() },
              onToggleReview = { viewModel.toggleReview() },
              onJumpToQuestion = { idx -> viewModel.jumpToQuestion(idx) },
              onSelectSection = { sec -> viewModel.selectSection(sec) },
              onTogglePause = { viewModel.togglePause() },
              onSetPaletteDrawerOpen = { open -> viewModel.setPaletteDrawerOpen(open) },
              onSetPaletteTab = { tab -> viewModel.setPaletteTab(tab) },
              onSetShowInstructionsModal = { show -> viewModel.setShowInstructionsModal(show) },
              onSetShowSubmitConfirmModal = { show -> viewModel.setShowSubmitConfirmModal(show) },
              onSetShowReportDialog = { show -> viewModel.setShowReportDialog(show) },
              onSubmitExam = { viewModel.submitExam() },
              onExitExam = { viewModel.navigateTo(Screen.FolderDetail(selectedCategory)) }
            )
          }
        }

        Screen.Scorecard -> {
          ScorecardScreen(
            attempt = latestAttempt,
            onOpenAiMentor = { viewModel.navigateTo(Screen.AiMentor) },
            onViewCertificate = { viewModel.setCertificateDialogVisible(true) },
            onReturnToCatalog = { viewModel.navigateTo(Screen.Home) }
          )
        }

        Screen.LiveClass -> {
          LiveWhiteboardScreen(
            isTeacher = isTeacherOrAdmin,
            whiteboardStrokes = whiteboardStrokes,
            activeStroke = activeStroke,
            currentTool = currentTool,
            currentStrokeColor = currentStrokeColor,
            currentStrokeWidth = currentStrokeWidth,
            streamResolution = streamResolution,
            isStreamPaused = isStreamPaused,
            handRaised = handRaised,
            chatMessages = chatMessages,
            chatFilterDoubtsOnly = chatFilterDoubtsOnly,
            onStartDrawing = { pt -> viewModel.startDrawing(pt) },
            onUpdateDrawing = { pt -> viewModel.updateDrawing(pt) },
            onFinishDrawing = { viewModel.finishDrawing() },
            onSetTool = { tool -> viewModel.setWhiteboardTool(tool) },
            onSetColor = { col -> viewModel.setWhiteboardColor(col) },
            onSetSize = { sz -> viewModel.setWhiteboardSize(sz) },
            onClearWhiteboard = { viewModel.clearWhiteboard() },
            onInsertOdishaFact = { viewModel.insertOdishaFactNote() },
            onSetStreamResolution = { res -> viewModel.setStreamResolution(res) },
            onToggleStreamPause = { viewModel.toggleStreamPause() },
            onToggleRaiseHand = { viewModel.toggleRaiseHand() },
            onToggleChatFilter = { doubts -> viewModel.toggleChatFilter(doubts) },
            onSendChatMessage = { txt, isDoubt -> viewModel.sendLiveChatMessage(txt, isDoubt) },
            onBack = { viewModel.navigateTo(Screen.Home) },
            onShowToast = { msg -> viewModel.showToast(msg) }
          )
        }

        Screen.AiMentor -> {
          AiMentorScreen(
            latestAttempt = latestAttempt,
            chatMessages = aiChatMessages,
            onSendMessage = { query -> viewModel.sendAiUserMessage(query) },
            onBack = { viewModel.navigateTo(Screen.Home) }
          )
        }

        Screen.CreateMock -> {
          CreateMockScreen(
            draftTitle = draftTitle,
            draftCategory = draftCategory,
            draftQuestions = draftQuestions,
            onUpdateTitle = { t -> viewModel.updateDraftTitle(t) },
            onUpdateCategory = { c -> viewModel.updateDraftCategory(c) },
            onUpdateQuestion = { idx, q -> viewModel.updateDraftQuestion(idx, q) },
            onAddQuestion = { viewModel.addDraftQuestion() },
            onRemoveQuestion = { idx -> viewModel.removeDraftQuestion(idx) },
            onPublish = {
              viewModel.publishDraftMock {
                viewModel.navigateTo(Screen.FolderDetail(draftCategory))
              }
            },
            onCancel = { viewModel.navigateTo(Screen.FolderDetail(selectedCategory)) }
          )
        }

        Screen.Auth -> {}
      }

      // Toast Notification Overlay
      AnimatedVisibility(
        visible = toastMessage != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        toastMessage?.let { msg ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Purple950)
              .border(1.dp, Purple700, RoundedCornerShape(12.dp))
              .padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Amber400)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = msg,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }

  // Modals & Dialogs
  if (showCertificateDialog) {
    CertificateDialog(
      attempt = latestAttempt,
      candidateName = currentUser?.name ?: "Aarav Mohanty",
      onDismiss = { viewModel.setCertificateDialogVisible(false) },
      onPrint = {
        viewModel.showToast("Certificate saved to device credentials!")
        viewModel.setCertificateDialogVisible(false)
      }
    )
  }

  if (showNotificationsDialog) {
    NotificationsDialog(
      isTeacherOrAdmin = isTeacherOrAdmin,
      approvalRequests = approvalRequests,
      onApprove = { req -> viewModel.approveTeacherRequest(req) },
      onDecline = { req -> viewModel.declineTeacherRequest(req) },
      onDismiss = { viewModel.setNotificationsDialogVisible(false) }
    )
  }

  if (showCommunityDialog) {
    CommunityBatchDialog(
      onDismiss = { viewModel.setCommunityDialogVisible(false) },
      onShowToast = { msg -> viewModel.showToast(msg) }
    )
  }

  if (showSecurityDialog) {
    com.example.ui.components.SecurityBackupDialog(
      currentUser = currentUser,
      remainingQuota = aiQuotaRemaining,
      onDismiss = { viewModel.setSecurityDialogVisible(false) },
      onExportBackup = { viewModel.exportDataBackup() },
      onRestoreBackup = { json ->
        viewModel.restoreDataBackup(
          jsonString = json,
          onSuccess = { msg -> viewModel.showToast(msg) },
          onError = { err -> viewModel.showToast("Error: $err") }
        )
      }
    )
  }
}
