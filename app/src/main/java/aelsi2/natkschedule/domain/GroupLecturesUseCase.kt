package aelsi2.natkschedule.domain

import aelsi2.natkschedule.domain.model.GroupedLectureWithState
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.domain.model.SubLecture
import aelsi2.natkschedule.model.Lecture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime

// TODO (возможно) перенести в уровень данных
class GroupLecturesUseCase(
    private val getLectureState: GetGroupedLectureStateUseCase,
) {
    operator fun invoke(
        lectures: List<Lecture>,
        stateCoroutineScope: CoroutineScope
    ): List<GroupedLectureWithState>{
        val groupedLectures = ArrayList<GroupedLectureWithState>()
        val similarLectures = ArrayList<Lecture>()
        for (i in 0..lectures.lastIndex) {
            val lecture = lectures[i]
            if (similarLectures.isNotEmpty()) {
                if (
                    similarLectures.first().date != lecture.date ||
                    similarLectures.first().startTime != lecture.startTime ||
                    similarLectures.first().endTime != lecture.endTime ||
                    similarLectures.first().disciplineName != lecture.disciplineName
                ) {
                    groupedLectures.add(createGroupedLecture(
                        similarLectures,
                        groupedLectures.lastOrNull(),
                        stateCoroutineScope
                    ))
                    similarLectures.clear()
                }
            }
            similarLectures.add(lecture)
        }
        if (similarLectures.isNotEmpty()) {
            groupedLectures.add(createGroupedLecture(
                similarLectures,
                groupedLectures.lastOrNull(),
                stateCoroutineScope
            ))
        }
        return groupedLectures
    }
    private fun createGroupedLecture(
        lectures: List<Lecture>,
        previous: GroupedLectureWithState?,
        stateCoroutineScope: CoroutineScope
    ): GroupedLectureWithState {
        if (lectures.isEmpty()) {
            throw IllegalArgumentException("Список лекций не должен быть пустым.")
        }
        val disciplineName: String = lectures.first().disciplineName
        val date: LocalDate = lectures.first().date
        val startTime: LocalTime? = lectures.first().startTime
        val endTime: LocalTime? = lectures.first().endTime
        val breakStartTime: LocalTime? = lectures.first().breakStartTime
        val breakEndTime: LocalTime? = lectures.first().breakEndTime

        val subLectures = lectures.map {
            SubLecture(
                it.teacher,
                it.classroom,
                it.group,
                it.subgroupNumber
            )
        }

        val groupedLectureWithState = GroupedLectureWithState(
            disciplineName,
            date,
            startTime,
            endTime,
            breakStartTime,
            breakEndTime,
            subLectures
        )
        val state: StateFlow<LectureState> = getLectureState(
            groupedLectureWithState, previous
        ).stateIn(
            scope = stateCoroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LectureState.HasNotStarted
        )
        groupedLectureWithState.state = state

        return groupedLectureWithState
    }
}