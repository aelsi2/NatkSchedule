package aelsi2.natkschedule.ui.components.schedule

import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.components.InfoDialog
import aelsi2.natkschedule.ui.components.InfoDialogRow
import aelsi2.natkschedule.ui.components.InfoDialogState
import aelsi2.natkschedule.ui.lectureIndexText
import aelsi2.natkschedule.ui.lectureStateTextFull
import aelsi2.natkschedule.ui.lectureStateTimeFromStartText
import aelsi2.natkschedule.ui.lectureStateTimeToEndText
import aelsi2.natkschedule.ui.lectureTimeText
import aelsi2.natkschedule.ui.shortDateText
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberLectureInfoDialogState(
    getLectureState: (ScheduleDay, Lecture) -> StateFlow<LectureState>,
    initialData: LectureInfoDialogData? = null,
    scrollState: ScrollState = rememberScrollState()
): LectureInfoDialogState = remember(getLectureState, initialData, scrollState) {
    LectureInfoDialogState(getLectureState, initialData, scrollState)
}

data class LectureInfoDialogData(
    val lecture: Lecture,
    val scheduleDay: ScheduleDay,
)

class LectureInfoDialogState(
    val getLectureState: (ScheduleDay, Lecture) -> StateFlow<LectureState>,
    initialData: LectureInfoDialogData?,
    scrollState: ScrollState,
) : InfoDialogState<LectureInfoDialogData>(initialData, scrollState) {
    fun show(lecture: Lecture, scheduleDay: ScheduleDay) {
        show(LectureInfoDialogData(lecture, scheduleDay))
    }
}

@Composable
fun LectureInfoDialog(
    state: LectureInfoDialogState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onScheduleClick: (identifier: ScheduleIdentifier) -> Unit = {}
) {
    InfoDialog(
        titleText = { it.lecture.discipline.name },
        onDismissRequest = onDismissRequest,
        state = state,
        modifier = modifier
    ) { data ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val lectureState by
            state.getLectureState(data.scheduleDay, data.lecture).collectAsState()
            LectureInfoDialogStatePanel(
                lectureState = lectureState
            )
            LectureInfoDialogDetailsPanel(
                lecture = data.lecture,
                scheduleDay = data.scheduleDay,
                onScheduleClick = onScheduleClick
            )
        }
    }
}

@Composable
private fun LectureInfoDialogStatePanel(
    lectureState: LectureState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = lectureStateTextFull(lectureState),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.run {
                when (lectureState) {
                    is LectureState.OngoingPreBreak -> primary
                    is LectureState.Ongoing -> primary
                    is LectureState.Upcoming -> onSurface
                    else -> onSurfaceVariant
                }
            }
        )
        if (lectureState is LectureState.HasTimeFromStart) {
            Text(
                text = lectureStateTimeFromStartText(lectureState),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (lectureState is LectureState.HasTimeToEnd) {
            Text(
                text = lectureStateTimeToEndText(lectureState),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LectureInfoDialogDetailsPanel(
    lecture: Lecture,
    scheduleDay: ScheduleDay,
    onScheduleClick: (scheduleIdentifier: ScheduleIdentifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        InfoDialogRow(
            mainText = shortDateText(scheduleDay.date),
            leadingIconResource = R.drawable.event_outlined,
        )
        if (lecture.startTime != null && lecture.endTime != null) {
            InfoDialogRow(
                mainText = lectureTimeText(lecture),
                leadingIconResource = R.drawable.time_period,
            )
        }
        if (lecture.index != null) {
            InfoDialogRow(
                mainText = lectureIndexText(lecture.index),
                leadingIconResource = R.drawable.numbers,
            )
        }
        for (lectureData in lecture.data) {
            Divider(modifier = Modifier.padding(horizontal = 8.dp))
            if (lectureData.classroom != null) {
                InfoDialogRow(
                    mainText = lectureData.classroom.fullName,
                    leadingIconResource = R.drawable.door_outlined,
                    showEndArrowIcon = true,
                    onClick = {
                        onScheduleClick(lectureData.classroom.scheduleIdentifier)
                    }
                )
            }
            if (lectureData.teacher != null) {
                InfoDialogRow(
                    mainText = lectureData.teacher.fullName,
                    leadingIconResource = R.drawable.person_outlined,
                    showEndArrowIcon = true,
                    onClick = {
                        onScheduleClick(lectureData.teacher.scheduleIdentifier)
                    }
                )
            }
            if (lectureData.group != null) {
                InfoDialogRow(
                    mainText = if (lectureData.subgroupIndex == null) {
                        lectureData.group.name
                    } else stringResource(
                        R.string.lecture_details_group_subgroup,
                        lectureData.group.name,
                        lectureData.subgroupIndex
                    ),
                    leadingIconResource = R.drawable.people_outlined,
                    showEndArrowIcon = true,
                    onClick = {
                        onScheduleClick(lectureData.group.scheduleIdentifier)
                    }
                )
            }
        }
    }
}