package aelsi2.natkschedule.data.database

import androidx.room.Room
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), ScheduleDatabase::class.java, "natk_schedule_database").build()
    }
    factory {
        get<ScheduleDatabase>().lectureDao()
    }
    factory {
        get<ScheduleDatabase>().teacherDao()
    }
    factory {
        get<ScheduleDatabase>().groupDao()
    }
    factory {
        get<ScheduleDatabase>().classroomDao()
    }
}