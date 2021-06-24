package com.saverio.wordoftheday_en

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import org.w3c.dom.Text
import java.util.*


class NotificationReceiver : BroadcastReceiver() {

    lateinit var title: String
    lateinit var text: String

    var connected = false
    val pattern = "dd/MM/yyyy"

    var attempts = 0
    val maxAttempts = 5

    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        var loadWord = LoadWord()
        loadWord.loadWord(context, attempts, maxAttempts, pattern, notificationReceiver = this)
    }

    fun sendNow(title: String, text: String, number: Int) {
        this.title = title
        this.text = text
        sendNotification(context, title, text, true, number)
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        autoCancel: Boolean = true,
        notificationNumber: Int
    ) {
        val NOTIFICATION_CHANNEL_ID =
            "${context.packageName.replace(".", "_")}_notification_${notificationNumber}"
        val NOTIFICATION_CHANNEL_NAME = "${context.packageName}_notification".replace(".", "_")
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(autoCancel) //.setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val savedDate = context.getSharedPreferences(
            "lastNotificationDate",
            Context.MODE_PRIVATE
        ).getString("lastNotificationDate", "")

        val c = Calendar.getInstance()

        val currentDate =
            "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH + 1)}-${c.get(Calendar.DAY_OF_MONTH)}"

        if (getPushNotifications(context)) {
            if (c.get(Calendar.HOUR_OF_DAY) >= 10 &&
                (currentDate != savedDate || (getSavedWord(context) != "" && getSavedWord(context) != title))
            ) {
                notificationManager!!.notify(
                    notificationNumber,
                    notificationBuilder.build()
                )
            } else {
                //Notification already sent (or it's not the 10 o'clock)
            }
        } else {
            //Notifications disabled
        }

        setWordSawToday(context, currentDate)
        setSavedWord(context, title)
    }

    fun getSavedWord(context: Context): String? {
        return context.getSharedPreferences("wordNotification", Context.MODE_PRIVATE)
            .getString("wordNotification", "")
    }

    fun setSavedWord(context: Context, word: String) {
        context.getSharedPreferences("wordNotification", Context.MODE_PRIVATE).edit()
            .putString("wordNotification", word).apply()
    }

    fun getPushNotifications(context: Context): Boolean {
        return context.getSharedPreferences(
            "pushNotifications",
            Context.MODE_PRIVATE
        ).getBoolean("pushNotifications", true)
    }

    fun setWordSawToday(context: Context, currentDate: String) {
        context.getSharedPreferences("lastNotificationDate", Context.MODE_PRIVATE).edit()
            .putString("lastNotificationDate", currentDate).apply()
    }
}