package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.screens.HomeScreen
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleApp() {
    val appState = rememberScheduleAppState()
    Scaffold(

    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = TopLevelDestinations.HOME_ROUTE,
            modifier = Modifier.consumedWindowInsets(innerPadding)
        ) {
            composable(TopLevelDestinations.HOME_ROUTE){
                HomeScreen()
            }
        }
    }
}

