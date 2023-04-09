package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.*
import aelsi2.natkschedule.model.ScheduleIdentifier
import androidx.room.*
import java.time.LocalDate

@Dao
interface ScheduleDao {
    @Transaction
    @Query(
        """
        SELECT * FROM ScheduleDays 
        WHERE scheduleDayDate >= :startDate 
            and scheduleDayDate <= :endDate
            and scheduleDayScheduleIdentifier = :scheduleIdentifier
        ORDER BY scheduleDayDate ASC
        """
    )
    suspend fun getDaysWithLectures(
        startDate: LocalDate,
        endDate: LocalDate,
        scheduleIdentifier: ScheduleIdentifier
    ): List<ScheduleDayWithLectures>

    @Upsert
    suspend fun putDay(day: ScheduleDayEntity): Long

    @Upsert
    suspend fun putLecture(lecture: LectureEntity): Long

    @Upsert
    suspend fun putLectureData(data: LectureDataEntity): Long

    @Query("SELECT * FROM ScheduleDays WHERE rowid = :rowId")
    suspend fun getScheduleDay(rowId: Long): ScheduleDayEntity

    @Query("SELECT * FROM Lectures WHERE rowid = :rowId")
    suspend fun getLecture(rowId: Long): LectureEntity

    @Query("DELETE FROM ScheduleDays WHERE scheduleDayDate < :date")
    suspend fun deleteDaysBefore(date: LocalDate)

    @Query(
        """
        DELETE FROM ScheduleDays 
        WHERE scheduleDayDate = :date
            and scheduleDayScheduleIdentifier = :scheduleIdentifier
        """
    )
    suspend fun deleteDay(
        date: LocalDate,
        scheduleIdentifier: ScheduleIdentifier
    )

    @Query(
        """
        DELETE FROM ScheduleDays
        WHERE
            not scheduleDayScheduleIdentifier in (:scheduleIdentifiers)
        """
    )
    suspend fun deleteAllExcept(
        scheduleIdentifiers: List<ScheduleIdentifier>
    )
}