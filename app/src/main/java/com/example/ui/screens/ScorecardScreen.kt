package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamAttemptResult
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.util.Locale

@Composable
fun ScorecardScreen(
  attempt: ExamAttemptResult?,
  onOpenAiMentor: () -> Unit,
  onViewCertificate: () -> Unit,
  onReturnToCatalog: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (attempt == null) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text("No exam attempt found.", color = Slate600)
    }
    return
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(28.dp))
        .border(1.dp, Slate200, RoundedCornerShape(28.dp)),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Trophy / Icon
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFFECFDF5)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "🎓", fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Odisha Mock Exam Concluded!",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Slate900,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = attempt.testTitle,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Purple800,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Evaluated under strict Odisha Recruitment marking:\n+1.00 for correct, -0.25 penalty for incorrect.",
          fontSize = 11.sp,
          color = Slate500,
          textAlign = TextAlign.Center,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4 Metric Breakdown Cards in a 2x2 Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ScoreMetricCard(
            value = "${attempt.correctCount}",
            label = "CORRECT (+1.00)",
            valueColor = Slate900,
            bgColor = Color(0xFFF8FAFC),
            modifier = Modifier.weight(1f)
          )

          ScoreMetricCard(
            value = "${attempt.wrongCount}",
            label = "INCORRECT",
            valueColor = Rose600,
            bgColor = Color(0xFFFFF1F2),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ScoreMetricCard(
            value = String.format(Locale.ROOT, "-%.2f", attempt.penaltyDeducted),
            label = "NEGATIVE PENALTY",
            valueColor = Amber700,
            bgColor = Color(0xFFFFFBEB),
            modifier = Modifier.weight(1f)
          )

          ScoreMetricCard(
            value = String.format(Locale.ROOT, "%.2f pts", attempt.netScore),
            label = "TOTAL NET SCORE",
            valueColor = Purple900,
            bgColor = Color(0xFFFAF5FF),
            borderColor = Purple200,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Action Buttons
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onOpenAiMentor,
            colors = ButtonDefaults.buttonColors(containerColor = Purple700),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("scorecard_ask_ai_btn")
          ) {
            Text(
              text = "🤖  Ask AI: What did I do wrong?",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Button(
            onClick = onViewCertificate,
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("scorecard_view_cert_btn")
          ) {
            Text(
              text = "📜  View Merit Certificate",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          OutlinedButton(
            onClick = onReturnToCatalog,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("scorecard_return_btn")
          ) {
            Text(
              text = "Back to Exam Folders",
              color = Slate700,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(60.dp))
  }
}

@Composable
fun ScoreMetricCard(
  value: String,
  label: String,
  valueColor: Color,
  bgColor: Color,
  borderColor: Color = Slate200,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(16.dp))
      .padding(14.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = valueColor
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = Slate500,
        textAlign = TextAlign.Center
      )
    }
  }
}
