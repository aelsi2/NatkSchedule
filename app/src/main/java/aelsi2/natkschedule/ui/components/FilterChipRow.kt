package aelsi2.natkschedule.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipRow(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp,
    spacing: Dp = 12.dp,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.horizontalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            content = content
        )

    }
}