package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.LectureData
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "LectureData",
    foreignKeys = [
        ForeignKey(
            entity = LectureEntity::class,
            parentColumns = ["lectureId"],
            childColumns = ["lectureDataLectureId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DisciplineEntity::class,
            parentColumns = ["disciplineId"],
            childColumns = ["lectureDataDisciplineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeacherEntity::class,
            parentColumns = ["teacherId"],
            childColumns = ["lectureDataTeacherId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ClassroomEntity::class,
            parentColumns = ["classroomId"],
            childColumns = ["lectureDataClassroomId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["lectureDataGroupId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("lectureDataLectureId", unique = false),
        Index("lectureDataDisciplineId", unique = false),
        Index("lectureDataTeacherId", unique = false),
        Index("lectureDataClassroomId", unique = false),
        Index("lectureDataGroupId", unique = false),
    ]
)
data class LectureDataEntity(
    val lectureDataLectureId: Long,
    val lectureDataDisciplineId: String,
    val lectureDataTeacherId: String?,
    val lectureDataClassroomId: String?,
    val lectureDataGroupId: String?,
    val lectureDataSubgroupIndex: Int?,
) {
    @PrimaryKey(autoGenerate = true) var lectureDataId: Long = 0
    companion object {
        fun fromLectureData(lectureData: LectureData, lectureId: Long) =
            LectureDataEntity(
                lectureId,
                lectureData.discipline.id,
                lectureData.teacher?.id,
                lectureData.classroom?.id,
                lectureData.group?.id,
                lectureData.subgroupIndex
            )
    }
}
