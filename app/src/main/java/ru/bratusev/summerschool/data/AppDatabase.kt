package ru.bratusev.summerschool.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Объявление класса для работы с БД
 * Аннотация Room для определения базы данных
 * - Указывает, что база данных содержит сущность TaskEntity
 * - Версия 2 (важно для миграций между версиями приложения)
 * */
@Database(entities = [TaskEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    // Абстрактный метод для получения DAO (Data Access Object) для работы с сущностью TaskEntity
    abstract fun taskDao(): TaskDao

    companion object {
        // @Volatile гарантирует видимость изменений переменной между потоками
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Получение экземпляра базы данных (потокобезопасная реализация)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Построение базы данных с использованием Room
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Используем ApplicationContext для избежания утечек
                    AppDatabase::class.java,
                    "app_database" // Имя файла базы данных
                )
                    .fallbackToDestructiveMigration() // Полная пересборка базы при изменении схемы (теряет данные!)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
ВАЖНЫЕ ЗАМЕЧАНИЯ:
1. fallbackToDestructiveMigration() - опасный метод! При изменении схемы БД (например, добавлении колонки)
   уничтожает существующие данные. Для продакшена рекомендуется использовать миграции через addMigrations()

2. Синглтон-паттерн:
   - Гарантирует один экземпляр базы данных для всего приложения
   - Потокобезопасная реализация через synchronized и @Volatile

3. Хранение контекста:
   - Используется applicationContext, а не Activity context, чтобы избежать утечек памяти

*/