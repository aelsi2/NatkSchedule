package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ClassroomDao {
    @Query("SELECT * FROM Classrooms")
    suspend fun getAll(): List<ClassroomEntity>

    @Upsert
    suspend fun putClassroom(classroom: ClassroomEntity)

    @Query("SELECT * FROM Classrooms WHERE classroomId = :id")
    suspend fun getClassroom(id: String): ClassroomEntity?

    @Query("SELECT * FROM Classrooms WHERE classroomId IN (:ids)")
    suspend fun getClassrooms(ids: List<String>): List<ClassroomEntity>

    @Query(
        """
        DELETE FROM Classrooms
        WHERE classroomId NOT IN
        (:favorites)
        AND classroomId NOT IN
        (
            SELECT lectureDataClassroomId
            FROM LectureData
            WHERE lectureDataClassroomId IS NOT NULL
        )
        """
    )
    suspend fun deleteUnused(favorites: List<String>)
}