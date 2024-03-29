package aelsi2.natkschedule.model

data class Group(
    val name : String,
    val programName : String,
    val year : Int,
    val id : String = "${name}_${programName}_${year}"
) : ScheduleAttribute {
    override val displayName: String
        get() = name
    override val longDisplayName: String
        get() = name
    override val scheduleIdentifier: ScheduleIdentifier
        get() = ScheduleIdentifier(ScheduleType.Group, id)

    override fun matchesString(string: String): Boolean =
        string.isEmpty() || name.contains(
            other = string,
            ignoreCase = true
        ) || programName.contains(
            other = string,
            ignoreCase = true
        ) || year.toString().contains(
            other = string,
            ignoreCase = true
        )
}