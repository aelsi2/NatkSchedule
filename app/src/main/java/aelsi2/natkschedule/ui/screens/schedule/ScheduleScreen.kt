package aelsi2.natkschedule.ui.screens.schedule

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ScheduleScreen() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenTopAppBar(
    showBackButton : Boolean,
    title: String,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}