package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.Teacher
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

class NatkDatabaseDataParser {
    fun parseLecture(
        rawDisciplineName : String?,
        rawDate : String?,
        rawTime : String?,
        rawTeacherName : String?,
        rawClassroomName : String?,
        rawGroupName : String?,
        rawGroupProgramName : String?,
        groupYear : Int,
        rawSubgroupNumber : Int
    ) : Lecture? {
        val disciplineName = rawDisciplineName.blankToNull() ?: return null
        val date = rawDate.toLocalDateNoExcept() ?: return null
        val teacher = parseTeacher(rawTeacherName)
        val classroom = parseClassroom(rawClassroomName)
        val group = parseGroup(rawGroupName, rawGroupProgramName, groupYear)
        if (teacher == null && classroom == null && group == null) {
            return null
        }
        val (startTime, endTime) = parseStartEndTime(rawTime)
        val breakStartTime = getBreakStartTime(startTime, endTime)
        val breakEndTime = getBreakEndTime(startTime, endTime)
        val subgroupNumber : Int? = rawSubgroupNumber.zeroToNull()
        return Lecture(
            disciplineName, date, startTime, endTime, teacher, classroom, group, subgroupNumber, breakStartTime, breakEndTime
        )
    }

    fun parseTeacher(rawName : String?) : Teacher? {
        val fullName = rawName.blankToNull() ?: return null
        val nameParts = fullName.split(TEACHER_NAME_DELIMITER)
        val shortName = when (nameParts.count()) {
            3 -> "${nameParts[0]} ${nameParts[1].first().uppercaseChar()}. ${nameParts[2].first().uppercaseChar()}."
            else -> null
        }
        return Teacher(fullName, shortName, rawName!!)
    }

    fun parseClassroom(rawName : String?) : Classroom? {
        var fullName : String = rawName.blankToNull() ?: return null
        var address : String? = null
        val parts = fullName.split(CLASSROOM_ADDRESS_DELIMITER)
        if (parts.count() == 2) {
            fullName = parts[0]
            address = parts[1]
        }
        val num = CLASSROOM_NUMBER_PATTERN.find(fullName)?.value
        if (num != null) {
            return Classroom(fullName, num, address, rawName!!)
        }
        if (fullName.matches(CLASSROOM_REMOTE_PATTERN)){
            return Classroom(fullName, CLASSROOM_REMOTE_SHORT, address, rawName!!)
        }
        if (fullName.matches(CLASSROOM_GYM_PATTERN)){
            return Classroom(fullName, CLASSROOM_GYM_SHORT, address, rawName!!)
        }
        return Classroom(fullName, null, address, rawName!!)
    }

    fun parseGroup(rawName : String?, rawProgramName : String?, year : Int) : Group? {
        val name: String = rawName.blankToNull() ?: return null
        val programName = rawProgramName.blankToNull() ?: return null
        return Group(name, programName, year, rawName!!)
    }
    private fun parseStartEndTime(string: String?) : Pair<LocalTime?, LocalTime?> {
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

    private fun getBreakStartTime(lectureStartTime : LocalTime?, lectureEndTime : LocalTime?) : LocalTime? {
        lectureStartTime ?: return null
        lectureEndTime ?: return null
        if (ChronoUnit.MINUTES.between(lectureStartTime, lectureEndTime) > HALF_LECTURE_LENGTH_MINUTES * 2) {
            return lectureStartTime.plusMinutes(HALF_LECTURE_LENGTH_MINUTES.toLong())
        }
        return null
    }
    private fun getBreakEndTime(lectureStartTime : LocalTime?, lectureEndTime : LocalTime?) : LocalTime? {
        lectureStartTime ?: return null
        lectureEndTime ?: return null
        if (ChronoUnit.MINUTES.between(lectureStartTime, lectureEndTime) > HALF_LECTURE_LENGTH_MINUTES * 2) {
            return lectureEndTime.minusMinutes(HALF_LECTURE_LENGTH_MINUTES.toLong())
        }
        return null
    }
    private fun String?.blankToNull() : String? {
        return when {
            this.isNullOrBlank() -> null
            else -> this
        }
    }

    private fun String?.toLocalDateNoExcept() : LocalDate? {
        this ?: return null
        return try {
            LocalDate.parse(this)
        } catch (e : DateTimeParseException) { null }
    }

    private fun Int?.zeroToNull() : Int? {
        return when (this) {
            0 -> null
            else -> this
        }
    }
    companion object {
        private const val HALF_LECTURE_LENGTH_MINUTES = 45
        private val TEACHER_NAME_DELIMITER = "\\s+".toRegex()
        private val CLASSROOM_ADDRESS_DELIMITER = "\\s+по\\s+адресу\\s+".toRegex(option = RegexOption.IGNORE_CASE)
        private val CLASSROOM_NUMBER_PATTERN = "№\\d+".toRegex(option = RegexOption.IGNORE_CASE)
        private val CLASSROOM_REMOTE_PATTERN = "\\s*дистанционные\\s+технологии\\s*".toRegex(option = RegexOption.IGNORE_CASE)
        private val CLASSROOM_GYM_PATTERN = "\\s*спортивный\\s+зал\\s*".toRegex(option = RegexOption.IGNORE_CASE)
        private const val CLASSROOM_GYM_SHORT = "Спортзал"
        private const val CLASSROOM_REMOTE_SHORT = "Дистант"
    }
}
