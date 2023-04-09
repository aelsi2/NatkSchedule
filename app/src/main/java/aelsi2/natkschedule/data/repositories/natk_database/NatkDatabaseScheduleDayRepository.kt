package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import aelsi2.natkschedule.model.*
import android.util.Log
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class NatkDatabaseScheduleDayRepository(
    private val database: NatkDatabase,
    private val parser: NatkDatabaseDataParser
) :
    ScheduleDayRepository {
    override suspend fun getDays(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<ScheduleDay>> = database.tryWithConnection(
        "An error occurred while trying to fetch schedule for ${identifier.type} ${identifier.stringId}."
    ) { connection ->
        val sqlResultSet = connection.executeGetLecturesQuery(fromDate, toDate, identifier)
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
                current.disciplineName,
                current.teacherName,
                current.classroomName,
                current.groupName,
                current.groupProgramName,
                current.groupYear,
                current.subgroupIndex
            )
            if (lectureData != null){
                currentLectureData.add(lectureData)
            }
            if (!parser.compareLectures(
                    rawIndex1 = current.index,
                    rawTime1 = current.stringTime,
                    rawIndex2 = next?.index ?: 0,
                    rawTime2 = next?.stringTime
                )
            ) {
                val lecture = parser.parseLecture(
                    current.index, current.stringTime, currentLectureData
                )
                if (lecture != null) {
                    currentDayLectures.add(lecture)
                }
                currentLectureData = ArrayList()
            }
            if (!parser.compareDays(current.stringDate, next?.stringDate)) {
                val day = parser.parseScheduleDay(current.stringDate, currentDayLectures)
                if (day != null) {
                    days.add(day)
                }
                currentDayLectures = ArrayList()
            }
        }
        return@tryWithConnection days
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
                ScheduleType.GROUP -> "`gruppa` = ?"
                ScheduleType.TEACHER -> "`prepod` = ?"
                ScheduleType.CLASSROOM -> "`auditoria` = ?"
            }
        } and ? <= data and data <= ?
            ORDER BY `data` ASC, `para` ASC, `podgruppa` ASC
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