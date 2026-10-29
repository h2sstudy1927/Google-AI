package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamAttemptResult
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AiChatMessage
import java.util.Locale

@Composable
fun AiMentorScreen(
  latestAttempt: ExamAttemptResult?,
  chatMessages: List<AiChatMessage>,
  onSendMessage: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var userInputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(chatMessages.size) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(chatMessages.size - 1)
    }
  }

  val accuracy = latestAttempt?.accuracyPercent ?: 0
  val penalty = latestAttempt?.penaltyDeducted ?: 0.0
  val netScore = latestAttempt?.netScore ?: 0.0
  val wrongAttempts = latestAttempt?.questionAttempts?.filter { !it.isCorrect && !it.isSkipped } ?: emptyList()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(Purple950, Color(0xFF312E81), Slate900)
              )
            )
            .padding(16.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("ai_back_btn")
              ) {
                Text("← Back", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(100.dp))
                  .background(Amber400.copy(alpha = 0.2f))
                  .border(1.dp, Amber400.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text("🤖 Odisha Exam AI Mentor", color = Amber400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Exam Diagnostics & Negative Penalty Coach",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "Active Test: ${latestAttempt?.testTitle ?: "None Attempted Yet"}",
              color = Purple200,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // Performance Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "📊  PERFORMANCE SUMMARY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Slate800,
            letterSpacing = 0.5.sp
          )

          // Diagnostic Alert Banner
          val (alertBg, alertBorder, alertText, alertColor) = when {
            latestAttempt == null -> Quad(
              Color(0xFFFFFBEB), Color(0xFFFDE68A),
              "Attempt a mock test first from any folder to activate question-level AI diagnostics.",
              Color(0xFFB45309)
            )
            accuracy >= 80 -> Quad(
              Color(0xFFECFDF5), Color(0xFFA7F3D0),
              "🌟 Topper Level Accuracy ($accuracy%)! Your negative mark control in Odisha GK was exceptional.",
              Emerald700
            )
            accuracy >= 50 -> Quad(
              Color(0xFFFFFBEB), Color(0xFFFDE68A),
              "⚡ Good Attempt ($accuracy%). You lost ${String.format(Locale.ROOT, "%.2f", penalty)} marks from doubtful negative guesses.",
              Color(0xFFB45309)
            )
            else -> Quad(
              Color(0xFFFFF1F2), Color(0xFFFECDD3),
              "⚠️ High Negative Risk ($accuracy%). Odia grammar & GK facts need structured revision.",
              Rose600
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(alertBg)
              .border(1.dp, alertBorder, RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Text(
              text = alertText,
              color = alertColor,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              lineHeight = 16.sp
            )
          }

          // Stats Rows
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Accuracy Ratio:", fontSize = 12.sp, color = Slate600)
            Text(if (latestAttempt != null) "$accuracy%" else "--%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Negative Penalty Lost:", fontSize = 12.sp, color = Slate600)
            Text(
              text = if (latestAttempt != null) String.format(Locale.ROOT, "-%.2f pts", penalty) else "-- pts",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Rose600
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Net Marks Earned:", fontSize = 12.sp, color = Slate600)
            Text(
              text = if (latestAttempt != null) String.format(Locale.ROOT, "%.2f pts", netScore) else "-- pts",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Purple700
            )
          }
        }
      }
    }

    // Topic Mistakes Breakdown
    if (wrongAttempts.isNotEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "⚠️  TOPIC MISTAKES BREAKDOWN",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = Rose600,
              letterSpacing = 0.5.sp
            )

            wrongAttempts.forEach { qa ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFFFFF1F2))
                  .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(12.dp))
                  .padding(10.dp)
              ) {
                Column {
                  Text(
                    text = "Q${qa.questionNumber}: ${qa.topic}",
                    color = Rose600,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = qa.question,
                    color = Slate900,
                    fontSize = 11.sp,
                    maxLines = 2
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Penalty: -0.25 pts. Correct Fact: ${qa.explanation}",
                    color = Slate600,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Interactive AI Coach Chat Box
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Quick Prompts
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
          ) {
            Text(
              text = "Ask AI Coach:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Slate600
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF1F5F9))
                  .clickable { onSendMessage("How can I improve my OSSSC & OSSC score?") }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("How to score high in OSSSC?", fontSize = 10.sp, color = Purple900, fontWeight = FontWeight.SemiBold)
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF1F5F9))
                  .clickable { onSendMessage("What are the key mistakes I made in Odisha GK?") }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("What are my Odisha GK mistakes?", fontSize = 10.sp, color = Purple900, fontWeight = FontWeight.SemiBold)
              }
            }
          }

          // Messages Stream
          LazyColumn(
            state = listState,
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(chatMessages) { msg ->
              val bubbleBg = if (msg.isAi) Color(0xFFFAF5FF) else Purple900
              val textColor = if (msg.isAi) Slate900 else Color.White

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(bubbleBg)
                  .border(1.dp, if (msg.isAi) Purple200 else Color.Transparent, RoundedCornerShape(12.dp))
                  .padding(10.dp)
              ) {
                Column {
                  Text(
                    text = "${if (msg.isAi) "🤖 " else ""}${msg.sender}:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (msg.isAi) Purple700 else Purple200
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = msg.text,
                    fontSize = 12.sp,
                    color = textColor,
                    lineHeight = 17.sp
                  )
                }
              }
            }
          }

          // Input Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC))
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = userInputText,
              onValueChange = { userInputText = it },
              placeholder = { Text("Ask AI Coach about negative marks...", fontSize = 11.sp) },
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("ai_query_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
              )
            )

            Button(
              onClick = {
                val text = userInputText.trim()
                if (text.isNotBlank()) {
                  onSendMessage(text)
                  userInputText = ""
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Purple700),
              modifier = Modifier
                .height(44.dp)
                .testTag("ai_ask_btn")
            ) {
              Text("Ask", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
