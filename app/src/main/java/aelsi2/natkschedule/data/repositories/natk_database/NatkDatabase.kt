package aelsi2.natkschedule.data.repositories.natk_database

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
class NatkDatabase {
    init {
        Class.forName("com.mysql.jdbc.Driver")
    }
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
    suspend inline fun <T>withConnection(
        crossinline action: (Connection) -> T
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
    suspend inline fun <T>tryWithConnection(
        errorMessage: String? = null,
        crossinline action: (Connection) -> T
    ): Result<T> {
        return try {
            Result.success(withConnection(action))
        } catch (e: SQLException) {
            Log.e("RemoteDb", errorMessage ?: "При осуществлении доступа к СУБД произошла ошибка.", e)
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
        } catch (e: SQLException) {
            Log.e("RemoteDb", errorMessage ?: "При осуществлении множественного доступа к СУБД произошла ошибка.", e)
            Result.failure(e)
        }
    }
    companion object {
        private const val DATABASE_URL = BuildConfig.DATABASE_URL
        private val CONNECTION_PARAMETERS = Properties().apply {
            setProperty("user", BuildConfig.DATABASE_USER)
            setProperty("password", BuildConfig.DATABASE_PASSWORD)
            setProperty("useUnicode", "true")
            setProperty("characterEncoding", "utf-8")
            setProperty("connectTimeout", "5000")
            setProperty("socketTimeout", "5000")
        }
    }
}