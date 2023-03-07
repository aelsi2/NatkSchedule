package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.TopLevelRoutes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScheduleNavbar(
    items : Iterable<AppTabs>,
    selectedTabRoute : String?,
    onItemClick : (route : String) -> Unit,
    modifier : Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            val isSelected = selectedTabRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            if (isSelected) {
                                item.iconSelected
                            }
                            else {
                                item.iconNormal
                            }
                        ),
                        contentDescription = stringResource(item.title)
                    )
                },
                label = { Text(stringResource(item.title)) },
                selected = isSelected,
                onClick = { onItemClick(item.route) }
            )
        }
    }
}

enum class AppTabs(val route : String, val title : Int, val iconNormal : Int, val iconSelected : Int) {
    GROUPS(TopLevelRoutes.GROUPS_ROUTE, R.string.groups_tab_name, R.drawable.people_outlined, R.drawable.people_filled),
    FAVOURITES(TopLevelRoutes.FAVORITES_ROUTE, R.string.favourites_tab_name, R.drawable.star_outlined, R.drawable.star_filled),
    HOME(TopLevelRoutes.HOME_ROUTE, R.string.home_tab_name, R.drawable.home_outlined, R.drawable.home_filled),
    TEACHERS(TopLevelRoutes.TEACHERS_ROUTE, R.string.teachers_tab_name, R.drawable.person_outlined, R.drawable.person_filled),
    CLASSROOMS(TopLevelRoutes.CLASSROOMS_ROUTE, R.string.classrooms_tab_name, R.drawable.door_outlined, R.drawable.door_filled)
}

@Preview
@Composable
fun ScheduleNavbarPreview(){
    ScheduleTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScheduleNavbar(
                items = AppTabs.values().asIterable(),
                selectedTabRoute = AppTabs.values().first().route,
                onItemClick = {  }
            )
        }
    }
}