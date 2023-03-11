package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.TopLevelRoutes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

@Composable
fun ScheduleNavbar(
    items : Iterable<AppTabs>,
    selectedTabRoute : String?,
    onItemClick : (route : String) -> Unit,
    modifier : Modifier = Modifier
) {
    val maxLabelFontSize = rememberMaxFontSize(
        text = items.map { stringResource(it.title) }.maxBy { it.length },
        style = MaterialTheme.typography.labelMedium,
        maxWidth = 64.dp
    )
    val showLabels = maxLabelFontSize >= 8.sp
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
                label = if (showLabels) {
                    {
                        Text(
                            text = stringResource(id = item.title),
                            style = LocalTextStyle.current + TextStyle(fontSize = maxLabelFontSize),
                            maxLines = 1
                        )
                    }
                } else null,
                selected = isSelected,
                onClick = { onItemClick(item.route) }
            )

        }
    }
}
@Composable
private fun rememberMaxFontSize(text: String, style: TextStyle, maxWidth: Dp): TextUnit {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    return remember {
        getMaxFontSize(
            text = text,
            style = style,
            maxWidth = maxWidth,
            density, fontFamilyResolver
        )
    }
}
private fun getMaxFontSize(
    text: String,
    style: TextStyle,
    maxWidth: Dp,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver
): TextUnit{
    var fontSize = style.fontSize
    while (true) {
        val width = with(density) {
            Paragraph(
                text = text,
                style = style + TextStyle(fontSize = fontSize),
                constraints = Constraints(),
                density = density,
                fontFamilyResolver = fontFamilyResolver
            ).minIntrinsicWidth.toDp()
        }
        if (width > maxWidth){
            fontSize *= 0.9f
        }
        else {
            return fontSize
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