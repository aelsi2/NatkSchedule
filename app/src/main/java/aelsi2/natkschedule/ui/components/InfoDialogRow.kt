package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun InfoDialogRow(
    mainText: String,
    leadingIconResource: Int,
    modifier: Modifier = Modifier,
    showEndArrowIcon: Boolean = false,
    onClick: () -> Unit = {},
) {
    ListItem(
        headlineContent = {
            Text(
                text = mainText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = leadingIconResource),
                contentDescription = mainText,
            )
        },
        trailingContent = if (showEndArrowIcon) {
            {
                Icon(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = mainText,
                )
            }
        } else null,
        modifier = modifier.clickable(onClick = onClick)
    )
}