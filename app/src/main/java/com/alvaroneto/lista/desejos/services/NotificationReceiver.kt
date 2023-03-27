package com.alvaroneto.lista.desejos.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alvaroneto.lista.desejos.R


const val notificationId = 0;

class NotificationReceiver: BroadcastReceiver() {

    var taskTitle: String = "";
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Notification", "Notification received")

        if (context != null) {
            sendNotification(context, intent)
        }
    }

    private fun sendNotification(ctx:Context, intent:Intent?)
    {
        if(intent != null)
            this.taskTitle = intent.getStringExtra("title") ?: ""

        val notification: NotificationCompat.Builder = NotificationCompat.Builder(ctx, "WishList")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$this.taskTitle está expirando!")
            .setContentText("Você está quase perdendo seu item: $this.taskTitle!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setSound(Uri.parse("https://firebasestorage.googleapis.com/v0/b/lista-de-desejos-9e937.appspot.com/o/sounds%2Ftones.mp3?alt=media&token=bc58be7e-3d95-4642-8b86-4eebe65498ea"))

        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification.build())
    }
}