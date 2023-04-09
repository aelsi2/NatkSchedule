package aelsi2.natkschedule.ui.components

import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.LectureData
import aelsi2.natkschedule.model.ScheduleDay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*
import kotlin.math.round

@Composable
fun LectureList(
    days: List<ScheduleDay>,
    getLectureState: ((ScheduleDay, Lecture) -> StateFlow<LectureState>),
    enablePullToRefresh: Boolean,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    displayTeacher: Boolean = true,
    displayClassroom: Boolean = true,
    displayGroup: Boolean = true,
    displaySubgroup: Boolean = true,
    onLectureClick: ((Lecture) -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    onReachedTop: (() -> Unit)? = null,
    onReachedBottom: (() -> Unit)? = null,
) {
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing,
        onRefresh ?: {},
        refreshThreshold = 48.dp,
        refreshingOffset = 48.dp
    )
    val listState = rememberLazyListState()
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clip(RectangleShape)
            .pullRefresh(pullRefreshState, enablePullToRefresh)
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        ) {
            for (day in days) {
                item{
                    DateDivider(
                        dayOfWeekText = DayOfWeek.from(day.date)
                            .getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        dateText = day.date.format(
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.MEDIUM
                            )
                        )
                    )
                }
                for (lecture in day.lectures) {
                    item {
                        val state = getLectureState(day, lecture).collectAsState().value
                        val lectureInfo = getLectureInfoString(
                            lecture.data,
                            lecture.startTime,
                            lecture.endTime,
                            displayTeacher,
                            displayClassroom,
                            displayGroup,
                            displaySubgroup
                        )
                        LectureCard(
                            titleText = lecture.data[0].discipline.name,
                            infoText = lectureInfo,
                            onClick = { onLectureClick?.invoke(lecture) },
                            stateText = when (state) {
                                is LectureState.OngoingPreBreak -> stringResource(R.string.lecture_state_ongoing)
                                is LectureState.Ongoing -> stringResource(R.string.lecture_state_ongoing)
                                is LectureState.Break -> stringResource(R.string.lecture_state_break)
                                else -> null
                            },
                            stateTimerText = when (state) {
                                is LectureState.Ongoing -> stringResource(
                                    R.string.lecture_time_to_end,
                                    state.endsIn.toHumanReadableString()
                                )
                                is LectureState.OngoingPreBreak -> stringResource(
                                    R.string.lecture_time_to_break,
                                    state.endsIn.toHumanReadableString()
                                )
                                is LectureState.Break -> stringResource(
                                    R.string.lecture_time_to_break_end,
                                    state.endsIn.toHumanReadableString()
                                )
                                is LectureState.UpNext -> stringResource(
                                    R.string.lecture_time_to_start,
                                    state.startsIn.toHumanReadableString()
                                )
                                else -> null
                            },
                            colors = when (state) {
                                is LectureState.Ongoing -> LectureCardColors.Highlighted
                                is LectureState.OngoingPreBreak -> LectureCardColors.Highlighted
                                is LectureState.Break -> LectureCardColors.Active
                                is LectureState.UpNext -> LectureCardColors.Active
                                else -> LectureCardColors.Inactive
                            }
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
    if (onReachedBottom != null) {
        val atBottom = remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val totalItemCount = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemCount - 1
            }
        }
        LaunchedEffect(key1 = atBottom) {
            snapshotFlow {
                atBottom.value
            }.filter { it }.collect {
                onReachedBottom()
            }
        }
    }
    if (onReachedTop != null) {
        val atTop = remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val firstVisibleItemIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index
                firstVisibleItemIndex != null && firstVisibleItemIndex <= 0
            }
        }

        LaunchedEffect(key1 = atTop) {
            snapshotFlow {
                atTop.value
            }.filter { it }.collect {
                onReachedTop()
            }
        }
    }
}

@Composable
private fun getLectureInfoString(
    subLectures: List<LectureData>,
    startTime: LocalTime?,
    endTime: LocalTime?,
    displayTeacher: Boolean,
    displayClassroom: Boolean,
    displayGroup: Boolean,
    displaySubgroup: Boolean
): String {
    val infoSb = StringBuilder()
    val attributeSeparator = stringResource(R.string.lecture_info_attribute_separator)
    if (startTime != null && endTime != null) {
        infoSb.append(
            stringResource(
                R.string.lecture_info_time,
                startTime.toString(),
                endTime.toString()
            )
        )
    }
    subLectures.forEach {
        if (infoSb.isNotEmpty()) {
            infoSb.append('\n')
        }
        var lineHasContent = false
        if (displaySubgroup && it.subgroupIndex != null) {
            infoSb.append(stringResource(R.string.lecture_info_subgroup, it.subgroupIndex))
            lineHasContent = true
        }
        if (displayClassroom && it.classroom != null) {
            if (lineHasContent) {
                infoSb.append(attributeSeparator)
            }
            infoSb.append(it.classroom.shortName)
            lineHasContent = true
        }
        if (displayTeacher && it.teacher != null) {
            if (lineHasContent) {
                infoSb.append(attributeSeparator)
            }
            infoSb.append(it.teacher.shortName)
            lineHasContent = true
        }
        if (displayGroup && it.group != null) {
            if (lineHasContent) {
                infoSb.append(attributeSeparator)
            }
            infoSb.append(it.group.name)
        }
    }
    return infoSb.toString()
}

private fun Duration.toHumanReadableString(): String {
    var timePart = round(toMillis() / 1000f).toLong()
    val seconds = timePart % 60
    timePart = (timePart - seconds) / 60
    val minutes = timePart % 60
    timePart = (timePart - minutes) / 60
    val hours = timePart % 24
    timePart = (timePart - hours) / 24
    val days = timePart
    return days.toDurationPartWithSeparator(isFirst = true) +
            hours.toDurationPartWithSeparator(isFirst = days == 0L) +
            minutes.toDurationPartWithSeparator(isFirst = days + hours == 0L, displayZero = true) +
            seconds.toDurationPartWithSeparator(isFirst = false, displayZero =  true)
}

private fun Long.toDurationPartWithSeparator(
    isFirst: Boolean,
    displayZero: Boolean = false
): String {
    if (isFirst) {
        if (this == 0L && !displayZero) {
            return ""
        }
        return toString()
    }
    return ':' + DecimalFormat("00").format(this)
}