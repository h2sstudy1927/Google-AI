package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExamCategoryEntity
import com.example.data.model.ExamAttemptResult
import com.example.data.model.ExamTest
import com.example.data.model.UserSession
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple50
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

/**
 * Exam Dashboard Screen displaying categorized folders for OPSC, OSSC, OSSSC, OUSSSC,
 * TEACHING, and other exams, dynamically populated from Room database entities.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamDashboardScreen(
  categories: List<ExamCategoryEntity>,
  tests: List<ExamTest>,
  currentUser: UserSession?,
  latestAttempt: ExamAttemptResult?,
  totalAttempted: Int,
  averageScore: Double?,
  onSelectCategory: (String) -> Unit,
  onAttemptTest: (ExamTest) -> Unit,
  onOpenLiveClass: () -> Unit,
  onOpenAiMentor: () -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier,
  onReviewAttempt: (() -> Unit)? = null,
  onOpenCommunity: (() -> Unit)? = null
) {
  val context = LocalContext.current

  var searchQuery by rememberSaveable { mutableStateOf("") }
  var selectedChip by rememberSaveable { mutableStateOf("ALL") }
  var expandedFolderId by rememberSaveable { mutableStateOf<String?>(null) }

  // Map tests by category for instant lookup and accurate Room-backed counts
  val testsByCategory by remember(tests) {
    derivedStateOf {
      tests.groupBy { it.category.uppercase() }
    }
  }

  // Filter categories based on search input and board chip
  val filteredCategories by remember(categories, searchQuery, selectedChip, tests) {
    derivedStateOf {
      categories.filter { category ->
        val matchesChip = when (selectedChip) {
          "ALL" -> true
          else -> category.id.equals(selectedChip, ignoreCase = true)
        }
        val q = searchQuery.trim().lowercase()
        val matchesSearch = q.isEmpty() ||
          category.title.lowercase().contains(q) ||
          category.subtitle.lowercase().contains(q) ||
          category.subCategoryTag.lowercase().contains(q) ||
          category.topics.lowercase().contains(q) ||
          category.id.lowercase().contains(q) ||
          // Also match any test title inside this category
          (testsByCategory[category.id.uppercase()] ?: emptyList()).any {
            it.title.lowercase().contains(q) || it.subject.lowercase().contains(q)
          }

        matchesChip && matchesSearch
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("exam_dashboard_scroll"),
    contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Candidate Greeting & Odisha Commission Header
    item(key = "header_summary") {
      DashboardGreetingCard(
        currentUser = currentUser,
        totalCategories = categories.size,
        totalTests = tests.size,
        totalAttempted = totalAttempted,
        averageScore = averageScore
      )
    }

    // 2. Recent Performance Card (if candidate has attempted a test)
    if (latestAttempt != null) {
      item(key = "recent_attempt_card") {
        RecentAttemptSummaryCard(
          attempt = latestAttempt,
          onReview = onReviewAttempt ?: onOpenAiMentor
        )
      }
    }

    // 3. Search Bar
    item(key = "search_bar") {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dashboard_search_input"),
        placeholder = {
          Text(
            text = "Search OPSC, OSSC, Odia grammar, tests...",
            fontSize = 13.sp,
            color = Slate400
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Purple700
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { searchQuery = "" },
              modifier = Modifier.testTag("dashboard_search_clear")
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = Slate500
              )
            }
          }
        },
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Purple700,
          unfocusedBorderColor = Slate200,
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        )
      )
    }

    // 4. Quick Filter Chips Row (OPSC, OSSC, OSSSC, Teaching, All)
    item(key = "category_chips") {
      val chips = listOf(
        "ALL" to "All Boards (${categories.size})",
        "OPSC" to "OPSC OAS/ASO",
        "OSSC" to "OSSC CGL",
        "OSSSC" to "OSSSC RI/Amin",
        "OUSSSC" to "OUSSSC Cadre",
        "TEACHING" to "Teaching/OTET"
      )
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp),
        modifier = Modifier.testTag("category_filter_chips_row")
      ) {
        items(chips, key = { it.first }) { (id, label) ->
          val isSelected = selectedChip == id
          FilterChip(
            selected = isSelected,
            onClick = { selectedChip = id },
            label = {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Purple900,
              selectedLabelColor = Color.White,
              containerColor = Color.White,
              labelColor = Slate700
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) Purple900 else Slate200,
              borderWidth = 1.dp
            ),
            modifier = Modifier.testTag("filter_chip_$id")
          )
        }
      }
    }

    // 5. Section Title & Live Count
    item(key = "section_title") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Official Exam Folders",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = Slate900
          )
          Text(
            text = "Sourced directly from offline Room storage with +1.00 / -0.25 grading",
            fontSize = 11.sp,
            color = Slate500
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Purple50)
            .border(1.dp, Purple200, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${filteredCategories.size} Folders",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Purple900
          )
        }
      }
    }

    // 6. Dynamic Categorized Folder Cards from Room Database
    if (filteredCategories.isEmpty()) {
      item(key = "empty_state") {
        EmptyFolderState(
          searchQuery = searchQuery,
          onReset = {
            searchQuery = ""
            selectedChip = "ALL"
          }
        )
      }
    } else {
      items(filteredCategories, key = { it.id }) { category ->
        val categoryTests = testsByCategory[category.id.uppercase()] ?: emptyList()
        val isExpanded = expandedFolderId == category.id

        DashboardFolderCard(
          category = category,
          tests = categoryTests,
          isExpanded = isExpanded,
          onToggleExpand = {
            expandedFolderId = if (isExpanded) null else category.id
          },
          onOpenFolder = { onSelectCategory(category.id) },
          onAttemptTest = onAttemptTest
        )
      }
    }

    // 7. Quick Prep Tools (Odisha Aspirants Hub)
    item(key = "quick_tools_section") {
      Text(
        text = "Odisha Aspirant Study Tools",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Slate900,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
      )
    }

    item(key = "tool_whiteboard") {
      StudyToolBannerCard(
        title = "Live Whiteboard Classroom",
        subtitle = "Canvas-based problem solving with pen, highlighter & math shapes",
        badge = "INTERACTIVE STUDIO",
        iconEmoji = "🎥",
        badgeColor = Amber400,
        gradient = listOf(Color(0xFF3B0764), Color(0xFF9333EA)),
        buttonText = "Open Whiteboard →",
        onClick = onOpenLiveClass,
        testTag = "dashboard_tool_whiteboard"
      )
    }

    item(key = "tool_ai_mentor") {
      StudyToolBannerCard(
        title = "AI Exam Coach & Diagnostic",
        subtitle = "Gemini 3.8 Flash analysis on negative marking penalties and weak topics",
        badge = "GEMINI POWERED",
        iconEmoji = "🤖",
        badgeColor = Color(0xFFA5B4FC),
        gradient = listOf(Color(0xFF1E1B4B), Color(0xFF4F46E5)),
        buttonText = "Chat with AI Mentor →",
        onClick = onOpenAiMentor,
        testTag = "dashboard_tool_ai"
      )
    }

    item(key = "tool_whatsapp") {
      StudyToolBannerCard(
        title = "Official WhatsApp Aspirant Batch",
        subtitle = "Daily syllabus updates, PDF hand-outs, and peer discussion",
        badge = "COMMUNITY",
        iconEmoji = "💬",
        badgeColor = Emerald400,
        gradient = listOf(Color(0xFF064E3B), Color(0xFF059669)),
        buttonText = "Join Community Batch →",
        onClick = {
          if (onOpenCommunity != null) {
            onOpenCommunity()
          } else {
            try {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("WhatsApp Group", "https://chat.whatsapp.com/H2SStudyOdishaGovt2026")
              clipboard.setPrimaryClip(clip)
              onShowToast("✓ WhatsApp Community link copied to clipboard!")
            } catch (_: Exception) {
              onShowToast("H2S WhatsApp: https://chat.whatsapp.com/H2SStudyOdishaGovt2026")
            }
          }
        },
        testTag = "dashboard_tool_whatsapp"
      )
    }
  }
}

/**
 * Top Header Card displaying candidate greeting, district badge, and Room DB test statistics.
 */
