package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.ScheduleDay
import androidx.room.Embedded
import androidx.room.Relation

data class ScheduleDayWithLectures(
    @Embedded val scheduleDayEntity: ScheduleDayEntity,
    @Relation(
        parentColumn = "scheduleDayId",
        entityColumn = "lectureScheduleDayId",
        entity = LectureEntity::class
    )
    val lectures: List<LectureWithDataAndDiscipline>
) {
    fun toScheduleDay() = ScheduleDay(
        scheduleDayEntity.scheduleDayDate,
        lectures.map(LectureWithDataAndDiscipline::toLecture).sortedBy { it.index }
    )
}
