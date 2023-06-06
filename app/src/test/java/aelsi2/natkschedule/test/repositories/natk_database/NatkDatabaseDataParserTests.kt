package aelsi2.natkschedule.test.repositories.natk_database

import aelsi2.natkschedule.data.repositories.natk_database.NatkDatabaseDataParser
import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Teacher
import org.junit.Assert
import org.junit.Test

class NatkDatabaseDataParserTests {
    @Test
    fun parseClassroom_CorrectWithNumber_shouldReturnClassroom() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Лекционная аудитория №355 по адресу Красный проспект 72"

        val expectedResult: Classroom = Classroom(
            fullName = "Лекционная аудитория №355",
            shortName = "№355",
            address = "Красный проспект 72",
            id = "Лекционная аудитория №355 по адресу Красный проспект 72"
        )

        val actualResult: Classroom? = parser.parseClassroom(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseClassroom_CorrectGym_shouldReturnClassroom() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Спортивный зал по адресу Красный проспект 72"

        val expectedResult: Classroom = Classroom(
            fullName = "Спортивный зал",
            shortName = "Спортзал",
            address = "Красный проспект 72",
            id = "Спортивный зал по адресу Красный проспект 72"
        )

        val actualResult: Classroom? = parser.parseClassroom(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseClassroom_CorrectOther_shouldReturnClassroom() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Тренажерный зал по адресу Дзержинского проспект 26"

        val expectedResult: Classroom = Classroom(
            fullName = "Тренажерный зал",
            shortName = null,
            address = "Дзержинского проспект 26",
            id = "Тренажерный зал по адресу Дзержинского проспект 26"
        )

        val actualResult: Classroom? = parser.parseClassroom(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseClassroom_CorrectWithNumberNoAddress_shouldReturnClassroom() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Аудитория №366"

        val expectedResult: Classroom = Classroom(
            fullName = "Аудитория №366",
            shortName = "№366",
            address = null,
            id = "Аудитория №366"
        )

        val actualResult: Classroom? = parser.parseClassroom(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseClassroom_IncorrectEmpty_shouldReturnNull() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = ""

        val expectedResult: Classroom? = null

        val actualResult: Classroom? = parser.parseClassroom(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseTeacher_Correct4nameParts_shouldReturnTeacherWithNoShortName() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Щерба Евгений Андреевич хы"

        val expectedResult: Teacher = Teacher(
            fullName = "Щерба Евгений Андреевич хы",
            shortName = null,
            id = "Щерба Евгений Андреевич хы"
        )

        val actualResult: Teacher? = parser.parseTeacher(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseTeacher_Correct3nameParts_shouldReturnTeacherWithShortName() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Щерба Евгений Андреевич"

        val expectedResult: Teacher = Teacher(
            fullName = "Щерба Евгений Андреевич",
            shortName = "Щерба Е. А.",
            id = "Щерба Евгений Андреевич"
        )

        val actualResult: Teacher? = parser.parseTeacher(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseTeacher_Correct2namePart_shouldReturnTeacherWithNoShortName() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Щерба Евгений"

        val expectedResult: Teacher = Teacher(
            fullName = "Щерба Евгений",
            shortName = null,
            id = "Щерба Евгений"
        )

        val actualResult: Teacher? = parser.parseTeacher(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseTeacher_Correct1namePart_shouldReturnTeacherWithNoShortName() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = "Щерба"

        val expectedResult: Teacher = Teacher(
            fullName = "Щерба",
            shortName = null,
            id = "Щерба"
        )

        val actualResult: Teacher? = parser.parseTeacher(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun parseTeacher_IncorrectEmpty_shouldReturnNull() {
        val parser = NatkDatabaseDataParser()

        val rawName: String = ""

        val expectedResult: Teacher? = null

        val actualResult: Teacher? = parser.parseTeacher(rawName)

        Assert.assertEquals(expectedResult, actualResult)
    }
}