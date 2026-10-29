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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TeacherApprovalRequest
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun NotificationsDialog(
  isTeacherOrAdmin: Boolean,
  approvalRequests: List<TeacherApprovalRequest>,
  onApprove: (TeacherApprovalRequest) -> Unit,
  onDecline: (TeacherApprovalRequest) -> Unit,
  onDismiss: () -> Unit
) {
  val pending = approvalRequests.filter { it.status == "pending" }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, Slate200, RoundedCornerShape(24.dp)),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(Purple950)
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔔", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Notifications & Approvals",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(24.dp).testTag("close_notif_dialog_btn")
            ) {
              Text("✕", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (isTeacherOrAdmin) {
            // Teacher approval section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(Amber400)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "PENDING TEACHER APPROVALS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate800
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${pending.size} Pending",
                    color = Amber700,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              if (pending.isEmpty()) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(12.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("No pending teacher role requests.", fontSize = 11.sp, color = Slate400)
                }
              } else {
                pending.forEach { req ->
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                  ) {
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Column {
                          Text(req.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                          Text("📱 +91 ${req.mobile} • ${req.district}", fontSize = 10.sp, color = Slate600)
                        }
                        Text(req.timestamp, fontSize = 9.sp, color = Amber700, fontWeight = FontWeight.Bold)
                      }

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Button(
                          onClick = { onApprove(req) },
                          colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                          shape = RoundedCornerShape(8.dp),
                          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                          modifier = Modifier.weight(1f).testTag("approve_teacher_${req.id}")
                        ) {
                          Text("✓ Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                          onClick = { onDecline(req) },
                          shape = RoundedCornerShape(8.dp),
                          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                          modifier = Modifier.weight(1f)
                        ) {
                          Text("Decline", fontSize = 11.sp, color = Slate600)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // General Platform Updates
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "RECENT PLATFORM ALERTS",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Slate400
            )

            listOf(
              "🎓 New Candidate Registered: Chinmayee Sahoo from Puri" to "12 mins ago",
              "📝 New OSSSC RI/Amin Special Mock published" to "1 hour ago",
              "🎥 Modern Odisha History lecture recorded in 1080p" to "Today"
            ).forEach { (alert, time) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF8FAFC))
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(alert, fontSize = 11.sp, color = Slate700, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(6.dp))
                Text(time, fontSize = 9.sp, color = Slate400)
              }
            }
          }
        }
      }
    }
  }
}
