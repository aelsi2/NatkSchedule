package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.DialogProperties

@Composable
fun <TData> rememberInfoDialogState(
    initialData: TData? = null,
    scrollState: ScrollState = rememberScrollState()
): InfoDialogState<TData> = remember(initialData, scrollState) {
    InfoDialogState(initialData, scrollState)
}

open class InfoDialogState<TData>(
    initialData: TData?,
    val scrollState: ScrollState,
) {
    var data: TData? by mutableStateOf(initialData)

    fun show(data: TData) {
        this.data = data
    }

    fun hide() {
        this.data = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <TData> InfoDialog(
    titleText: @Composable (data: TData) -> String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: InfoDialogState<TData> = rememberInfoDialogState(),
    content: @Composable (data: TData) -> Unit = {},
) {
    val data = state.data
    if (data != null) {
        AlertDialog(
            modifier = modifier
                .verticalScroll(state.scrollState)
                .padding(16.dp),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismissRequest
        ) {
            Surface(
                shape = MaterialTheme.shapes.large
            ) {
                Column {
                    InfoPopupTitle(
                        text = titleText(data),
                        onCloseClick = onDismissRequest
                    )
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        content(data)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoPopupTitle(
    text: String,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
) {
    Row(modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .weight(1f)
        )
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = stringResource(R.string.action_back),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}