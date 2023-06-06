package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.DisciplineEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface DisciplineDao {
    @Query("SELECT * FROM Disciplines")
    suspend fun getAll(): List<DisciplineEntity>

    @Upsert
    suspend fun putDiscipline(discipline: DisciplineEntity)

    @Query("SELECT * FROM Disciplines WHERE disciplineId = :id")
    suspend fun getDiscipline(id: String): DisciplineEntity?

    @Query("SELECT * FROM Disciplines WHERE disciplineId IN (:ids)")
    suspend fun getDisciplines(ids : List<String>) : List<DisciplineEntity>

    @Query("""
        DELETE FROM Disciplines
        WHERE disciplineId NOT IN
        (
            SELECT lectureDisciplineId
            FROM Lectures
            WHERE lectureDisciplineId IS NOT NULL
        )
        """)
    suspend fun deleteUnused()
}