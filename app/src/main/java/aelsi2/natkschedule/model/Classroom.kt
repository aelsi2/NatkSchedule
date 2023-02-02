package aelsi2.natkschedule.model

data class Classroom(
    val fullName : String,
    val shortName : String = fullName,
    val id : String = fullName
    )