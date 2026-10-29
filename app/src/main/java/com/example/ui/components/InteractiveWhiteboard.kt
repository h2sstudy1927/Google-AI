package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple950
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

enum class BoardTool(val label: String, val iconText: String) {
  PEN("Pen", "🖊️"),
  HIGHLIGHTER("Highlighter", "🖍️"),
  ERASER("Eraser", "🧹"),
  LINE("Line", "📏"),
  RECTANGLE("Box", "🔲"),
  CIRCLE("Circle", "⭕")
}

enum class GridType(val label: String) {
  BLANK("Blank"),
  GRAPH("Graph Grid"),
  DOTS("Dot Matrix")
}

data class DrawnStroke(
  val points: List<Offset>,
  val color: Color,
  val strokeWidth: Float,
  val tool: BoardTool
)

/**
 * High-performance, Canvas-based interactive Whiteboard component.
 * Supports smooth path drawing, adjustable stroke thickness with live preview,
 * multi-color swatches, problem-solving grids (Graph/Dots), undo/redo stacks, and quick formula aids.
 */
@Composable
fun InteractiveWhiteboard(
  modifier: Modifier = Modifier,
  initialColor: Color = Color.White,
  initialStrokeWidth: Float = 4f,
  aspectRatio: Float = 16f / 10f,
  title: String = "Interactive Problem Solving Whiteboard",
  subtitle: String = "Solve arithmetic, reasoning & diagram problems in real time",
  onClear: () -> Unit = {},
  onInsertFormula: (String) -> Unit = {}
) {
  var currentTool by remember { mutableStateOf(BoardTool.PEN) }
  var currentColor by remember { mutableStateOf(initialColor) }
  var currentStrokeWidth by remember { mutableFloatStateOf(initialStrokeWidth) }
  var currentGrid by remember { mutableStateOf(GridType.GRAPH) }

  val strokes = remember { mutableStateListOf<DrawnStroke>() }
  val redoStack = remember { mutableStateListOf<DrawnStroke>() }
  var activePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

  val paletteColors = remember {
    listOf(
      Color.White,
      Color(0xFFFBBF24), // Amber Gold
      Color(0xFF22D3EE), // Cyan
      Color(0xFF34D399), // Emerald
      Color(0xFFF43F5E), // Coral Rose
      Color(0xFFA855F7), // Neon Purple
      Color(0xFFFB923C)  // Vibrant Orange
    )
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Slate900),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {

      // --- 1. TITLE & UTILITY ACTIONS BAR ---
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Slate950)
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = subtitle,
            color = Purple200,
            fontSize = 10.sp
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Undo Button
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (strokes.isNotEmpty()) Slate800 else Slate900)
              .clickable(enabled = strokes.isNotEmpty()) {
                val removed = strokes.removeLastOrNull()
                if (removed != null) redoStack.add(removed)
              }
              .testTag("wb_undo_btn"),
            contentAlignment = Alignment.Center
          ) {
            Text("↩️", fontSize = 14.sp)
          }

          // Redo Button
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (redoStack.isNotEmpty()) Slate800 else Slate900)
              .clickable(enabled = redoStack.isNotEmpty()) {
                val restored = redoStack.removeLastOrNull()
                if (restored != null) strokes.add(restored)
              }
              .testTag("wb_redo_btn"),
            contentAlignment = Alignment.Center
          ) {
            Text("↪️", fontSize = 14.sp)
          }

          // Clear Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Rose500.copy(alpha = 0.2f))
              .border(1.dp, Rose500.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .clickable {
                strokes.clear()
                redoStack.clear()
                activePoints = emptyList()
                onClear()
              }
              .padding(horizontal = 10.dp, vertical = 8.dp)
              .testTag("wb_clear_btn"),
            contentAlignment = Alignment.Center
          ) {
            Text("Clear", color = Rose500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // --- 2. TOOLBAR: TOOLS, SHAPES, COLORS & THICKNESS ---
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E293B))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Tools Selection Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            BoardTool.entries.forEach { tool ->
              val isSelected = currentTool == tool
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) Purple700 else Slate800)
                  .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = if (isSelected) Purple200 else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable { currentTool = tool }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
                  .testTag("tool_${tool.name.lowercase()}"),
                contentAlignment = Alignment.Center
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(tool.iconText, fontSize = 11.sp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = tool.label,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                }
              }
            }
          }

          // Grid Background Toggle
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Slate800)
              .clickable {
                currentGrid = when (currentGrid) {
                  GridType.BLANK -> GridType.GRAPH
                  GridType.GRAPH -> GridType.DOTS
                  GridType.DOTS -> GridType.BLANK
                }
              }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("grid_toggle_btn"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "📐 ${currentGrid.label}",
              color = Cyan400,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Color Swatches & Stroke Width Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Color Swatches
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            paletteColors.forEach { col ->
              val isSelected = currentColor == col && currentTool != BoardTool.ERASER
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(col)
                  .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) Color.White else Slate600,
                    shape = CircleShape
                  )
                  .clickable {
                    currentColor = col
                    if (currentTool == BoardTool.ERASER) currentTool = BoardTool.PEN
                  }
                  .testTag("color_swatch_${col.value}")
              )
            }
          }

          // Thickness Controls with Live Circle Preview
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("Width:", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)

            // Live stroke preview
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Slate950),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(currentStrokeWidth.dp.coerceIn(2.dp, 18.dp))
                  .clip(CircleShape)
                  .background(if (currentTool == BoardTool.ERASER) Slate400 else currentColor)
              )
            }

            Slider(
              value = currentStrokeWidth,
              onValueChange = { currentStrokeWidth = it },
              valueRange = 2f..24f,
              modifier = Modifier
                .width(100.dp)
                .testTag("stroke_width_slider"),
              colors = SliderDefaults.colors(
                thumbColor = Purple200,
                activeTrackColor = Purple700,
                inactiveTrackColor = Slate800
              )
            )

            Text(
              text = "${currentStrokeWidth.toInt()}px",
              color = Color.White,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // --- 3. MAIN TOUCH CANVAS ---
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(aspectRatio)
          .background(Slate950)
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = { offset ->
                activePoints = listOf(offset)
                redoStack.clear()
              },
              onDrag = { change, _ ->
                change.consume()
                activePoints = activePoints + change.position
              },
              onDragEnd = {
                if (activePoints.isNotEmpty()) {
                  val finalStroke = DrawnStroke(
                    points = activePoints,
                    color = if (currentTool == BoardTool.ERASER) Slate950 else currentColor,
                    strokeWidth = if (currentTool == BoardTool.ERASER) currentStrokeWidth * 4f
                                  else if (currentTool == BoardTool.HIGHLIGHTER) currentStrokeWidth * 3f
                                  else currentStrokeWidth,
                    tool = currentTool
                  )
                  strokes.add(finalStroke)
                  activePoints = emptyList()
                }
              },
              onDragCancel = {
                activePoints = emptyList()
              }
            )
          }
          .testTag("interactive_canvas")
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          // A. Draw Problem Solving Grid
          drawProblemGrid(currentGrid)

          // B. Draw Committed Strokes
          strokes.forEach { stroke ->
            renderStroke(stroke)
          }

          // C. Draw Current Active Stroke in progress
          if (activePoints.isNotEmpty()) {
            val previewStroke = DrawnStroke(
              points = activePoints,
              color = if (currentTool == BoardTool.ERASER) Slate950 else currentColor,
              strokeWidth = if (currentTool == BoardTool.ERASER) currentStrokeWidth * 4f
                            else if (currentTool == BoardTool.HIGHLIGHTER) currentStrokeWidth * 3f
                            else currentStrokeWidth,
              tool = currentTool
            )
            renderStroke(previewStroke)
          }
        }

        // D. Canvas Watermark & Helper Overlay
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Slate900.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "H2S STUDY • PROBLEM SOLVING SCRATCHPAD",
            color = Slate400,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // --- 4. PROBLEM SOLVING SHORTCUT FORMULA BAR ---
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Slate950)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Quick Formulas:",
          color = Amber400,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(
            "SI = (P×R×T)/100",
            "Speed = Dist/Time",
            "Area = πr²",
            "(a+b)² = a²+2ab+b²"
          ).forEach { formula ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Slate800)
                .clickable {
                  onInsertFormula(formula)
                }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Text(formula, color = Purple200, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }
    }
  }
}

