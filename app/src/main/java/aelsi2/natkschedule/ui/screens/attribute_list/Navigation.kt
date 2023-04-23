package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.screens.attribute_list.favorites.FavoritesListScreen
import aelsi2.natkschedule.ui.screens.schedule.RegularScheduleScreen
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

fun NavController.navigateToFavoriteSchedule(
    route: String,
    scheduleIdentifier: ScheduleIdentifier,
) {
    navigate("$route/schedule/${Uri.encode(scheduleIdentifier.toString())}".trimIndent()) {
        launchSingleTop = true
    }
}

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
    onScheduleBackClick: () -> Unit,
    setUiState: SetUiStateLambda,
    onScheduleError: suspend () -> Unit = {},
    list: @Composable (setUiState: SetUiStateLambda) -> Unit
) {
    navigation(startDestination = "$route/list", route = route) {
        composable("$route/list"){
            list(setUiState)
        }
        composable(
            "$route/schedule/{stringId}",
            arguments = listOf(
                navArgument("stringId") { type = NavType.StringType }
            )
        ) {
            val stringId = it.arguments?.getString("stringId")
            if (stringId != null) {
                RegularScheduleScreen(
                    scheduleIdentifier = ScheduleIdentifier(scheduleType, Uri.decode(stringId)),
                    onBackClick = onScheduleBackClick,
                    onError = onScheduleError,
                    setUiState = setUiState
                )
            }
        }
    }
}

fun NavGraphBuilder.favoritesListTab(
    route: String,
    onScheduleBackClick: () -> Unit,
    onNavigateToSchedule: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    onListError: suspend () -> Unit = {},
    onScheduleError: suspend () -> Unit = {},
) {
    navigation(startDestination = "$route/list", route = route) {
        composable("$route/list"){
            FavoritesListScreen(
                onAttributeClick = onNavigateToSchedule,
                setUiState = setUiState,
                onError = onListError,
            )
        }
        composable(
            "$route/schedule/{stringId}",
            arguments = listOf(
                navArgument("stringId") { type = NavType.StringType }
            )
        ) {
            val stringId = it.arguments?.getString("stringId")
            val scheduleIdentifier = ScheduleIdentifier.fromString(Uri.decode(stringId))
            if (scheduleIdentifier != null) {
                RegularScheduleScreen(
                    scheduleIdentifier = scheduleIdentifier,
                    onBackClick = onScheduleBackClick,
                    setUiState = setUiState,
                    onError = onScheduleError,
                )
            }
        }
    }
}