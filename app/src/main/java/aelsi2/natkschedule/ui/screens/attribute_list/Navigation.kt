package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.TopLevelRoutes
import aelsi2.natkschedule.ui.screens.attribute_list.teacher_list.TeacherListScreen
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreen
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

fun NavController.navigateToSchedule(
    route: String,
    stringId: String,
) {
    navigate("$route/schedule/${Uri.encode(stringId)}".trimIndent()) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.attributeListTab(
    route: String,
    scheduleType: ScheduleType,
    list: @Composable () -> Unit
) {
    navigation(startDestination = "$route/list", route = route) {
        composable("$route/list"){
            list()
        }
        composable(
            "$route/schedule/{stringId}",
            arguments = listOf(
                navArgument("stringId") { type = NavType.StringType }
            )
        ) {
            val stringId = it.arguments?.getString("stringId")
            if (stringId != null) {
                ScheduleScreen(
                    scheduleIdentifier = ScheduleIdentifier(scheduleType, Uri.decode(stringId))
                )
            }
        }
    }
}