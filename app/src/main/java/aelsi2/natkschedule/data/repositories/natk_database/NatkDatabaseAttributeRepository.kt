package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.repositories.AttributeRepository
import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class NatkDatabaseAttributeRepository(private val database: NatkDatabase, private val parser: NatkDatabaseDataParser) : AttributeRepository {
    override suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<LectureAttribute>> {
        val teachers = ArrayList<ScheduleIdentifier>()
        val classrooms = ArrayList<ScheduleIdentifier>()
        val groups = ArrayList<ScheduleIdentifier>()
        ids.forEach {
            when (it.type){
                ScheduleType.TEACHER -> teachers.add(it)
                ScheduleType.CLASSROOM -> classrooms.add(it)
                ScheduleType.GROUP -> groups.add(it)
            }
        }
        val results = database.tryWithConnections(
            "Во время загрузки списка атрибутов по id произошла ошибка.",
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.TEACHER, teachers)
                sqlResultSet.map {
                    parser.parseTeacher(getString("prepod"))
                }
            },
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.CLASSROOM, classrooms)
                sqlResultSet.map {
                    parser.parseClassroom(getString("auditoria"))
                }
            },
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.GROUP, groups)
                sqlResultSet.map {
                    parser.parseGroup(getString("gruppa"), getString("shift"), getInt("kurs"))
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

    override suspend fun getAllAttributes(
        type: ScheduleType
    ): Result<List<LectureAttribute>> = database.tryWithConnection(
        "Во время загрузки всех атрибутов $type произошла ошибка."
    ) { connection ->
        val sqlResultSet = connection.executeGetAllAttributesQuery(type)
        return@tryWithConnection when (type) {
            ScheduleType.TEACHER -> sqlResultSet.map {
                parser.parseTeacher(getString("prepod"))
            }
            ScheduleType.CLASSROOM -> sqlResultSet.map {
                parser.parseClassroom(getString("auditoria"))
            }
            ScheduleType.GROUP -> sqlResultSet.map {
                parser.parseGroup(getString("gruppa"), getString("shift"), getInt("kurs"))
            }
        }
    }
    private suspend fun getAttributesByIdHomogenous(
        ids: List<ScheduleIdentifier>,
        type: ScheduleType
    ) : Result<List<LectureAttribute>> = database.tryWithConnection {
        val sqlResultSet = it.executeGetAttributesByIdQuery(type, ids)
        when (type) {
            ScheduleType.TEACHER -> sqlResultSet.map {
                parser.parseTeacher(getString("prepod"))
            }
            ScheduleType.CLASSROOM -> sqlResultSet.map {
                parser.parseClassroom(getString("auditoria"))
            }
            ScheduleType.GROUP -> sqlResultSet.map {
                parser.parseGroup(getString("gruppa"), getString("shift"), getInt("kurs"))
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