package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Teacher
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Teachers")
data class TeacherEntity(
    @PrimaryKey val teacherId : String,
    val teacherLastName: String,
    val teacherFirstName: String,
    val teacherMiddleName: String? = null
) {
    companion object {
        fun fromTeacher(teacher: Teacher) =
            TeacherEntity(teacher.id, teacher.lastName, teacher.firstName, teacher.middleName)
    }
    fun toTeacher() = Teacher(teacherLastName, teacherFirstName, teacherMiddleName, teacherId)
}
