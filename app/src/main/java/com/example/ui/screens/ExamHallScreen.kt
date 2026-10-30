package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CbtQuestionProgress
import com.example.data.model.CbtQuestionStatus
import com.example.data.model.ExamTest
import com.example.ui.components.InteractiveWhiteboard
import java.util.Locale

// Theme colors matching reference screenshots 2 & 3
private val DarkBg = Color(0xFF14171C)
private val CardBg = Color(0xFF1F242D)
private val CardBorder = Color(0xFF2E3846)
private val BluePrimary = Color(0xFF3B82F6)
private val BlueLight = Color(0xFF60A5FA)
private val StarRose = Color(0xFFE11D48)
private val StarYellow = Color(0xFFFBBF24)
private val GreenMark = Color(0xFF10B981)
private val RedMark = Color(0xFFEF4444)
private val UnattemptedGrey = Color(0xFF475569)
private val UnseenBorder = Color(0xFF334155)
private val TextWhite = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamHallScreen(
  test: ExamTest,
  questionIndex: Int,
  selectedOptionIndex: Int?,
  secondsRemaining: Int,
  questionTimeSeconds: Int,
  isPaused: Boolean,
  cbtProgress: Map<Int, CbtQuestionProgress>,
  activeSection: String,
  isPaletteDrawerOpen: Boolean,
  paletteTab: String,
  showInstructionsModal: Boolean,
  showSubmitConfirmModal: Boolean,
  showReportDialog: Boolean,
  onSelectOption: (Int) -> Unit,
  onClearOption: () -> Unit,
  onSaveAndNext: () -> Unit,
  onMarkAndNext: () -> Unit,
  onToggleBookmark: () -> Unit,
  onToggleReview: () -> Unit,
  onJumpToQuestion: (Int) -> Unit,
  onSelectSection: (String) -> Unit,
  onTogglePause: () -> Unit,
  onSetPaletteDrawerOpen: (Boolean) -> Unit,
  onSetPaletteTab: (String) -> Unit,
  onSetShowInstructionsModal: (Boolean) -> Unit,
  onSetShowSubmitConfirmModal: (Boolean) -> Unit,
  onSetShowReportDialog: (Boolean) -> Unit,
  onSubmitExam: () -> Unit,
  onExitExam: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentQuestion = test.questions.getOrNull(questionIndex) ?: return
  val totalQuestions = test.questions.size

  // Group questions by section/topic
  val sections = remember(test) {
    test.questions.map { it.topic }.distinct()
  }

  val hours = secondsRemaining / 3600
  val minutes = (secondsRemaining % 3600) / 60
  val seconds = secondsRemaining % 60
  val timerFormatted = String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)

  val qMinutes = questionTimeSeconds / 60
  val qSeconds = questionTimeSeconds % 60
  val questionTimerFormatted = String.format(Locale.ROOT, "%02d:%02d", qMinutes, qSeconds)

  val currentProg = cbtProgress[questionIndex] ?: CbtQuestionProgress(questionIndex)
  val isBookmarked = currentProg.isBookmarked
  val isMarkedForReview = currentProg.isMarkedForReview

  var showWhiteboard by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg)
      .windowInsetsPadding(WindowInsets.safeDrawing)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {

      // ==========================================
      // 1. TOP HEADER APP BAR (Screenshot 2)
      // ==========================================
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF181B20))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Pause Button
        IconButton(
          onClick = onTogglePause,
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF22272F))
            .testTag("pause_exam_btn")
        ) {
          Text(
            text = if (isPaused) "▶" else "⏸",
            color = if (isPaused) GreenMark else Color.White,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Timer HH:MM:SS
        Text(
          text = timerFormatted,
          color = if (secondsRemaining < 300) RedMark else Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.testTag("exam_timer_display")
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Test Title
        Text(
          text = test.title,
          color = TextWhite,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Section Tabs
        Row(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          sections.forEach { sectionName ->
            val isCurrentSection = sectionName == currentQuestion.topic
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isCurrentSection) Color(0xFF2E3846) else Color.Transparent)
                .clickable { onSelectSection(sectionName) }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = sectionName,
                color = if (isCurrentSection) Color.White else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isCurrentSection) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Scratchpad / Whiteboard rough work button
        IconButton(
          onClick = { showWhiteboard = !showWhiteboard },
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (showWhiteboard) BluePrimary else Color(0xFF22272F))
            .testTag("rough_whiteboard_btn")
        ) {
          Text("✏️", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Palette Toggle Hamburger
        IconButton(
          onClick = { onSetPaletteDrawerOpen(!isPaletteDrawerOpen) },
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF22272F))
            .testTag("palette_toggle_btn")
        ) {
          Text("☰", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      }

      HorizontalDivider(color = CardBorder, thickness = 1.dp)

      // ==========================================
      // 2. SUB-BAR: Question number, timer, marking, icons (Screenshot 2)
      // ==========================================
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF14171C))
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left side: Question Badge + Question Timer + Marking scheme
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Question Number Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CardBg)
              .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Question ${questionIndex + 1}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Per-question timer: ⏱ 00:14
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CardBg)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("⏱", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = questionTimerFormatted,
              color = TextMuted,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.SemiBold
            )
          }

          // +1.0 Green Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF064E3B).copy(alpha = 0.5f))
              .border(1.dp, GreenMark.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "+1.0",
              color = GreenMark,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // -0.25 Red Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF7F1D1D).copy(alpha = 0.4f))
              .border(1.dp, RedMark.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "-0.25",
              color = RedMark,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Right side: Report ⚠, Bookmark 🔖, Star ⭐
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Report ⚠
          IconButton(
            onClick = { onSetShowReportDialog(true) },
            modifier = Modifier
              .size(34.dp)
              .testTag("report_question_btn")
          ) {
            Text("⚠", color = TextMuted, fontSize = 15.sp)
          }

          // Bookmark 🔖
          IconButton(
            onClick = onToggleBookmark,
            modifier = Modifier
              .size(34.dp)
              .testTag("bookmark_question_btn")
          ) {
            Text(
              text = "🔖",
              color = if (isBookmarked) StarYellow else TextMuted,
              fontSize = 16.sp
            )
          }

          // Mark for Review ⭐
          IconButton(
            onClick = onToggleReview,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(if (isMarkedForReview) StarRose.copy(alpha = 0.2f) else Color.Transparent)
              .testTag("star_review_btn")
          ) {
            Text(
              text = "⭐",
              color = if (isMarkedForReview) StarYellow else TextMuted,
              fontSize = 16.sp
            )
          }
        }
      }

      HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 1.dp)

      // ==========================================
      // 3. MAIN CONTENT: Question & 5 Options (Scrollable)
      // ==========================================
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Section header subtitle
          Text(
            text = "SECTION: ${currentQuestion.topic.uppercase()}",
            color = BlueLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )

          // Question Statement
          Text(
            text = currentQuestion.question,
            color = TextWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("exam_question_statement")
          )

          Spacer(modifier = Modifier.height(6.dp))

          // 5 Option Cards
          val options = currentQuestion.options
          options.forEachIndexed { optIndex, optionText ->
            val isSelected = selectedOptionIndex == optIndex
            val optionNumber = "${optIndex + 1}."

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Color(0xFF1E2A38) else CardBg)
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) BluePrimary else CardBorder,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { onSelectOption(optIndex) }
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("option_card_$optIndex")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
              ) {
                // Option number prefix: 1., 2., 3., 4., 5.
                Text(
                  text = optionNumber,
                  color = if (isSelected) BlueLight else TextMuted,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.width(26.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Option Text
                Text(
                  text = optionText,
                  color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                  fontSize = 14.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                  lineHeight = 20.sp,
                  modifier = Modifier.weight(1f)
                )

                if (isSelected) {
                  Spacer(modifier = Modifier.width(8.dp))
                  Box(
                    modifier = Modifier
                      .size(20.dp)
                      .clip(CircleShape)
                      .background(BluePrimary),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))
        }

        // Optional Scratchpad / Whiteboard Overlay for rough calculations
        if (showWhiteboard) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0xEE0B0E14))
              .padding(12.dp)
          ) {
            Column(modifier = Modifier.fillMaxSize()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Scratchpad / Rough Work Canvas",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                  onClick = { showWhiteboard = false },
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Close", color = Color.White, fontSize = 12.sp)
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              InteractiveWhiteboard(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
                title = "Rough Sheet",
                subtitle = "Draw diagrams, equations & quick rough calculations"
              )
            }
          }
        }
      }

      HorizontalDivider(color = CardBorder, thickness = 1.dp)

      // ==========================================
      // 4. BOTTOM ACTION BAR (Screenshot 2)
      // Buttons: Mark & Next | Clear | Save & Next
      // ==========================================
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF181B20))
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Mark & Next Button (Outlined with star)
        OutlinedButton(
          onClick = onMarkAndNext,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
          ),
          modifier = Modifier
            .height(44.dp)
            .testTag("mark_and_next_btn")
        ) {
          Text("⭐ ", fontSize = 12.sp)
          Text("Mark & Next", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        // Clear Button (Outlined)
        OutlinedButton(
          onClick = onClearOption,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextMuted
          ),
          modifier = Modifier
            .height(44.dp)
            .testTag("clear_option_btn")
        ) {
          Text("Clear", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        // Save & Next Button (Solid Vibrant Blue)
        Button(
          onClick = onSaveAndNext,
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .height(44.dp)
            .testTag("save_and_next_btn")
        ) {
          Text(
            text = if (questionIndex == totalQuestions - 1) "Save & Submit" else "Save & Next",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // ==========================================
    // 5. QUESTION PALETTE DRAWER (Screenshot 3)
    // Slides in from the right edge
    // ==========================================
    AnimatedVisibility(
      visible = isPaletteDrawerOpen,
      enter = slideInHorizontally(initialOffsetX = { it }),
      exit = slideOutHorizontally(targetOffsetX = { it }),
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .fillMaxHeight()
        .width(360.dp)
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF181B20),
        shadowElevation = 16.dp
      ) {
        Column(modifier = Modifier.fillMaxSize()) {

          // Drawer Header with Close '✕'
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Question Palette",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            IconButton(
              onClick = { onSetPaletteDrawerOpen(false) },
              modifier = Modifier.size(30.dp)
            ) {
              Text("✕", color = TextMuted, fontSize = 16.sp)
            }
          }

          // Tabs: Grid View | List View
          TabRow(
            selectedTabIndex = if (paletteTab == "GRID") 0 else 1,
            containerColor = Color(0xFF14171C),
            contentColor = BluePrimary,
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(
                  tabPositions[if (paletteTab == "GRID") 0 else 1]
                ),
                color = BluePrimary
              )
            }
          ) {
            Tab(
              selected = paletteTab == "GRID",
              onClick = { onSetPaletteTab("GRID") },
              text = {
                Text(
                  "Grid View",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (paletteTab == "GRID") BlueLight else TextMuted
                )
              }
            )
            Tab(
              selected = paletteTab == "LIST",
              onClick = { onSetPaletteTab("LIST") },
              text = {
                Text(
                  "List View",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (paletteTab == "LIST") BlueLight else TextMuted
                )
              }
            )
          }

          // "View Instructions" link
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSetShowInstructionsModal(true) }
              .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("ℹ️", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "View Instructions",
              color = BlueLight,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          HorizontalDivider(color = CardBorder, thickness = 1.dp)

          // Status Legend (Matching TCS iON / SBI / IBPS pattern)
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF14171C))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              TcsLegendBadge(
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                bg = Color(0xFF16A34A),
                label = "Attempted"
              )
              TcsLegendBadge(
                shape = RoundedCornerShape(0.dp),
                bg = Color.White,
                border = Color(0xFF1E293B),
                label = "Unseen"
              )
              TcsLegendBadge(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                bg = Color(0xFFDC2626),
                label = "Unattempted"
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              TcsLegendBadge(
                shape = CircleShape,
                bg = Color(0xFF7C3AED),
                label = "Marked for Review"
              )
              TcsLegendBadge(
                shape = CircleShape,
                bg = Color(0xFF7C3AED),
                label = "Ans & Marked",
                hasGreenDot = true
              )
            }
          }

          HorizontalDivider(color = CardBorder, thickness = 1.dp)

          // Section Accordions & Questions List / Grid
          Column(
            modifier = Modifier
              .weight(1f)
              .verticalScroll(rememberScrollState())
              .padding(12.dp)
          ) {
            sections.forEach { sectionName ->
              val sectionQuestions = test.questions.withIndex().filter { it.value.topic == sectionName }
              val indices = sectionQuestions.map { it.index }

              var isSectionExpanded by remember { mutableStateOf(true) }

              // Count stats for this section
              val markedCount = indices.count { cbtProgress[it]?.isMarkedForReview == true }
              val attemptedCount = indices.count { cbtProgress[it]?.selectedOption != null }
              val unattemptedCount = indices.count {
                val p = cbtProgress[it]
                p != null && p.isVisited && p.selectedOption == null
              }
              val unseenCount = indices.count {
                val p = cbtProgress[it]
                p == null || !p.isVisited
              }

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2228))
              ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  // Accordion Header
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { isSectionExpanded = !isSectionExpanded }
                      .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = if (isSectionExpanded) "▼" else "▶",
                        color = TextMuted,
                        fontSize = 10.sp
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = sectionName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 140.dp)
                      )
                    }

                    // Badge counts: [⭐ 0] [🔵 1] [⚪ 0] [⭕ 29]
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      MiniStatBadge(icon = "⭐", count = markedCount, bg = StarRose.copy(alpha = 0.3f))
                      MiniStatBadge(icon = "🔵", count = attemptedCount, bg = BluePrimary.copy(alpha = 0.3f))
                      MiniStatBadge(icon = "⚪", count = unattemptedCount, bg = UnattemptedGrey.copy(alpha = 0.3f))
                      MiniStatBadge(icon = "⭕", count = unseenCount, bg = UnseenBorder.copy(alpha = 0.3f))
                    }
                  }

                  // Questions in this section
                  if (isSectionExpanded) {
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

                    if (paletteTab == "GRID") {
                      Column(modifier = Modifier.fillMaxWidth()) {
                        // SECTION Header Banner (Pale cyan/blue banner matching user reference image IMG_20261030_175207_564_0f3b302f.jpg)
                        Box(
                          modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFC7DDE7))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                          Text(
                            text = "SECTION : ${sectionName.uppercase(Locale.ROOT)}",
                            color = Color(0xFF1E293B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                          )
                        }

                        // Grid of 5-column questions with authentic TCS iON shapes on light background
                        Column(
                          modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEF3F6))
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                          verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                          val rows = sectionQuestions.chunked(5)
                          rows.forEach { rowQuestions ->
                            Row(
                              modifier = Modifier.fillMaxWidth(),
                              horizontalArrangement = Arrangement.SpaceBetween,
                              verticalAlignment = Alignment.CenterVertically
                            ) {
                              rowQuestions.forEach { (qIdx, _) ->
                                val prog = cbtProgress[qIdx] ?: CbtQuestionProgress(qIdx)
                                val isCurrent = qIdx == questionIndex
                                CbtGridBadge(
                                  questionNumber = qIdx + 1,
                                  status = prog.status,
                                  isCurrent = isCurrent,
                                  onClick = { onJumpToQuestion(qIdx) }
                                )
                              }
                              // Fill remaining slots in last row to maintain strict 5-column grid alignment
                              repeat(5 - rowQuestions.size) {
                                Spacer(modifier = Modifier.width(46.dp))
                              }
                            }
                          }
                        }
                      }
                    } else {
                      // List View with snippets
                      Column(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        sectionQuestions.forEach { (qIdx, qItem) ->
                          val prog = cbtProgress[qIdx] ?: CbtQuestionProgress(qIdx)
                          val isCurrent = qIdx == questionIndex

                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .clip(RoundedCornerShape(6.dp))
                              .background(if (isCurrent) Color(0xFF2E3846) else Color(0xFF14171C))
                              .clickable { onJumpToQuestion(qIdx) }
                              .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Text(
                              text = "Q${qIdx + 1}",
                              color = if (isCurrent) BlueLight else Color.White,
                              fontWeight = FontWeight.Bold,
                              fontSize = 12.sp,
                              modifier = Modifier.width(32.dp)
                            )
                            Text(
                              text = qItem.question,
                              color = TextMuted,
                              fontSize = 11.sp,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis,
                              modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val statusIcon = when (prog.status) {
                              CbtQuestionStatus.ATTEMPTED -> "🔵"
                              CbtQuestionStatus.ATTEMPTED_AND_MARKED -> "🔵★"
                              CbtQuestionStatus.MARKED_FOR_REVIEW -> "⭐"
                              CbtQuestionStatus.UNATTEMPTED -> "⚪"
                              CbtQuestionStatus.UNSEEN -> "⭕"
                            }
                            Text(statusIcon, fontSize = 11.sp)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          HorizontalDivider(color = CardBorder, thickness = 1.dp)

          // Sticky Bottom Buttons in Drawer (Screenshot 3)
          // SUBMIT SECTION | SUBMIT TEST
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF14171C))
              .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { onSetShowSubmitConfirmModal(true) },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("submit_section_btn")
            ) {
              Text(
                text = "SUBMIT SECTION",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Button(
              onClick = { onSetShowSubmitConfirmModal(true) },
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("submit_test_btn")
            ) {
              Text(
                text = "SUBMIT TEST",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // ==========================================
    // 6. MODALS & DIALOGS
    // ==========================================

    // A. Submit Confirmation Dialog
    if (showSubmitConfirmModal) {
      val total = test.questions.size
      val attempted = cbtProgress.values.count { it.selectedOption != null }
      val marked = cbtProgress.values.count { it.isMarkedForReview }
      val unattempted = cbtProgress.values.count { it.isVisited && it.selectedOption == null }
      val unseen = total - cbtProgress.values.count { it.isVisited }

      AlertDialog(
        onDismissRequest = { onSetShowSubmitConfirmModal(false) },
        containerColor = Color(0xFF1E2228),
        title = {
          Text(
            text = "Submit Test Confirmation",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Exam Summary Statistics:",
              color = TextMuted,
              fontSize = 13.sp
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Total Questions:", color = TextMuted, fontSize = 12.sp)
              Text("$total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Attempted:", color = BlueLight, fontSize = 12.sp)
              Text("$attempted", color = BlueLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Marked for Review:", color = StarYellow, fontSize = 12.sp)
              Text("$marked", color = StarYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Unattempted:", color = UnattemptedGrey, fontSize = 12.sp)
              Text("$unattempted", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Unseen:", color = TextMuted, fontSize = 12.sp)
              Text("$unseen", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Are you sure you want to finish the exam? You cannot modify answers after submission.",
              color = Color(0xFFE2E8F0),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        },
        confirmButton = {
          Button(
            onClick = onSubmitExam,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("confirm_submit_btn")
          ) {
            Text("Submit Exam", color = Color.White, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          OutlinedButton(
            onClick = { onSetShowSubmitConfirmModal(false) },
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Cancel", color = Color.White)
          }
        }
      )
    }

    // B. View Instructions Modal
    if (showInstructionsModal) {
      AlertDialog(
        onDismissRequest = { onSetShowInstructionsModal(false) },
        containerColor = Color(0xFF1E2228),
        title = {
          Text(
            text = "Exam Instructions",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        },
        text = {
          Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("• Each question has 5 options out of which only one is correct.", color = TextMuted, fontSize = 12.sp)
            Text("• Award: +1.0 for each correct answer; Penalty: -0.25 for wrong answer.", color = TextMuted, fontSize = 12.sp)
            Text("• No negative marking for unattempted questions.", color = TextMuted, fontSize = 12.sp)
            Text("• Color Legend in Palette: 🔵 Attempted, ⭐ Marked for Review, ⚪ Unattempted, ⭕ Unseen.", color = TextMuted, fontSize = 12.sp)
            Text("• You can jump across sections and questions using the Question Palette.", color = TextMuted, fontSize = 12.sp)
          }
        },
        confirmButton = {
          Button(
            onClick = { onSetShowInstructionsModal(false) },
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Close", color = Color.White)
          }
        }
      )
    }

    // C. Exam Paused Overlay
    if (isPaused) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xD9000000)),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2228)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Text("⏸", fontSize = 36.sp)
            Text(
              text = "Exam Paused",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Timer is paused. Click Resume to return to your questions.",
              color = TextMuted,
              fontSize = 13.sp,
              textAlign = TextAlign.Center
            )
            Button(
              onClick = onTogglePause,
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Resume Test", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // D. Report Question Dialog
    if (showReportDialog) {
      AlertDialog(
        onDismissRequest = { onSetShowReportDialog(false) },
        containerColor = Color(0xFF1E2228),
        title = {
          Text("Report Question", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
          Text(
            text = "Do you want to report an issue (e.g. typographical error, incorrect options, or formatting issue) in Question ${questionIndex + 1}? Our subject matter faculty will inspect it.",
            color = TextMuted,
            fontSize = 13.sp
          )
        },
        confirmButton = {
          Button(
            onClick = { onSetShowReportDialog(false) },
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Submit Report", color = Color.White)
          }
        },
        dismissButton = {
          OutlinedButton(
            onClick = { onSetShowReportDialog(false) },
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Dismiss", color = Color.White)
          }
        }
      )
    }
  }
}

@Composable
private fun LegendItem(icon: String, label: String, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(3.dp)
  ) {
    Text(icon, fontSize = 11.sp)
    Text(
      text = label,
      color = Color(0xFFCBD5E1),
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
private fun MiniStatBadge(icon: String, count: Int, bg: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(bg)
      .padding(horizontal = 4.dp, vertical = 2.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(icon, fontSize = 8.sp)
      Text(
        text = "$count",
        color = Color.White,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun TcsLegendBadge(
  shape: androidx.compose.ui.graphics.Shape,
  bg: Color,
  border: Color = Color.Transparent,
  label: String,
  hasGreenDot: Boolean = false
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(16.dp, 12.dp)
        .clip(shape)
        .background(bg)
        .then(if (border != Color.Transparent) Modifier.border(1.dp, border, shape) else Modifier)
    ) {
      if (hasGreenDot) {
        Box(
          modifier = Modifier
            .size(4.dp)
            .align(Alignment.BottomEnd)
            .background(Color(0xFF22C55E), CircleShape)
        )
      }
    }
    Text(
      text = label,
      color = Color(0xFFCBD5E1),
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
fun CbtGridBadge(
  questionNumber: Int,
  status: CbtQuestionStatus,
  isCurrent: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // TCS iON / SBI / IBPS shape and color mapping matching user uploaded image IMG_20261030_175207_564_0f3b302f.jpg
  val shape = when {
    isCurrent && (status == CbtQuestionStatus.ATTEMPTED || status == CbtQuestionStatus.ATTEMPTED_AND_MARKED) -> {
      // Pill shape for active answered like '1' in user photo
      RoundedCornerShape(50)
    }
    status == CbtQuestionStatus.ATTEMPTED -> {
      // Dome / arch shape (arched top, flat bottom) like '2', '3', '4', '5' in user photo
      RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    }
    status == CbtQuestionStatus.UNATTEMPTED -> {
      // Inverted dome shape (flat top, curved bottom)
      RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }
    status == CbtQuestionStatus.UNSEEN -> {
      // Crisp rectangular box with sharp 90-degree corners like '6', '7', '8', '9', '10' in user photo
      RoundedCornerShape(0.dp)
    }
    status == CbtQuestionStatus.MARKED_FOR_REVIEW || status == CbtQuestionStatus.ATTEMPTED_AND_MARKED -> {
      CircleShape
    }
    else -> {
      if (isCurrent) RoundedCornerShape(50) else RoundedCornerShape(0.dp)
    }
  }

  val backgroundColor = when (status) {
    CbtQuestionStatus.ATTEMPTED -> Color(0xFF16A34A) // Bright vivid green (like 1, 2, 3, 4, 5 in user photo)
    CbtQuestionStatus.UNATTEMPTED -> Color(0xFFDC2626) // Vivid red
    CbtQuestionStatus.UNSEEN -> Color.White // Pure white box (like 6, 7, 8, 9, 10 in user photo)
    CbtQuestionStatus.MARKED_FOR_REVIEW -> Color(0xFF7C3AED) // Purple circle
    CbtQuestionStatus.ATTEMPTED_AND_MARKED -> Color(0xFF7C3AED) // Purple circle
  }

  val textColor = when (status) {
    CbtQuestionStatus.UNSEEN -> Color(0xFF0F172A) // Dark bold text on white box
    else -> Color.White // White bold text
  }

  val borderStroke = when {
    isCurrent -> 2.dp
    status == CbtQuestionStatus.UNSEEN -> 1.dp
    else -> 0.dp
  }

  val borderColor = when {
    isCurrent -> Color(0xFF0F172A)
    status == CbtQuestionStatus.UNSEEN -> Color(0xFF1E293B) // Sharp dark outline
    else -> Color.Transparent
  }

  Box(
    modifier = modifier
      .width(46.dp)
      .height(32.dp)
      .clip(shape)
      .background(backgroundColor)
      .border(borderStroke, borderColor, shape)
      .clickable(onClick = onClick)
      .testTag("grid_q_$questionNumber"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$questionNumber",
      color = textColor,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )

    // Green dot badge for Attempted & Marked for Review
    if (status == CbtQuestionStatus.ATTEMPTED_AND_MARKED) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 2.dp, bottom = 2.dp)
          .size(6.dp)
          .clip(CircleShape)
          .background(Color(0xFF22C55E))
          .border(0.5.dp, Color.White, CircleShape)
      )
    }
  }
}
