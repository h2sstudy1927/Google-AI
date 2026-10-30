package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExamAttemptResult
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple600
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CertificateDialog(
  attempt: ExamAttemptResult?,
  candidateName: String,
  onDismiss: () -> Unit,
  onPrint: () -> Unit
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val dateString = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
    Date(attempt?.timestamp ?: System.currentTimeMillis())
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 24.dp)
        .clip(RoundedCornerShape(24.dp))
        .border(3.dp, Amber400.copy(alpha = 0.7f), RoundedCornerShape(24.dp)),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "H2S STUDY • Odisha State Academic Credential",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Slate600
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp).testTag("close_cert_btn")
          ) {
            Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate600)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Certificate Frame
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(Color(0xFFFFFBEB), Color.White, Color(0xFFFAF5FF))
              )
            )
            .border(2.dp, Amber400.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .padding(18.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            // Emblem Seal
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(Amber400, Purple600, Purple900)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "H2S",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "CERTIFICATE OF ACADEMIC MERIT",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = Slate900,
              letterSpacing = 1.sp,
              textAlign = TextAlign.Center
            )

            Text(
              text = "Odisha State Competitive Examination Board • H2S STUDY",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Amber700,
              letterSpacing = 0.5.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "This certificate is proudly awarded to candidate",
              fontSize = 11.sp,
              color = Slate600,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = candidateName.ifBlank { "Aarav Mohanty" },
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Purple950,
              fontStyle = FontStyle.Italic,
              textAlign = TextAlign.Center
            )

            Box(
              modifier = Modifier
                .width(160.dp)
                .height(1.dp)
                .background(Purple800.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "for demonstrating conceptual accuracy and finishing the recruitment simulation exam:",
              fontSize = 10.sp,
              color = Slate600,
              textAlign = TextAlign.Center
            )

            Text(
              text = attempt?.testTitle ?: "OSSSC Combined Mock Test 2026",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Slate900,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Small Stats Badges
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = String.format(Locale.ROOT, "%.2f pts", attempt?.netScore ?: 3.75),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Purple800
                )
                Text("NET SCORE", fontSize = 8.sp, color = Slate400, fontWeight = FontWeight.Bold)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${attempt?.accuracyPercent ?: 92}%",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Emerald700
                )
                Text("ACCURACY", fontSize = 8.sp, color = Slate400, fontWeight = FontWeight.Bold)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = dateString,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
                Text("ISSUE DATE", fontSize = 8.sp, color = Slate400, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verification & Signatures
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              Column {
                Text(
                  text = attempt?.verificationId ?: "ID: H2S-OD-9482",
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace,
                  color = Slate400
                )
                Text(
                  text = "✓ Verified Digital Record",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Emerald700
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Hemanand Hans",
                  fontSize = 13.sp,
                  fontStyle = FontStyle.Italic,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
                Text(
                  text = "Founder & Lead Developer",
                  fontSize = 8.sp,
                  color = Slate600
                )
                Text(
                  text = "H2S STUDY • Kalahandi",
                  fontSize = 8.sp,
                  color = Slate600
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onPrint,
            colors = ButtonDefaults.buttonColors(containerColor = Purple700),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("cert_print_btn")
          ) {
            Text("🖨️  Save / Print", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          if (attempt != null) {
            Button(
              onClick = {
                try {
                  val file = com.example.util.PdfExporter.exportResultPdf(context, attempt)
                  com.example.util.PdfExporter.shareOrViewPdf(context, file)
                } catch (_: Exception) {}
              },
              colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("cert_export_pdf_btn")
            ) {
              Text("📄  Export PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}
