package aelsi2.natkschedule.domain

import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.domain.model.GroupedLectureWithState
import aelsi2.natkschedule.domain.model.LectureState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.ZonedDateTime

class GetGroupedLectureStateUseCase(
    private val timeManager: TimeManager
) {
    operator fun invoke(
        lecture: GroupedLectureWithState,
        previousLecture: GroupedLectureWithState? = null
    ): Flow<LectureState> = flow {
        val zonedStartTime = timeManager.localToCollegeZoned(
            if (lecture.startTime == null) {
                lecture.date.atStartOfDay()
            } else {
                lecture.date.atTime(lecture.startTime)
            }
        )
        val zonedEndTime = timeManager.localToCollegeZoned(
            if (lecture.startTime == null) {
                lecture.date.plusDays(1).atStartOfDay()
            } else {
                lecture.date.atTime(lecture.endTime)
            }
        )
        val zonedBreakStartTime = if (lecture.breakStartTime != null)
            timeManager.localToCollegeZoned(lecture.date.atTime(lecture.breakStartTime))
        else null
        val zonedBreakEndTime = if (lecture.breakEndTime != null)
            timeManager.localToCollegeZoned(lecture.date.atTime(lecture.breakEndTime))
        else null
        val zonedPreviousLectureEndTime = if (previousLecture != null)
            timeManager.localToCollegeZoned(
                if (previousLecture.startTime == null) {
                    previousLecture.date.plusDays(1).atStartOfDay()
                } else {
                    previousLecture.date.atTime(previousLecture.endTime)
                }
            )
        else null
        timeManager.runEverySecond {
            emit(
                getCurrentState(
                    zonedStartTime,
                    zonedEndTime,
                    zonedBreakStartTime,
                    zonedBreakEndTime,
                    zonedPreviousLectureEndTime
                )
            )
        }
    }.conflate().distinctUntilChanged()

    private fun getCurrentState(
        zonedStartTime: ZonedDateTime,
        zonedEndTime: ZonedDateTime,
        zonedBreakStartTime: ZonedDateTime?,
        zonedBreakEndTime: ZonedDateTime?,
        zonedPreviousLectureEndTime: ZonedDateTime?
    ): LectureState {
        val hasBreak = zonedBreakStartTime != null && zonedBreakEndTime != null
        val zonedCurrentTime = timeManager.currentCollegeZonedDateTime
        // Проверка на "неначатость"
        if (zonedCurrentTime < zonedStartTime) {
            // Если задано время окончания предыдущей лекции, и она закончилась, значит проверяемая лекция следующая
            if (zonedPreviousLectureEndTime != null && zonedCurrentTime > zonedPreviousLectureEndTime) {
                return LectureState.UpNext(
                    Duration.between(zonedCurrentTime, zonedStartTime)
                )
            }
            // Если предыдущая лекция не задана, то возвращаем просто "не началась"
            return LectureState.HasNotStarted
        }
        // Проверка на окончание
        if (zonedCurrentTime > zonedEndTime) {
            return LectureState.HasEnded
        }
        // Проверка на перерыв
        if (hasBreak) {
            // Идет до перерыва
            if (zonedCurrentTime in zonedStartTime..zonedBreakStartTime!!) {
                return LectureState.OngoingPreBreak(
                    Duration.between(zonedStartTime, zonedCurrentTime),
                    Duration.between(zonedCurrentTime, zonedBreakStartTime)
                )
            }
            // Перерыв
            if (zonedCurrentTime in zonedBreakEndTime!!..zonedStartTime) {
                return LectureState.Ongoing(
                    Duration.between(zonedBreakEndTime, zonedCurrentTime),
                    Duration.between(zonedCurrentTime, zonedEndTime)
                )
            }
            //Идет после перерыва
            return LectureState.Break(
                Duration.between(zonedBreakStartTime, zonedCurrentTime),
                Duration.between(zonedCurrentTime, zonedBreakEndTime)
            )
        }
        // Если никакие условия не выполнены, значит лекция идет без перерыва
        return LectureState.Ongoing(
            Duration.between(zonedStartTime, zonedCurrentTime),
            Duration.between(zonedCurrentTime, zonedEndTime)
        )
    }
}