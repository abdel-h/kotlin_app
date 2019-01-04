package space.majid.testing.materieldestesting


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        startActivity<FriendsListActivity>()
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        setContentView(R.layout.activity_main)
        val loginEmail: TextInputLayout = findViewById(R.id.login_email)
        val loginPassword: TextInputLayout = findViewById(R.id.login_password)
        // Login button
        val loginButton: MaterialButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val email = loginEmail.editText?.text.toString()
            val password = loginPassword.editText?.text.toString()
            // TODO validation of these values
            signInUser(email, password)
        }

        // Register Link
        val registerButton: MaterialButton = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            Log.d("REGISTER_LINK", "clickListen:success")
            startActivity<RegisterActivity>()
        }
    }
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { authRes ->
                    if (authRes.isSuccessful) {
                        Log.d("FIRE_BASE", "signInWithEmail:success")
                        Log.d("FIRE_BASE_USER", authRes.result?.user?.uid)
                        val currentUser = auth.currentUser
                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                            Log.d("FIRE_BASE_TOKEN", "Token:success")
                            Log.d("FIRE_BASE_TOKEN", it.token)

                            // Saving token to db
                            val deviceTokenId = it.token
                            val deviceToken: Map<String, Any> = hashMapOf("token_id" to deviceTokenId)
                            usersRef.child(currentUser!!.uid).updateChildren(deviceToken)
                                    .addOnSuccessListener {
                                        // TODO take user to it's profile
                                        startActivity<ProfileActivity>()
                                        Log.d("FIRE_BASE", "updateToken:success")
                                    }
                                    .addOnFailureListener {
                                        Log.d("FIRE_BASE", "updateToken:failure")
                                    }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("FIRE_BASE", "signInWithEmail:failure", authRes.exception)
                    }
                }
    }

}