/**
 * Draws coordinate graph lines, dot matrix, or blank surface according to the selected GridType.
 */
private fun DrawScope.drawProblemGrid(grid: GridType) {
  val spacing = 28f
  when (grid) {
    GridType.GRAPH -> {
      var x = spacing
      while (x < size.width) {
        drawLine(
          color = Color(0xFF1E293B).copy(alpha = 0.5f),
          start = Offset(x, 0f),
          end = Offset(x, size.height),
          strokeWidth = 1f
        )
        x += spacing
      }

      var y = spacing
      while (y < size.height) {
        drawLine(
          color = Color(0xFF1E293B).copy(alpha = 0.5f),
          start = Offset(0f, y),
          end = Offset(size.width, y),
          strokeWidth = 1f
        )
        y += spacing
      }
    }
    GridType.DOTS -> {
      var x = spacing
      while (x < size.width) {
        var y = spacing
        while (y < size.height) {
          drawCircle(
            color = Color(0xFF334155).copy(alpha = 0.6f),
            radius = 1.5f,
            center = Offset(x, y)
          )
          y += spacing
        }
        x += spacing
      }
    }
    GridType.BLANK -> {}
  }
}

/**
 * Renders individual strokes (Freehand, Straight Line, Box, Circle, Highlighter, or Eraser) on the Canvas.
 */
