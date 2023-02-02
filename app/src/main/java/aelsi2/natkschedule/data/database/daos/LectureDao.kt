package aelsi2.natkschedule.data.database.daos

import aelsi2.natkschedule.data.database.model.LectureEntity
import aelsi2.natkschedule.data.database.model.PopulatedLectureEntity
import androidx.room.*
import java.time.Instant

@Dao
interface LectureDao {
    @Transaction
    @Query ("SELECT * FROM Lectures ORDER BY lectureStartTime ASC, lectureSubgroupNumber ASC")
    fun getPopulatedLectures() : List<PopulatedLectureEntity>

    @Upsert
    fun putLectures(lectures : List<LectureEntity>)

    @Query("DELETE FROM Lectures WHERE lectureStartTime <= :time")
    fun deleteLecturesBefore(time : Instant)

    @Query("DELETE FROM Lectures WHERE lectureStartTime >= :startTime and lectureEndTime <= :endTime")
    fun deleteLecturesBetween(startTime : Instant, endTime : Instant)

    @Query("""
        DELETE FROM Lectures
        WHERE
            lectureGroupId is null
            and lectureClassroomId is null
            and lectureTeacherId is null
    """)
    fun deleteInvalidLectures()
}