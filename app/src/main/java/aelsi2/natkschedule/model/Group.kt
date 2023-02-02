package aelsi2.natkschedule.model

data class Group(
    val name : String,
    val fieldOfStudy : String,
    val year : Int,
    val id : String = "${name}_${fieldOfStudy}_${year}"
    )
