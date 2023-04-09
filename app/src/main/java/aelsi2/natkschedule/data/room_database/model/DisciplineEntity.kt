package aelsi2.natkschedule.data.room_database.model

import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Discipline
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Disciplines")
data class DisciplineEntity(
    @PrimaryKey val disciplineId : String,
    val disciplineName : String
) {
    companion object {
        fun fromDiscipline(discipline: Discipline) =
            DisciplineEntity(
                discipline.id,
                discipline.name
            )
    }

    fun toDiscipline() =
        Discipline(
            disciplineName,
            disciplineId
        )
}
