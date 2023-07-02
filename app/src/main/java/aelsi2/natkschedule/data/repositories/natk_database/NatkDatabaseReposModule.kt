package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val natkDatabaseReposModule = module {
    singleOf(::NatkDatabaseScheduleDayRepository) bind ScheduleDayRepository::class
    singleOf(::NatkDatabaseScheduleAttributeRepository) bind ScheduleAttributeRepository::class
}