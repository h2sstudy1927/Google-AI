package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategory
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple900
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun HomeFoldersScreen(
  onSelectCategory: (String) -> Unit,
  onOpenLiveClass: () -> Unit,
  onOpenAiMentor: () -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Banner
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Select Exam Category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Slate900
          )
          Text(
            text = "Choose your target Odisha Commission or Teaching folder",
            fontSize = 12.sp,
            color = Slate500
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Purple200.copy(alpha = 0.5f))
            .border(1.dp, Purple900.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "Odisha 2026–27",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Purple900
          )
        }
      }
    }

    // 1. OPSC EXAMS
    item {
      ExamFolderCard(
        badge = "CLASS 1 & 2 GAZETTED",
        badgeBg = Amber400,
        badgeTextColor = Slate950,
        title = "OPSC EXAMS",
        subCategory = "OCS / OAS • ASO • CIVIL SERVICES",
        footerLeft = "Mocks • Odisha History & Polity",
        footerRight = "Open Folder →",
        iconEmoji = "🏛️",
        gradient = listOf(Color(0xFF1E1B4B), Color(0xFF1E3A8A), Color(0xFF312E81)),
        borderColor = Amber400.copy(alpha = 0.6f),
        onClick = { onSelectCategory("OPSC") },
        testTag = "folder_card_opsc"
      )
    }

    // 2. OSSC EXAMS
    item {
      ExamFolderCard(
        badge = "GROUP B & C CADRE",
        badgeBg = Cyan400,
        badgeTextColor = Slate950,
        title = "OSSC EXAMS",
        subCategory = "CGL • SUB-INSPECTOR • JUNIOR CLERK",
        footerLeft = "Mocks • Arithmetic & Reasoning",
        footerRight = "Open Folder →",
        iconEmoji = "📋",
        gradient = listOf(Color(0xFF082F49), Color(0xFF0284C7), Color(0xFF1E3A8A)),
        borderColor = Cyan400.copy(alpha = 0.6f),
        onClick = { onSelectCategory("OSSC") },
        testTag = "folder_card_ossc"
      )
    }

    // 3. OSSSC EXAMS
    item {
      ExamFolderCard(
        badge = "DISTRICT REVENUE CADRE",
        badgeBg = Emerald400,
        badgeTextColor = Slate950,
        title = "OSSSC EXAMS",
        subCategory = "RI • ARI • AMIN • PEO • JA",
        footerLeft = "Mocks • Odia Grammar & GK",
        footerRight = "Open Folder →",
        iconEmoji = "🌾",
        gradient = listOf(Color(0xFF064E3B), Color(0xFF059669), Color(0xFF047857)),
        borderColor = Emerald400.copy(alpha = 0.6f),
        onClick = { onSelectCategory("OSSSC") },
        testTag = "folder_card_osssc"
      )
    }

    // 4. OUSSSC EXAMS
    item {
      ExamFolderCard(
        badge = "UNIVERSITY & STATE CADRE",
        badgeBg = Purple200,
        badgeTextColor = Slate950,
        title = "OUSSSC EXAMS",
        subCategory = "STATE & UNIVERSITY RECRUITMENT",
        footerLeft = "Mocks • Junior Assistant & Staff",
        footerRight = "Open Folder →",
        iconEmoji = "🏛️",
        gradient = listOf(Color(0xFF3B0764), Color(0xFF7C3AED), Color(0xFF6D28D9)),
        borderColor = Purple200.copy(alpha = 0.6f),
        onClick = { onSelectCategory("OUSSSC") },
        testTag = "folder_card_ousssc"
      )
    }

    // 5. TEACHING EXAMS
    item {
      ExamFolderCard(
        badge = "TEACHER RECRUITMENT",
        badgeBg = Rose500.copy(alpha = 0.3f),
        badgeTextColor = Color.White,
        title = "TEACHING EXAM",
        subCategory = "OTET • OSSTET • RHT • JT • OAVS",
        footerLeft = "Mocks • Pedagogy & Odia Byakarana",
        footerRight = "Open Folder →",
        iconEmoji = "🎓",
        gradient = listOf(Color(0xFF881337), Color(0xFFE11D48), Color(0xFFBE123C)),
        borderColor = Rose500.copy(alpha = 0.6f),
        onClick = { onSelectCategory("TEACHING") },
        testTag = "folder_card_teaching"
      )
    }

    // 6. LIVE CLASSROOM & WHITEBOARD
    item {
      ExamFolderCard(
        badge = "WHITEBOARD STUDIO",
        badgeBg = Amber400,
        badgeTextColor = Slate950,
        title = "LIVE CLASSROOM",
        subCategory = "LIVE LECTURES • WHITEBOARD • DOUBTS",
        footerLeft = "Hemanand Hans • 144p-1080p",
        footerRight = "Join Live →",
        iconEmoji = "🎥",
        gradient = listOf(Color(0xFF3B0764), Color(0xFF9333EA), Color(0xFF4338CA)),
        borderColor = Amber400.copy(alpha = 0.6f),
        onClick = { onOpenLiveClass() },
        testTag = "folder_card_live"
      )
    }

    // 7. AI MOCK MENTOR
    item {
      ExamFolderCard(
        badge = "AI EXAM DIAGNOSTICS",
        badgeBg = Color(0xFFA5B4FC),
        badgeTextColor = Slate950,
        title = "AI MOCK MENTOR",
        subCategory = "SCORE & NEGATIVE MARK ANALYSIS",
        footerLeft = "Ask AI: What did I do wrong?",
        footerRight = "Chat Now →",
        iconEmoji = "🤖",
        gradient = listOf(Color(0xFF1E1B4B), Color(0xFF6366F1), Color(0xFF4F46E5)),
        borderColor = Color(0xFFA5B4FC).copy(alpha = 0.6f),
        onClick = { onOpenAiMentor() },
        testTag = "folder_card_ai"
      )
    }

    // 8. WHATSAPP DOUBT BATCH
    item {
      ExamFolderCard(
        badge = "OFFICIAL COMMUNITY",
        badgeBg = Emerald400,
        badgeTextColor = Slate950,
        title = "WHATSAPP BATCH",
        subCategory = "ODISHA ASPIRANTS DOUBT GROUP",
        footerLeft = "Daily PDF Notes & Updates",
        footerRight = "Join Batch →",
        iconEmoji = "💬",
        gradient = listOf(Color(0xFF064E3B), Color(0xFF10B981), Color(0xFF059669)),
        borderColor = Emerald400.copy(alpha = 0.6f),
        onClick = {
          onShowToast("Opening H2S STUDY WhatsApp Community batch...")
          try {
            val intent = Intent(
              Intent.ACTION_VIEW,
              Uri.parse("https://api.whatsapp.com/send?text=Join%20H2S%20STUDY%20Odisha%20Govt%20Exams%20Portal")
            )
            context.startActivity(intent)
          } catch (_: Exception) {}
        },
        testTag = "folder_card_whatsapp"
      )
    }
  }
}

@Composable
fun ExamFolderCard(
  badge: String,
  badgeBg: Color,
  badgeTextColor: Color,
  title: String,
  subCategory: String,
  footerLeft: String,
  footerRight: String,
  iconEmoji: String,
  gradient: List<Color>,
  borderColor: Color,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .border(2.dp, borderColor, RoundedCornerShape(24.dp))
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(24.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.linearGradient(gradient))
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeBg)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = badge,
                color = badgeTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = title,
              color = Color.White,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(Slate950.copy(alpha = 0.6f))
                .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = subCategory,
                color = Amber400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // 3D-styled Icon Avatar
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color.White.copy(alpha = 0.15f))
              .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = iconEmoji,
              fontSize = 32.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card Footer
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.15f))
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = footerLeft,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = footerRight,
            color = Amber400,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
