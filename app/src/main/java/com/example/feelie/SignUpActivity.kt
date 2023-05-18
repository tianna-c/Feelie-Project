package com.example.feelie

//import necessary utilities and packages
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    // initialize variables
    private lateinit var signupUsername: EditText
    private lateinit var signupFullname: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupConfirmPassword: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var redirectLogin: TextView

    // initialize firebase authentication service
    private lateinit var fbAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // assignment variables to UI elements
        signupUsername = findViewById(R.id.signup_username)
        signupFullname = findViewById(R.id.signup_fullname)
        signupEmail = findViewById(R.id.signup_email)
        signupConfirmPassword = findViewById(R.id.signup_confirm_password)
        signupPassword = findViewById(R.id.signup_password)
        signupButton = findViewById(R.id.signup_btn)
        redirectLogin = findViewById(R.id.redirect_login)

        // initialize Firebase auth object
        fbAuth = Firebase.auth

        // initialize listener to call signup function
        signupButton.setOnClickListener {
            signupUser()
        }

        // redirect user to login if already registered
        redirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // sign up function
    private fun signupUser() {
        val username = signupUsername.text.toString()
        val fullname = signupFullname.text.toString()
        val email = signupEmail.text.toString()
        val password = signupPassword.text.toString()
        val confirmPassword = signupConfirmPassword.text.toString()

        // checkers
        when {

            TextUtils.isEmpty(fullname)->
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username)->
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email)->
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password)->
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            (password != confirmPassword)->
                Toast.makeText(this, "Passwords do not not match", Toast.LENGTH_SHORT).show()

            else-> {
                fbAuth = FirebaseAuth.getInstance() //create auth object

                // firebase authentication through email and password signup
                fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                        task ->
                    if (task.isSuccessful) {
                        saveUserData(username, fullname, email) // call save data function
                    }
                    else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        fbAuth.signOut() // sign out user
                    }
                }
            }
        }
    }

    // save user data to database
    private fun saveUserData(username: String, fullname: String, email: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!! //get current user's uid
        // create users table in database
        val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        // create map to store data
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["username"] = username.lowercase()
        userMap["fullname"] = fullname
        userMap["email"] = email

        // set values to data map
        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Welcome $username!", Toast.LENGTH_SHORT).show()

                    // create follow and following tables
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserId)
                        .child("Following").child(currentUserId).setValue(true)

                    // go to main activity
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut() // sign out user
                }
            }
    }
}