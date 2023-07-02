package aelsi2.natkschedule.ui.components.schedule

import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.ui.dayOfWeekString
import aelsi2.natkschedule.ui.lectureIndexDisciplineString
import aelsi2.natkschedule.ui.lectureInfoString
import aelsi2.natkschedule.ui.simpleLectureStateString
import aelsi2.natkschedule.ui.lectureStateTimeToEndString
import aelsi2.natkschedule.ui.mediumDateString
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
fun LectureList(
    scheduleDays: List<ScheduleDay>,
    getLectureState: ((ScheduleDay, Lecture) -> StateFlow<LectureState>),
    modifier: Modifier = Modifier,
    displayTeacher: Boolean = true,
    displayClassroom: Boolean = true,
    displayGroup: Boolean = true,
    displaySubgroup: Boolean = true,
    onLectureClick: (Lecture, ScheduleDay) -> Unit = { _, _ -> },
    lazyListState: LazyListState = rememberLazyListState()
) {
    val highlightedCardColors = LectureCardColors.Highlighted.remember()
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        for (scheduleDay in scheduleDays) {
            item(key = DateDividerKey(scheduleDay.date)) {
                DateDivider(
                    dayOfWeekText = dayOfWeekString(scheduleDay.date),
                    dateText = mediumDateString(scheduleDay.date)
                )
            }
            itemsIndexed(
                scheduleDay.lectures,
                key = { index, _ -> LectureCardKey(scheduleDay.date, index) }) { _, lecture ->
                val state = getLectureState(scheduleDay, lecture).collectAsState().value
                LectureCard(
                    titleText = lectureIndexDisciplineString(lecture.index, lecture.discipline?.name),
                    infoText = lectureInfoString(
                        lecture = lecture,
                        displayTeacher = displayTeacher,
                        displayClassroom = displayClassroom,
                        displayGroup = displayGroup,
                        displaySubgroup = displaySubgroup
                    ),
                    onClick = { onLectureClick(lecture, scheduleDay) },
                    stateText = simpleLectureStateString(state),
                    stateTimerText = if (state is LectureState.HasTimeToEnd) {
                        lectureStateTimeToEndString(state)
                    } else null,
                    colors = when (state) {
                        is LectureState.Ongoing -> highlightedCardColors
                        is LectureState.OngoingPreBreak -> highlightedCardColors
                        is LectureState.Break -> LectureCardColors.Active
                        is LectureState.Upcoming -> LectureCardColors.Active
                        else -> LectureCardColors.Inactive
                    }
                )
            }
        }
    }
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
    private constructor(parcel: Parcel) : this(
        LocalDate.ofEpochDay(parcel.readLong()),
        parcel.readInt()
    )

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