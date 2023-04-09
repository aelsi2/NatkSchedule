package aelsi2.natkschedule.data.room_database.daos

import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import aelsi2.natkschedule.data.room_database.model.DisciplineEntity
import aelsi2.natkschedule.data.room_database.model.GroupEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GroupDao {
    @Query("SELECT * FROM Groups")
    suspend fun getAll(): List<GroupEntity>

    @Upsert
    suspend fun putGroup(group: GroupEntity)

    @Query("SELECT * FROM Groups WHERE groupId = :id")
    suspend fun getGroup(id: String): GroupEntity?

    @Query("SELECT * FROM Groups WHERE groupId in (:ids)")
    suspend fun getGroups(ids: List<String>): List<GroupEntity>

    @Query(
        """
        DELETE FROM Groups
        WHERE groupId NOT IN
        (:favorites)
        AND groupId NOT IN
        (
            SELECT lectureDataGroupId
            FROM LectureData
            WHERE lectureDataGroupId IS NOT NULL
        )
        """
    )
    suspend fun deleteUnused(favorites: List<String>)
}