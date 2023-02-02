package aelsi2.natkschedule.data.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class PopulatedLectureEntity(
    @Embedded val lecture : LectureEntity,
    @Relation(
        parentColumn = "lectureClassroomId",
        entityColumn = "classroomId"
    )
    val classroomEntity: ClassroomEntity,
    @Relation(
        parentColumn = "lectureTeacherId",
        entityColumn = "teacherId"
    )
    val teacherEntity: TeacherEntity,
    @Relation(
        parentColumn = "lectureGroupId",
        entityColumn = "groupId"
    )
    val groupEntity: GroupEntity,

)
