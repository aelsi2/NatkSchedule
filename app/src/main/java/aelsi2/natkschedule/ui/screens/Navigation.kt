package aelsi2.natkschedule.ui.screens

import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.TopLevelRoutes
import aelsi2.natkschedule.ui.screens.attribute_list.attributeListTab
import aelsi2.natkschedule.ui.screens.attribute_list.classrooms.ClassroomListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.favoritesListTab
import aelsi2.natkschedule.ui.screens.attribute_list.groups.GroupListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.teachers.TeacherListScreen
import aelsi2.natkschedule.ui.screens.schedule.main.MainScheduleScreen
import aelsi2.natkschedule.ui.screens.settings.SettingsScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.scheduleNavGraph(
    setUiState: SetUiStateLambda,
    navigateBack: () -> Unit,
    navigateToSchedule: (ScheduleIdentifier) -> Unit,
    navigateToFavoriteSchedule: (ScheduleIdentifier) -> Unit,
    navigateToSettings: () -> Unit,
    onListError: suspend () -> Unit = {},
    onScheduleError: suspend () -> Unit = {},
) {
    composable(TopLevelRoutes.SETTINGS) {
        SettingsScreen(
            setUiState = setUiState,
            onBackClick = navigateBack
        )
    }
    composable(TopLevelRoutes.HOME_ROUTE) {
        MainScheduleScreen(
            setUiState = setUiState,
            onScheduleClick = navigateToSchedule,
            onSettingsClick = navigateToSettings,
            onError = onScheduleError,
        )
    }
    favoritesListTab(
        route = TopLevelRoutes.FAVORITES_ROUTE,
        onScheduleBackClick = navigateBack,
        onListScheduleClick = navigateToFavoriteSchedule,
        onScheduleClick = navigateToSchedule,
        onSettingsClick = navigateToSettings,
        onListError = onListError,
        onScheduleError = onScheduleError,
        setUiState = setUiState
    )
    attributeListTab(
        route = TopLevelRoutes.TEACHERS_ROUTE,
        scheduleType = ScheduleType.Teacher,
        onScheduleBackClick = navigateBack,
        onScheduleClick = navigateToSchedule,
        onSettingsClick = navigateToSettings,
        onScheduleError = onScheduleError,
        setUiState = setUiState
    ) { setUiState_inner, onSettingClick_inner ->
        TeacherListScreen(
            setUiState = setUiState_inner,
            onAttributeClick = navigateToSchedule,
            onSettingsClick = onSettingClick_inner,
            onError = onListError
        )
    }
    attributeListTab(
        route = TopLevelRoutes.CLASSROOMS_ROUTE,
        scheduleType = ScheduleType.Classroom,
        setUiState = setUiState,
        onScheduleError = onScheduleError,
        onScheduleBackClick = navigateBack,
        onScheduleClick = navigateToSchedule,
        onSettingsClick = navigateToSettings
    ) { setUiState_inner, onSettingClick_inner ->
        ClassroomListScreen(
            setUiState = setUiState_inner,
            onAttributeClick = navigateToSchedule,
            onSettingsClick = onSettingClick_inner,
            onError = onListError
        )
    }
    attributeListTab(
        route = TopLevelRoutes.GROUPS_ROUTE,
        scheduleType = ScheduleType.Group,
        setUiState = setUiState,
        onScheduleError = onScheduleError,
        onScheduleBackClick = navigateBack,
        onScheduleClick = navigateToSchedule,
        onSettingsClick = navigateToSettings
    ) { setUiState_inner, onSettingClick_inner ->
        GroupListScreen(
            setUiState = setUiState_inner,
            onAttributeClick = navigateToSchedule,
            onSettingsClick = onSettingClick_inner,
            onError = onListError,
        )
    }
}