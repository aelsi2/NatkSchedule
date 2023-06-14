package aelsi2.natkschedule.ui.components

import aelsi2.compose.material3.TopAppBar
import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.TopAppBarScrollBehavior
import aelsi2.natkschedule.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalScreenTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_arrow),
                    contentDescription = stringResource(R.string.action_back)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}