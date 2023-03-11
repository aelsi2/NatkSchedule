package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.repositories.LectureRepository
import aelsi2.natkschedule.model.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class NatkDatabaseLectureRepository(private val database: NatkDatabase, private val parser: NatkDatabaseDataParser) :
    LectureRepository {
    override suspend fun getLectures(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<Lecture>> = database.tryWithConnection(
    "An error occurred while trying to fetch schedule for ${identifier.type} ${identifier.stringId}."
    ) {connection ->
        val sqlResultSet = connection.executeGetLecturesQuery(fromDate, toDate, identifier)
        sqlResultSet.map {
            parser.parseLecture(
                rawDisciplineName = getString("disciplina"),
                rawDate = getString("data"),
                rawTime = getString("vremya"),
                rawTeacherName = getString("prepod"),
                rawClassroomName = getString("auditoria"),
                rawGroupName = getString("gruppa"),
                rawGroupProgramName = getString("shift"),
                groupYear = getInt("kurs"),
                rawSubgroupNumber = getInt("podgruppa")
            )
        }
    }

    @Throws(SQLException::class)
    private fun Connection.executeGetLecturesQuery(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier
    ): ResultSet = prepareStatement(
        """
            SELECT `disciplina`, `data`, `vremya`, `prepod`, `auditoria`, `gruppa`, `podgruppa`, `kurs`, `shift`
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
}