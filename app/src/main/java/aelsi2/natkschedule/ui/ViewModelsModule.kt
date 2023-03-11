package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        ScheduleScreenViewModel(get(), get(), get(), get(parameters = {it}), get(), get(), get())
    } withOptions {
        named("other")
    }
    viewModel {
        ScheduleScreenViewModel(get(), get(), get(), get(qualifier = named("main")), get(), get(), get())
    } withOptions {
        named("main")
    }
}