package aelsi2.natkschedule.ui.components

import aelsi2.compose.material3.TopBarScaffold
import aelsi2.compose.material3.pullrefresh.PullRefreshIndicator
import aelsi2.compose.material3.pullrefresh.PullRefreshState
import aelsi2.compose.material3.pullrefresh.pullRefresh
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun InnerScaffold(
    modifier: Modifier = Modifier,
    pullRefreshState: PullRefreshState? = null,
    nestedScrollConnection: NestedScrollConnection? = null,
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    TopBarScaffold(
        modifier = modifier.run {
            if (pullRefreshState == null) this else pullRefresh(pullRefreshState)
        }.run {
            if (nestedScrollConnection == null) this else nestedScroll(nestedScrollConnection)
        },
        topBar = topBar
    ) {
        Box(modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))) {
            content()
            if (pullRefreshState != null) {
                PullRefreshIndicator(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}