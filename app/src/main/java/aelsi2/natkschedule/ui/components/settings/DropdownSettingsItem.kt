package aelsi2.natkschedule.ui.components.settings

import aelsi2.natkschedule.ui.DISABLED_SETTINGS_ITEM_ALPHA
import aelsi2.natkschedule.ui.DROPDOWN_MAX_HEIGHT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownSettingsItem(
    mainText: String,
    selectedItemText: String,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconResource: Int? = null,
    description: String? = null,
    isEnabled: Boolean = true,
    dropdownContent: @Composable ColumnScope.() -> Unit = {},
) {
    // Костыль; почему-то крашится
    val expand = remember(onExpandedChange) {
        {
            onExpandedChange(true)
        }
    }
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
        trailingContent = {
            Text(selectedItemText)
            DropdownMenu(
                expanded = isExpanded,
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    clippingEnabled = false,
                ),
                onDismissRequest = {
                    onExpandedChange(false)
                },
                modifier = Modifier.requiredSizeIn(maxHeight = DROPDOWN_MAX_HEIGHT),
                content = dropdownContent
            )
        },
        modifier = if (isEnabled) modifier.clickable(onClick = expand) else modifier.alpha(
            DISABLED_SETTINGS_ITEM_ALPHA
        )
    )
}