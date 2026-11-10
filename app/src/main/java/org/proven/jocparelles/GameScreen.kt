package org.proven.jocparelles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class AppScreen {
    HOME,
    PARELLES,
    TIC_TAC_TOE
}

@Composable
fun GameScreen() {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(
                onSelectParelles = { currentScreen = AppScreen.PARELLES },
                onSelectTicTacToe = { currentScreen = AppScreen.TIC_TAC_TOE }
            )
        }
        AppScreen.PARELLES -> {
            ParellesScreen(
                onBackToMenu = { currentScreen = AppScreen.HOME }
            )
        }
        AppScreen.TIC_TAC_TOE -> {
            TicTacToeScreen(
                onBackToMenu = { currentScreen = AppScreen.HOME }
            )
        }
    }
}
