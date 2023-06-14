package aelsi2.natkschedule.domain

import aelsi2.natkschedule.domain.use_cases.*
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::CleanUpCacheUseCase)
    singleOf(::GetLectureStateUseCase)
    singleOf(::LoadScheduleUseCase)
    singleOf(::LoadAllAttributesUseCase)
    singleOf(::LoadAttributesUseCase)
    singleOf(::GetScheduleIsMainUseCase)
    singleOf(::GetScheduleIsFavoriteUseCase)
    singleOf(::SetMainScheduleUseCase)
    singleOf(::ToggleScheduleFavoriteUseCase)
    singleOf(::GetMainScheduleIsSetUseCase)
    singleOf(::GetFavoritesNotEmptyUseCase)
    singleOf(::DoBackgroundWorkUseCase)
    singleOf(::DoStartupWorkUseCase)
    singleOf(::SetUpBackgroundWorkUseCase)

    factory {params ->
        GetRegularScheduleParametersUseCase(params.get(), get(), get())
    } bind(GetScheduleParametersUseCase::class) withOptions {
        this.named("regular")
    }
    singleOf(::GetMainScheduleParametersUseCase) {
        this.named("main")
    } bind(GetScheduleParametersUseCase::class)
}