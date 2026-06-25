package com.example.weathercloth.v2.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "收到广播: " + intent.action)
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("ReminderReceiver", "设备启动完成，重新安排提醒")
                ReminderScheduler.scheduleNext(context)
            }
            "com.example.weathercloth.v2.ACTION_RESCHEDULE" -> {
                ReminderScheduler.scheduleNext(context)
            }
        }
    }
}
