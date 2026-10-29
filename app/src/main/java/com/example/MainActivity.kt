package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainAppShell
import com.example.ui.theme.H2sStudyTheme
import com.example.ui.viewmodel.H2sViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

  private val viewModel: H2sViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Configure Firebase App Check with launch intent before any UI or Firebase operations
    configureAppCheck(this, intent)
    enableEdgeToEdge()
    setContent {
      H2sStudyTheme {
        MainAppShell(viewModel = viewModel)
      }
    }
  }
}
