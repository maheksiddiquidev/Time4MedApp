package com.example.time4medapp

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        // Receive all data
        val medicineName = intent?.getStringExtra("medicineName") ?: "Medicine"
        val time = intent?.getStringExtra("time") ?: ""
        val medicineType = intent?.getStringExtra("medicineType") ?: "Tablet"
        val historyId = intent?.getIntExtra("historyId", -1) ?: -1

        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // FULL SCREEN INTENT with ALL data
        val fullScreenIntent = Intent(this, AlarmFullScreenActivity::class.java)
        fullScreenIntent.putExtra("medicineName", medicineName)
        fullScreenIntent.putExtra("time", time)
        fullScreenIntent.putExtra("medicineType", medicineType)
        fullScreenIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        fullScreenIntent.putExtra("historyId", historyId)


        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "alarm_channel")
            .setSmallIcon(R.drawable.ic_tablet)
            .setContentTitle("💊 Medicine Reminder")
            .setContentText("It's time to take your $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Stop",
                stopPendingIntent
            )
            .build()

        startForeground(1, notification)

        mediaPlayer = MediaPlayer.create(
            this,
            android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
        )
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}