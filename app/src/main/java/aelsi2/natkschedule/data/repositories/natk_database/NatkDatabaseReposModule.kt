package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val natkDatabaseReposModule = module {
    singleOf(::NatkDatabaseScheduleDayRepository) { named("default") } bind ScheduleDayRepository::class
    singleOf(::NatkDatabaseScheduleAttributeRepository) { named("default") } bind ScheduleAttributeRepository::class
    single {
        get<ScheduleDayRepository>(named("default"))
    } bind ScheduleDayRepository::class
    single {
        get<ScheduleAttributeRepository>(named("default"))
    } bind ScheduleAttributeRepository::class
}