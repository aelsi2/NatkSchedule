package aelsi2.natkschedule.data.repositories.room_database

import aelsi2.natkschedule.data.repositories.WritableAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableLectureRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val roomDatabaseReposModule = module {
    singleOf(::RoomDatabaseLectureRepository) bind WritableLectureRepository::class
    singleOf(::RoomDatabaseAttributeRepository) bind WritableAttributeRepository::class
}