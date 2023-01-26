package aelsi2.natkschedule.model

import aelsi2.natkschedule.model.data.Classroom
import aelsi2.natkschedule.model.data.Group
import aelsi2.natkschedule.model.data.Teacher

interface ScheduleRepository {
    fun getTeachers() : Iterable<Teacher>
    fun getGroups() : Iterable<Group>
    fun getClassrooms() : Iterable<Classroom>

}