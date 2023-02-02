package aelsi2.natkschedule.data.database.daos

import aelsi2.natkschedule.data.database.model.GroupEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GroupDao {
    @Upsert
    fun putGroups(groups : List<GroupEntity>)

    @Query("SELECT * FROM Groups WHERE groupId in (:ids)")
    fun getGroups(ids : List<String>) : List<GroupEntity>

    @Query("""
        DELETE FROM Groups
        WHERE groupId NOT IN
        (:favorites)
        AND groupId NOT IN
        (
            SELECT lectureGroupId
            FROM Lectures
            WHERE lectureGroupId IS NOT NULL
        )
        """)
    fun deleteUnused(favorites : List<String>)
}