package aelsi2.natkschedule.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

object TopLevelDestinations {
    const val HOME_ROUTE = "home"
    const val FAVORITES_ROUTE = "favorites"
    const val GROUPS_ROUTE = "groups"
    const val TEACHERS_ROUTE = "teachers"
    const val CLASSROOMS_ROUTE = "classrooms"
}

@Composable
fun rememberScheduleAppState(
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    navController: NavHostController = rememberNavController()
) : ScheduleAppState {
    return remember(snackBarHostState, navController) {
        ScheduleAppState(snackBarHostState, navController)
    }
}

class ScheduleAppState(
    val snackBarHostState: SnackbarHostState,
    val navController: NavHostController
) {

}