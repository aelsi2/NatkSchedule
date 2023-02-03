package aelsi2.natkschedule.model

data class Group(
    val name : String,
    val programName : String,
    val year : Int,
    override val id : String = "${name}_${programName}_${year}"
    ) : LectureAttribute {
    override fun toScheduleIdentifier() = ScheduleIdentifier(ScheduleType.GROUP, id)
    }
