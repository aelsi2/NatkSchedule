package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.DROPDOWN_MAX_HEIGHT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableFilterChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    FilterChip(
        label = {
            Text(text)
        },
        leadingIcon = {
            if (selected) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = "",
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFilterChip(
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    isExpanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    dropdownContent: @Composable ColumnScope.() -> Unit = {}
) {
    Box(modifier = modifier) {
        FilterChip(
            label = {
                Text(text)
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(when {
                        isSelected -> R.drawable.close
                        isExpanded -> R.drawable.dropup_arrow
                        else -> R.drawable.dropdown_arrow
                    }),
                    contentDescription = "",
                    modifier = Modifier.size(18.dp)
                )
            },
            enabled = isEnabled,
            selected = isSelected,
            onClick = {
                if (isSelected) {
                    onClearClick()
                }
                else {
                    setExpanded(true)
                }
            }
        )
        DropdownMenu(
            expanded = isExpanded,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                clippingEnabled = false,
            ),
            onDismissRequest = {
                setExpanded(false)
            },
            modifier = Modifier.requiredSizeIn(maxHeight = DROPDOWN_MAX_HEIGHT),
            content = dropdownContent
        )
    }
}