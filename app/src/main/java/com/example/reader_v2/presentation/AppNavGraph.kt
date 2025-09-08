package com.example.reader_v2.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reader_v2.presentation.screen.library.LibraryScreen

sealed class Screen(
	val route: String,
	val title: String
) {
	object Library : Screen(
		route = "library",
		title = "Library"
	)
}

@Composable
fun AppNavGraph() {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = Screen.Library.route
	) {
		composable(route = Screen.Library.route) {
			LibraryScreen()
		}
	}
}