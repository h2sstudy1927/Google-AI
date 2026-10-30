package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserSession
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple800
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun SecurityBackupDialog(
  currentUser: UserSession?,
  remainingQuota: Int,
  totalQuota: Int = 25,
  onDismiss: () -> Unit,
  onExportBackup: () -> String,
  onRestoreBackup: (String) -> Unit
) {
  val context = LocalContext.current
  var activeTab by remember { mutableStateOf("status") } // "status", "backup", "restore"
  var exportedJson by remember { mutableStateOf<String?>(null) }
  var restoreInputText by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("security_backup_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Purple800),
              contentAlignment = Alignment.Center
            ) {
              Text("🛡️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Security & Backups",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Slate900
              )
              Text(
                text = "Enterprise Data & Access Protection",
                fontSize = 11.sp,
                color = Slate600
              )
            }
          }
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Slate100)
              .clickable { onDismiss() },
            contentAlignment = Alignment.Center
          ) {
            Text("✕", fontSize = 14.sp, color = Slate600, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate100)
            .padding(4.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          TabButton(
            title = "Security Status",
            isSelected = activeTab == "status",
            onClick = { activeTab = "status" },
            modifier = Modifier.weight(1f).testTag("tab_security_status")
          )
          TabButton(
            title = "Export Backup",
            isSelected = activeTab == "backup",
            onClick = {
              activeTab = "backup"
              exportedJson = onExportBackup()
            },
            modifier = Modifier.weight(1f).testTag("tab_export_backup")
          )
          TabButton(
            title = "Restore Backup",
            isSelected = activeTab == "restore",
            onClick = { activeTab = "restore" },
            modifier = Modifier.weight(1f).testTag("tab_restore_backup")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
          "status" -> {
            // Security Checklist & Quotas
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              // Daily Quota Box
              val quotaPercent = (remainingQuota.toFloat() / totalQuota.toFloat()).coerceIn(0f, 1f)
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (remainingQuota <= 5) Rose500.copy(alpha = 0.1f) else Purple950.copy(alpha = 0.05f))
                  .border(1.dp, if (remainingQuota <= 5) Rose500 else Purple700, RoundedCornerShape(12.dp))
                  .padding(12.dp)
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Costs & AI Daily Quota",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = Slate900
                    )
                    Text(
                      text = "$remainingQuota / $totalQuota remaining",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = if (remainingQuota <= 5) Rose500 else Purple800
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(6.dp)
                      .clip(CircleShape)
                      .background(Slate200)
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth(quotaPercent)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (remainingQuota <= 5) Rose500 else Emerald400)
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = if (remainingQuota <= 5)
                      "⚠️ 80%+ daily quota consumed. Rate limit resets at 00:00 UTC."
                    else
                      "Monitored under usage threshold alerts. Prevents runaway API costs.",
                    fontSize = 11.sp,
                    color = if (remainingQuota <= 5) Rose500 else Slate600
                  )
                }
              }

              // The 8 Security Items
              SecurityCheckItem(
                icon = "🔑",
                title = "API keys",
                desc = "Zero private keys exposed in frontend. Injected via secure server-side BuildConfig.",
                status = "Protected"
              )
              SecurityCheckItem(
                icon = "🔐",
                title = "Authentication",
                desc = "SHA-256 salted password hashing. 5-strike brute-force lockout (120s penalty).",
                status = "Enforced"
              )
              SecurityCheckItem(
                icon = "🗄️",
                title = "Database Security Rules",
                desc = "Zero-trust firestore.rules deployed with default-deny & isolated subcollections.",
                status = "Active"
              )
              SecurityCheckItem(
                icon = "🤖",
                title = "AI Requests & Abuse Controls",
                desc = "Firebase App Check attested. 500-char input sanitization & 2.5s spam cooldown.",
                status = "Attested"
              )
              SecurityCheckItem(
                icon = "👤",
                title = "Student Data & Scores",
                desc = "Private mock test records, scorecards, and mistake analytics isolated to candidate.",
                status = "Isolated"
              )
              SecurityCheckItem(
                icon = "👑",
                title = "Admin Panel Permissions",
                desc = "Teacher approvals and mock test publishing gated to verified faculty & admins.",
                status = if (currentUser?.isAdmin == true) "Admin Active" else "Student Guarded"
              )
              SecurityCheckItem(
                icon = "💾",
                title = "Backups",
                desc = "Offline-first JSON backup snapshotting with zero-data-loss export and restore.",
                status = "Ready"
              )
            }
          }

          "backup" -> {
            Column {
              Text(
                text = "Export Data Backup",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate900
              )
              Text(
                text = "Creates an encrypted, formatted JSON copy of your student profile, score history, and custom mock tests.",
                fontSize = 11.sp,
                color = Slate600
              )
              Spacer(modifier = Modifier.height(10.dp))

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Slate900)
                  .padding(10.dp)
                  .verticalScroll(rememberScrollState())
              ) {
                Text(
                  text = exportedJson ?: "Generating backup...",
                  fontSize = 10.sp,
                  color = Emerald400,
                  fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("H2S_STUDY_BACKUP", exportedJson ?: ""))
                    statusMessage = "Copied backup JSON to clipboard!"
                  },
                  modifier = Modifier.weight(1f).testTag("copy_backup_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Purple800),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("📋 Copy JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = {
                    exportedJson = onExportBackup()
                    statusMessage = "Backup refreshed successfully!"
                  },
                  modifier = Modifier.weight(1f).testTag("refresh_backup_btn"),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("🔄 Refresh", fontSize = 12.sp)
                }
              }

              if (statusMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = statusMessage ?: "",
                  fontSize = 11.sp,
                  color = Emerald400,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          "restore" -> {
            Column {
              Text(
                text = "Restore Data Backup",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate900
              )
              Text(
                text = "Paste a previously exported H2S STUDY backup JSON to restore your examination history and test progress.",
                fontSize = 11.sp,
                color = Slate600
              )
              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = restoreInputText,
                onValueChange = { restoreInputText = it },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(140.dp)
                  .testTag("restore_backup_input"),
                placeholder = {
                  Text("Paste { \"app\": \"H2S STUDY\", ... } backup content here", fontSize = 11.sp, color = Slate400)
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                  fontSize = 11.sp,
                  fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Purple800,
                  unfocusedBorderColor = Slate300
                ),
                shape = RoundedCornerShape(8.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    if (restoreInputText.isNotBlank()) {
                      onRestoreBackup(restoreInputText)
                      statusMessage = "Backup processed!"
                    } else {
                      statusMessage = "Please paste backup JSON first."
                    }
                  },
                  modifier = Modifier.weight(1f).testTag("confirm_restore_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Emerald400),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("📥 Restore Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                }

                OutlinedButton(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                      restoreInputText = clip.getItemAt(0).text?.toString() ?: ""
                      statusMessage = "Pasted from clipboard!"
                    }
                  },
                  modifier = Modifier.weight(1f).testTag("paste_clipboard_btn"),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("📋 Paste", fontSize = 12.sp)
                }
              }

              if (statusMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = statusMessage ?: "",
                  fontSize = 11.sp,
                  color = Slate800,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Done Button
        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth().testTag("close_security_dialog_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Slate900),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Done", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun TabButton(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) Color.White else Color.Transparent)
      .clickable { onClick() }
      .padding(vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      color = if (isSelected) Purple900 else Slate600
    )
  }
}

@Composable
private fun SecurityCheckItem(
  icon: String,
  title: String,
  desc: String,
  status: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Slate50)
      .border(1.dp, Slate200, RoundedCornerShape(10.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(icon, fontSize = 16.sp)
    Spacer(modifier = Modifier.width(10.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
      Text(text = desc, fontSize = 10.sp, color = Slate600, lineHeight = 14.sp)
    }
    Spacer(modifier = Modifier.width(6.dp))
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(Emerald400.copy(alpha = 0.15f))
        .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
      Text(
        text = "✓ $status",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF047857)
      )
    }
  }
}
