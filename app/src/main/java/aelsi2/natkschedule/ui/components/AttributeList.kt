package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun AttributeList(
    attributes: List<ScheduleAttribute>,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    enablePullToRefresh: Boolean = true,
    onAttributeClick: ((ScheduleIdentifier) -> Unit)? = null,
    onRefresh: (() -> Unit)? = null
) {
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        onRefresh ?: {},
        refreshThreshold = 48.dp,
        refreshingOffset = 48.dp
    )
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clip(RectangleShape)
            .pullRefresh(pullRefreshState, enablePullToRefresh)
    ) {
        LazyColumn {
            items(attributes) { attribute ->
                AttributeListItem(
                    mainText = when (attribute) {
                        is Teacher -> attribute.fullName
                        is Classroom -> attribute.fullName
                        is Group -> attribute.name
                        else -> ""
                    },
                    supportingText = when (attribute) {
                        is Teacher -> null
                        is Classroom -> attribute.address
                        is Group -> stringResource(R.string.group_info, attribute.year, attribute.programName)
                        else -> null
                    },
                    leadingIconResource = when (attribute) {
                        is Teacher -> R.drawable.person_outlined
                        is Classroom -> R.drawable.door_outlined
                        is Group -> R.drawable.people_outlined
                        else -> R.drawable.question_mark
                    },
                    onClick = {
                        onAttributeClick?.invoke(attribute.scheduleIdentifier)
                    }
                )
            }
        }
        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}