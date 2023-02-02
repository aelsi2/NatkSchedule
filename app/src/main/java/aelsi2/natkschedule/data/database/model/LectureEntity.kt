package aelsi2.natkschedule.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "Lectures")
data class LectureEntity(
    @PrimaryKey val id : String,
    val lectureDisciplineName : String,
    val lectureStartTime : Instant,
    val lectureEndTime : Instant,
    val lectureTeacherId : String?,
    val lectureClassroomId : String?,
    val lectureGroupId : String?,
    val lectureSubgroupNumber : Int?
)
