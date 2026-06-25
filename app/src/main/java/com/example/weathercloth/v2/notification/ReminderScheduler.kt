package com.example.weathercloth.v2.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weathercloth.v2.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"
    private const val WORK_NAME = "daily_reminder_work"

    fun scheduleReminder(context: Context, hour: Int, minute: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now) || equals(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val delayMs = target.timeInMillis - now.timeInMillis
        val delayMinutes = delayMs / 60_000

        Log.d(TAG, "安排提醒: " + hour + ":" + String.format("%02d", minute) + ", 延迟 " + delayMinutes + " 分钟")

        val work = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, work)
    }

    fun scheduleNext(context: Context) {
        runBlocking {
            try {
                val db = AppDatabase.create(context)
                val reminders = db.dao().observeReminders().first()
                val enabled = reminders.firstOrNull { it.enabled }
                if (enabled != null) {
                    scheduleReminder(context, enabled.hour, enabled.minute)
                }
                Unit
            } catch (e: Exception) {
                Log.e(TAG, "重新调度失败", e)
            }
        }
    }

    fun cancel(context: Context) {
        Log.d(TAG, "取消所有提醒")
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun testNotification(context: Context) {
        NotificationHelper.showDailyAdvice(
            context,
            "提醒测试",
            "如果你看到这条消息，说明通知功能工作正常。"
        )
    }
}
