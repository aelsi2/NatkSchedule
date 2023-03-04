package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Teacher
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Teachers")
data class TeacherEntity(
    @PrimaryKey val teacherId : String,
    val teacherFullName: String,
    val teacherShortName: String?
) {
    companion object {
        fun fromTeacher(teacher: Teacher) =
            TeacherEntity(teacher.id, teacher.fullName, teacher.shortName)
    }
    fun toTeacher() = Teacher(teacherFullName, teacherShortName, teacherId)
}
