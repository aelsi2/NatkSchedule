package aelsi2.natkschedule.data.repositories.network

import aelsi2.natkschedule.BuildConfig
import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.sql.Connection
import java.time.Instant
import kotlinx.coroutines.withContext
import java.sql.DriverManager
import java.util.Properties

class NetworkDatabaseScheduleRepository() : ScheduleRepository {
    override val syncable: Boolean
        get() = false
    override suspend fun getSchedule(
        startDate: Instant,
        endDate: Instant,
        identifier : ScheduleIdentifier,
        sync : Boolean
    ) : Result<Iterable<Lecture>> {
        TODO("Not yet implemented")
    }

    private fun openConnection(username : String, password : String) : Connection {
        TODO("Not yet implemented")
    }
    companion object {
        private val DATABASE_URL = BuildConfig.DATABASE_URL
        private val CONNECTION_PARAMETERS = Properties().apply {
            setProperty("user", BuildConfig.DATABASE_USER)
            setProperty("password", BuildConfig.DATABASE_PASSWORD)
            setProperty("useUnicode", "true")
            setProperty("characterEncoding", "utf-8")
        }
    }
}