package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PreloadedData
import com.example.ui.theme.Amber400
import com.example.ui.theme.Purple200
import com.example.ui.theme.Purple600
import com.example.ui.theme.Purple700
import com.example.ui.theme.Purple900
import com.example.ui.theme.Purple950
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthGateScreen(
  onSignIn: (String, () -> Unit, (String) -> Unit) -> Unit,
  onSignUp: (String, String, String, String, () -> Unit) -> Unit,
  onAuthenticated: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isLoginTab by remember { mutableStateOf(true) }

  // Sign In Form State
  var loginMobile by remember { mutableStateOf("9861012345") }
  var loginPassword by remember { mutableStateOf("odisha2026") }

  // Sign Up Form State
  var suName by remember { mutableStateOf("") }
  var suMobile by remember { mutableStateOf("") }
  var suDistrict by remember { mutableStateOf("Kalahandi") }
  var suRole by remember { mutableStateOf("student") }
  var suPassword by remember { mutableStateOf("") }

  var districtExpanded by remember { mutableStateOf(false) }
  var roleExpanded by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.linearGradient(
          colors = listOf(Purple950, Purple900, Slate900)
        )
      )
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(28.dp))
        .border(1.dp, Slate200.copy(alpha = 0.2f), RoundedCornerShape(28.dp)),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Top Branding Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(Purple900, Purple950, Purple900)
              )
            )
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
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
                fontSize = 18.sp
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "H2S STUDY",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Odisha State Competitive Examinations Portal",
              color = Amber400,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.Center
            )
            Text(
              text = "OPSC • OSSC • OSSSC • OUSSSC • TEACHING EXAM",
              color = Purple200,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }

        // Tab Selector
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isLoginTab) Color.White else Color.Transparent)
              .clickable {
                isLoginTab = true
                errorMessage = null
              }
              .padding(vertical = 10.dp)
              .testTag("tab_login"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Sign In (Login)",
              color = if (isLoginTab) Purple900 else Slate600,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (!isLoginTab) Color.White else Color.Transparent)
              .clickable {
                isLoginTab = false
                errorMessage = null
              }
              .padding(vertical = 10.dp)
              .testTag("tab_signup"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Sign Up (1st Time)",
              color = if (!isLoginTab) Purple900 else Slate600,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        errorMessage?.let { msg ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFFF1F2))
              .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Text(
              text = msg,
              color = Color(0xFFBE123C),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Forms Container
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (isLoginTab) {
            // SIGN IN FORM
            OutlinedTextField(
              value = loginMobile,
              onValueChange = { if (it.length <= 10) loginMobile = it },
              label = { Text("Registered Mobile Number") },
              placeholder = { Text("10-digit mobile number") },
              prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = Slate600) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_mobile_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                focusedLabelColor = Purple700
              ),
              shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
              value = loginPassword,
              onValueChange = { loginPassword = it },
              label = { Text("Password") },
              placeholder = { Text("Enter your password") },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                focusedLabelColor = Purple700
              ),
              shape = RoundedCornerShape(14.dp)
            )

            Button(
              onClick = {
                if (loginMobile.length != 10) {
                  errorMessage = "Please enter a valid 10-digit mobile number."
                  return@Button
                }
                onSignIn(
                  loginMobile,
                  { onAuthenticated() },
                  { err -> errorMessage = err }
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("login_submit_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Purple700),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = "Sign In to H2S STUDY",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          } else {
            // SIGN UP FORM
            OutlinedTextField(
              value = suName,
              onValueChange = { suName = it },
              label = { Text("Full Name") },
              placeholder = { Text("e.g. Subrat Mohapatra") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_name_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                focusedLabelColor = Purple700
              ),
              shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
              value = suMobile,
              onValueChange = { if (it.length <= 10) suMobile = it },
              label = { Text("Mobile Number (Login ID)") },
              placeholder = { Text("10-digit mobile number") },
              prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = Slate600) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_mobile_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                focusedLabelColor = Purple700
              ),
              shape = RoundedCornerShape(14.dp)
            )

            // District Dropdown
            ExposedDropdownMenuBox(
              expanded = districtExpanded,
              onExpandedChange = { districtExpanded = !districtExpanded }
            ) {
              OutlinedTextField(
                value = suDistrict,
                onValueChange = {},
                readOnly = true,
                label = { Text("District (Odisha)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                modifier = Modifier
                  .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                  .fillMaxWidth()
                  .testTag("signup_district_select"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Purple700,
                  focusedLabelColor = Purple700
                )
              )
              ExposedDropdownMenu(
                expanded = districtExpanded,
                onDismissRequest = { districtExpanded = false }
              ) {
                PreloadedData.odishaDistricts.forEach { dist ->
                  DropdownMenuItem(
                    text = { Text(dist) },
                    onClick = {
                      suDistrict = dist
                      districtExpanded = false
                    }
                  )
                }
              }
            }

            // Role Selector
            ExposedDropdownMenuBox(
              expanded = roleExpanded,
              onExpandedChange = { roleExpanded = !roleExpanded }
            ) {
              OutlinedTextField(
                value = if (suRole == "student") "Student / Candidate" else "Teacher / Developer",
                onValueChange = {},
                readOnly = true,
                label = { Text("Account Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                modifier = Modifier
                  .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                  .fillMaxWidth()
                  .testTag("signup_role_select"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Purple700,
                  focusedLabelColor = Purple700
                )
              )
              ExposedDropdownMenu(
                expanded = roleExpanded,
                onDismissRequest = { roleExpanded = false }
              ) {
                DropdownMenuItem(
                  text = { Text("Student / Candidate (Folders, Mocks & Live Class)") },
                  onClick = {
                    suRole = "student"
                    roleExpanded = false
                  }
                )
                DropdownMenuItem(
                  text = { Text("Teacher / Developer (Authoring & Live Studio)") },
                  onClick = {
                    suRole = "teacher"
                    roleExpanded = false
                  }
                )
              }
            }

            OutlinedTextField(
              value = suPassword,
              onValueChange = { suPassword = it },
              label = { Text("Create Password") },
              placeholder = { Text("At least 6 characters") },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_password_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple700,
                focusedLabelColor = Purple700
              ),
              shape = RoundedCornerShape(14.dp)
            )

            Button(
              onClick = {
                if (suName.isBlank()) {
                  errorMessage = "Please enter your full name."
                  return@Button
                }
                if (suMobile.length != 10) {
                  errorMessage = "Please enter a valid 10-digit mobile number."
                  return@Button
                }
                if (suPassword.length < 6) {
                  errorMessage = "Password must be at least 6 characters."
                  return@Button
                }
                onSignUp(
                  suName.trim(),
                  suMobile.trim(),
                  suDistrict,
                  suRole
                ) {
                  onAuthenticated()
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("signup_submit_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Purple700),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = "Create Account & Continue",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    }
  }
}
