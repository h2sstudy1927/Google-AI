package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassChatMessage
import com.example.data.model.WhiteboardPoint
import com.example.data.model.WhiteboardStroke
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber700
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWhiteboardScreen(
  isTeacher: Boolean,
  whiteboardStrokes: List<WhiteboardStroke>,
  activeStroke: WhiteboardStroke?,
  currentTool: String,
  currentStrokeColor: Color,
  currentStrokeWidth: Float,
  streamResolution: String,
  isStreamPaused: Boolean,
  handRaised: Boolean,
  chatMessages: List<ClassChatMessage>,
  chatFilterDoubtsOnly: Boolean,
  onStartDrawing: (WhiteboardPoint) -> Unit,
  onUpdateDrawing: (WhiteboardPoint) -> Unit,
  onFinishDrawing: () -> Unit,
  onSetTool: (String) -> Unit,
  onSetColor: (Color) -> Unit,
  onSetSize: (Float) -> Unit,
  onClearWhiteboard: () -> Unit,
  onInsertOdishaFact: () -> Unit,
  onSetStreamResolution: (String) -> Unit,
  onToggleStreamPause: () -> Unit,
  onToggleRaiseHand: () -> Unit,
  onToggleChatFilter: (Boolean) -> Unit,
  onSendChatMessage: (String, Boolean) -> Unit,
  onBack: () -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var chatInputText by remember { mutableStateOf("") }
  var isDoubtChecked by remember { mutableStateOf(false) }
  var resDropdownExpanded by remember { mutableStateOf(false) }
  val resolutions = listOf("1080p Full HD", "720p HD", "480p Standard", "360p Data Saver", "240p Low", "144p Ultra Saver")

  val listState = rememberLazyListState()
  LaunchedEffect(chatMessages.size) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(chatMessages.size - 1)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
              onClick = onBack,
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.testTag("live_back_btn")
            ) {
              Text("← Back", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "Odisha Live Classroom",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
              )
              Text(
                text = "Live Whiteboard • Low Data (144p-1080p)",
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }

          // Resolution Dropdown
          ExposedDropdownMenuBox(
            expanded = resDropdownExpanded,
            onExpandedChange = { resDropdownExpanded = !resDropdownExpanded }
          ) {
            Box(
              modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .clip(RoundedCornerShape(8.dp))
                .background(Slate800)
                .clickable { resDropdownExpanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("res_dropdown_trigger")
            ) {
              Text(
                text = streamResolution,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            ExposedDropdownMenu(
              expanded = resDropdownExpanded,
              onDismissRequest = { resDropdownExpanded = false }
            ) {
              resolutions.forEach { res ->
                DropdownMenuItem(
                  text = { Text(res, fontSize = 12.sp) },
                  onClick = {
                    onSetStreamResolution(res)
                    resDropdownExpanded = false
                  }
                )
              }
            }
          }
        }
      }
    }

    // Whiteboard Controls Card (if Teacher or Viewer Tools)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Stream Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Slate950)
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(100.dp))
                  .background(Rose600)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "● LIVE NOW",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Text(
                text = "Odisha History, Geography & Byakarana",
                color = Purple200,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
              )
            }
          }

          // Teacher Toolbar
          if (isTeacher) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  listOf("pen", "highlighter", "eraser", "line", "rect").forEach { tool ->
                    val isSelected = currentTool == tool
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Purple700 else Slate800)
                        .clickable { onSetTool(tool) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("wb_tool_$tool")
                    ) {
                      Text(
                        text = tool.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Purple800)
                      .clickable { onInsertOdishaFact() }
                      .padding(horizontal = 6.dp, vertical = 4.dp)
                  ) {
                    Text("+ Fact", color = Amber400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Rose600.copy(alpha = 0.3f))
                      .clickable { onClearWhiteboard() }
                      .padding(horizontal = 6.dp, vertical = 4.dp)
                  ) {
                    Text("Clear", color = Rose500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Color choices
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  listOf(Color.White, Amber400, Cyan400, Rose500, Emerald400).forEach { col ->
                    val isPicked = currentStrokeColor == col
                    Box(
                      modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(col)
                        .border(
                          width = if (isPicked) 2.dp else 0.dp,
                          color = Purple200,
                          shape = CircleShape
                        )
                        .clickable { onSetColor(col) }
                    )
                  }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("Size: ", color = Slate400, fontSize = 10.sp)
                  Slider(
                    value = currentStrokeWidth,
                    onValueChange = { onSetSize(it) },
                    valueRange = 2f..16f,
                    modifier = Modifier.width(90.dp),
                    colors = SliderDefaults.colors(
                      thumbColor = Purple200,
                      activeTrackColor = Purple700
                    )
                  )
                }
              }
            }
          }

          // Interactive Touch Whiteboard Canvas
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(16f / 10f)
              .background(Slate950)
              .pointerInput(Unit) {
                detectDragGestures(
                  onDragStart = { offset ->
                    onStartDrawing(WhiteboardPoint(offset.x, offset.y))
                  },
                  onDrag = { change, _ ->
                    change.consume()
                    onUpdateDrawing(WhiteboardPoint(change.position.x, change.position.y))
                  },
                  onDragEnd = {
                    onFinishDrawing()
                  },
                  onDragCancel = {
                    onFinishDrawing()
                  }
                )
              }
              .testTag("whiteboard_canvas")
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              // Draw persistent background guide lines and default Odisha notes
              drawLine(
                color = Color(0xFF1E293B),
                start = Offset(0f, 40f),
                end = Offset(size.width, 40f),
                strokeWidth = 1f
              )

              // Draw completed strokes
              whiteboardStrokes.forEach { stroke ->
                drawCustomStroke(stroke)
              }

              // Draw active stroke currently in progress
              activeStroke?.let { stroke ->
                drawCustomStroke(stroke)
              }
            }

            // Fixed Default Odia History Teaching Notes (Overlay)
            Column(
              modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
            ) {
              Text(
                text = "H2S STUDY • Odisha History & Heritage Masterclass",
                color = Amber400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Faculty: Hemanand Hans | Target: OPSC, OSSC, OSSSC",
                color = Cyan400,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text("• 261 BC : Historic Kalinga War on Daya river", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("• 1st Cent BC : Kharavela (Hathigumpha inscription, Udayagiri)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("• 1817 AD : Paika Rebellion (Bakshi Jagabandhu)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
              Text("• 1 April 1936 : Formation of Odisha Province (Utkal Divas)", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            }

            // Floating Faculty PIP Badge
            Box(
              modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(12.dp))
                .background(Slate950.copy(alpha = 0.9f))
                .border(1.5.dp, Amber400, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(Emerald400)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Hemanand Hans", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Text("👨‍🏫 OPSC/OSSC Faculty", color = Amber400, fontSize = 8.sp)
                Text("HD Mic On 🎙️", color = Slate400, fontSize = 7.sp)
              }
            }

            Text(
              text = "H2S STUDY • ODISHA GOVT EXAMS",
              color = Color(0xFF334155),
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
            )
          }

          // Stream Controls Footer
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Slate950)
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = if (isStreamPaused) "▶ Resume" else "⏸ Pause",
                color = Purple200,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .clickable { onToggleStreamPause() }
                  .testTag("stream_pause_toggle")
              )
              Text("•", color = Slate600)
              Text("👥 2,410 Aspirants", color = Color.White, fontSize = 11.sp)
              Text("•", color = Slate600)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Emerald700.copy(alpha = 0.2f))
                  .border(1.dp, Emerald700, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(streamResolution, color = Emerald400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (handRaised) Amber400 else Purple800)
                .clickable { onToggleRaiseHand() }
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .testTag("raise_hand_btn")
            ) {
              Text(
                text = if (handRaised) "✋ Hand Raised" else "✋ Raise Hand",
                color = if (handRaised) Slate950 else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Lecture Handout Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Special Lecture Handout: Modern Odisha History",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
            Text(
              text = "Summary notes of Paika Rebellion & Utkal Divas (PDF)",
              fontSize = 11.sp,
              color = Slate600
            )
          }

          Button(
            onClick = { onShowToast("Downloading Odisha History Handout (PDF)...") },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple700),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("download_handout_btn")
          ) {
            Text("📄 PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Live Doubts & Discussion Chat Container
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Chat Header & Tabs
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Live Class Q&A & Doubts",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
              )
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Emerald400)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F5F9))
                .padding(3.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (!chatFilterDoubtsOnly) Color.White else Color.Transparent)
                  .clickable { onToggleChatFilter(false) }
                  .padding(vertical = 6.dp)
                  .testTag("chat_tab_all"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "All Discussion",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (!chatFilterDoubtsOnly) Purple900 else Slate600
                )
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (chatFilterDoubtsOnly) Color.White else Color.Transparent)
                  .clickable { onToggleChatFilter(true) }
                  .padding(vertical = 6.dp)
                  .testTag("chat_tab_doubts"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Doubts Only ❓",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (chatFilterDoubtsOnly) Purple900 else Slate600
                )
              }
            }
          }

          // Messages List
          val filteredMessages = if (chatFilterDoubtsOnly) chatMessages.filter { it.isDoubt } else chatMessages
          LazyColumn(
            state = listState,
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(filteredMessages, key = { it.id }) { msg ->
              val isFaculty = msg.senderRole == "faculty"
              val isDoubt = msg.isDoubt

              val bubbleBg = when {
                isFaculty -> Color(0xFFFFFBEB)
                isDoubt -> Color(0xFFFEF3C7)
                msg.senderRole == "moderator" -> Color(0xFFF1F5F9)
                else -> Purple900
              }
              val textColor = if (!isFaculty && !isDoubt && msg.senderRole != "moderator") Color.White else Slate900

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(bubbleBg)
                  .border(
                    1.dp,
                    if (isFaculty || isDoubt) Amber400.copy(alpha = 0.5f) else Slate200,
                    RoundedCornerShape(12.dp)
                  )
                  .padding(10.dp)
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "${msg.senderName} ${if (isDoubt) "❓ (Live Doubt)" else ""}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isFaculty) Amber700 else if (textColor == Color.White) Purple200 else Purple800
                    )
                    Text(
                      text = msg.timeFormatted,
                      fontSize = 9.sp,
                      color = Slate400
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = msg.message,
                    fontSize = 12.sp,
                    color = textColor,
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }

          // Chat Input Form
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC))
              .padding(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(bottom = 6.dp)
            ) {
              Checkbox(
                checked = isDoubtChecked,
                onCheckedChange = { isDoubtChecked = it },
                colors = CheckboxDefaults.colors(checkedColor = Purple700),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Mark as Important Doubt",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = chatInputText,
                onValueChange = { chatInputText = it },
                placeholder = { Text("Ask Prof. Mohapatra a question...", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("chat_input_text"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Purple700,
                  unfocusedContainerColor = Color.White,
                  focusedContainerColor = Color.White
                )
              )

              Button(
                onClick = {
                  val text = chatInputText.trim()
                  if (text.isNotBlank()) {
                    onSendChatMessage(text, isDoubtChecked)
                    chatInputText = ""
                    isDoubtChecked = false
                  }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                modifier = Modifier
                  .height(44.dp)
                  .testTag("chat_send_btn")
              ) {
                Text("Send", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCustomStroke(stroke: WhiteboardStroke) {
  if (stroke.points.size < 2) return

  when (stroke.shapeType) {
    "line" -> {
      val first = stroke.points.first()
      val last = stroke.points.last()
      drawLine(
        color = stroke.color,
        start = Offset(first.x, first.y),
        end = Offset(last.x, last.y),
        strokeWidth = stroke.strokeWidth,
        cap = StrokeCap.Round
      )
    }
    "rect" -> {
      val first = stroke.points.first()
      val last = stroke.points.last()
      val left = minOf(first.x, last.x)
      val top = minOf(first.y, last.y)
      val right = maxOf(first.x, last.x)
      val bottom = maxOf(first.y, last.y)
      drawRect(
        color = stroke.color,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        style = Stroke(width = stroke.strokeWidth)
      )
    }
    else -> {
      val path = Path()
      val first = stroke.points.first()
      path.moveTo(first.x, first.y)
      for (i in 1 until stroke.points.size) {
        val pt = stroke.points[i]
        path.lineTo(pt.x, pt.y)
      }
      drawPath(
        path = path,
        color = if (stroke.isHighlighter) stroke.color.copy(alpha = 0.35f) else stroke.color,
        style = Stroke(
          width = stroke.strokeWidth,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }
  }
}
