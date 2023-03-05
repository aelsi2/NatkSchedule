package aelsi2.natkschedule.data.repositories.remote_database

import aelsi2.natkschedule.BuildConfig
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class RemoteDbManager {
    @Throws(SQLException::class)
    fun openConnection() : Connection = DriverManager.getConnection(
        DATABASE_URL,
        CONNECTION_PARAMETERS
    )
    companion object{
        const val DATABASE_URL = BuildConfig.DATABASE_URL
        val CONNECTION_PARAMETERS = Properties().apply {
            setProperty("user", BuildConfig.DATABASE_USER)
            setProperty("password", BuildConfig.DATABASE_PASSWORD)
            setProperty("useUnicode", "true")
            setProperty("characterEncoding", "utf-8")
        }
    }
}