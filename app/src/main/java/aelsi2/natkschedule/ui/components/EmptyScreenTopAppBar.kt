package aelsi2.natkschedule.ui.components

import aelsi2.compose.material3.TopAppBar
import aelsi2.natkschedule.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyScreenTopAppBar(title: String, onSettingsClick: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(title)
        },
        actions = {
            var menuVisible: Boolean by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = {
                    menuVisible = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.more_vertical),
                        contentDescription = stringResource(R.string.action_menu)
                    )
                }
                DropdownMenu(
                    expanded = menuVisible,
                    onDismissRequest = { menuVisible = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        clippingEnabled = false,
                    ),
                    modifier = Modifier.defaultMinSize(200.dp, 50.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.action_settings))
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.settings_outlined),
                                contentDescription = stringResource(R.string.action_settings)
                            )
                        },
                        onClick = {
                            menuVisible = false
                            onSettingsClick()
                        }
                    )
                }
            }
        },
        modifier = modifier
    )
}