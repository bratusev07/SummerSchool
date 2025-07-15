package ru.bratusev.summerschool

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

/** Класс, реализующий Worker для работы с уведомлениями */
class NotifyTaskCreatedWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    /** Переопределенный метод для описания работы worker */
    override fun doWork(): Result {
        val channelId = "task_created_channel"
        val notificationId = 1

        // Создание канала уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Created"
            val descriptionText = "Уведомления о создании задачи"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Создание канала уведомлений с атрибутами, описанными выше
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Создание уведомления в конкретном канале для них с указанными атрибутами
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Поздравляем, вы создали задачу!")
            .setContentText("Не хотите создать еще одну?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            // Проверка наличия разрешения на размещение уведомлений
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Отправка сообщения о том, что нет разрешения
                Toast.makeText(applicationContext, "No notification permission", Toast.LENGTH_SHORT).show()
                return Result.failure()
            }
            // Публикация уведомления
            notify(notificationId, builder.build())
        }

        return Result.success()
    }
}