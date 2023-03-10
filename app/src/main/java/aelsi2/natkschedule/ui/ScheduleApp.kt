package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.components.*
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleApp() {
    val appState = rememberScheduleAppState()
    Scaffold(
        bottomBar = {
            ScheduleNavbar(
                items = AppTabs.values().toList(),
                selectedTabRoute = appState.currentTopRoute,
                onItemClick = { route -> appState.navigateToTab(route) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = TopLevelRoutes.HOME_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelRoutes.HOME_ROUTE){
                ScheduleScreen()
            }
            composable(TopLevelRoutes.TEACHERS_ROUTE){
                Text("Teachers")
            }
            composable(TopLevelRoutes.GROUPS_ROUTE){
                Text("Groups")
            }
            composable(TopLevelRoutes.FAVORITES_ROUTE){
                Text("Favorites")
            }
            composable(TopLevelRoutes.CLASSROOMS_ROUTE){
                Text("Classrooms")
            }
        }
    }
}

