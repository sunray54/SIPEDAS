package util

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelReminder() {
        workManager.cancelUniqueWork("daily_reminder")
    }

    class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result {
            // Tampilkan notifikasi di sini
            NotificationHelper.showNotification(
                applicationContext,
                "Cek kondisi tanamanmu hari ini!",
                "Gunakan SIPEDAS untuk diagnosis."
            )
            return Result.success()
        }
    }
}