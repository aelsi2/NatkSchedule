package aelsi2.natkschedule.ui

import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.model.Lecture
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.round

@Composable
fun lectureInfoDialogTitleString(
    disciplineName: String?
): String = disciplineName ?: stringResource(R.string.lecture_details_placeholder_title)

@Composable
fun lectureIndexString(
    index: Int
): String = stringResource(R.string.lecture_details_index, index)

@Composable
fun lectureIndexDisciplineString(
    index: Int?,
    disciplineName: String?
): String = when {
    index != null && disciplineName != null -> stringResource(
        R.string.lecture_info_format_index_discipline,
        index,
        disciplineName
    )

    index == null && disciplineName != null -> stringResource(R.string.lecture_info_format_discipline, disciplineName)
    index != null && disciplineName == null -> stringResource(R.string.lecture_info_format_index, index.toString())
    else -> ""
}

@Composable
fun dayOfWeekString(
    date: LocalDate
): String = remember(date) {
    DayOfWeek.from(date)
        .getDisplayName(TextStyle.FULL, Locale.getDefault())
}

@Composable
fun mediumDateString(
    date: LocalDate
): String = remember(date) {
    date.format(
        DateTimeFormatter.ofLocalizedDate(
            FormatStyle.MEDIUM
        )
    )
}

@Composable
fun shortDateString(
    date: LocalDate
): String = remember(date) {
    date.format(
        DateTimeFormatter.ofLocalizedDate(
            FormatStyle.SHORT
        )
    )
}

@Composable
fun simpleLectureStateString(
    lectureState: LectureState
): String? = when (lectureState) {
    is LectureState.OngoingPreBreak -> stringResource(R.string.lecture_state_ongoing)
    is LectureState.Ongoing -> stringResource(R.string.lecture_state_ongoing)
    is LectureState.Break -> stringResource(R.string.lecture_state_break)
    else -> null
}

@Composable
fun fullLectureStateString(
    lectureState: LectureState
): String = when (lectureState) {
    is LectureState.NotStarted -> stringResource(R.string.lecture_state_not_started)
    is LectureState.Ended -> stringResource(R.string.lecture_state_ended)
    is LectureState.Upcoming -> stringResource(R.string.lecture_state_next)
    is LectureState.OngoingPreBreak -> stringResource(R.string.lecture_state_ongoing)
    is LectureState.Ongoing -> stringResource(R.string.lecture_state_ongoing)
    is LectureState.Break -> stringResource(R.string.lecture_state_break)
    else -> "???"
}

@Composable
fun lectureStateTimeFromStartString(lectureState: LectureState.HasTimeFromStart): String {
    return stringResource(
        when (lectureState) {
            is LectureState.OngoingPreBreak -> R.string.lecture_time_from_start
            is LectureState.Ongoing -> R.string.lecture_time_from_start
            is LectureState.Break -> R.string.lecture_time_from_break_start
            else -> R.string.lecture_time_from_state_start
        },
        lectureState.timeFromStart.toHumanReadableString()
    )
}

@Composable
fun lectureStateTimeToEndString(lectureState: LectureState.HasTimeToEnd): String {
    return stringResource(
        when (lectureState) {
            is LectureState.OngoingPreBreak -> R.string.lecture_time_to_break
            is LectureState.Ongoing -> R.string.lecture_time_to_end
            is LectureState.Break -> R.string.lecture_time_to_break_end
            is LectureState.Upcoming -> R.string.lecture_time_to_start
            else -> R.string.lecture_time_to_state_end
        },
        lectureState.timeToEnd.toHumanReadableString()
    )
}

@Composable
fun lectureInfoString(
    lecture: Lecture,
    displayTeacher: Boolean = true,
    displayClassroom: Boolean = true,
    displayGroup: Boolean = true,
    displaySubgroup: Boolean = true
): String {
    val resources = LocalContext.current.resources
    return remember(
        resources,
        lecture,
        displayTeacher,
        displayClassroom,
        displayGroup,
        displaySubgroup
    ) {
        makeLectureInfoString(
            resources,
            lecture,
            displayTeacher,
            displayClassroom,
            displayGroup,
            displaySubgroup
        )
    }
}

@Composable
fun lectureTimeString(
    startTime: LocalTime?,
    endTime: LocalTime?
): String {
    val resources = LocalContext.current.resources
    return remember(startTime, endTime) {
        makeLectureTimeString(startTime, endTime, resources)
    }
}

private fun makeLectureTimeString(
    startTime: LocalTime?,
    endTime: LocalTime?,
    resources: Resources
): String = when {
    startTime != null && endTime != null -> resources.getString(
        R.string.lecture_info_start_end_time,
        startTime.toString(),
        endTime.toString()
    )

    startTime != null -> resources.getString(
        R.string.lecture_info_start_time,
        startTime.toString()
    )

    endTime != null -> resources.getString(
        R.string.lecture_info_end_time,
        endTime.toString()
    )

    else -> ""
}

private fun makeLectureInfoString(
    resources: Resources,
    lecture: Lecture,
    displayTeacher: Boolean,
    displayClassroom: Boolean,
    displayGroup: Boolean,
    displaySubgroup: Boolean
): String {
    val infoSb = StringBuilder()
    val attributeSeparator = resources.getString(R.string.lecture_info_attribute_separator)
    if (lecture.startTime != null || lecture.endTime != null) {
        infoSb.append(
            makeLectureTimeString(lecture.startTime, lecture.endTime, resources)
        )
    }
    lecture.data.forEach {
        if (infoSb.isNotEmpty()) {
            infoSb.append('\n')
        }
        var lineHasContent = false
        if (displaySubgroup && it.subgroupIndex != null) {
            infoSb.append(
                resources.getString(
                    R.string.lecture_info_subgroup,
                    it.subgroupIndex
                )
            )
            lineHasContent = true
        }
        if (displayClassroom && it.classroom != null) {
            if (lineHasContent) {
                infoSb.append(attributeSeparator)
            }
            infoSb.append(it.classroom.shortName ?: it.classroom.fullName)
            lineHasContent = true
        }
        if (displayTeacher && it.teacher != null) {
            if (lineHasContent) {
                infoSb.append(attributeSeparator)
            }
            infoSb.append(it.teacher.shortName ?: it.teacher.fullName)
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

fun Duration.toHumanReadableString(): String {
    var timePart = round(toMillis() / 1000f).toLong()
    val seconds = timePart % 60
    timePart = (timePart - seconds) / 60
    val minutes = timePart % 60
    timePart = (timePart - minutes) / 60
    val hours = timePart % 24
    timePart = (timePart - hours) / 24
    val days = timePart
    return days.toDurationPartWithSeparatorString(isFirst = true) +
            hours.toDurationPartWithSeparatorString(isFirst = days == 0L) +
            minutes.toDurationPartWithSeparatorString(
                isFirst = days + hours == 0L,
                displayZero = true
            ) +
            seconds.toDurationPartWithSeparatorString(isFirst = false, displayZero = true)
}

private fun Long.toDurationPartWithSeparatorString(
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