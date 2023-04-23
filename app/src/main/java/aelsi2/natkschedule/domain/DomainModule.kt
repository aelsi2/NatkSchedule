package aelsi2.natkschedule.domain

import aelsi2.natkschedule.domain.use_cases.*
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::CleanCacheUnusedUseCase)
    singleOf(::GetLectureStateUseCase)
    singleOf(::LoadScheduleUseCase)
    singleOf(::LoadAllAttributesUseCase)
    singleOf(::LoadAttributesUseCase)
    singleOf(::GetScheduleIsMainUseCase)
    singleOf(::GetScheduleIsFavoriteUseCase)
    singleOf(::SetMainScheduleUseCase)
    singleOf(::ToggleScheduleFavoriteUseCase)

    factory {params ->
        GetRegularScheduleParametersUseCase(params.get(), get(), get())
    } bind(GetScheduleParametersUseCase::class) withOptions {
        this.named("other")
    }
    singleOf(::GetMainScheduleParametersUseCase) {
        this.named("main")
    } bind(GetScheduleParametersUseCase::class)
}