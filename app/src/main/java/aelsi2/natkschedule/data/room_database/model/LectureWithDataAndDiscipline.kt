package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.Lecture
import androidx.room.Embedded
import androidx.room.Relation

data class LectureWithDataAndDiscipline(
    @Embedded val lectureEntity: LectureEntity,
    @Relation(
        parentColumn = "lectureId",
        entityColumn = "lectureDataLectureId",
        entity = LectureDataEntity::class
    )
    val data: List<PopulatedLectureData>,
    @Relation(
        parentColumn = "lectureDisciplineId",
        entityColumn = "disciplineId"
    )
    val discipline: DisciplineEntity
) {
    fun toLecture() = Lecture(
        lectureEntity.lectureIndex,
        lectureEntity.lectureStartTime,
        lectureEntity.lectureEndTime,
        lectureEntity.lectureBreakStartTime,
        lectureEntity.lectureBreakEndTime,
        discipline.toDiscipline(),
        data.map(PopulatedLectureData::toLectureData)
    )
}
