package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import aelsi2.natkschedule.data.room_database.model.DisciplineEntity
import aelsi2.natkschedule.data.room_database.model.TeacherEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TeacherDao {
    @Query("SELECT * FROM Teachers")
    suspend fun getAll(): List<TeacherEntity>

    @Upsert
    suspend fun putTeacher(teacher: TeacherEntity)

    @Query("SELECT * FROM Teachers WHERE teacherId = :id")
    suspend fun getTeacher(id: String): TeacherEntity?

    @Query("SELECT * FROM Teachers WHERE teacherId in (:ids)")
    suspend fun getTeachers(ids : List<String>) : List<TeacherEntity>

    @Query("""
        DELETE FROM Teachers
        WHERE teacherId NOT IN
        (:favorites)
        AND teacherId NOT IN
        (
            SELECT lectureDataTeacherId
            FROM LectureData
            WHERE lectureDataTeacherId IS NOT NULL
        )
        """)
    suspend fun deleteUnused(favorites : List<String>)
}