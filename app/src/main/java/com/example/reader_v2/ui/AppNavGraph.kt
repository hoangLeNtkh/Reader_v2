package com.example.reader_v2.ui

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reader_v2.ui.screen.library.LibraryScreen
import com.example.reader_v2.ui.screen.reader.ReaderScreen
import com.example.reader_v2.ui.screen.reader.ReaderViewModel

sealed class Screen(
    val route: String,
    val title: String
) {
    object Library : Screen(
        route = "library",
        title = "Library"
    )

    object Reader : Screen(
        route = "reader/{bookId}",
        title = "Reader"
    ) {
        fun createRoute(bookId: String): String = "reader/$bookId"
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Library.route
    ) {
        composable(route = Screen.Library.route) {
            LibraryScreen(
                openBook = { bookId ->
                    navController.navigate(Screen.Reader.createRoute(bookId))
                }
            )
        }


        composable(route = Screen.Reader.route) {
            val activity = LocalContext.current as ComponentActivity
            val readerViewModel: ReaderViewModel = hiltViewModel(activity)

            ReaderScreen(readerViewModel = readerViewModel)
        }
    }
}
