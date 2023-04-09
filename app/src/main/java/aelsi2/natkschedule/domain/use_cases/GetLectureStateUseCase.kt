package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.ScheduleDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.ZonedDateTime

class GetLectureStateUseCase(
    private val timeManager: TimeManager
) {
    operator fun invoke(
        scheduleDay: ScheduleDay,
        lecture: Lecture,
    ): Flow<LectureState> = flow {
        val isToday = scheduleDay.date == timeManager.currentCollegeLocalDate
        val previousLecture = if (isToday && lecture.startTime != null) {
            scheduleDay.lectures.lastOrNull {
                it.endTime != null && it.endTime < lecture.startTime
            }
        } else null
        val zonedStartTime = timeManager.localToCollegeZoned(
            if (lecture.startTime == null) {
                scheduleDay.date.atStartOfDay()
            } else {
                scheduleDay.date.atTime(lecture.startTime)
            }
        )
        val zonedEndTime = timeManager.localToCollegeZoned(
            if (lecture.startTime == null) {
                scheduleDay.date.plusDays(1).atStartOfDay()
            } else {
                scheduleDay.date.atTime(lecture.endTime)
            }
        )
        val zonedBreakStartTime = if (lecture.breakStartTime != null)
            timeManager.localToCollegeZoned(scheduleDay.date.atTime(lecture.breakStartTime))
        else null
        val zonedBreakEndTime = if (lecture.breakEndTime != null)
            timeManager.localToCollegeZoned(scheduleDay.date.atTime(lecture.breakEndTime))
        else null
        val zonedPreviousLectureEndTime = if (previousLecture != null)
            timeManager.localToCollegeZoned(
                if (previousLecture.startTime == null) {
                    scheduleDay.date.plusDays(1).atStartOfDay()
                } else {
                    scheduleDay.date.atTime(previousLecture.endTime)
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
                    zonedPreviousLectureEndTime,
                    isToday
                )
            )
        }
    }.conflate().distinctUntilChanged()

    private fun getCurrentState(
        zonedStartTime: ZonedDateTime,
        zonedEndTime: ZonedDateTime,
        zonedBreakStartTime: ZonedDateTime?,
        zonedBreakEndTime: ZonedDateTime?,
        zonedPreviousLectureEndTime: ZonedDateTime?,
        isToday: Boolean
    ): LectureState {
        val hasBreak = zonedBreakStartTime != null && zonedBreakEndTime != null
        val zonedCurrentTime = timeManager.currentCollegeZonedDateTime
        // Проверка на "неначатость"
        if (zonedCurrentTime < zonedStartTime) {
            // Если задано время окончания предыдущей лекции, и она закончилась, значит проверяемая лекция следующая
            if (isToday && (zonedPreviousLectureEndTime == null || zonedCurrentTime > zonedPreviousLectureEndTime)) {
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
            if (zonedCurrentTime <= zonedBreakStartTime!!) {
                return LectureState.OngoingPreBreak(
                    Duration.between(zonedStartTime, zonedCurrentTime),
                    Duration.between(zonedCurrentTime, zonedBreakStartTime)
                )
            }
            //Идет после перерыва
            if (zonedCurrentTime >= zonedBreakEndTime!!) {
                return LectureState.Ongoing(
                    Duration.between(zonedBreakEndTime, zonedCurrentTime),
                    Duration.between(zonedCurrentTime, zonedEndTime)
                )
            }
            // Перерыв
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