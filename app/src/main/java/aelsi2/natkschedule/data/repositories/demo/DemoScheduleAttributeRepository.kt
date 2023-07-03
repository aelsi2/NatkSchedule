package aelsi2.natkschedule.data.repositories.demo

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

class DemoScheduleAttributeRepository(
    private val repository: ScheduleAttributeRepository
) : ScheduleAttributeRepository {
    override suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<ScheduleAttribute>> {
        val result = repository.getAttributesById(ids)
        val idsContainDemoGroup = ids.contains(DEMO_GROUP.scheduleIdentifier)
        val idsContainDemoClassroom = ids.contains(DEMO_GROUP.scheduleIdentifier)
        val idsContainDemoTeacher = ids.contains(DEMO_GROUP.scheduleIdentifier)
        if (!(idsContainDemoGroup || idsContainDemoClassroom || idsContainDemoTeacher)) {
            return result
        }
        val list: MutableList<ScheduleAttribute> = result.fold(
            onSuccess = {
                it.toMutableList()
            },
            onFailure = {
                mutableListOf()
            }
        )
        if (idsContainDemoGroup) {
            list.add(0, DEMO_GROUP)
        }
        if (idsContainDemoClassroom) {
            list.add(0, DEMO_CLASSROOM)
        }
        if (idsContainDemoTeacher) {
            list.add(0, DEMO_TEACHER)
        }
        return Result.success(list)
    }

    override suspend fun getAttributeById(
        id: ScheduleIdentifier
    ): Result<ScheduleAttribute?> = when (id) {
        DEMO_GROUP.scheduleIdentifier -> Result.success(DEMO_GROUP)
        DEMO_CLASSROOM.scheduleIdentifier -> Result.success(DEMO_CLASSROOM)
        DEMO_TEACHER.scheduleIdentifier -> Result.success(DEMO_TEACHER)
        else -> repository.getAttributeById(id)
    }

    override suspend fun getAllAttributes(
        type: ScheduleType
    ): Result<List<ScheduleAttribute>> {
        val list: MutableList<ScheduleAttribute> = repository.getAllAttributes(type).fold(
            onSuccess = {
                it.toMutableList()
            },
            onFailure = {
                mutableListOf()
            }
        )
        when (type) {
            ScheduleType.Group -> list.add(0, DEMO_GROUP)
            ScheduleType.Classroom -> list.add(0, DEMO_CLASSROOM)
            ScheduleType.Teacher -> list.add(0, DEMO_TEACHER)
        }
        return Result.success(list)
    }
}