@Composable
fun DashboardGreetingCard(
  currentUser: UserSession?,
  totalCategories: Int,
  totalTests: Int,
  totalAttempted: Int,
  averageScore: Double?,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .border(1.dp, Purple200.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Namaskar, ${currentUser?.name ?: "Aspirant"} 🙏",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = Slate900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "District: ${currentUser?.district ?: "Odisha"} • ${currentUser?.role?.replaceFirstChar { it.uppercase() } ?: "Student"}",
            fontSize = 12.sp,
            color = Slate500
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFEF3C7))
            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "Odisha 2026-27",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF92400E)
          )
        }
      }

      // Quick Stat Pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatBadge(
          title = "Folders",
          value = "$totalCategories",
          emoji = "📁",
          modifier = Modifier.weight(1f)
        )
        StatBadge(
          title = "Room Mocks",
          value = "$totalTests",
          emoji = "📝",
          modifier = Modifier.weight(1f)
        )
        StatBadge(
          title = "Attempted",
          value = "$totalAttempted",
          emoji = "🎯",
          modifier = Modifier.weight(1f)
        )
        StatBadge(
          title = "Avg Score",
          value = averageScore?.let { String.format("%.1f", it) } ?: "—",
          emoji = "⚡",
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun StatBadge(
  title: String,
  value: String,
  emoji: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Slate100)
      .padding(vertical = 8.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = value,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = Purple950
        )
      }
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = Slate600
      )
    }
  }
}

