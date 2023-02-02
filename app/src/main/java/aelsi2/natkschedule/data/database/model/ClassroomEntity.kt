package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Classroom
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Classrooms")
data class ClassroomEntity(
    @PrimaryKey val classroomId : String,
    val classroomFullName : String,
    val classroomShortName : String = classroomFullName
) {
    companion object {
        fun fromClassroom(classroom: Classroom) =
            ClassroomEntity(classroom.id, classroom.fullName, classroom.shortName)
    }
    fun toClassroom() = Classroom(classroomFullName, classroomShortName, classroomId)
}