private fun DrawScope.renderStroke(stroke: DrawnStroke) {
  if (stroke.points.size < 2) {
    stroke.points.firstOrNull()?.let { pt ->
      drawCircle(
        color = stroke.color,
        radius = stroke.strokeWidth / 2f,
        center = pt
      )
    }
    return
  }

  val first = stroke.points.first()
  val last = stroke.points.last()

  when (stroke.tool) {
    BoardTool.LINE -> {
      drawLine(
        color = stroke.color,
        start = first,
        end = last,
        strokeWidth = stroke.strokeWidth,
        cap = StrokeCap.Round
      )
    }
    BoardTool.RECTANGLE -> {
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
    BoardTool.CIRCLE -> {
      val radius = (last - first).getDistance() / 2f
      val center = Offset((first.x + last.x) / 2f, (first.y + last.y) / 2f)
      drawCircle(
        color = stroke.color,
        radius = radius,
        center = center,
        style = Stroke(width = stroke.strokeWidth)
      )
    }
    BoardTool.HIGHLIGHTER -> {
      val path = Path().apply {
        moveTo(first.x, first.y)
        for (i in 1 until stroke.points.size) {
          lineTo(stroke.points[i].x, stroke.points[i].y)
        }
      }
      drawPath(
        path = path,
        color = stroke.color.copy(alpha = 0.35f),
        style = Stroke(
          width = stroke.strokeWidth,
          cap = StrokeCap.Square,
          join = StrokeJoin.Round
        )
      )
    }
    else -> { // PEN and ERASER
      val path = Path().apply {
        moveTo(first.x, first.y)
        for (i in 1 until stroke.points.size) {
          val prev = stroke.points[i - 1]
          val curr = stroke.points[i]
          val midX = (prev.x + curr.x) / 2f
          val midY = (prev.y + curr.y) / 2f
          quadraticTo(prev.x, prev.y, midX, midY)
        }
        lineTo(last.x, last.y)
      }
      drawPath(
        path = path,
        color = stroke.color,
        style = Stroke(
          width = stroke.strokeWidth,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }
  }
}