/**
 * Summary card shown if candidate has attempted an exam.
 */
@Composable
fun RecentAttemptSummaryCard(
  attempt: ExamAttemptResult,
  onReview: () -> Unit,
  modifier: Modifier = Modifier
) {
  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(18.dp)),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF0FDF4))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(Emerald700)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LATEST ATTEMPT RESULT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Emerald700
          )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = attempt.testTitle,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "Net Score: ${String.format(java.util.Locale.US, "%.2f", attempt.netScore)} • Accuracy: ${attempt.accuracyPercent}% (Correct: ${attempt.correctCount}, Wrong: ${attempt.wrongCount})",
          fontSize = 11.sp,
          color = Slate600
        )
      }

      Button(
        onClick = onReview,
        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("dashboard_review_attempt_btn")
      ) {
        Text("Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Main Categorized Exam Folder Card populated from Room database entities.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardFolderCard(
  category: ExamCategoryEntity,
  tests: List<ExamTest>,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onOpenFolder: () -> Unit,
  onAttemptTest: (ExamTest) -> Unit,
  modifier: Modifier = Modifier
) {
  // Theme palette based on Category ID
  val (gradient, badgeColor, borderColor) = when (category.id.uppercase()) {
    "OPSC" -> Triple(
      listOf(Color(0xFF1E1B4B), Color(0xFF1E3A8A), Color(0xFF312E81)),
      Amber400,
      Amber400.copy(alpha = 0.6f)
    )
    "OSSC" -> Triple(
      listOf(Color(0xFF082F49), Color(0xFF0284C7), Color(0xFF1E3A8A)),
      Cyan400,
      Cyan400.copy(alpha = 0.6f)
    )
    "OSSSC" -> Triple(
      listOf(Color(0xFF064E3B), Color(0xFF059669), Color(0xFF047857)),
      Emerald400,
      Emerald400.copy(alpha = 0.6f)
    )
    "OUSSSC" -> Triple(
      listOf(Color(0xFF3B0764), Color(0xFF7C3AED), Color(0xFF6D28D9)),
      Purple200,
      Purple200.copy(alpha = 0.6f)
    )
    "TEACHING" -> Triple(
      listOf(Color(0xFF881337), Color(0xFFE11D48), Color(0xFFBE123C)),
      Rose500.copy(alpha = 0.4f),
      Rose500.copy(alpha = 0.6f)
    )
    else -> Triple(
      listOf(Color(0xFF0F172A), Color(0xFF334155), Color(0xFF1E293B)),
      Amber400,
      Slate400
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .border(2.dp, borderColor, RoundedCornerShape(22.dp))
      .animateContentSize(animationSpec = spring())
      .testTag("dashboard_folder_${category.id.lowercase()}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.linearGradient(gradient))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Top Badges Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(badgeColor)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = category.badge.ifEmpty { "ODISHA CADRE" },
              color = Slate950,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black
            )
          }

          // Live count badge from Room DB
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color.White.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "📝 ${tests.size} Mocks in Room",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title and Emoji
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = category.iconEmoji.ifEmpty { "📁" },
            fontSize = 32.sp
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = category.title,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Text(
              text = category.subCategoryTag.ifEmpty { category.subtitle },
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White.copy(alpha = 0.8f)
            )
          }
        }

        // Syllabus Topic Tags
        if (category.topics.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            category.topics.split(",").take(4).forEach { topic ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.Black.copy(alpha = 0.25f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "• ${topic.trim()}",
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Divider
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card Action Controls (Open Folder and Toggle Preview)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Preview Accordion Toggle
          Row(
            modifier = Modifier
              .clickable { onToggleExpand() }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isExpanded) "Hide Tests" else "Preview ${tests.size} Tests",
              color = Color.White.copy(alpha = 0.9f),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          // Open Full Folder Button
          Button(
            onClick = onOpenFolder,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("dashboard_open_folder_${category.id.lowercase()}")
          ) {
            Text(
              text = "Open Folder →",
              color = Slate950,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        // Expandable Quick Test List Preview
        AnimatedVisibility(
          visible = isExpanded,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (tests.isEmpty()) {
              Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "No tests currently loaded in Room for ${category.title}.",
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 12.sp,
                  modifier = Modifier.padding(12.dp),
                  textAlign = TextAlign.Center
                )
              }
            } else {
              tests.forEach { test ->
                FolderPreviewTestItem(
                  test = test,
                  onAttempt = { onAttemptTest(test) }
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Compact test item inside the expandable folder preview.
 */
@Composable
fun FolderPreviewTestItem(
  test: ExamTest,
  onAttempt: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = test.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "${test.subject} • ${test.questions.size} Questions • ⏱️ ${test.durationMinutes}m (+1.00 / -0.25)",
          fontSize = 11.sp,
          color = Slate600
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onAttempt,
        colors = ButtonDefaults.buttonColors(containerColor = Purple800),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("preview_start_mock_${test.id}")
      ) {
        Text("Start", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Empty search or filter state.
 */
@Composable
fun EmptyFolderState(
  searchQuery: String,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text("🔍", fontSize = 36.sp)
      Text(
        text = "No matching exam folders found",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Slate900
      )
      Text(
        text = "No categories or tests match \"$searchQuery\". Try checking the spelling or reset filters.",
        fontSize = 12.sp,
        color = Slate500,
        textAlign = TextAlign.Center
      )
      OutlinedButton(
        onClick = onReset,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("dashboard_reset_filter_btn")
      ) {
        Text("Reset Search & Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Reusable banner card for aspirant study tools (Whiteboard, AI, WhatsApp).
 */
@Composable
fun StudyToolBannerCard(
  title: String,
  subtitle: String,
  badge: String,
  iconEmoji: String,
  badgeColor: Color,
  gradient: List<Color>,
  buttonText: String,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.linearGradient(gradient))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = iconEmoji, fontSize = 30.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeColor)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = Slate950
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = subtitle,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "→",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
