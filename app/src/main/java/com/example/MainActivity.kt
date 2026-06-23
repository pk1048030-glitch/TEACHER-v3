package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.AcademyMainShell
import com.example.ui.AcademyViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Obtain application-scoped instance of the ViewModel
    val viewModel = ViewModelProvider(this)[AcademyViewModel::class.java]

    setContent {
      MyApplicationTheme {
        AcademyMainShell(viewModel = viewModel)
      }
    }
  }
}
