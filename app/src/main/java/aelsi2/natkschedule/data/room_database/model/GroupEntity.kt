package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.Group
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groups")
data class GroupEntity(
    @PrimaryKey val groupId : String,
    val groupName : String,
    val groupProgramName : String?,
    val groupYear : Int,
) {
    companion object {
        fun fromGroup(group: Group) = GroupEntity(group.id, group.name, group.programName, group.year)
    }
    fun toGroup() = Group(groupName, groupProgramName, groupYear, groupId)
}
