package aelsi2.natkschedule.data.repositories.network

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

fun parseStartEndTime(string: String?) : Pair<LocalTime?, LocalTime?> {
    val stringTimes = string?.split(';')
    if (stringTimes.isNullOrEmpty()) {
        return Pair(null, null)
    }
    val startTime = try {
        LocalTime.parse(stringTimes[0])
    } catch (e : DateTimeParseException) { null }
    if (stringTimes.count() < 2) {
        return Pair(startTime, null)
    }
    val endTime = try {
        LocalTime.parse(stringTimes[1])
    } catch (e : DateTimeParseException) { null }
    return Pair(startTime, endTime)
}

val CLASSROOM_ADDRESS_DELIMITER = "\\s+по\\s+адресу\\s+".toRegex(option = RegexOption.IGNORE_CASE)
val CLASSROOM_NUMBER_PATTERN = "№\\d+".toRegex(option = RegexOption.IGNORE_CASE)
val CLASSROOM_REMOTE_PATTERN = "\\s*дистанционные\\s+технологии\\s*".toRegex(option = RegexOption.IGNORE_CASE)
val CLASSROOM_GYM_PATTERN = "\\s*спортивный\\s+зал\\s*".toRegex(option = RegexOption.IGNORE_CASE)
const val CLASSROOM_GYM_SHORT = "Спортзал"
const val CLASSROOM_REMOTE_SHORT = "Дистант"

// Полное название, сокращенное название, адрес
fun parseClassroomName(string : String?): Triple<String?, String?, String?> {
    string ?: return Triple(null, null, null)
    var fullName : String = string
    var address : String? = null
    val parts = string.split(CLASSROOM_ADDRESS_DELIMITER)
    if (parts.count() == 2) {
        fullName = parts[0]
        address = parts[1]
    }
    val num = CLASSROOM_NUMBER_PATTERN.find(string)?.value
    if (num != null) {
        return Triple(fullName, num, address)
    }
    if (fullName.matches(CLASSROOM_REMOTE_PATTERN)){
        return Triple(fullName, CLASSROOM_REMOTE_SHORT, address)
    }
    if (fullName.matches(CLASSROOM_GYM_PATTERN)){
        return Triple(fullName, CLASSROOM_GYM_SHORT, address)
    }
    return Triple(fullName, null, address)
}

val NAME_DELIMITER = "\\s+".toRegex()

fun getTeacherShortName(string : String?): String? {
    string ?: return null
    val nameParts = string.split(NAME_DELIMITER)
    if (nameParts.count() != 3) {
        return null
    }
    return "${nameParts[0]} ${nameParts[1].first().uppercaseChar()}. ${nameParts[2].first().uppercaseChar()}."
}

fun String?.blankToNull() : String? {
    return if (this.isNullOrBlank()) {
        null
    }
    else {
        this
    }
}
fun String?.toIntNoExcept() : Int? {
    this ?: return null
    return try {
        this.toInt()
    } catch (e : java.lang.NumberFormatException) { null }
}

fun String?.toLocalDateNoExcept() : LocalDate? {
    this ?: return null
    return try {
        LocalDate.parse(this)
    } catch (e : DateTimeParseException) { null }
}

fun Int?.zeroToNull() : Int? {
    this ?: return null
    return when (this) {
        0 -> null
        else -> this
    }
}