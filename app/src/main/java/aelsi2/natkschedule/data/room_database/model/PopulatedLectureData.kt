package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.LectureData
import androidx.room.Embedded
import androidx.room.Relation

data class PopulatedLectureData(
    @Embedded val lectureDataEntity : LectureDataEntity,
    @Relation(
        parentColumn = "lectureDataDisciplineId",
        entityColumn = "disciplineId"
    )
    val disciplineEntity: DisciplineEntity,
    @Relation(
        parentColumn = "lectureDataClassroomId",
        entityColumn = "classroomId"
    )
    val classroomEntity: ClassroomEntity?,
    @Relation(
        parentColumn = "lectureDataTeacherId",
        entityColumn = "teacherId"
    )
    val teacherEntity: TeacherEntity?,
    @Relation(
        parentColumn = "lectureDataGroupId",
        entityColumn = "groupId",
    )
    val groupEntity: GroupEntity?,
) {
    fun toLectureData() = LectureData(
        disciplineEntity.toDiscipline(),
        teacherEntity?.toTeacher(),
        classroomEntity?.toClassroom(),
        groupEntity?.toGroup(),
        lectureDataEntity.lectureDataSubgroupIndex
    )
}