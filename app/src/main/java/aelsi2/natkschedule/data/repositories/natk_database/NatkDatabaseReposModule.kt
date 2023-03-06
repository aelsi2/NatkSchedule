package aelsi2.natkschedule.data.repositories.natk_database

import aelsi2.natkschedule.data.repositories.AttributeRepository
import aelsi2.natkschedule.data.repositories.LectureRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val natkDatabaseReposModule = module {
    singleOf(::NatkDatabase)
    singleOf(::NatkDatabaseDataParser)
    singleOf(::NatkDatabaseLectureRepository) bind LectureRepository::class
    singleOf(::NatkDatabaseAttributeRepository) bind AttributeRepository::class
}