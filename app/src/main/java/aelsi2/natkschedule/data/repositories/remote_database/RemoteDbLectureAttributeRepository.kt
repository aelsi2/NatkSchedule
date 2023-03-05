package aelsi2.natkschedule.data.repositories.remote_database

import aelsi2.natkschedule.data.repositories.LectureAttributeRepository
import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class RemoteDbLectureAttributeRepository(private val connectionManager: RemoteDbConnectionManager) : LectureAttributeRepository {
    override val syncable: Boolean
        get() = false

    override suspend fun getAttributes(
        keys: List<ScheduleIdentifier>,
        sync: Boolean
    ): Result<List<LectureAttribute>> {
        val teachers = ArrayList<ScheduleIdentifier>()
        val classrooms = ArrayList<ScheduleIdentifier>()
        val groups = ArrayList<ScheduleIdentifier>()
        keys.forEach {
            when (it.type){
                ScheduleType.TEACHER -> teachers.add(it)
                ScheduleType.CLASSROOM -> classrooms.add(it)
                ScheduleType.GROUP -> groups.add(it)
            }
        }
        val results = connectionManager.tryWithConnections(
            "An error occurred while trying to fetch a list of attributes.",
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.TEACHER, teachers)
                sqlResultSet.map {
                    parseTeacher(getString("prepod"))
                }
            },
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.CLASSROOM, classrooms)
                sqlResultSet.map {
                    parseClassroom(getString("auditoria"))
                }
            },
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.GROUP, groups)
                sqlResultSet.map {
                    parseGroup(getString("gruppa"), getString("shift"), getInt("kurs"))
                }
            }
        )
        return results.fold(
            onSuccess = {
                Result.success(it.flatten())
            },
            onFailure = {e ->
                Result.failure(e)
            }
        )
    }

    override suspend fun getAttributesOfType(
        type: ScheduleType,
        sync: Boolean
    ): Result<List<LectureAttribute>> = connectionManager.tryWithConnection(
        "An error occurred while trying to fetch all $type attributes."
    ) { connection ->
        val sqlResultSet = connection.executeGetAllAttributesQuery(type)
        return@tryWithConnection when (type) {
            ScheduleType.TEACHER -> sqlResultSet.map {
                parseTeacher(getString("prepod"))
            }
            ScheduleType.CLASSROOM -> sqlResultSet.map {
                parseClassroom(getString("auditoria"))
            }
            ScheduleType.GROUP -> sqlResultSet.map {
                parseGroup(getString("gruppa"), getString("shift"), getInt("kurs"))
            }
        }
    }
    @Throws(SQLException::class)
    private fun Connection.executeGetAllAttributesQuery(
        type : ScheduleType
    ) : ResultSet = prepareStatement(
        """
            SELECT DISTINCT ${
                when (type) {
                    ScheduleType.GROUP -> "`gruppa`, `kurs`, `shift`"
                    ScheduleType.TEACHER -> "`prepod`"
                    ScheduleType.CLASSROOM -> "`auditoria`"
                }
            }
            FROM `1c_shedule`
        """
    ).executeQuery()

    @Throws(SQLException::class)
    private fun Connection.executeGetAttributesByIdQuery(
        type : ScheduleType,
        attributes : List<ScheduleIdentifier>
    ) : ResultSet? {
        if (attributes.isEmpty()){
            return null
        }
        return prepareStatement(
            """
            SELECT ${
                when (type) {
                    ScheduleType.GROUP -> "`gruppa`, `kurs`, `shift`"
                    ScheduleType.TEACHER -> "`prepod`"
                    ScheduleType.CLASSROOM -> "`auditoria`"
                }
            }
            WHERE ${
                when (type) {
                    ScheduleType.GROUP -> "`gruppa`"
                    ScheduleType.TEACHER -> "`prepod`"
                    ScheduleType.CLASSROOM -> "`auditoria`"
                }
            } IN (${attributes.joinToString {"?"}})
            FROM `1c_shedule`
        """
        ).apply {
            for (i in 1..attributes.count()) {
                setString(i, attributes[i - 1].stringId)
            }
        }.executeQuery()
    }
}