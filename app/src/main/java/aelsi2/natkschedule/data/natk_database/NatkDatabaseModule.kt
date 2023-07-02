package aelsi2.natkschedule.data.natk_database

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val natkDatabaseModule = module {
    singleOf(::NatkDatabase)
    singleOf(::NatkDatabaseDataParser)
}