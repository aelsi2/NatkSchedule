package aelsi2.natkschedule.ui

import aelsi2.natkschedule.ui.screens.attribute_list.groups.GroupListScreenViewModel
import aelsi2.natkschedule.ui.screens.attribute_list.teachers.TeacherListScreenViewModel
import aelsi2.natkschedule.ui.screens.attribute_list.classrooms.ClassroomListScreenViewModel
import aelsi2.natkschedule.ui.screens.attribute_list.favorites.FavoriteListScreenViewModel
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreenViewModel
import aelsi2.natkschedule.ui.screens.schedule.main.MainScheduleScreenViewModel
import aelsi2.natkschedule.ui.screens.settings.SettingsScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        ScheduleScreenViewModel(
            get(parameters = { it }),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        MainScheduleScreenViewModel(
            get(qualifier = named("main")),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModelOf(::GroupListScreenViewModel)
    viewModelOf(::TeacherListScreenViewModel)
    viewModelOf(::ClassroomListScreenViewModel)
    viewModelOf(::FavoriteListScreenViewModel)
    viewModelOf(::SettingsScreenViewModel)
}