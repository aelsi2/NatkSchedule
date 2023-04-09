package aelsi2.natkschedule.data.repositories.natk_database

import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList

/**
 * Применяет трансформацию к каждой строке [ResultSet].
 * @return Список результатов трансформации.
 */
@Throws(SQLException::class)
inline fun <T> ResultSet?.map(transform: ResultSet.() -> T?) : List<T> {
    this ?: return listOf()
    val items = ArrayList<T>()
    while (this.next()) {
        val item = this.transform()
        if (item != null) {
            items.add(item)
        }
    }
    return items
}

/**
 * Выполняет действие для каждой строки [ResultSet].
 * @return Список результатов трансформации.
 */
@Throws(SQLException::class)
inline fun ResultSet?.forEach(action: ResultSet.() -> Unit) {
    this ?: return
    while (this.next()) {
        this.action()
    }
}