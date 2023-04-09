package aelsi2.natkschedule.data.room_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Programs")
data class ProgramEntity(
    @PrimaryKey val programId : String,
    val programName : String
)
