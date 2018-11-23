package space.majid.testing.materieldestesting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.setOnClickListener {
            val email = login_email.text.toString()
            val password = login_pass.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIRE_BASE", "signInWithEmail:success")
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("FIRE_BASE", "signInWithEmail:failure", task.exception)
                        }
                    }
        }

    }
}
