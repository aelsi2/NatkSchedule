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
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.round

@Composable
fun lectureIndexText(
    index: Int
): String = stringResource(R.string.lecture_details_index, index)

@Composable
fun lectureIndexDisciplineText(
    index: Int?,
    disciplineName: String?
): String? = when {
    index != null && disciplineName != null -> stringResource(
        R.string.lecture_info_format_index_discipline,
        index,
        disciplineName
    )
    index == null && disciplineName != null -> disciplineName
    index != null && disciplineName == null -> index.toString()
    else -> null
}

@Composable
fun dayOfWeekText(
    date: LocalDate
): String = remember(date) {
    DayOfWeek.from(date)
        .getDisplayName(TextStyle.FULL, Locale.getDefault())
}

@Composable
fun mediumDateText(
    date: LocalDate
): String = remember(date) {
    date.format(
        DateTimeFormatter.ofLocalizedDate(
            FormatStyle.MEDIUM
        )
    )
}

@Composable
fun shortDateText(
    date: LocalDate
): String = remember(date) {
    date.format(
        DateTimeFormatter.ofLocalizedDate(
            FormatStyle.SHORT
        )
    )
}

@Composable
fun lectureStateTextSimple(
    lectureState: LectureState
): String? = when (lectureState) {
    is LectureState.OngoingPreBreak -> stringResource(R.string.lecture_state_ongoing)
    is LectureState.Ongoing -> stringResource(R.string.lecture_state_ongoing)
    is LectureState.Break -> stringResource(R.string.lecture_state_break)
    else -> null
}

@Composable
fun lectureStateTextFull(
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
fun lectureStateTimeFromStartText(lectureState: LectureState.HasTimeFromStart): String {
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
fun lectureStateTimeToEndText(lectureState: LectureState.HasTimeToEnd): String {
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
fun lectureInfoText(
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
        makeLectureInfoText(
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
fun lectureTimeText(
    lecture: Lecture
): String {
    val resources = LocalContext.current.resources
    return remember(resources, lecture) {
        makeLectureTimeText(resources, lecture)
    }
}

private fun makeLectureTimeText(
    resources: Resources,
    lecture: Lecture,
): String = resources.getString(
    R.string.lecture_info_time,
    lecture.startTime.toString(),
    lecture.endTime.toString()
)

private fun makeLectureInfoText(
    resources: Resources,
    lecture: Lecture,
    displayTeacher: Boolean,
    displayClassroom: Boolean,
    displayGroup: Boolean,
    displaySubgroup: Boolean
): String {
    val infoSb = StringBuilder()
    val attributeSeparator = resources.getString(R.string.lecture_info_attribute_separator)
    if (lecture.startTime != null && lecture.endTime != null) {
        infoSb.append(
            makeLectureTimeText(resources, lecture)
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
    return days.toDurationPartWithSeparator(isFirst = true) +
            hours.toDurationPartWithSeparator(isFirst = days == 0L) +
            minutes.toDurationPartWithSeparator(isFirst = days + hours == 0L, displayZero = true) +
            seconds.toDurationPartWithSeparator(isFirst = false, displayZero = true)
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