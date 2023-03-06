package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.TeacherEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TeacherDao {
    @Upsert
    suspend fun putTeachers(teachers : List<TeacherEntity>)

    @Query("SELECT * FROM Teachers WHERE teacherId in (:ids)")
    suspend fun getTeachers(ids : List<String>) : List<TeacherEntity>

    @Query("""
        DELETE FROM Teachers
        WHERE teacherId NOT IN
        (:favorites)
        AND teacherId NOT IN
        (
            SELECT lectureTeacherId
            FROM Lectures
            WHERE lectureTeacherId IS NOT NULL
        )
        """)
    suspend fun deleteUnused(favorites : List<String>)
}