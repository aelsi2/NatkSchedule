package aelsi2.natkschedule.data.repositories.remote_database

import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class RemoteDbScheduleRepository(private val databaseManager: RemoteDbManager) :
    ScheduleRepository {
    override val syncable: Boolean
        get() = false

    override suspend fun getSchedule(
        startDate: LocalDate, endDate: LocalDate, identifier: ScheduleIdentifier, sync: Boolean
    ): Result<List<Lecture>> {
        return withContext(Dispatchers.IO) {
            try {
                databaseManager.openConnection().use { connection ->
                    val sqlResultSet = executeGetScheduleQuery(connection, startDate, endDate, identifier)
                    return@withContext Result.success(parseLectures(sqlResultSet))
                }
            } catch (e: Throwable) {
                return@withContext Result.failure(e)
            }
        }
    }

    private fun executeGetScheduleQuery(
        connection: Connection,
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier
    ): ResultSet = connection.prepareStatement(
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

    private fun parseLectures(sqlResultSet: ResultSet): List<Lecture> {
        val lectures = ArrayList<Lecture>()
        while (sqlResultSet.next()) {
            try {
                sqlResultSet.apply {
                    val lecture = parseLecture(
                        rawDisciplineName = getString("disciplina"),
                        rawDate = getString("data"),
                        rawTime = getString("vremya"),
                        rawTeacherName = getString("prepod"),
                        rawClassroomName = getString("auditoria"),
                        rawGroupName = getString("gruppa"),
                        rawGroupProgramName = getString("shift"),
                        groupYear = getInt("kurs"),
                        rawSubgroupNumber = getInt("podgruppa")
                    ) ?: return@apply
                    lectures.add(lecture)
                }
            } catch (e: SQLException) {
                continue
            }
        }
        return lectures
    }
}