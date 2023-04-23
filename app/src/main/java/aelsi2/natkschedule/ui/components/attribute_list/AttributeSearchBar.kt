package aelsi2.natkschedule.ui.components.attribute_list

import aelsi2.compose.material3.SearchTopAppBar
import aelsi2.natkschedule.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun AttributeSearchBar(
    searchText: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    placeholderText: String = "",
) {
    SearchTopAppBar(
        inputText = searchText,
        onTextChange = onTextChange,
        leadingIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.back_arrow),
                    contentDescription = stringResource(R.string.action_back)
                )
            }
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.action_clear_search_field)
                    )
                }
            }
        },
        placeholderText = placeholderText,
        modifier = modifier
    )
}