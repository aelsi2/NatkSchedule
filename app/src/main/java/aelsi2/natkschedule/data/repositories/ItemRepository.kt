package aelsi2.natkschedule.data.repositories

/**
 * Репозиторий элементов, требующих только фильтрацию по строковому ключу.
 */
interface ItemRepository<T> {
    /**
     * Поддерживает ли репозиторий синхронизацию.
     */
    val syncable : Boolean
    /**
     * Получить последовательность элементов.
     * @param sync Нужно ли выполнить синхронизацию (если поддерживается).
     * @param keys Ключи элементов, которые нужно загрузить (null = загрузить все).
     */
    suspend fun getItems(
        sync : Boolean = true,
        keys : List<String>? = null
    ) : Result<Iterable<T>>
}