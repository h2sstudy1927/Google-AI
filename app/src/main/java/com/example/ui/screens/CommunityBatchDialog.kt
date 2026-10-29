package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

private const val WHATSAPP_COMMUNITY_LINK = "https://chat.whatsapp.com/H2SStudyOdishaGovt2026"
private const val TELEGRAM_CHANNEL = "@H2SStudyOdisha"
private const val HELPLINE_PHONE = "+91 94390 12345"

/**
 * Community Batch & Support Dialog.
 * Provides safe in-app clipboard sharing, WhatsApp/Telegram aspirant details,
 * and an explicit GitHub Pages 404 troubleshooting guide.
 */
@Composable
fun CommunityBatchDialog(
  onDismiss: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current
  var isGithubGuideExpanded by remember { mutableStateOf(false) }

  fun copyToClipboard(label: String, text: String) {
    try {
      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText(label, text)
      clipboard.setPrimaryClip(clip)
      onShowToast("✓ $label copied to clipboard!")
    } catch (_: Exception) {
      onShowToast("Copied: $text")
    }
  }

  fun shareWithFriends(text: String) {
    try {
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Join H2S STUDY Odisha Aspirants Circle")
        putExtra(Intent.EXTRA_TEXT, text)
      }
      val chooser = Intent.createChooser(shareIntent, "Share with Odisha Aspirants")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (_: Exception) {
      copyToClipboard("Invitation Link", text)
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
        .border(1.dp, Slate200, RoundedCornerShape(26.dp)),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFF064E3B), Emerald700)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text("💬", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "H2S Aspirants Circle",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = Slate900
              )
              Text(
                text = "Official Odisha Govt Exams Group",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Emerald700
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Slate100)
              .testTag("community_dialog_close")
          ) {
            Text("✕", color = Slate600, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WhatsApp Community Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Emerald400.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "OFFICIAL WHATSAPP BATCH",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Emerald900,
                letterSpacing = 0.5.sp
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Emerald700)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "DAILY 7:00 AM",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Get daily current affairs capsule, official syllabus PDF circulars, and Odisha GK question banks directly on WhatsApp.",
              fontSize = 12.sp,
              color = Slate700,
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Link Preview Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, Emerald400.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
              Text(
                text = WHATSAPP_COMMUNITY_LINK,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = Emerald900,
                maxLines = 1
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { copyToClipboard("WhatsApp Link", WHATSAPP_COMMUNITY_LINK) },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("copy_whatsapp_link_btn")
              ) {
                Text("📋 Copy Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = {
                  shareWithFriends(
                    "Join H2S STUDY Odisha Govt Exams (OPSC, OSSC, OSSSC, TEACHING) Community Batch: $WHATSAPP_COMMUNITY_LINK"
                  )
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("share_whatsapp_btn")
              ) {
                Text("📤 Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald900)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Telegram Channel & Helpline Info
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
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
              Column {
                Text(
                  text = "🚀 Telegram Channel",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate900
                )
                Text(
                  text = TELEGRAM_CHANNEL,
                  fontSize = 11.sp,
                  color = Purple800,
                  fontFamily = FontFamily.Monospace
                )
              }

              Button(
                onClick = { copyToClipboard("Telegram Handle", TELEGRAM_CHANNEL) },
                colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_telegram_btn")
              ) {
                Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            HorizontalDivider(color = Slate200)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "📞 Odisha Study Helpdesk",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate900
                )
                Text(
                  text = "$HELPLINE_PHONE (Kalahandi Cell)",
                  fontSize = 11.sp,
                  color = Slate600
                )
              }

              Button(
                onClick = { copyToClipboard("Helpline Number", HELPLINE_PHONE) },
                colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_helpline_btn")
              ) {
                Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // GitHub 404 Troubleshooting Guide Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Purple200, RoundedCornerShape(18.dp))
            .clickable { isGithubGuideExpanded = !isGithubGuideExpanded },
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌐", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "GitHub 404 Error Fix Guide",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple950
                  )
                  Text(
                    text = "Why published links show '404 File Not Found'",
                    fontSize = 10.sp,
                    color = Purple800
                  )
                }
              }

              Text(
                text = if (isGithubGuideExpanded) "▲ Hide" else "▼ View",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Purple700
              )
            }

            AnimatedVisibility(visible = isGithubGuideExpanded) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                HorizontalDivider(color = Purple200)

                GuideStepRow(
                  number = "1",
                  title = "File Name Must Be 'index.html'",
                  desc = "GitHub Pages specifically looks for index.html at the root of the repository. Check that it is strictly lowercase (not Index.html or main.html)."
                )

                GuideStepRow(
                  number = "2",
                  title = "Configure Pages in Repo Settings",
                  desc = "In your GitHub repo: click Settings → Pages → under 'Branch' select 'main' (or master) and folder '/ (root)', then click Save."
                )

                GuideStepRow(
                  number = "3",
                  title = "Wait 2-3 Minutes for Build",
                  desc = "GitHub Pages takes about 1-3 minutes to deploy. Checking immediately results in GitHub's 404 message until the Actions workflow finishes."
                )

                GuideStepRow(
                  number = "4",
                  title = "Check Full URL Format",
                  desc = "The valid address is https://<your-username>.github.io/<repository-name>/. If your repo is private, make it Public to enable free Pages."
                )

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3E8FF))
                    .padding(8.dp)
                ) {
                  Text(
                    text = "💡 Good News: This H2S STUDY native app stores all Odisha exam folders, mock questions, and your test history 100% offline via local Room DB. No external hosting is needed to practice!",
                    fontSize = 10.sp,
                    color = Purple900,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Done button
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Slate900),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("community_dialog_done")
        ) {
          Text("Got It, Return to App", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun GuideStepRow(
  number: String,
  title: String,
  desc: String
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .clip(CircleShape)
        .background(Purple800),
      contentAlignment = Alignment.Center
    ) {
      Text(number, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.width(8.dp))

    Column {
      Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate900)
      Text(desc, fontSize = 10.sp, color = Slate600, lineHeight = 14.sp)
    }
  }
}
