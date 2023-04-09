package aelsi2.natkschedule.model

data class LectureData(
    val discipline: Discipline,
    val teacher: Teacher?,
    val classroom: Classroom?,
    val group: Group?,
    val subgroupIndex: Int?
)
