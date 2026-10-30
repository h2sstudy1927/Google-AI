package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.model.ExamAttemptResult
import com.example.data.model.QuestionAttempt
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enterprise PDF Generator for H2S STUDY Mock Examination Results.
 * Generates an official, beautifully styled A4 report card with full
 * question-by-question breakdown, negative marking audit, and verification ID.
 */
object PdfExporter {

  private const val PAGE_WIDTH = 595 // Standard A4 width in PostScript points (72 dpi)
  private const val PAGE_HEIGHT = 842 // Standard A4 height in PostScript points

  fun exportResultPdf(context: Context, attempt: ExamAttemptResult): File {
    val pdfDocument = PdfDocument()

    val paint = Paint().apply { isAntiAlias = true }
    val titlePaint = Paint().apply {
      isAntiAlias = true
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val questionsPerPage = 12
    val totalQuestions = attempt.questionAttempts.size
    val totalPages = if (totalQuestions <= 8) 1 else 1 + ((totalQuestions - 8 + questionsPerPage - 1) / questionsPerPage)

    // Formatters
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ROOT)
    val dateString = dateFormat.format(Date(attempt.timestamp))

    // PAGE 1: Header, Summary, Scorecard Grid, and First Batch of Questions
    run {
      val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
      val page = pdfDocument.startPage(pageInfo)
      val canvas = page.canvas

      drawHeader(canvas, titlePaint, paint, attempt, dateString)
      drawCandidateDetails(canvas, titlePaint, paint, attempt)
      drawScoreBreakdownCards(canvas, titlePaint, paint, attempt)

      val startY = 290f
      val firstPageQuestions = attempt.questionAttempts.take(8)
      drawQuestionsTable(canvas, titlePaint, paint, firstPageQuestions, startY, startIndex = 1)
      drawFooter(canvas, paint, 1, totalPages, attempt.verificationId)

      pdfDocument.finishPage(page)
    }

    // SUBSEQUENT PAGES: Continuation of Questions
    if (totalQuestions > 8) {
      val remainingQuestions = attempt.questionAttempts.drop(8)
      val chunks = remainingQuestions.chunked(questionsPerPage)

      chunks.forEachIndexed { pageIdx, chunk ->
        val currentPageNumber = pageIdx + 2
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Compact Header for continuation pages
        drawContinuationHeader(canvas, titlePaint, paint, attempt)
        val startQuestionIndex = 8 + (pageIdx * questionsPerPage) + 1
        drawQuestionsTable(canvas, titlePaint, paint, chunk, startY = 80f, startIndex = startQuestionIndex)
        drawFooter(canvas, paint, currentPageNumber, totalPages, attempt.verificationId)

        pdfDocument.finishPage(page)
      }
    }

    // Save PDF to Documents Directory or Internal Cache
    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    if (!docsDir.exists()) docsDir.mkdirs()

    val safeTestTitle = attempt.testTitle.replace(Regex("[^a-zA-Z0-9]"), "_").take(25)
    val fileName = "H2S_Scorecard_${safeTestTitle}_${System.currentTimeMillis()}.pdf"
    val file = File(docsDir, fileName)

    FileOutputStream(file).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()

    return file
  }

  // --- DRAWING HELPERS ---

  private fun drawHeader(
    canvas: Canvas,
    titlePaint: Paint,
    paint: Paint,
    attempt: ExamAttemptResult,
    dateString: String
  ) {
    // Purple Header Bar
    paint.color = Color.rgb(59, 7, 100) // Purple900 #3B0764
    canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 70f, paint)

    // Brand Title
    titlePaint.color = Color.WHITE
    titlePaint.textSize = 18f
    canvas.drawText("H2S STUDY • ODISHA GOVT COMPETITIVE EXAMS", 24f, 28f, titlePaint)

    // Subtitle & Exam Category Badge
    paint.color = Color.rgb(221, 214, 254) // Purple200
    paint.textSize = 10f
    canvas.drawText("Official CBT Scorecard & Performance Audit • ${attempt.category} Examination", 24f, 44f, paint)

