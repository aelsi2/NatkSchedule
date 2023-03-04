package aelsi2.natkschedule.data.database.daos

import aelsi2.natkschedule.data.database.model.*
import androidx.room.*
import java.time.LocalDate

@Dao
interface LectureDao {
    @Transaction
    @Query ("""
        SELECT * FROM Lectures 
        WHERE lectureDate >= :startDate 
            and lectureDate <= :endDate
            and (:teacherId is null or lectureTeacherId = :teacherId)
            and (:groupId is null or lectureGroupId = :groupId)
            and (:classroomId is null or lectureClassroomId = :classroomId)
        ORDER BY lectureDate ASC, lectureStartTime ASC, lectureSubgroupNumber ASC""")
    suspend fun getPopulatedLectures(
        startDate : LocalDate,
        endDate : LocalDate,
        teacherId : String?,
        groupId : String?,
        classroomId : String?
    ) : List<PopulatedLectureEntity>

    @Upsert
    suspend fun putLectures(lectures : List<LectureEntity>)

    @Query("DELETE FROM Lectures WHERE lectureDate <= :date")
    suspend fun deleteLecturesBefore(date : LocalDate)

    @Query("DELETE FROM Lectures WHERE lectureDate >= :startDate and lectureDate <= :endDate")
    suspend fun deleteLecturesBetween(startDate : LocalDate, endDate : LocalDate)

    @Query("""
        DELETE FROM Lectures
        WHERE
            lectureGroupId is null
            and lectureClassroomId is null
            and lectureTeacherId is null
    """)
    suspend fun deleteInvalidLectures()
}