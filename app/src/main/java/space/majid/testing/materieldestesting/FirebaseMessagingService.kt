package space.majid.testing.materieldestesting

import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FindMeFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var auth: FirebaseAuth
    private lateinit var usersRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Log.d("FIRE_BASE:onMess", "Message Received")
        // Notification
        /*
        val title = remoteMessage?.notification?.title
        val body = remoteMessage?.notification?.body
        Log.d("FIRE_BASE_Notif:title", remoteMessage?.notification?.title)
        Log.d("FIRE_BASE_Notif:body", remoteMessage?.notification?.body)
        */
        // Notification data

        val title = remoteMessage?.data?.get("title")
        val body = remoteMessage?.data?.get("body")
        val action = remoteMessage?.data?.get("action")

        // Build the notification
        buildNotification(title, body)

        Log.d("FIRE_BASE_Notif:data", title)
        Log.d("FIRE_BASE_Notif:data", body)
        Log.d("FIRE_BASE_Notif:data", action)
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        val currentUser = auth.currentUser
        Log.d("FIRE_BASE_TOKEN_FMS", token)
        val deviceToken: Map<String, String?> = hashMapOf("token_id" to token)
        usersRef.child(currentUser!!.uid).updateChildren(deviceToken)
                .addOnSuccessListener {
                    // TODO take user to it's profile
                    Log.d("FIRE_BASE", "updateToken:success")
                }
                .addOnFailureListener {
                    Log.d("FIRE_BASE", "updateToken:failure")
                }
    }

    private fun buildNotification(title: String? = "", body: String? = "") {
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
        val mNotificationId = System.currentTimeMillis().toInt()
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if( Build.VERSION.SDK_INT >= 26 ) {
            val chanelId = "CHANNEL_1"
            val mChanel = NotificationChannel(chanelId, "Channel_1", NotificationManager.IMPORTANCE_HIGH)
            mNotifyMgr.createNotificationChannel(mChanel)
            mBuilder.setChannelId(chanelId)
        }
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }
}