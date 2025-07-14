package ru.bratusev.summerschool.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Аннотация @Entity связывает класс с таблицей в базе данных Room
// tableName указывает имя таблицы в БД - "tasks"
@Entity(tableName = "tasks")
data class TaskEntity(
    // Первичный ключ с автоинкрементом
    // autoGenerate = true позволяет Room автоматически генерировать ID
    // По умолчанию 0 - для новых записей, существующие получат свой ID из БД
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Поле заголовка задачи
    // Тип String автоматически маппится в TEXT в SQLite
    val title: String,

    // Описание задачи
    val description: String,

    // Статус задачи (например: "новая", "в процессе", "завершена")
    val status: String,

    // Дата создания в формате timestamp (Long)
    val creationDate: Long
)

/**
ВАЖНЫЕ ЗАМЕЧАНИЯ:
1. Первичный ключ:
   - autoGenerate = true работает только с Long/Integer типами
   - При вставке объекта с id=0 Room автоматически присвоит новый ID

2. Типы данных:
   - Room автоматически конвертирует Kotlin типы в SQLite типы:
     String -> TEXT
     Long -> INTEGER
   - Для сложных типов (например, LocalDate) требуется TypeConverter
*/