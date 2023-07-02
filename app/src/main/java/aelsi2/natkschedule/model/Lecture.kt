package aelsi2.natkschedule.model

import java.time.LocalTime

data class Lecture(
    /**
     * Номер пары начиная с 1 (`null`, когда, например, на весь день стоит одно занятие).
     */
    val index: Int?,
    /**
     * Время начала занятия (`null`, когда, например, на весь день стоит одно занятие).
     */
    val startTime: LocalTime?,
    /**
     * Время конца занятия (`null`, когда по рандому нет).
     */
    val endTime: LocalTime?,
    /**
     * Время начала перерыва (`null`, если перерыва нет).
     */
    val breakStartTime: LocalTime? = null,
    /**
     * Время конца перерыва (`null`, если перерыва нет).
     */
    val breakEndTime: LocalTime? = null,
    /**
     * Учебная дисциплина занятия (оказывается, ее какого-то хрена может не быть — `null`).
     */
    val discipline: Discipline?,
    /**
     * Список "данных занятия" [LectureData], содержащие преподавателя, аудиторию, группу и
     * номер подгруппы. Может быть несколько, если есть несколько подгрупп.
     */
    val data: List<LectureData>
)