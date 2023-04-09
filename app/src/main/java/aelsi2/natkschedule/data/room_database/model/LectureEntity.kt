package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.ScheduleDay
import androidx.room.*
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "Lectures",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleDayEntity::class,
            parentColumns = ["scheduleDayId"],
            childColumns = ["lectureScheduleDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ Index("lectureScheduleDayId", unique = false) ]
)
data class LectureEntity(
    val lectureScheduleDayId: Long,
    val lectureIndex: Int?,
    val lectureStartTime: LocalTime?,
    val lectureEndTime: LocalTime?,
    val lectureBreakStartTime: LocalTime?,
    val lectureBreakEndTime: LocalTime?,
) {
    @PrimaryKey(autoGenerate = true) var lectureId: Long = 0
    companion object {
        fun fromLecture(lecture: Lecture, scheduleDayId: Long) =
            LectureEntity(
                scheduleDayId,
                lecture.index,
                lecture.startTime,
                lecture.endTime,
                lecture.breakStartTime,
                lecture.breakEndTime
            )
    }
}