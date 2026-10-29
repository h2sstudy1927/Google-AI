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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamTest
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple50
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun FolderDetailScreen(
  category: String,
  tests: List<ExamTest>,
  isTeacher: Boolean,
  onBack: () -> Unit,
  onAttemptTest: (ExamTest) -> Unit,
  onOpenCreateMock: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredTests = tests.filter {
    if (category == "ALL") true
    else it.category.equals(category, ignoreCase = true)
  }.ifEmpty { tests }

  val title = when (category) {
    "OPSC" -> "OPSC Exams Folder (OCS / OAS & ASO)"
    "OSSC" -> "OSSC Exams Folder (CGL & Sub-Inspector)"
    "OSSSC" -> "OSSSC Exams Folder (RI, ARI, Amin, PEO, JA)"
    "OUSSSC" -> "OUSSSC Exams Folder (State & University Cadre)"
    "TEACHING" -> "Teaching Exams Folder (OTET, OSSTET, RHT, JT)"
    else -> "Odisha State Competitive Exams Catalog"
  }

  val subtitle = when (category) {
    "OPSC" -> "Class 1 & 2 Gazetted Civil Services & Assistant Section Officer tests (+1.00 / -0.25)"
    "OSSC" -> "Combined Graduate Level, SI, Junior Clerk & Technical Prelims (+1.00 / -0.25)"
    "OSSSC" -> "Revenue Inspector, Panchayat Executive Officer & District Cadre Tests (+1.00 / -0.25)"
    "OUSSSC" -> "University Junior Assistant & State Sub-Ordinate Services (+1.00 / -0.25)"
    "TEACHING" -> "Secondary & Elementary Teacher Recruitment & Pedagogy (+1.00 / -0.25)"
    else -> "Official Odisha State Government recruitment mock series"
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = onBack,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("folder_back_btn")
            ) {
              Text("← All Exam Folders", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            if (isTeacher) {
              Button(
                onClick = onOpenCreateMock,
                colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("folder_add_mock_btn")
              ) {
                Text("➕ Add Mock", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Slate900
          )
          Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Slate600
          )
        }
      }
    }

    // List of Mock Cards
    items(filteredTests, key = { it.id }) { test ->
      MockTestItemCard(
        test = test,
        onAttempt = { onAttemptTest(test) }
      )
    }
  }
}

@Composable
fun MockTestItemCard(
  test: ExamTest,
  onAttempt: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .border(1.dp, Slate200, RoundedCornerShape(20.dp)),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Purple50)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = test.subject,
            color = Purple900,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Text(
          text = "⏱️ ${test.durationMinutes} Mins",
          fontSize = 12.sp,
          color = Slate400,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = test.title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Slate900
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = test.description,
        fontSize = 12.sp,
        color = Slate600,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(14.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(Slate200.copy(alpha = 0.6f))
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Questions: ${test.questions.size}",
          fontSize = 12.sp,
          color = Slate700,
          fontWeight = FontWeight.Medium
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFECFDF5))
            .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = "+1.00 / -0.25 Mark",
            color = Emerald700,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = onAttempt,
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("attempt_test_btn_${test.id}"),
        colors = ButtonDefaults.buttonColors(containerColor = Purple800),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "Attempt Mock Test  →",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}
