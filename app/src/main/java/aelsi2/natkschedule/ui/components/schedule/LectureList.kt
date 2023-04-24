package aelsi2.natkschedule.ui.components.schedule

import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.LectureData
import aelsi2.natkschedule.model.ScheduleDay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import android.content.res.Resources
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
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
    modifier: Modifier = Modifier,
    displayTeacher: Boolean = true,
    displayClassroom: Boolean = true,
    displayGroup: Boolean = true,
    displaySubgroup: Boolean = true,
    onLectureClick: ((Lecture) -> Unit)? = null,
    lazyListState: LazyListState =  rememberLazyListState()
) {
    val highlightedCardColors = LectureCardColors.Highlighted.remember()
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        for (day in days) {
            item(key = DateDividerKey(day.date)) {
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
            itemsIndexed(day.lectures, key = { index, _ -> LectureCardKey(day.date, index) }) { _, lecture ->
                val state = getLectureState(day, lecture).collectAsState().value
                val resources = LocalContext.current.resources
                val lectureInfo = remember(lecture) {
                    getLectureInfoString(
                        resources,
                        lecture.data,
                        lecture.startTime,
                        lecture.endTime,
                        displayTeacher,
                        displayClassroom,
                        displayGroup,
                        displaySubgroup
                    )
                }
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
                        is LectureState.Ongoing -> highlightedCardColors
                        is LectureState.OngoingPreBreak -> highlightedCardColors
                        is LectureState.Break -> LectureCardColors.Active
                        is LectureState.UpNext -> LectureCardColors.Active
                        else -> LectureCardColors.Inactive
                    }
                )
            }
        }
    }
}

private fun getLectureInfoString(
    resources: Resources,
    lectureData: List<LectureData>,
    startTime: LocalTime?,
    endTime: LocalTime?,
    displayTeacher: Boolean,
    displayClassroom: Boolean,
    displayGroup: Boolean,
    displaySubgroup: Boolean
): String {
    val infoSb = StringBuilder()
    val attributeSeparator = resources.getString(R.string.lecture_info_attribute_separator)
    if (startTime != null && endTime != null) {
        infoSb.append(
            resources.getString(
                R.string.lecture_info_time,
                startTime.toString(),
                endTime.toString()
            )
        )
    }
    lectureData.forEach {
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

interface LazyListKeyWithDate {
    val date: LocalDate
}

data class DateDividerKey(
    override val date: LocalDate
) : Parcelable, LazyListKeyWithDate {
    private constructor(parcel: Parcel) : this(LocalDate.ofEpochDay(parcel.readLong()))
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(date.toEpochDay())
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<DateDividerKey> {
        override fun createFromParcel(parcel: Parcel): DateDividerKey {
            return DateDividerKey(parcel)
        }

        override fun newArray(size: Int): Array<DateDividerKey?> {
            return arrayOfNulls(size)
        }
    }
}

data class LectureCardKey(
    override val date: LocalDate,
    val index: Int,
) : Parcelable, LazyListKeyWithDate {
    private constructor(parcel: Parcel) : this(LocalDate.ofEpochDay(parcel.readLong()), parcel.readInt())
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(date.toEpochDay())
        parcel.writeInt(index)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<LectureCardKey> {
        override fun createFromParcel(parcel: Parcel): LectureCardKey {
            return LectureCardKey(parcel)
        }

        override fun newArray(size: Int): Array<LectureCardKey?> {
            return arrayOfNulls(size)
        }
    }
}