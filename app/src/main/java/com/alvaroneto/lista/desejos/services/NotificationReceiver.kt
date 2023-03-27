package com.alvaroneto.lista.desejos.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alvaroneto.lista.desejos.R

const val notificationId = 0;

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Notification", "Notification received")

        if (context != null) {
            sendNotification(context)
        }
    }

    private fun sendNotification(ctx:Context)
    {
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(ctx, "WishList")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Wish item expirating!")
            .setContentText("You are almost loosing your Wish item!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(Uri.parse("https://firebasestorage.googleapis.com/v0/b/lista-de-desejos-9e937.appspot.com/o/sounds%2Ftones.mp3?alt=media&token=bc58be7e-3d95-4642-8b86-4eebe65498ea"))

        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification.build())
    }
}