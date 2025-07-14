package ru.bratusev.summerschool.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/** Интерфейс DAO (Data Access Object) для работы с таблицей задач в Room */
@Dao
interface TaskDao {
    // Получение всех задач в виде Flow - автоматическое обновление при изменении БД
    // Flow<List<TaskEntity>> позволяет наблюдать за изменениями данных в реальном времени
    @Query("SELECT * FROM tasks")
    fun getAllItems(): Flow<List<TaskEntity>>

    // Вставка одной задачи с заменой при конфликте
    // OnConflictStrategy.REPLACE: если запись с таким же идентикатором (PrimaryKey), она будет заменена
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: TaskEntity)

    // Вставка списка задач с аналогичной стратегией замены
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<TaskEntity>)

    // Обновление существующей задачи
    // Требует, чтобы объект TaskEntity имел существующий Primary Key
    @Update
    suspend fun updateItem(item: TaskEntity)

    // Удаление одной задачи из БД
    @Delete
    suspend fun deleteItem(item: TaskEntity)

    // Полное удаление всех задач через SQL-запрос
    // Более эффективно, чем последовательный вызов deleteItem()
    @Query("DELETE FROM tasks")
    suspend fun deleteAllItems()
}

/**
Ключевые особенности:
1. Flow - реактивный поток данных для подписки на изменения БД
2. suspend - все операции выполняются асинхронно (в корутинах)
3. Стратегии конфликтов:
   - REPLACE - безопасная перезапись при дублировании ключей
   - IGNORE - пропуск дубликатов (требует дополнительной проверки)
*/