package aelsi2.natkschedule.ui

import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.*
import aelsi2.natkschedule.ui.screens.group_list.GroupListScreen
import aelsi2.natkschedule.ui.screens.group_list.GroupListScreenViewModel
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreen
import android.util.Log
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
import org.koin.androidx.compose.koinViewModel
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
            navigation(startDestination = "list", route = TopLevelRoutes.TEACHERS_ROUTE) {
                composable("list"){
                    Text("Teachers")
                }
            }
            navigation(startDestination = "${TopLevelRoutes.GROUPS_ROUTE}/", route = TopLevelRoutes.GROUPS_ROUTE) {
                composable("${TopLevelRoutes.GROUPS_ROUTE}/"){
                    GroupListScreen(
                        onGroupClick = {
                            val stringId = URLEncoder.encode(it.stringId, Charsets.UTF_8.toString())
                            appState.navigate("${TopLevelRoutes.GROUPS_ROUTE}/${stringId}")
                        }
                    )
                }
                composable(
                    "${TopLevelRoutes.GROUPS_ROUTE}/{groupId}",
                    arguments = listOf(
                        navArgument("groupId") { type = NavType.StringType }
                    )
                ) {
                    val groupId = it.arguments?.getString("groupId")
                    if (groupId != null) {
                        ScheduleScreen(
                            scheduleIdentifier = ScheduleIdentifier(ScheduleType.GROUP, groupId)
                        )
                    }
                }
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

