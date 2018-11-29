package space.majid.testing.materieldestesting


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)
        // Login button
        login_button.setOnClickListener {
            val email = login_email.text.toString()
            val password = login_pass.text.toString()
            // TODO validation of these values
            signInUser(email, password)
        }

        // Register Link
        register_link.setOnClickListener {
            Log.d("REGISTER_LINK", "clickListen:success")
            startActivity<RegisterActivity>()
        }
    }
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("FIRE_BASE", "signInWithEmail:success")
                        Log.d("FIRE_BASE_USER", it.result?.user?.uid)
                        // TODO save user to database too with username
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("FIRE_BASE", "signInWithEmail:failure", it.exception)
                    }
                }
    }
}
