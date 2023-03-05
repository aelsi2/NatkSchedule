package aelsi2.natkschedule.data.repositories.remote_database

import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class RemoteDbScheduleRepository(private val connectionManager: RemoteDbConnectionManager) :
    ScheduleRepository {
    override val syncable: Boolean
        get() = false
    override suspend fun getSchedule(
        startDate: LocalDate, endDate: LocalDate, identifier: ScheduleIdentifier, sync: Boolean
    ): Result<List<Lecture>> = connectionManager.tryWithConnection(
    "An error occurred while trying to fetch schedule for ${identifier.type} ${identifier.stringId}."
    ) {connection ->
        val sqlResultSet = connection.executeGetLecturesQuery(startDate, endDate, identifier)
        sqlResultSet.map {
            parseLecture(
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
            FROM `1c_shedule`
            WHERE ${
                when (identifier.type) {
                    ScheduleType.GROUP -> "`gruppa` = ?"
                    ScheduleType.TEACHER -> "`prepod` = ?"
                    ScheduleType.CLASSROOM -> "`auditoria` = ?"
                }
            } and ? <= data and data <= ?
            ORDER BY `data` ASC, `vremya` ASC, `podgruppa` ASC
        """
    ).apply {
        setString(1, identifier.stringId)
        setString(2, startDate.toString())
        setString(3, endDate.toString())
    }.executeQuery()
}