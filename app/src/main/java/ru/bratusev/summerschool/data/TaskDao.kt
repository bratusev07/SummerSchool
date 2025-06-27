package ru.bratusev.summerschool.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllItems(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<TaskEntity>)

    @Update
    suspend fun updateItem(item: TaskEntity)

    @Delete
    suspend fun deleteItem(item: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllItems()
} 