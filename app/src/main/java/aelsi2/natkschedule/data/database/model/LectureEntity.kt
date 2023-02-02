package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.Teacher
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "Lectures")
data class LectureEntity(
    @PrimaryKey val id : String,
    val lectureDiscipline : String,
    val lectureStartTime : Instant,
    val lectureEndTime : Instant,
    val lectureTeacherId : String?,
    val lectureClassroomId : String?,
    val lectureGroupId : String?,
    val lectureSubgroup : Int?
)
