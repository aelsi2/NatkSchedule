package aelsi2.natkschedule.data.room_database

import androidx.room.Room
import org.koin.dsl.bind
import org.koin.dsl.module

val roomDatabaseModule = module {
    single {
        Room.databaseBuilder(get(), ScheduleDatabase::class.java, "natk_schedule_database")
            .fallbackToDestructiveMigration().build()
    } bind ScheduleDatabase::class
    factory {
        get<ScheduleDatabase>().lectureDao()
    }
    factory {
        get<ScheduleDatabase>().disciplineDao()
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