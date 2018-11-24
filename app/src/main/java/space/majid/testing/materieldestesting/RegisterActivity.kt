package space.majid.testing.materieldestesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MESSAGE: String = "RegisterActivity.MESSAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}
