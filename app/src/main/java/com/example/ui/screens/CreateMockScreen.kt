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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber700
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.DraftQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMockScreen(
  draftTitle: String,
  draftCategory: String,
  draftQuestions: List<DraftQuestion>,
  onUpdateTitle: (String) -> Unit,
  onUpdateCategory: (String) -> Unit,
  onUpdateQuestion: (Int, DraftQuestion) -> Unit,
  onAddQuestion: () -> Unit,
  onRemoveQuestion: (Int) -> Unit,
  onPublish: () -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier
) {
  var catDropdownExpanded by remember { mutableStateOf(false) }
  val categories = listOf("OPSC", "OSSC", "OSSSC", "OUSSSC", "TEACHING")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
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
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFFEF3C7))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "TEACHER & DEVELOPER AUTHORING STUDIO",
              color = Amber700,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "Add Mock Test by Yourself",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Slate900
          )

          Text(
            text = "Create customized mock tests for OPSC, OSSC, OSSSC, OUSSSC, or Teaching aspirants (+1.00 / -0.25).",
            fontSize = 12.sp,
            color = Slate600
          )
        }
      }
    }

    // Metadata Fields (Title & Category)
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
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedTextField(
            value = draftTitle,
            onValueChange = onUpdateTitle,
            label = { Text("Test Title") },
            placeholder = { Text("e.g. OSSSC RI/Amin Special Odia Byakarana Test 02") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("create_mock_title_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Purple700,
              focusedLabelColor = Purple700
            )
          )

          ExposedDropdownMenuBox(
            expanded = catDropdownExpanded,
            onExpandedChange = { catDropdownExpanded = !catDropdownExpanded }
          ) {
            OutlinedTextField(
              value = draftCategory,
              onValueChange = {},
              readOnly = true,
              label = { Text("Folder Category") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catDropdownExpanded) },
              modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .testTag("create_mock_category_select"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                focusedLabelColor = Purple700
              )
            )

            ExposedDropdownMenu(
              expanded = catDropdownExpanded,
              onDismissRequest = { catDropdownExpanded = false }
            ) {
              categories.forEach { cat ->
                DropdownMenuItem(
                  text = { Text(cat) },
                  onClick = {
                    onUpdateCategory(cat)
                    catDropdownExpanded = false
                  }
                )
              }
            }
          }
        }
      }
    }

    // Questions Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Questions List (${draftQuestions.size})",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )

        Button(
          onClick = onAddQuestion,
          colors = ButtonDefaults.buttonColors(containerColor = Purple700),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_question_row_btn")
        ) {
          Text("+ Add Question", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // List of Question Forms
    itemsIndexed(draftQuestions) { index, q ->
      QuestionDraftCard(
        index = index,
        question = q,
        canRemove = draftQuestions.size > 1,
        onUpdate = { updated -> onUpdateQuestion(index, updated) },
        onRemove = { onRemoveQuestion(index) }
      )
    }

    // Final Action Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onCancel,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
        ) {
          Text("Cancel", fontSize = 13.sp, color = Slate600)
        }

        Button(
          onClick = onPublish,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Purple700),
          modifier = Modifier
            .weight(2f)
            .height(48.dp)
            .testTag("publish_mock_btn")
        ) {
          Text("Publish to Exam Folder", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDraftCard(
  index: Int,
  question: DraftQuestion,
  canRemove: Boolean,
  onUpdate: (DraftQuestion) -> Unit,
  onRemove: () -> Unit
) {
  var correctOptExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Question ${index + 1}",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Purple900
        )

        if (canRemove) {
          Text(
            text = "Remove",
            color = Rose600,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onRemove() }
          )
        }
      }

      OutlinedTextField(
        value = question.statement,
        onValueChange = { onUpdate(question.copy(statement = it)) },
        label = { Text("Question Statement") },
        placeholder = { Text("e.g. Odia Grammar or Odisha GK...") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = question.optA,
          onValueChange = { onUpdate(question.copy(optA = it)) },
          label = { Text("Option A") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = question.optB,
          onValueChange = { onUpdate(question.copy(optB = it)) },
          label = { Text("Option B") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = question.optC,
          onValueChange = { onUpdate(question.copy(optC = it)) },
          label = { Text("Option C") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = question.optD,
          onValueChange = { onUpdate(question.copy(optD = it)) },
          label = { Text("Option D") },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        ExposedDropdownMenuBox(
          expanded = correctOptExpanded,
          onExpandedChange = { correctOptExpanded = !correctOptExpanded },
          modifier = Modifier.weight(1f)
        ) {
          OutlinedTextField(
            value = "Option ${('A'.code + question.correctIndex).toChar()}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Correct Option") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = correctOptExpanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(10.dp)
          )
          ExposedDropdownMenu(
            expanded = correctOptExpanded,
            onDismissRequest = { correctOptExpanded = false }
          ) {
            listOf("Option A", "Option B", "Option C", "Option D").forEachIndexed { optIdx, label ->
              DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                  onUpdate(question.copy(correctIndex = optIdx))
                  correctOptExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = question.explanation,
          onValueChange = { onUpdate(question.copy(explanation = it)) },
          label = { Text("Concept Explanation") },
          modifier = Modifier.weight(2f),
          shape = RoundedCornerShape(10.dp)
        )
      }
    }
  }
}