    // Verification Date
    paint.color = Color.rgb(251, 191, 36) // Amber400
    paint.textSize = 9f
    canvas.drawText("Attempt Date: $dateString", 24f, 58f, paint)

    // Verification ID on Top Right
    titlePaint.color = Color.WHITE
    titlePaint.textSize = 9f
    val verifText = "ID: ${attempt.verificationId}"
    val verifWidth = titlePaint.measureText(verifText)
    canvas.drawText(verifText, PAGE_WIDTH - 24f - verifWidth, 28f, titlePaint)
  }

  private fun drawCandidateDetails(
    canvas: Canvas,
    titlePaint: Paint,
    paint: Paint,
    attempt: ExamAttemptResult
  ) {
    val boxRect = RectF(24f, 80f, PAGE_WIDTH - 24f, 142f)
    paint.color = Color.rgb(248, 250, 252) // Slate50
    canvas.drawRoundRect(boxRect, 8f, 8f, paint)

    paint.color = Color.rgb(226, 232, 240) // Slate200 border
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(boxRect, 8f, 8f, paint)
    paint.style = Paint.Style.FILL

    // Column 1: Candidate Profile
    titlePaint.color = Color.rgb(30, 41, 59) // Slate800
    titlePaint.textSize = 11f
    canvas.drawText("Candidate: ${attempt.candidateName}", 36f, 102f, titlePaint)

    paint.color = Color.rgb(100, 116, 139) // Slate500
    paint.textSize = 9.5f
    canvas.drawText("District: ${attempt.candidateDistrict} • Odisha", 36f, 118f, paint)
    canvas.drawText("Accuracy: ${attempt.accuracyPercent}%", 36f, 132f, paint)

    // Column 2: Test Information
    titlePaint.color = Color.rgb(88, 28, 135) // Purple800
    canvas.drawText("Exam: ${attempt.testTitle}", 280f, 102f, titlePaint)

    paint.color = Color.rgb(100, 116, 139)
    canvas.drawText("Pattern: +1.00 Correct, -0.25 Negative Marking Penalty", 280f, 118f, paint)
    val statusText = if (attempt.netScore > 0) "Status: QUALIFIED / PASSED" else "Status: NEEDS REVISION"
    titlePaint.color = if (attempt.netScore > 0) Color.rgb(5, 150, 105) else Color.rgb(225, 29, 72)
    canvas.drawText(statusText, 280f, 132f, titlePaint)
  }

  private fun drawScoreBreakdownCards(
    canvas: Canvas,
    titlePaint: Paint,
    paint: Paint,
    attempt: ExamAttemptResult
  ) {
    val cardWidth = (PAGE_WIDTH - 48f - 30f) / 4f
    val cardHeight = 58f
    val cardY = 152f

    // 1. Correct Card
    drawMetricCard(
      canvas, titlePaint, paint,
      x = 24f, y = cardY, w = cardWidth, h = cardHeight,
      title = "CORRECT",
      value = "+${attempt.correctCount}",
      sub = "+1.00 pts each",
      bgColor = Color.rgb(236, 253, 245), // Emerald50
      textColor = Color.rgb(4, 120, 87) // Emerald700
    )

    // 2. Incorrect Card
    drawMetricCard(
      canvas, titlePaint, paint,
      x = 24f + (cardWidth + 10f), y = cardY, w = cardWidth, h = cardHeight,
      title = "INCORRECT",
      value = "${attempt.wrongCount}",
      sub = "-0.25 penalty",
      bgColor = Color.rgb(255, 241, 242), // Rose50
      textColor = Color.rgb(225, 29, 72) // Rose600
    )

    // 3. Penalty Deducted Card
    drawMetricCard(
      canvas, titlePaint, paint,
      x = 24f + (cardWidth + 10f) * 2, y = cardY, w = cardWidth, h = cardHeight,
      title = "PENALTY",
      value = String.format(Locale.ROOT, "-%.2f", attempt.penaltyDeducted),
      sub = "Marks lost",
      bgColor = Color.rgb(254, 243, 199), // Amber100
      textColor = Color.rgb(180, 83, 9) // Amber700
    )

    // 4. Net Score Card
    drawMetricCard(
      canvas, titlePaint, paint,
      x = 24f + (cardWidth + 10f) * 3, y = cardY, w = cardWidth, h = cardHeight,
      title = "NET SCORE",
      value = String.format(Locale.ROOT, "%.2f", attempt.netScore),
      sub = "${attempt.accuracyPercent}% Accuracy",
      bgColor = Color.rgb(250, 245, 255), // Purple50
      textColor = Color.rgb(107, 33, 168) // Purple700
    )

    // Section Header for Breakdown Table
    titlePaint.color = Color.rgb(15, 23, 42) // Slate900
    titlePaint.textSize = 12f
    canvas.drawText("Question-by-Question Marking Breakdown", 24f, 230f, titlePaint)

    paint.color = Color.rgb(100, 116, 139)
    paint.textSize = 9f
    canvas.drawText("Detailed audit of candidate choices, official answer keys, and marking deductions", 24f, 244f, paint)
  }

  private fun drawMetricCard(
    canvas: Canvas,
    titlePaint: Paint,
    paint: Paint,
    x: Float, y: Float, w: Float, h: Float,
    title: String,
    value: String,
    sub: String,
    bgColor: Int,
    textColor: Int
  ) {
    val rect = RectF(x, y, x + w, y + h)
    paint.color = bgColor
    canvas.drawRoundRect(rect, 8f, 8f, paint)

    paint.color = Color.rgb(226, 232, 240)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 1f
    canvas.drawRoundRect(rect, 8f, 8f, paint)
    paint.style = Paint.Style.FILL

    // Label
    paint.color = Color.rgb(100, 116, 139)
    paint.textSize = 8f
    canvas.drawText(title, x + 8f, y + 16f, paint)

    // Value
    titlePaint.color = textColor
    titlePaint.textSize = 15f
    canvas.drawText(value, x + 8f, y + 36f, titlePaint)

    // Subtext
    paint.color = Color.rgb(100, 116, 139)
    paint.textSize = 7.5f
    canvas.drawText(sub, x + 8f, y + 49f, paint)
  }

  private fun drawQuestionsTable(
    canvas: Canvas,
    titlePaint: Paint,
    paint: Paint,
    questions: List<QuestionAttempt>,
    startY: Float,
    startIndex: Int
  ) {
    var curY = startY

    // Table Header Bar
    paint.color = Color.rgb(241, 245, 249) // Slate100
    canvas.drawRect(24f, curY, PAGE_WIDTH - 24f, curY + 20f, paint)

    titlePaint.color = Color.rgb(51, 65, 85) // Slate700
    titlePaint.textSize = 8.5f

    canvas.drawText("#", 30f, curY + 14f, titlePaint)
    canvas.drawText("Topic", 52f, curY + 14f, titlePaint)
    canvas.drawText("Question Statement", 120f, curY + 14f, titlePaint)
    canvas.drawText("Candidate", 365f, curY + 14f, titlePaint)
    canvas.drawText("Answer Key", 430f, curY + 14f, titlePaint)
    canvas.drawText("Marking", 500f, curY + 14f, titlePaint)

    curY += 24f

    questions.forEachIndexed { idx, q ->
      val qNum = startIndex + idx
      val rowBg = if (idx % 2 == 0) Color.WHITE else Color.rgb(248, 250, 252)
      paint.color = rowBg
      canvas.drawRect(24f, curY - 12f, PAGE_WIDTH - 24f, curY + 18f, paint)

      // Q Number
      paint.color = Color.rgb(71, 85, 105)
      paint.textSize = 8f
      canvas.drawText("Q$qNum", 30f, curY + 2f, paint)

      // Topic
      paint.color = Color.rgb(107, 33, 168)
      paint.textSize = 8f
      val topicText = q.topic.take(14)
      canvas.drawText(topicText, 52f, curY + 2f, paint)

      // Question preview (truncated to fit)
      paint.color = Color.rgb(30, 41, 59)
      paint.textSize = 8f
      val shortQ = if (q.question.length > 50) q.question.take(48) + "..." else q.question
      canvas.drawText(shortQ, 120f, curY + 2f, paint)

      // Candidate answer
      val chosenText = when {
        q.isSkipped -> "Skipped"
        q.chosenIndex != null -> "Opt ${(q.chosenIndex + 'A'.code).toChar()}"
        else -> "None"
      }
      paint.color = if (q.isCorrect) Color.rgb(4, 120, 87) else if (q.isSkipped) Color.rgb(100, 116, 139) else Color.rgb(225, 29, 72)
      canvas.drawText(chosenText, 365f, curY + 2f, paint)

      // Correct answer
      val correctText = "Opt ${(q.correctIndex + 'A'.code).toChar()}"
      paint.color = Color.rgb(4, 120, 87)
      canvas.drawText(correctText, 430f, curY + 2f, paint)

      // Marking Result
      val markText = when {
        q.isCorrect -> "+1.00 pts"
        q.isSkipped -> " 0.00 pts"
        else -> "-0.25 pts"
      }
      titlePaint.textSize = 8f
      titlePaint.color = when {
        q.isCorrect -> Color.rgb(4, 120, 87)
        q.isSkipped -> Color.rgb(100, 116, 139)
        else -> Color.rgb(225, 29, 72)
      }
      canvas.drawText(markText, 500f, curY + 2f, titlePaint)

      curY += 24f
    }
  }

  private fun drawContinuationHeader(
    canvas: Canvas,
    titlePaint: Paint,
    paint: Paint,
    attempt: ExamAttemptResult
  ) {
    paint.color = Color.rgb(59, 7, 100)
    canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 40f, paint)

    titlePaint.color = Color.WHITE
    titlePaint.textSize = 12f
    canvas.drawText("H2S STUDY • ${attempt.testTitle} • Results Breakdown", 24f, 24f, titlePaint)

    paint.color = Color.rgb(221, 214, 254)
    paint.textSize = 9f
    val idText = "Verification: ${attempt.verificationId}"
    val idWidth = paint.measureText(idText)
    canvas.drawText(idText, PAGE_WIDTH - 24f - idWidth, 24f, paint)
  }

  private fun drawFooter(
    canvas: Canvas,
    paint: Paint,
    currentPage: Int,
    totalPages: Int,
    verificationId: String
  ) {
    val footerY = PAGE_HEIGHT - 24f

    // Divider line
    paint.color = Color.rgb(226, 232, 240)
    paint.strokeWidth = 1f
    canvas.drawLine(24f, footerY - 12f, PAGE_WIDTH - 24f, footerY - 12f, paint)

    paint.color = Color.rgb(148, 163, 184)
    paint.textSize = 8f
    canvas.drawText("Verified by H2S STUDY • Developer Hemanand Hans (Kalahandi, Odisha)", 24f, footerY, paint)

    val pageText = "Page $currentPage of $totalPages"
    val pageWidth = paint.measureText(pageText)
    canvas.drawText(pageText, PAGE_WIDTH - 24f - pageWidth, footerY, paint)
  }

  /**
   * Helper to launch a viewer or share sheet for the exported PDF file.
   */
  fun shareOrViewPdf(context: Context, file: File) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "H2S STUDY Mock Exam Results")
        putExtra(Intent.EXTRA_TEXT, "Here is my official H2S STUDY mock examination scorecard and negative marking performance breakdown.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(intent, "Open or Share Exam Scorecard PDF")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (_: Exception) {
      // Fallback: If share sheet is unavailable, open with ACTION_VIEW
      try {
        val uri: Uri = FileProvider.getUriForFile(
          context,
          "${context.packageName}.fileprovider",
          file
        )
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
          setDataAndType(uri, "application/pdf")
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(viewIntent)
      } catch (_: Exception) {}
    }
  }
}
