package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.Lecture
import androidx.room.Embedded
import androidx.room.Relation

data class LectureWithData(
    @Embedded val lectureEntity: LectureEntity,
    @Relation(
        parentColumn = "lectureId",
        entityColumn = "lectureDataLectureId",
        entity = LectureDataEntity::class
    )
    val data: List<PopulatedLectureData>
) {
    fun toLecture() = Lecture(
        lectureEntity.lectureIndex,
        lectureEntity.lectureStartTime,
        lectureEntity.lectureEndTime,
        lectureEntity.lectureBreakStartTime,
        lectureEntity.lectureBreakEndTime,
        data.map(PopulatedLectureData::toLectureData)
    )
}
