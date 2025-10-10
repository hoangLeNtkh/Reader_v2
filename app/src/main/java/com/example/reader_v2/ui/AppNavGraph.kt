package com.example.reader_v2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reader_v2.ui.screen.library.LibraryScreen
import com.example.reader_v2.ui.screen.reader.ReaderScreen

sealed class Screen(
    val route: String,
    val title: String,
) {
    object Library : Screen(
        route = "library",
        title = "Library",
    )

    object Reader : Screen(
        route = "reader/{bookId}",
        title = "Reader",
    ) {
        fun createRoute(bookId: String): String = "reader/$bookId"
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Library.route,
    ) {
        composable(route = Screen.Library.route) {
            LibraryScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.Reader.createRoute(bookId))
                },
            )
        }

        composable(route = Screen.Reader.route) {
            ReaderScreen()
        }
    }
}
