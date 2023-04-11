package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.screens.attribute_list.group_list.GroupListScreenViewModel
import aelsi2.natkschedule.ui.screens.attribute_list.teacher_list.TeacherListScreenViewModel
import aelsi2.natkschedule.ui.screens.attribute_list.classroom_list.ClassroomListScreenViewModel
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        ScheduleScreenViewModel(get(parameters = {it}), get(), get(), get(), get(), get())
    } withOptions {
        named("other")
    }
    viewModel {
        ScheduleScreenViewModel(get(qualifier = named("main")), get(), get(), get(), get(), get())
    } withOptions {
        named("main")
    }
    viewModelOf(::GroupListScreenViewModel)
    viewModelOf(::TeacherListScreenViewModel)
    viewModelOf(::ClassroomListScreenViewModel)
}