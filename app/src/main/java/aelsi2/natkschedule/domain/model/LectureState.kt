package aelsi2.natkschedule.domain.model

import java.time.Duration

sealed interface LectureState{
    sealed interface HasTimeToEnd : LectureState {
        val timeToEnd: Duration
    }
    sealed interface HasTimeFromStart : LectureState {
        val timeFromStart: Duration
    }
    sealed interface HasTimeBoth: HasTimeFromStart, HasTimeToEnd

    object NotStarted : LectureState
    object Ended : LectureState

    data class Upcoming(
        override val timeToEnd : Duration
    ) : HasTimeToEnd
    data class Ongoing(
        override val timeFromStart: Duration,
        override val timeToEnd: Duration
    ) : HasTimeBoth
    data class OngoingPreBreak(
        override val timeFromStart: Duration,
        override val timeToEnd: Duration
    ) : HasTimeBoth
    data class Break(
        override val timeFromStart: Duration,
        override val timeToEnd: Duration
    ) : HasTimeBoth
}