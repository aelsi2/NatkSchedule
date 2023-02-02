package aelsi2.natkschedule.data.database.model

import aelsi2.natkschedule.model.Group
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groups")
data class GroupEntity(
    @PrimaryKey val groupId : String,
    val groupName : String,
    val groupFieldOfStudy : String,
    val groupYear : Int,
) {
    companion object {
        fun fromGroup(group: Group) = GroupEntity(group.id, group.name, group.fieldOfStudy, group.year)
    }
    fun toGroup() = Group(groupName, groupFieldOfStudy, groupYear, groupId)
}
