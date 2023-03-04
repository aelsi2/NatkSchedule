package aelsi2.natkschedule.data.repositories.network

import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalTime

class NetworkDatabaseScheduleRepository(private val databaseManager: NetworkDatabaseManager) :
    ScheduleRepository {
    override val syncable: Boolean
        get() = false

    override suspend fun getSchedule(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier,
        sync: Boolean
    ): Result<List<Lecture>> {
        return withContext(Dispatchers.IO) {
            try {
                databaseManager.openConnection().use { connection ->
                    val sqlResult = connection.prepareStatement(
                        """
                    SELECT `disciplina`, `data`, `vremya`, `prepod`, `auditoria`, `gruppa`, `podgruppa`, `kurs`, `shift`
                    FROM `1c_shedule`
                    WHERE ${
                            when (identifier.type) {
                                ScheduleType.GROUP -> "`gruppa` = ?"
                                ScheduleType.TEACHER -> "`prepod` = ?"
                                ScheduleType.CLASSROOM -> "`auditoria` = ?"
                            }
                        } and ? <= data and data <= ?
                    ORDER BY `data` ASC, `vremya` ASC, `podgruppa` ASC
                    """.trimIndent()
                    ).apply {
                        setString(1, identifier.stringId)
                        setString(2, startDate.toString())
                        setString(3, endDate.toString())
                    }.executeQuery()
                    return@withContext Result.success(readSchedule(sqlResult))
                }
            } catch (e: Throwable) {
                return@withContext Result.failure(e)
            }
        }
    }
    private fun readSchedule(sqlResult : ResultSet) : List<Lecture> {
        val lectures = ArrayList<Lecture>()
        while (sqlResult.next()) {
            try {
                val disciplineName = sqlResult.getString("disciplina").blankToNull() ?: continue
                val date = sqlResult.getString("data").toLocalDateNoExcept() ?: continue
                val (startTime, endTime) = parseStartEndTime(sqlResult.getString("vremya"))
                val breakStartTime = getBreakStartTime(startTime)
                val breakEndTime = getBreakEndTime(endTime)

                val teacher = getTeacher(sqlResult)
                val classroom = getClassroom(sqlResult)
                val group = getGroup(sqlResult)
                val subgroupNumber : Int? = sqlResult.getInt("podgruppa").zeroToNull()

                lectures.add(Lecture(
                    disciplineName,
                    date,
                    startTime,
                    endTime,
                    teacher,
                    classroom,
                    group,
                    subgroupNumber,
                    breakStartTime,
                    breakEndTime
                ))
            } catch (e: SQLException) {
                continue
            }
        }
        return lectures
    }
    private fun getTeacher(sqlResult : ResultSet) : Teacher? {
        val teacherFullName = sqlResult.getString("prepod").blankToNull()
        val teacherShortName = getTeacherShortName(teacherFullName)
        return if (teacherFullName != null) {
            Teacher(teacherFullName, teacherShortName)
        } else { null }
    }
    private fun getGroup(sqlResult : ResultSet) : Group? {
        val groupName : String? = sqlResult.getString("gruppa").blankToNull()
        val groupProgramName : String? = sqlResult.getString("shift").blankToNull()
        if (groupName != null && groupProgramName == null) {
            return null
        }
        val groupYear = sqlResult.getInt("kurs")
        return if (groupName != null) {
            Group(groupName, groupProgramName!!, groupYear)
        } else { null }
    }
    private fun getClassroom(sqlResult: ResultSet) : Classroom? {
        val classroomRawName = sqlResult.getString("auditoria").blankToNull()
        val (classroomFullName, classroomShortName, classroomAddress) = parseClassroomName(
            classroomRawName
        )
        return if (classroomRawName != null) {
            Classroom(classroomFullName!!, classroomShortName, classroomAddress, classroomRawName)
        } else { null }
    }
    private fun getBreakStartTime(lectureStartTime : LocalTime?) : LocalTime? {
        lectureStartTime ?: return null
        return lectureStartTime.plusMinutes(45)
    }
    private fun getBreakEndTime(lectureEndTime : LocalTime?) : LocalTime? {
        lectureEndTime ?: return null
        return lectureEndTime.minusMinutes(45)
    }
}