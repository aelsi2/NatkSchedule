package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.natk_database.NatkDatabase
import aelsi2.natkschedule.data.natk_database.NatkDatabaseDataParser
import aelsi2.natkschedule.data.natk_database.map
import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class NatkDatabaseScheduleAttributeRepository(private val database: NatkDatabase, private val parser: NatkDatabaseDataParser) : ScheduleAttributeRepository {
    override suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<ScheduleAttribute>> {
        val teachers = ArrayList<ScheduleIdentifier>()
        val classrooms = ArrayList<ScheduleIdentifier>()
        val groups = ArrayList<ScheduleIdentifier>()
        ids.forEach {
            when (it.type){
                ScheduleType.Teacher -> teachers.add(it)
                ScheduleType.Classroom -> classrooms.add(it)
                ScheduleType.Group -> groups.add(it)
            }
        }
        val results = database.tryWithConnections(
            "Во время загрузки списка атрибутов по id произошла ошибка.",
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.Teacher, teachers)
                sqlResultSet.map {
                    parser.parseTeacher(getString("prepod"))
                }
            },
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.Classroom, classrooms)
                sqlResultSet.map {
                    parser.parseClassroom(getString("auditoria"))
                }
            },
            {
                val sqlResultSet = it.executeGetAttributesByIdQuery(ScheduleType.Group, groups)
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

    override suspend fun getAttributeById(
        id: ScheduleIdentifier
    ): Result<ScheduleAttribute?> = database.tryWithConnection(
        "Во время загрузки атрибута по id произошла ошибка."
    ) {
        val sqlResultSet = it.executeGetAttributesByIdQuery(id.type, listOf(id))
        sqlResultSet.map {
            when (id.type) {
                ScheduleType.Teacher -> parser.parseTeacher(getString("prepod"))
                ScheduleType.Classroom -> parser.parseClassroom(getString("auditoria"))
                ScheduleType.Group -> parser.parseGroup(
                    getString("gruppa"), getString("shift"), getInt("kurs")
                )
            }
        }.firstOrNull()
    }

    override suspend fun getAllAttributes(
        type: ScheduleType
    ): Result<List<ScheduleAttribute>> = database.tryWithConnection(
        "Во время загрузки всех атрибутов $type произошла ошибка."
    ) { connection ->
        val sqlResultSet = connection.executeGetAllAttributesQuery(type)
        return@tryWithConnection when (type) {
            ScheduleType.Teacher -> sqlResultSet.map {
                parser.parseTeacher(getString("prepod"))
            }
            ScheduleType.Classroom -> sqlResultSet.map {
                parser.parseClassroom(getString("auditoria"))
            }
            ScheduleType.Group -> sqlResultSet.map {
                parser.parseGroup(getString("gruppa"), getString("shift"), getInt("kurs"))
            }
        }
    }
    private suspend fun getAttributesByIdHomogenous(
        ids: List<ScheduleIdentifier>,
        type: ScheduleType
    ) : Result<List<ScheduleAttribute>> = database.tryWithConnection {
        val sqlResultSet = it.executeGetAttributesByIdQuery(type, ids)
        when (type) {
            ScheduleType.Teacher -> sqlResultSet.map {
                parser.parseTeacher(getString("prepod"))
            }
            ScheduleType.Classroom -> sqlResultSet.map {
                parser.parseClassroom(getString("auditoria"))
            }
            ScheduleType.Group -> sqlResultSet.map {
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
                    ScheduleType.Group -> "`gruppa`, `kurs`, `shift`"
                    ScheduleType.Teacher -> "`prepod`"
                    ScheduleType.Classroom -> "`auditoria`"
                }
            }
            FROM `pl4453-mobile`.`1c_shedule`
            ORDER BY ${
                when (type) {
                    ScheduleType.Group -> "`gruppa`"
                    ScheduleType.Teacher -> "`prepod`"
                    ScheduleType.Classroom -> "`auditoria`"
                }
            }
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
                    ScheduleType.Group -> "`gruppa`, `kurs`, `shift`"
                    ScheduleType.Teacher -> "`prepod`"
                    ScheduleType.Classroom -> "`auditoria`"
                }
            }
            FROM `pl4453-mobile`.`1c_shedule`
            WHERE ${
                when (type) {
                    ScheduleType.Group -> "`gruppa`"
                    ScheduleType.Teacher -> "`prepod`"
                    ScheduleType.Classroom -> "`auditoria`"
                }
            } IN (${attributes.joinToString {"?"}})
        """
        ).apply {

            for (i in 1..attributes.count()) {
                setString(i, attributes[i - 1].stringId)
            }
        }.executeQuery()
    }
}