package aelsi2.compose.material3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    inputText: String,
    onTextChange: (String) -> Unit,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    trailingIcon: @Composable () -> Unit = {},
    onSearchClick: () -> Unit = {},
    colors: SearchTopAppBarColors = SearchTopAppBarDefaults.colors,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(colors.backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(windowInsets)
                .padding(horizontal = 4.dp)
                .defaultMinSize(minHeight = 64.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val focusManager = LocalFocusManager.current
            Box {
                CompositionLocalProvider(
                    LocalContentColor provides colors.leadingIconColor,
                    content = leadingIcon
                )
            }
            BasicTextField(
                value = inputText,
                onValueChange = onTextChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSearchClick()
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f, fill = true),
                decorationBox = { innerField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerField()
                        if (inputText.isEmpty()) {
                            Text(
                                text = placeholderText,
                                style = MaterialTheme.typography.bodyLarge.copy(color = colors.placeholderTextColor),
                            )
                        }
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.searchTextColor),
                cursorBrush = SolidColor(colors.searchTextCursorColor)
            )
            Box {
                CompositionLocalProvider(
                    LocalContentColor provides colors.trailingIconColor,
                    content = trailingIcon
                )
            }
        }
        Divider(
            color = colors.outlineColor,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

data class SearchTopAppBarColors(
    val backgroundColor: Color,
    val searchTextColor: Color,
    val searchTextCursorColor: Color,
    val placeholderTextColor: Color,
    val leadingIconColor: Color,
    val trailingIconColor: Color,
    val outlineColor: Color
)

object SearchTopAppBarDefaults {
    val colors: SearchTopAppBarColors
        @Composable
        get() = SearchTopAppBarColors(
            backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
            searchTextColor = MaterialTheme.colorScheme.onSurface,
            searchTextCursorColor = MaterialTheme.colorScheme.primary,
            placeholderTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconColor = MaterialTheme.colorScheme.onSurface,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            outlineColor = MaterialTheme.colorScheme.outline
        )
}