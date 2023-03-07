package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.components.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleApp() {
    val appState = rememberScheduleAppState()
    Scaffold(
        bottomBar = {
            ScheduleNavbar(
                items = AppTabs.values().asIterable(),
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
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    DateDivider(
                        dayOfWeekText = "Вторник",
                        dateText = "07.03.2023",
                    )
                    LectureCard(
                        titleText = "МДК.01.03 Разработка мобильных приложений",
                        onClick = {},
                        modifier = Modifier.defaultMinSize(minHeight = 75.dp),
                        infoText = "16:20 – 18:00\n№366 • Климова И. С.",
                        stateText = "Идет",
                        stateTimerText = "До перерыва: 40:31",
                        colors = LectureCardColors.Highlighted
                    )
                }
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

