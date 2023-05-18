package com.example.feelie

// import utilities and packages
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // initialize variables
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var redirectSignUp: TextView

    // create auth object
    private lateinit var fbAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // connect variables to UI elements
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_btn)
        redirectSignUp = findViewById(R.id.redirect_signup)
        // Initialize Firebase auth object
        fbAuth = FirebaseAuth.getInstance()

        // go to sign-up page instead
        redirectSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        // proceed to login
        loginButton.setOnClickListener {
            login()
        }
    }

    //login function
    private fun login() {
        // get email and password entered by user
        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString()

        // checkers
        when {
            TextUtils.isEmpty(email)->
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password)->
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()

            else-> {
                fbAuth = FirebaseAuth.getInstance() // create authentication instance

                // firebase email and password sign method
                fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show()
                        fbAuth.signOut() // sign out user
                    }
                }
            }
        }
    }

    // if user is logged in during a previous session, they will remain logged in for following sessions
    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser!=null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}