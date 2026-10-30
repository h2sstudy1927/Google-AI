package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamTest

@Composable
fun ExamInstructionsScreen(
  test: ExamTest,
  selectedLanguage: String,
  onSelectLanguage: (String) -> Unit,
  onStartExam: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var languageMenuExpanded by remember { mutableStateOf(false) }
  val sections = remember(test) { test.questions.map { it.topic }.distinct() }
  val totalQuestions = test.questions.size
  val totalMarks = test.totalMarks.toDouble()

  val darkBg = Color(0xFF14171C)
  val textMuted = Color(0xFF94A3B8)
  val textLight = Color(0xFFE2E8F0)
  val bluePrimary = Color(0xFF3B82F6)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(darkBg)
      .windowInsetsPadding(WindowInsets.safeDrawing)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
      // Top Navigation: "< Your Tests"
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onBack() }
          .padding(vertical = 6.dp, horizontal = 4.dp)
          .testTag("instructions_back_btn")
      ) {
        Text(
          text = "‹",
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Your Tests",
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Center Test Title
      Text(
        text = test.title,
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Duration & Maximum Marks Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Duration: ${test.durationMinutes} Mins.",
          color = textMuted,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
        Text(
          text = String.format(java.util.Locale.ROOT, "Maximum Marks: %.1f", totalMarks),
          color = textMuted,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Bulleted Instructions (Scrollable)
      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        val sectionCount = if (sections.isNotEmpty()) sections.size else 1
        InstructionBullet(
          text = "• The test contain $sectionCount sections having $totalQuestions questions."
        )
        InstructionBullet(
          text = "• Each question has 5 options out of which only one is correct."
        )
        InstructionBullet(
          text = "• You have to finish the test in ${test.durationMinutes} minutes."
        )
        InstructionBullet(
          text = "• You will be awarded 1 mark for each correct answer and 0.25 will be deducted for each wrong answer."
        )
        InstructionBullet(
          text = "• There is no negative marking for the questions that you have not attempted."
        )
        InstructionBullet(
          text = "• You can write this test only once. Make sure that you complete the test before you submit the test and/or close the browser."
        )
        InstructionBullet(
          text = "• I have read all the instructions carefully and have understood them. I agree not to cheat or use unfair means in this examination. I understand that using unfair means of any sort for my own or someone else's advantage will lead to my immediate disqualification. The decision of H2S Study will be final in these matters and cannot be appealed."
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // "Choose your Default Language" Selector Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
          .background(Color(0xFF1E2228))
          .clickable { languageMenuExpanded = true }
          .padding(horizontal = 14.dp, vertical = 12.dp)
          .testTag("choose_language_btn")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Choose your Default Language: $selectedLanguage",
            color = textLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
          )
          Text(
            text = "▼",
            color = textMuted,
            fontSize = 10.sp
          )
        }

        DropdownMenu(
          expanded = languageMenuExpanded,
          onDismissRequest = { languageMenuExpanded = false },
          modifier = Modifier.background(Color(0xFF1E242B))
        ) {
          listOf("English", "Odia", "Hindi").forEach { lang ->
            DropdownMenuItem(
              text = {
                Text(
                  text = lang,
                  color = if (lang == selectedLanguage) bluePrimary else Color.White,
                  fontSize = 13.sp,
                  fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Normal
                )
              },
              onClick = {
                onSelectLanguage(lang)
                languageMenuExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Primary Action: "Agree and Continue" Button
      Button(
        onClick = onStartExam,
        colors = ButtonDefaults.buttonColors(containerColor = bluePrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("agree_and_continue_btn")
      ) {
        Text(
          text = "Agree and Continue",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun InstructionBullet(
  text: String,
  modifier: Modifier = Modifier
) {
  Text(
    text = text,
    color = Color(0xFF94A3B8),
    fontSize = 13.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Normal,
    modifier = modifier.fillMaxWidth()
  )
}
