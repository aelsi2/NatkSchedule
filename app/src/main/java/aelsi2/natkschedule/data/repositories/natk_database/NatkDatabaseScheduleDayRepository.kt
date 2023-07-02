package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.natk_database.NatkDatabase
import aelsi2.natkschedule.data.natk_database.NatkDatabaseDataParser
import aelsi2.natkschedule.data.natk_database.map
import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.model.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class NatkDatabaseScheduleDayRepository(
    private val database: NatkDatabase,
    private val parser: NatkDatabaseDataParser,
    private val timeManager: TimeManager,
) :
    ScheduleDayRepository {
    override suspend fun getDays(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<ScheduleDay>> {
        val currentDate = timeManager.currentCollegeLocalDate
        if (toDate < currentDate) {
            return Result.success(listOf())
        }
        val fromDateCorrected = if (fromDate < currentDate) {
            currentDate
        } else fromDate
        return database.tryWithConnection(
            "Во время получения расписания для ${identifier.type} ${identifier.stringId} произошла ошибка."
        ) { connection ->
            val sqlResultSet = connection.executeGetLecturesQuery(fromDateCorrected, toDate, identifier)
            val results = sqlResultSet.map {
                ResultRow(
                    getInt("para"),
                    getString("disciplina"),
                    getString("data"),
                    getString("vremya"),
                    getString("prepod"),
                    getString("auditoria"),
                    getString("gruppa"),
                    getInt("podgruppa"),
                    getInt("kurs"),
                    getString("shift")
                )
            }.asSequence()
            val days = ArrayList<ScheduleDay>()
            var currentDayLectures = ArrayList<Lecture>()
            var currentLectureData = ArrayList<LectureData>()
            for ((current, next) in results.zip(results.drop(1) + listOf(null))) {
                val lectureData = parser.parseLectureData(
                    current.teacherName,
                    current.classroomName,
                    current.groupName,
                    current.groupProgramName,
                    current.groupYear,
                    current.subgroupIndex
                )
                val sameDay = parser.compareDays(current.stringDate, next?.stringDate)
                val sameLecture = parser.compareLectures(
                    rawIndex1 = current.index,
                    rawTime1 = current.stringTime,
                    rawIndex2 = next?.index ?: 0,
                    rawTime2 = next?.stringTime
                )
                val sameDiscipline = parser.compareDisciplines(current.disciplineName, next?.disciplineName)
                currentLectureData.add(lectureData)
                if (!sameLecture || !sameDay || !sameDiscipline) {
                    val lecture = parser.parseLecture(
                        current.index, current.stringTime, current.disciplineName, currentLectureData
                    )
                    if (lecture != null) {
                        currentDayLectures.add(lecture)
                    }
                    currentLectureData = ArrayList()
                }
                if (!sameDay) {
                    val day = parser.parseScheduleDay(current.stringDate, currentDayLectures)
                    if (day != null) {
                        days.add(day)
                    }
                    currentDayLectures = ArrayList()
                }
            }
            return@tryWithConnection days
        }
    }

    @Throws(SQLException::class)
    private fun Connection.executeGetLecturesQuery(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier
    ): ResultSet = prepareStatement(
        """
            SELECT `para`, `disciplina`, `data`, `vremya`, `prepod`, `auditoria`, `gruppa`, `podgruppa`, `kurs`, `shift`
            FROM `pl4453-mobile`.`1c_shedule`
            WHERE ${
            when (identifier.type) {
                ScheduleType.Group -> "`gruppa` = ?"
                ScheduleType.Teacher -> "`prepod` = ?"
                ScheduleType.Classroom -> "`auditoria` = ?"
            }
        } and ? <= data and data <= ?
            ORDER BY `data` ASC, `vremya` ASC, `para` ASC, `podgruppa` ASC, `disciplina` ASC
        """
    ).apply {
        setString(1, identifier.stringId)
        setString(2, startDate.toString())
        setString(3, endDate.toString())
    }.executeQuery()

    data class ResultRow(
        val index: Int,
        val disciplineName: String?,
        val stringDate: String?,
        val stringTime: String?,
        val teacherName: String?,
        val classroomName: String?,
        val groupName: String?,
        val subgroupIndex: Int,
        val groupYear: Int,
        val groupProgramName: String?,
    )
}