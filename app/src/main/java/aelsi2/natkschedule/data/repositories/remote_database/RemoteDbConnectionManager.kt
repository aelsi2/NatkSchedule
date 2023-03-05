package aelsi2.natkschedule.data.repositories.remote_database

import aelsi2.natkschedule.BuildConfig
import android.util.Log
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

/**
 * Менеджер соединений СУБД. Занимается их созданием и закрытием.
 */
class RemoteDbConnectionManager {
    /**
     * Открывает новое соединение СУБД.
     * @return Соединение СУБД.
     */
    @Throws(SQLException::class)
    fun openConnection() : Connection = DriverManager.getConnection(
        DATABASE_URL,
        CONNECTION_PARAMETERS
    )

    /**
     * Создает соединение СУБД, выполняет с ним блок кода и закрывает его.
     * @param action Блок кода, который нужно выполнить с соединением.
     * @return Возвращаемое из блока кода значение.
     */
    suspend fun <T>withConnection(
        action: (Connection) -> T
    ): T = withContext(Dispatchers.IO) {
        openConnection().use {
            action(it)
        }
    }
    /**
     * Создает несколько соединений СУБД, параллельно выполняет с ними соответствующие блоки кода и закрывает их.
     * @param actions Блоки кода, которые нужно выполнить с соединениями.
     * @return Возвращаемые из блоков кода значения.
     */
    suspend fun <T>withConnections(
        vararg actions: (Connection) -> T
    ): List<T> = coroutineScope {
        val jobs = actions.map { action ->
            async { withConnection { action(it) } }
        }
        jobs.awaitAll()
    }
    /**
     * Создает соединение СУБД, выполняет с ним блок кода и закрывает его.
     * При возникновении исключения в блоке кода логирует его и возвращает [Result.failure].
     * @param errorMessage Сообщение, записываемое в логи при возникновении исключения.
     * @param action Блок кода, который нужно выполнить с соединением.
     * @return Возвращаемое из блока кода значение, обернутое в [Result.success] или исключение в [Result.failure].
     */
    suspend fun <T>tryWithConnection(
        errorMessage: String? = null,
        action: (Connection) -> T
    ): Result<T> {
        return try {
            Result.success(withConnection(action))
        } catch (e: Throwable) {
            Log.e("RemoteDb", errorMessage ?: "An error occurred while accessing the database.", e)
            Result.failure(e)
        }
    }
    /**
     * Создает несколько соединений СУБД, параллельно выполняет с ними соответствующие блоки кода и закрывает их.
     * При возникновении исключения в любом из блоков кода логирует его и возвращает [Result.Failure].
     * @param errorMessage Сообщение, записываемое в логи при возникновении исключения.
     * @param actions Блоки кода, которые нужно выполнить с соединениями.
     * @return Возвращаемые из блоков кода значения, обернутые в [Result.success] или исключение в [Result.failure].
     */
    suspend fun <T>tryWithConnections(
        errorMessage: String? = null,
        vararg actions: (Connection) -> T
    ): Result<List<T>> {
        return try {
            Result.success(withConnections(actions = actions))
        } catch (e: Throwable) {
            Log.e("RemoteDb", errorMessage ?: "An error occurred while accessing the database.", e)
            Result.failure(e)
        }
    }
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