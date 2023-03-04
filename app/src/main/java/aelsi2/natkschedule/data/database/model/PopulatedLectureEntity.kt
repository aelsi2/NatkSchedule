package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Lecture
import androidx.room.Embedded
import androidx.room.Relation

data class PopulatedLectureEntity(
    @Embedded val lectureEntity : LectureEntity,
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
) {
    fun toLecture() = Lecture(
        lectureEntity.lectureDisciplineName,
        lectureEntity.lectureDate,
        lectureEntity.lectureStartTime,
        lectureEntity.lectureEndTime,
        teacherEntity.toTeacher(),
        classroomEntity.toClassroom(),
        groupEntity.toGroup(),
        lectureEntity.lectureSubgroupNumber,
        lectureEntity.lectureBreakStartTime,
        lectureEntity.lectureEndTime
    )
}
