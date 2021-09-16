package it.matteo.metronome.service

import android.app.*
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import it.matteo.metronome.Metronome.Metronome
import it.matteo.metronome.R

class BackgroundService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 110
    }

    private lateinit var metronome: Metronome

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        showNotification()
        metronome = Metronome(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // start service
        // send broadcast message
        val tempo = intent?.getLongExtra("tempo", 60L)
        Log.i("Metronome", "Service started with tempo $tempo")
        metronome.start(tempo)
        return START_STICKY
    }

    fun sendBroadcastUpdate() {
        val actionName = javaClass.name
        val intent = Intent()
            .apply {
                action = actionName
                flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                // put extra information
            }
        sendBroadcast(intent)
    }

    private fun stopService() {
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.cancel(NOTIFICATION_ID)
        metronome.stop()
    }

    override fun onDestroy() {
        stopService()
    }


    /**
     * It creates a new Notification
     * Links the service to a notification bar that keeps the service on "foreground'
     *  so it won't be killed even if the app that launches the service is killed
     *  @return Notification
     */
    private fun createNotification(): Notification {

        // it creates the notification builder
        val notificationBuilder = NotificationCompat.Builder(
            this,
            R.string.channel_name.toString()
        )
        // it arranges the intent that will be fired when the stop button will be pressed
        val intent = Intent(this, BroadcastServiceStopper::class.java)
        val pendingIntent = PendingIntent
            .getBroadcast(this, 0, intent, FLAG_CANCEL_CURRENT)

        // set the notification that will be linked to the service
        val builder = notificationBuilder
            .setContentTitle(getText(R.string.app_name))
            .setContentText(getText(R.string.channel_name))
            .setSmallIcon(R.drawable.ic_metronome_on)
            .addAction(R.drawable.ic_metronome_off, getText(R.string.stop), pendingIntent)
            .setOngoing(true)
        // if Android version's is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // it creates a notification channel
            createNotificationChannel(
                R.string.channel_name.toString(),
                R.string.channel_description.toString()
            )
            // it adds priority and category type to the notification
            builder.apply {
                priority = NotificationManager.IMPORTANCE_DEFAULT
                setCategory(Notification.CATEGORY_SERVICE)
            }
        }
        return builder.build()
    }


    /**
     * Usable only with Android's version Oreo or later
     * It creates a notification channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        startForeground(NOTIFICATION_ID, createNotification())
    }
}

/**
 * Custom broadcast receiver that stops the background service
 */
class BroadcastServiceStopper : BroadcastReceiver() {

    /**
     * Stop the background service when receiving the broadcast
     */
    override fun onReceive(context: Context, intent: Intent) {
        context.stopService(Intent(context, BackgroundService::class.java))
    }
}




