package space.majid.testing.materieldestesting

import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

        Log.d("FIRE_BASE:onMessage", "Message Received")
        // Notification data

        val title = remoteMessage?.data?.get("title")
        val body = remoteMessage?.data?.get("body")
        val action = remoteMessage?.data?.get("action")

        // Build the notification
        buildNotification(title, body, action)
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
    }

    private fun buildNotification(title: String? = "", body: String? = "", action: String? = "") {
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)

        if(action == "location_invite") {
            val intent = Intent(this, FindUserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            mBuilder.setContentIntent(pendingIntent)
        } else if( action == "new_friend_request") {
            val intent = Intent(this, FriendsListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            mBuilder.setContentIntent(pendingIntent)
        }
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