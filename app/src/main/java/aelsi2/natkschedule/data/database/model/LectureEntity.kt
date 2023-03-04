package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Lecture
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "Lectures")
data class LectureEntity(
    @PrimaryKey val id : String,
    val lectureDisciplineName : String,
    val lectureDate : LocalDate,
    val lectureStartTime : LocalTime?,
    val lectureEndTime : LocalTime?,

    val lectureTeacherId : String?,
    val lectureClassroomId : String?,
    val lectureGroupId : String?,
    val lectureSubgroupNumber : Int?,
    val lectureBreakStartTime : LocalTime?,
    val lectureBreakEndTime : LocalTime?
) {
    companion object{
        fun fromLecture(lecture: Lecture) = LectureEntity(
            lecture.id,
            lecture.disciplineName,
            lecture.date,
            lecture.startTime,
            lecture.endTime,
            lecture.teacher?.id,
            lecture.classroom?.id,
            lecture.group?.id,
            lecture.subgroupNumber,
            lecture.breakStartTime,
            lecture.breakEndTime
        )
    }
}
