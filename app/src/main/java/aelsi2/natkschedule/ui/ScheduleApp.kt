package aelsi2.natkschedule.ui

import aelsi2.compose.material3.BottomBarScaffold
import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.components.*
import aelsi2.natkschedule.ui.screens.scheduleNavGraph
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.navigation.compose.NavHost
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

typealias SetUiStateLambda = (
    onCurrentTabClick: () -> Unit,
    navigationBarVisible: Boolean
) -> Unit

fun ScheduleAppState.setUiState(
    onCurrentTabClick: () -> Unit = {},
    navigationBarVisible: Boolean = true
) {
    this.onCurrentTabClick = onCurrentTabClick
    this.navigationBarVisible = navigationBarVisible
}

@Composable
fun ScheduleApp(
    appState: ScheduleAppState = rememberScheduleAppState()
) {
    val noInternetMessage = stringResource(R.string.message_no_internet)
    val internetRestoredMessage = stringResource(R.string.message_internet_restored)

    var wasOnline by rememberSaveable { mutableStateOf(true) }
    val isOnline by appState.networkMonitor.isOnline.collectAsState()
    LaunchedEffect(isOnline) {
        if (isOnline){
            if (!wasOnline) {
                wasOnline = true
                appState.showMessage(internetRestoredMessage)
            }
        } else {
            wasOnline = false
            appState.showPersistentMessage(noInternetMessage)
        }
    }
    
    BottomBarScaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (appState.navigationBarVisible) {
                NavBar(
                    items = ScheduleAppTab.values().toList(),
                    isItemSelected = { route ->
                        appState.isAtTopRoute(route)
                    },
                    onItemClick = { route -> appState.navigateToTab(route) },
                )
            }
        }
    ) {
        Box {
            val attributeListErrorMessage =
                stringResource(R.string.message_attribute_list_error)
            val scheduleErrorMessage =
                stringResource(R.string.message_schedule_error)

            suspend fun onListError() {
                if (appState.snackBarHostState.currentSnackbarData == null) {
                    appState.showMessage(attributeListErrorMessage)
                }
            }
            suspend fun onScheduleError() {
                if (appState.snackBarHostState.currentSnackbarData == null) {
                    appState.showMessage(scheduleErrorMessage)
                }
            }

            NavHost(
                navController = appState.navController,
                startDestination = TopLevelRoutes.HOME_ROUTE
            ) {
                scheduleNavGraph(
                    setUiState = appState::setUiState,
                    navigateBack = appState::navigateBack,
                    navigateToSchedule = appState::navigateToSchedule,
                    navigateToFavoriteSchedule = appState::navigateToFavoriteSchedule,
                    navigateToSettings = appState::navigateToSettings,
                    onListError = ::onListError,
                    onScheduleError = ::onScheduleError
                )
            }
            SnackbarHost(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                hostState = appState.snackBarHostState
            )
        }
    }
}