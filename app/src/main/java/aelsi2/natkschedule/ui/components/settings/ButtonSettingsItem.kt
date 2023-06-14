package aelsi2.natkschedule.ui.components.settings

import aelsi2.natkschedule.ui.DISABLED_SETTINGS_ITEM_ALPHA
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource

@Composable
fun ButtonSettingsItem(
    mainText: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconResource: Int? = null,
    description: String? = null,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    ListItem(
        headlineContent = {
            Text(mainText)
        },
        supportingContent = if (description == null) null else {
            {
                Text(description)
            }
        },
        leadingContent = if (leadingIconResource == null) null else {
            {
                Icon(
                    painter = painterResource(leadingIconResource),
                    contentDescription = mainText
                )
            }
        },
        modifier = if (isEnabled) modifier.clickable(onClick = onClick) else modifier.alpha(
            DISABLED_SETTINGS_ITEM_ALPHA
        )
    )
}