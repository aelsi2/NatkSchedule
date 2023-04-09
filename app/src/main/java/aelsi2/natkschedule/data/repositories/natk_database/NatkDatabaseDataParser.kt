package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.model.*
import android.util.Log
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

class NatkDatabaseDataParser {
    fun compareDays(
        rawDate1: String?,
        rawDate2: String?
    ): Boolean {
        val date1 = rawDate1.toLocalDateNoExcept()
        val date2 = rawDate2.toLocalDateNoExcept()
        return date1 == date2
    }

    fun compareLectures(
        rawIndex1: Int,
        rawTime1: String?,
        rawIndex2: Int,
        rawTime2: String?,
    ): Boolean {
        val index1 = rawIndex1.nonPositiveToNull()
        val (startTime1, endTime1) = parseStartEndTime(rawTime1)
        val index2 = rawIndex2.nonPositiveToNull()
        val (startTime2, endTime2) = parseStartEndTime(rawTime2)
        return (index1 == index2) && (startTime1 == startTime2) && (endTime1 == endTime2)
    }

    fun parseScheduleDay(
        rawDate: String?,
        lectures: List<Lecture>
    ): ScheduleDay? {
        val date = rawDate.toLocalDateNoExcept() ?: return null
        return ScheduleDay(
            date,
            lectures
        )
    }

    fun parseLecture(
        rawIndex: Int,
        rawTime: String?,
        lectureData: List<LectureData>
    ): Lecture? {
        if (lectureData.isEmpty()) {
            return null
        }
        val index = rawIndex.nonPositiveToNull()
        val (startTime, endTime) = parseStartEndTime(rawTime)
        val breakStartTime = getBreakStartTime(startTime, endTime)
        val breakEndTime = getBreakEndTime(startTime, endTime)
        return Lecture(
            index,
            startTime,
            endTime,
            breakStartTime,
            breakEndTime,
            lectureData
        )
    }

    fun parseLectureData(
        rawDisciplineName: String?,
        rawTeacherName: String?,
        rawClassroomName: String?,
        rawGroupName: String?,
        rawGroupProgramName: String?,
        groupYear: Int,
        rawSubgroupIndex: Int
    ): LectureData? {
        val discipline = parseDiscipline(rawDisciplineName) ?: return null
        val teacher = parseTeacher(rawTeacherName)
        val classroom = parseClassroom(rawClassroomName)
        val group = parseGroup(rawGroupName, rawGroupProgramName, groupYear)
        val subgroupIndex: Int? = rawSubgroupIndex.nonPositiveToNull()
        return LectureData(
            discipline,
            teacher,
            classroom,
            group,
            subgroupIndex
        )
    }

    fun parseTeacher(rawName: String?): Teacher? {
        val fullName = rawName.blankToNull() ?: return null
        val nameParts = fullName.split(TEACHER_NAME_DELIMITER)
        val shortName = when (nameParts.count()) {
            3 -> "${nameParts[0]} ${nameParts[1].first().uppercaseChar()}. ${
                nameParts[2].first().uppercaseChar()
            }."
            else -> null
        }
        return Teacher(fullName, shortName, rawName!!)
    }

    fun parseClassroom(rawName: String?): Classroom? {
        var fullName: String = rawName.blankToNull() ?: return null
        var address: String? = null
        val parts = fullName.split(CLASSROOM_ADDRESS_DELIMITER)
        if (parts.count() == 2) {
            fullName = parts[0]
            address = parts[1]
        }
        val num = CLASSROOM_NUMBER_PATTERN.find(fullName)?.value
        if (num != null) {
            return Classroom(fullName, num, address, rawName!!)
        }
        if (fullName.matches(CLASSROOM_REMOTE_PATTERN)) {
            return Classroom(fullName, CLASSROOM_REMOTE_SHORT, address, rawName!!)
        }
        if (fullName.matches(CLASSROOM_GYM_PATTERN)) {
            return Classroom(fullName, CLASSROOM_GYM_SHORT, address, rawName!!)
        }
        return Classroom(fullName, null, address, rawName!!)
    }

    fun parseGroup(rawName: String?, rawProgramName: String?, year: Int): Group? {
        val name: String = rawName.blankToNull() ?: return null
        val programName = rawProgramName.blankToNull() ?: return null
        return Group(name, programName, year, rawName!!)
    }

    fun parseDiscipline(rawName: String?): Discipline? {
        val name: String = rawName.blankToNull() ?: return null
        return Discipline(name)
    }

    private fun parseStartEndTime(string: String?): Pair<LocalTime?, LocalTime?> {
        val stringTimes = string?.split(';')
        if (stringTimes.isNullOrEmpty()) {
            return Pair(null, null)
        }
        val startTime = try {
            LocalTime.parse(stringTimes[0])
        } catch (e: DateTimeParseException) {
            null
        }
        if (stringTimes.count() < 2) {
            return Pair(startTime, null)
        }
        val endTime = try {
            LocalTime.parse(stringTimes[1])
        } catch (e: DateTimeParseException) {
            null
        }
        return Pair(startTime, endTime)
    }

    private fun getBreakStartTime(
        lectureStartTime: LocalTime?,
        lectureEndTime: LocalTime?
    ): LocalTime? {
        lectureStartTime ?: return null
        lectureEndTime ?: return null
        if (ChronoUnit.MINUTES.between(
                lectureStartTime,
                lectureEndTime
            ) > HALF_LECTURE_LENGTH_MINUTES * 2
        ) {
            return lectureStartTime.plusMinutes(HALF_LECTURE_LENGTH_MINUTES.toLong())
        }
        return null
    }

    private fun getBreakEndTime(
        lectureStartTime: LocalTime?,
        lectureEndTime: LocalTime?
    ): LocalTime? {
        lectureStartTime ?: return null
        lectureEndTime ?: return null
        if (ChronoUnit.MINUTES.between(
                lectureStartTime,
                lectureEndTime
            ) > HALF_LECTURE_LENGTH_MINUTES * 2
        ) {
            return lectureEndTime.minusMinutes(HALF_LECTURE_LENGTH_MINUTES.toLong())
        }
        return null
    }

    private fun String?.blankToNull(): String? {
        return when {
            this.isNullOrBlank() -> null
            else -> this
        }
    }

    private fun String?.toLocalDateNoExcept(): LocalDate? {
        this ?: return null
        return try {
            LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private fun Int?.nonPositiveToNull(): Int? {
        return when (this) {
            in Int.MIN_VALUE..0 -> null
            else -> this
        }
    }

    companion object {
        private const val HALF_LECTURE_LENGTH_MINUTES = 45
        private val TEACHER_NAME_DELIMITER = "\\s+".toRegex()
        private val CLASSROOM_ADDRESS_DELIMITER =
            "\\s+по\\s+адресу\\s+".toRegex(option = RegexOption.IGNORE_CASE)
        private val CLASSROOM_NUMBER_PATTERN = "№\\d+".toRegex(option = RegexOption.IGNORE_CASE)
        private val CLASSROOM_REMOTE_PATTERN =
            "\\s*дистанционные\\s+технологии\\s*".toRegex(option = RegexOption.IGNORE_CASE)
        private val CLASSROOM_GYM_PATTERN =
            "\\s*спортивный\\s+зал\\s*".toRegex(option = RegexOption.IGNORE_CASE)
        private const val CLASSROOM_GYM_SHORT = "Спортзал"
        private const val CLASSROOM_REMOTE_SHORT = "Дистант"
    }
}
