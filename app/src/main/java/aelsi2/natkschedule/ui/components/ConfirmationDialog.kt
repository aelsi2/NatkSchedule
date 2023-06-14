package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import androidx.annotation.DrawableRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    titleText: String,
    contentText: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconResource: Int? = null,
    onYesClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = if (iconResource == null) null else {
            {
                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = titleText
                )
            }
        },
        title = {
            Text(titleText)
        },
        text = {
            Text(contentText)
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onYesClick()
            }) {
                Text(stringResource(R.string.action_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.action_no))
            }
        },
        modifier = modifier
    )
}