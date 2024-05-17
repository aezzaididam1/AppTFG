package com.amine.mytfg.Services

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amine.mytfg.R

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Tiene una tarea pendiente"
        val taskTime = intent.getStringExtra("taskTime") ?: "Hora no definida"
        Log.d("ReminderBroadcastReceiver", "Received: taskTitle=$taskTitle, taskTime=$taskTime")

        val notificationManager = NotificationManagerCompat.from(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_settings_alt_24)
            .setContentTitle("Recordatorio de Tarea")
            .setContentText("$taskTitle a las $taskTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(taskTitle.hashCode(), builder.build())
            Log.d("ReminderBroadcastReceiver", "Notification sent for taskTitle=$taskTitle")
        } else {
            Log.e("ReminderBroadcastReceiver", "Permission not granted for POST_NOTIFICATIONS")
        }
    }

    companion object {
        const val CHANNEL_ID = "task_reminders"
    }
}