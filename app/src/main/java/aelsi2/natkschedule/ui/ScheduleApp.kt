package aelsi2.natkschedule.ui

import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.*
import aelsi2.natkschedule.ui.screens.attribute_list.attributeListTab
import aelsi2.natkschedule.ui.screens.attribute_list.classroom_list.ClassroomListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.group_list.GroupListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.teacher_list.TeacherListScreen
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreen
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleApp() {
    val appState = rememberScheduleAppState()
    Scaffold(
        bottomBar = {
            ScheduleNavbar(
                items = ScheduleAppTab.values().toList(),
                isItemSelected = { route ->
                    appState.isAtTopRoute(route)
                },
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
            composable(TopLevelRoutes.FAVORITES_ROUTE){

                Text("Favorites")
            }
            attributeListTab(route = TopLevelRoutes.TEACHERS_ROUTE, scheduleType = ScheduleType.TEACHER) {
                TeacherListScreen(
                    onAttributeClick = {
                        appState.navigateToSchedule(it)
                    }
                )
            }
            attributeListTab(route = TopLevelRoutes.CLASSROOMS_ROUTE, scheduleType = ScheduleType.CLASSROOM) {
                ClassroomListScreen(
                    onAttributeClick = {
                        appState.navigateToSchedule(it)
                    }
                )
            }
            attributeListTab(route = TopLevelRoutes.GROUPS_ROUTE, scheduleType = ScheduleType.GROUP) {
                GroupListScreen(
                    onAttributeClick = {
                        appState.navigateToSchedule(it)
                    }
                )
            }
        }
    }
}

