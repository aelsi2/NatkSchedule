package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.ui.TopLevelRoutes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt

@Composable
fun NavBar(
    items: List<ScheduleAppTab>,
    isItemSelected: @Composable (route: String) -> Boolean,
    onItemClick: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth()
    ) {
        val maxFontSizeFactor = remember {
            mutableStateOf<Float?>(null)
        }
        items.forEach { item ->
            val isSelected = isItemSelected(item.route)
            NavigationBarItem(
                modifier = Modifier.fillMaxWidth(),
                icon = {
                    Icon(
                        painter = painterResource(
                            if (isSelected) {
                                item.iconSelected
                            } else {
                                item.iconNormal
                            }
                        ),
                        contentDescription = stringResource(item.title)
                    )
                },
                label = {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (maxFontSizeFactor.value == null) {
                            maxFontSizeFactor.value = getMinMaxLabelFontSizeFactor(
                                items,
                                LocalTextStyle.current,
                                maxWidth * 0.99f
                            )
                        }
                        Text(
                            text = stringResource(id = item.title),
                            style = LocalTextStyle.current + TextStyle(
                                fontSize = LocalTextStyle.current.fontSize * maxFontSizeFactor.value!!
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                selected = isSelected,
                onClick = { onItemClick(item.route) }
            )
        }
    }
}

@Composable
private fun getMinMaxLabelFontSizeFactor(items: List<ScheduleAppTab>, style: TextStyle, maxWidth: Dp): Float {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    return items.map {
        getMaxFontSizeFactor(
            stringResource(it.title),
            style,
            maxWidth,
            density,
            fontFamilyResolver
        )
    }.min()
}

private fun getMaxFontSizeFactor(
    text: String,
    style: TextStyle,
    maxWidth: Dp,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver
): Float {
    var factor = 1f
    while (true) {
        val paragraph = with(density) {
            Paragraph(
                text = text,
                style = style + TextStyle(fontSize = style.fontSize * factor),
                constraints = Constraints(maxWidth = maxWidth.toPx().roundToInt()),
                density = density,
                fontFamilyResolver = fontFamilyResolver,
                maxLines = 1
            )
        }
        if (paragraph.didExceedMaxLines) {
            factor *= 0.9f
        } else {
            return factor
        }
    }
}

enum class ScheduleAppTab(val route: String, val title: Int, val iconNormal: Int, val iconSelected: Int) {
    GROUPS(
        TopLevelRoutes.GROUPS_ROUTE,
        R.string.groups_tab_name,
        R.drawable.people_outlined,
        R.drawable.people_filled
    ),
    FAVORITES(
        TopLevelRoutes.FAVORITES_ROUTE,
        R.string.favorites_tab_name,
        R.drawable.star_outlined,
        R.drawable.star_filled
    ),
    HOME(
        TopLevelRoutes.HOME_ROUTE,
        R.string.home_tab_name,
        R.drawable.home_outlined,
        R.drawable.home_filled
    ),
    TEACHERS(
        TopLevelRoutes.TEACHERS_ROUTE,
        R.string.teachers_tab_name,
        R.drawable.person_outlined,
        R.drawable.person_filled
    ),
    CLASSROOMS(
        TopLevelRoutes.CLASSROOMS_ROUTE,
        R.string.classrooms_tab_name,
        R.drawable.door_outlined,
        R.drawable.door_filled
    )
}

@Preview
@Composable
fun NavBarPreview() {
    ScheduleTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavBar(
                items = ScheduleAppTab.values().toList(),
                isItemSelected = { route ->
                    route == TopLevelRoutes.GROUPS_ROUTE
                },
                onItemClick = { }
            )
        }
    }
}