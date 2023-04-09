package aelsi2.natkschedule.domain.model

import java.time.Duration

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