package aelsi2.natkschedule.data.repositories.remote_database

import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList

/**
 * Выполняет трансформацию для каждой строки [ResultSet].
 * @return Список результатов трансформации.
 */
@Throws(SQLException::class)
fun <T> ResultSet?.map(transform: ResultSet.() -> T?) : List<T> {
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