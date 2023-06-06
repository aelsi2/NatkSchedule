package aelsi2.natkschedule.model

data class Classroom(
    val fullName: String,
    val shortName: String? = null,
    val address: String? = null,
    val id: String = fullName
) : ScheduleAttribute {
    override val displayName: String
        get() = shortName ?: fullName
    override val longDisplayName: String
        get() = fullName
    override val scheduleIdentifier: ScheduleIdentifier
        get() = ScheduleIdentifier(ScheduleType.Classroom, id)

    override fun matchesString(string: String): Boolean =
        string.isEmpty() || fullName.contains(
            other = string,
            ignoreCase = true
        ) || shortName?.contains(
            other = string,
            ignoreCase = true
        ) ?: false || address?.contains(
            other = string,
            ignoreCase = true
        ) ?: false
}