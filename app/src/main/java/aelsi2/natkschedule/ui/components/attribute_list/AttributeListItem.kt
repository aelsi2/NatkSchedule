package aelsi2.natkschedule.ui.components.attribute_list

import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.theme.ScheduleTheme
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AttributeListItem(
    mainText: String,
    leadingIconResource: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    supportingText: String? = null
){
    ListItem(
        headlineContent = {
            Text(
                text = mainText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = leadingIconResource),
                contentDescription = mainText,
            )
        },
        trailingContent = {
            Icon(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = mainText,
            )
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Preview
@Composable
fun AttributeListItemPreview() {
    ScheduleTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AttributeListItem(
                mainText = "Брикман Марина Алексеевна",
                supportingText = "vip.brikman@mail.ru",
                leadingIconResource = R.drawable.person_outlined,
                onClick = { }
            )
        }
    }
}