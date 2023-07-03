package aelsi2.natkschedule.data.repositories.demo

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val demoReposModule = module {
    single(named("demo")) {
        DemoScheduleDayRepository(get(named("default")))
    } bind ScheduleDayRepository::class
    single(named("demo")) {
        DemoScheduleAttributeRepository(get(named("default")))
    } bind ScheduleAttributeRepository::class

    single {
        get<ScheduleDayRepository>(named("demo"))
    } bind ScheduleDayRepository::class
    single {
        get<ScheduleAttributeRepository>(named("demo"))
    } bind ScheduleAttributeRepository::class
}