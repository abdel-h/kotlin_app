package space.majid.testing.materieldestesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    companion object {
        const val EXTRA_MESSAGE: String = "RegisterActivity.MESSAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {
            val username = register_username.text.toString()
            val email = register_email.text.toString()
            val password = register_pass.text.toString()
            val user = User(username, email, password)
            // TODO make sure to validate these values
            createNewUser(user)
        }
    }

    private fun createNewUser(user: User) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("FIRE_BASE", "createUserWithEmailAndPassword:success")
                        // Saving the user to the database
                        val newUser = auth.currentUser
                        Log.d("FIRE_BASE", "User ID = ${newUser?.uid}")
                        usersRef.child(newUser!!.uid).setValue(user)
                                .addOnSuccessListener {
                                    Log.d("FIRE_BASE", "usersaved:success")
                                }
                                .addOnFailureListener {
                                    Log.d("FIRE_BASE", "usersaved:failure")
                                    Log.d("FIRE_BASE", it.message)
                                }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("FIRE_BASE", "createUserWithEmailAndPassword:failure", task.exception)
                    }
                }
    }
}

class User(var username: String, var email: String, var password: String) {
}
