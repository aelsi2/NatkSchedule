package aelsi2.natkschedule.data.time

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val timeModule = module {
    singleOf(::TimeManager)
}