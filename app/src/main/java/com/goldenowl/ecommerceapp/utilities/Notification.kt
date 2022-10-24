package com.goldenowl.ecommerceapp.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R

class Notification(private val context: Context) {
    private lateinit var channel: NotificationChannel

    fun notify(title: String, content: String) {
        if (!::channel.isInitialized) {
            createNotificationChannel()
        }

        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_app)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

    fun notifyOrder(idOrder: String){
        if (!::channel.isInitialized) {
            createNotificationChannel()
        }

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.orderDetailFragment)
            .setArguments(bundleOf(ID_ORDER to idOrder))
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_app)
            .setContentTitle(TITLE_DEFAULT)
            .setContentText("Order №$idOrder success")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        val name = NAME
        val importance = NotificationManager.IMPORTANCE_HIGH
        channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = DESCRIPTION
        }
        // Register the channel with the system
        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "EVENT_CHANNEL_ID"
        const val NAME = "APP_NAME"
        const val DESCRIPTION = "A channel for app"
        const val TITLE_DEFAULT = "Notification"
    }
}