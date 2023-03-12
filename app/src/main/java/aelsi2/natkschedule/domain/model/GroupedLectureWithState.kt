package aelsi2.natkschedule.domain.model

import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.Teacher
import kotlinx.coroutines.flow.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class GroupedLectureWithState(
    val disciplineName: String,
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val breakStartTime: LocalTime?,
    val breakEndTime: LocalTime?,
    val subLectures: List<SubLecture>
) {
    lateinit var state: StateFlow<LectureState>
}

sealed interface LectureState{
    object HasNotStarted : LectureState
    object HasEnded : LectureState
    data class UpNext(
        val startsIn : Duration
    ) : LectureState
    data class Ongoing(
        val started: Duration,
        val endsIn: Duration
    ) : LectureState
    data class OngoingPreBreak(
        val started: Duration,
        val endsIn: Duration
    ) : LectureState
    data class Break(
        val started: Duration,
        val endsIn: Duration
    ) : LectureState
}

data class SubLecture(
    val teacher: Teacher?,
    val classroom: Classroom?,
    val group: Group?,
    val subgroupNumber: Int?
)