package aelsi2.natkschedule.data.repositories.room_database

import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleDayRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val roomDatabaseReposModule = module {
    singleOf(::RoomDatabaseScheduleDayRepository) bind WritableScheduleDayRepository::class
    singleOf(::RoomDatabaseScheduleAttributeRepository) bind WritableScheduleAttributeRepository::class
}