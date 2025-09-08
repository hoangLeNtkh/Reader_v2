package com.example.reader_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.reader_v2.presentation.AppNavGraph
import com.example.reader_v2.ui.theme.Readerv2Theme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			Readerv2Theme {
				AppNavGraph()
			}
		}
	}
}

