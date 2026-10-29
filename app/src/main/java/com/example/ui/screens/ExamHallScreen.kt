package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.ui.components.InteractiveWhiteboard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamTest
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple600
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import java.util.Locale

@Composable
fun ExamHallScreen(
  test: ExamTest,
  questionIndex: Int,
  selectedOptionIndex: Int?,
  isSubmitted: Boolean,
  liveScore: Double,
  secondsRemaining: Int,
  onSelectOption: (Int) -> Unit,
  onSubmitAnswer: () -> Unit,
  onNextQuestion: () -> Unit,
  onSkipQuestion: () -> Unit,
  onExitExam: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentQuestion = test.questions.getOrNull(questionIndex) ?: return
  val totalQuestions = test.questions.size
  val progress = (questionIndex + 1).toFloat() / totalQuestions
  val progressPercent = ((questionIndex + 1) * 100) / totalQuestions

  val minutes = secondsRemaining / 60
  val seconds = secondsRemaining % 60
  val timeString = String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
  var showScratchpad by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Spacer(modifier = Modifier.height(4.dp))

    // Top Exam Header Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          OutlinedButton(
            onClick = onExitExam,
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("exit_exam_btn")
          ) {
            Text("← Exit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Text(
            text = test.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900,
            maxLines = 1
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "TIME LEFT",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Slate400
            )
            Text(
              text = timeString,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = if (secondsRemaining < 120) Rose600 else Purple700,
              modifier = Modifier.testTag("exam_timer")
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(28.dp)
              .background(Slate200)
          )

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "LIVE SCORE",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Slate400
            )
            Text(
              text = String.format(Locale.ROOT, "%.2f pts", liveScore),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = if (liveScore >= 0) Emerald700 else Rose600,
              modifier = Modifier.testTag("exam_live_score")
            )
          }
        }
      }
    }

    // Main Question Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, Slate200, RoundedCornerShape(24.dp)),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Progress Section
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Question ${questionIndex + 1} of $totalQuestions",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate600
          )
          Text(
            text = "$progressPercent% Completed",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Purple700
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = Purple700,
          trackColor = Slate200
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Topic Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFFAF5FF))
              .border(1.dp, Purple200, RoundedCornerShape(6.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = currentQuestion.topic,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Purple900
            )
          }

          Text(
            text = "Marking: +1.00 / -0.25",
            fontSize = 11.sp,
            color = Slate400,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Statement
        Text(
          text = currentQuestion.question,
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          color = Slate900,
          lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Multiple Choice Options
        currentQuestion.options.forEachIndexed { optIndex, optionText ->
          val isSelected = selectedOptionIndex == optIndex
          val isCorrect = optIndex == currentQuestion.correctIndex

          val (cardBg, borderColor, textColor) = when {
            isSubmitted && isCorrect -> Triple(Color(0xFFECFDF5), Emerald700, Emerald700)
            isSubmitted && isSelected && !isCorrect -> Triple(Color(0xFFFFF1F2), Rose600, Rose600)
            isSelected -> Triple(Color(0xFFFAF5FF), Purple700, Purple900)
            else -> Triple(Color.White, Slate200, Slate800)
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(cardBg)
              .border(
                width = if (isSelected || (isSubmitted && isCorrect)) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
              )
              .clickable(enabled = !isSubmitted) {
                onSelectOption(optIndex)
              }
              .padding(14.dp)
              .testTag("option_item_$optIndex")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) Purple700 else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = ('A'.code + optIndex).toChar().toString(),
                  color = if (isSelected) Color.White else Slate700,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Text(
                text = optionText,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
              )

              if (isSubmitted) {
                if (isCorrect) {
                  Text("✓", color = Emerald700, fontWeight = FontWeight.Black, fontSize = 16.sp)
                } else if (isSelected) {
                  Text("✗", color = Rose600, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
              }
            }
          }
        }

        // Explanation Box
        if (isSubmitted) {
          Spacer(modifier = Modifier.height(16.dp))
          val wasCorrect = selectedOptionIndex == currentQuestion.correctIndex
          val explanationBg = if (wasCorrect) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
          val explanationBorder = if (wasCorrect) Color(0xFFA7F3D0) else Color(0xFFFECDD3)

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(explanationBg)
              .border(1.dp, explanationBorder, RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Column {
              Text(
                text = if (wasCorrect) "✓ Correct! (+1.00 mark earned)" else "✗ Incorrect! (-0.25 negative penalty deducted)",
                color = if (wasCorrect) Emerald700 else Rose600,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentQuestion.explanation,
                color = Slate700,
                fontSize = 12.sp,
                lineHeight = 18.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (!isSubmitted) {
            OutlinedButton(
              onClick = onSkipQuestion,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("skip_question_btn")
            ) {
              Text(
                text = "Skip (0 penalty)",
                fontSize = 12.sp,
                color = Slate600
              )
            }

            Button(
              onClick = onSubmitAnswer,
              enabled = selectedOptionIndex != null,
              colors = ButtonDefaults.buttonColors(containerColor = Purple700),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("submit_answer_btn")
            ) {
              Text(
                text = "Submit Answer",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          } else {
            Spacer(modifier = Modifier.weight(1f))
            Button(
              onClick = onNextQuestion,
              colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("next_question_btn")
            ) {
              Text(
                text = if (questionIndex < totalQuestions - 1) "Next Question →" else "Finish Exam →",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // Interactive Problem Solving Scratchpad for Rough Work
    OutlinedButton(
      onClick = { showScratchpad = !showScratchpad },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("toggle_scratchpad_btn"),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text(
        text = if (showScratchpad) "Hide Problem Solving Scratchpad ▲" else "📐 Open Scratchpad / Whiteboard (Rough Work) ▼",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Purple700
      )
    }

    if (showScratchpad) {
      InteractiveWhiteboard(
        title = "Candidate Scratchpad (Rough Work)",
        subtitle = "Solve calculations, reasoning puzzles & geometry with adjustable stroke width and colors",
        aspectRatio = 16f / 9f
      )
    }

    Spacer(modifier = Modifier.height(60.dp))
  }
}
