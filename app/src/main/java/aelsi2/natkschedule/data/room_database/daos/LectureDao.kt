package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.*
import androidx.room.*
import java.time.LocalDate

@Dao
interface LectureDao {
    @Transaction
    @Query(
        """
        SELECT * FROM Lectures 
        WHERE lectureDate >= :startDate 
            and lectureDate <= :endDate
            and (:teacherId is null or lectureTeacherId = :teacherId)
            and (:groupId is null or lectureGroupId = :groupId)
            and (:classroomId is null or lectureClassroomId = :classroomId)
        ORDER BY lectureDate ASC, lectureStartTime ASC, lectureSubgroupNumber ASC
    """
    )
    suspend fun getPopulatedLectures(
        startDate: LocalDate,
        endDate: LocalDate,
        teacherId: String?,
        groupId: String?,
        classroomId: String?
    ): List<PopulatedLectureEntity>

    @Upsert
    suspend fun putLectures(lectures: List<LectureEntity>)

    @Query("DELETE FROM Lectures WHERE lectureDate < :date")
    suspend fun deleteLecturesBefore(date: LocalDate)

    @Query(
        """
        DELETE FROM Lectures 
        WHERE lectureDate >= :startDate 
            and lectureDate <= :endDate
            and (:teacherId is null or lectureTeacherId = :teacherId)
            and (:groupId is null or lectureGroupId = :groupId)
            and (:classroomId is null or lectureClassroomId = :classroomId)
    """
    )
    suspend fun deleteLecturesBetween(
        startDate: LocalDate,
        endDate: LocalDate,
        teacherId: String?,
        groupId: String?,
        classroomId: String?
    )

    @Query(
        """
        DELETE FROM Lectures
        WHERE
            lectureGroupId is null
            and lectureClassroomId is null
            and lectureTeacherId is null
    """
    )
    suspend fun deleteInvalidLectures()

    @Query("""
        DELETE FROM Lectures
        WHERE
            not (
                lectureTeacherId in (:teacherIds) or
                lectureClassroomId in (:classroomIds) or
                lectureGroupId in (:groupIds)
            )
    """)
    suspend fun deleteAllExcept(
        teacherIds: List<String>,
        classroomIds: List<String>,
        groupIds: List<String>
    )
}