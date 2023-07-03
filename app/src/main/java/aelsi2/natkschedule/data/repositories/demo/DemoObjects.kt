package aelsi2.natkschedule.data.repositories.demo

import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Discipline
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.LectureData
import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.Teacher
import java.time.LocalDate
import java.time.LocalTime

val DEMO_GROUP = Group(
    name = "ХД-69.420",
    programName = "01.02.03",
    year = 3
)
val DEMO_CLASSROOM = Classroom(
    shortName = "№666",
    fullName = "Призывная аудитория №666",
    address = "Зеленый проспект 72"
)
val DEMO_TEACHER = Teacher(
    fullName = "Вонави Нави Чивонави",
    shortName = "Вонави Н. Ч."
)
val DEMO_DISCIPLINE = Discipline("Имитация бурной деятельности")

private const val DAY_LECTURE_COUNT = 5

fun createDemoScheduleDay(
    date: LocalDate
): ScheduleDay = ScheduleDay(
    date,
    (1..DAY_LECTURE_COUNT).map {
        createDemoLecture(it)
    }
)

private fun createDemoLectureData(subgroupIndex: Int?) = LectureData(
    DEMO_TEACHER,
    DEMO_CLASSROOM,
    DEMO_GROUP,
    subgroupIndex
)

private const val MAX_SUBGROUP_COUNT = 2
private val DAY_START_TIME =  LocalTime.of(8, 30)
private const val LECTURE_DURATION_MINUTES: Long = 45 + 45 + 10
private const val BREAK_DURATION_MINUTES: Long = 10
private const val MID_LECTURE_BREAK_OFFSET_MINUTES: Long = 45
private const val MID_LECTURE_BREAK_DURATION_MINUTES: Long = BREAK_DURATION_MINUTES
private fun createDemoLecture(index: Int): Lecture {
    val startTime = DAY_START_TIME.plusMinutes(
        (LECTURE_DURATION_MINUTES + BREAK_DURATION_MINUTES)
                * (index - 1))
    val breakStartTime = startTime.plusMinutes(MID_LECTURE_BREAK_OFFSET_MINUTES)
    return Lecture(
        index,
        startTime = startTime,
        endTime = startTime.plusMinutes(LECTURE_DURATION_MINUTES),
        breakStartTime = breakStartTime,
        breakEndTime = breakStartTime.plusMinutes(MID_LECTURE_BREAK_DURATION_MINUTES),
        discipline = DEMO_DISCIPLINE,
        data = when(index % MAX_SUBGROUP_COUNT) {
            0 -> listOf(createDemoLectureData(null))
            else -> {
                (1..(index % MAX_SUBGROUP_COUNT + 1)).map {
                    createDemoLectureData(it)
                }
            }
        }
    )
}