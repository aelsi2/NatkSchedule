package aelsi2.natkschedule.data.database.daos

import aelsi2.natkschedule.data.database.model.ClassroomEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ClassroomDao {
    @Upsert
    fun putClassrooms(classrooms : List<ClassroomEntity>)

    @Query("SELECT * FROM Classrooms WHERE classroomId IN (:ids)")
    fun getClassrooms(ids : List<String>) : List<ClassroomEntity>

    @Query("""
        DELETE FROM Classrooms
        WHERE classroomId NOT IN
        (:favorites)
        AND classroomId NOT IN
        (
            SELECT lectureClassroomId
            FROM Lectures
            WHERE lectureClassroomId IS NOT NULL
        )
        """)
    fun deleteUnused(favorites : List<String>)
}