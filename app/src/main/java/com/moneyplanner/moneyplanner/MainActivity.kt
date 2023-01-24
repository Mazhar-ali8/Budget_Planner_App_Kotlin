package com.moneyplanner.moneyplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.moneyplanner.moneyplanner.activities.dashbaord.DashbaordActivity
import com.moneyplanner.moneyplanner.helpers.KEY_USER_LOGIN
import com.moneyplanner.moneyplanner.helpers.PrefImpl
import com.moneyplanner.moneyplanner.helpers.USER_OBJECT
import com.moneyplanner.moneyplanner.model.Users
import com.moneyplanner.moneyplanner.activities.register_activity.ForgetPassword
import com.moneyplanner.moneyplanner.activities.register_activity.RegistrationActivity

class MainActivity : AppCompatActivity() {
    lateinit var iPref: PrefImpl

    var email: EditText? = null
    var password: EditText? = null
    var forgotPassword: TextView? = null
    var registerAccount: Button? = null
    var signIn: Button? = null
    var mAuth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        checkLoginUser()



    }

    private fun checkLoginUser() {
        val isUserLoggedIn: Boolean = iPref.bool(KEY_USER_LOGIN,false)
        if (isUserLoggedIn) {
            Log.e("TAG", "shared Bool: " + isUserLoggedIn)
            val intent = Intent(this@MainActivity, DashbaordActivity::class.java)
            startActivity(intent)

        }
    }

    private fun initView() {
        email = findViewById(R.id.email_et)
        password = findViewById(R.id.password_et)
        forgotPassword = findViewById(R.id.forgotpassword)
        signIn = findViewById(R.id.btn_signin)
        registerAccount = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progressbar)
        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
        iPref = PrefImpl(this)
        val firebaseUser = mAuth!!.currentUser
        signIn?.setOnClickListener(View.OnClickListener {
            progressBar!!.setVisibility(View.VISIBLE)
            UserLogin()
        })
        registerAccount?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
            startActivity(intent)
        })

        forgotPassword?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, ForgetPassword::class.java)
            startActivity(intent)
        })
    }


    private fun UserLogin() {
        val email_text = email!!.text.toString().trim { it <= ' ' }
        val password_text = password!!.text.toString().trim { it <= ' ' }
        if (email_text.isEmpty()) {
            email!!.error = "Email is Required"
            email!!.requestFocus()
            return        }
        if (password_text.isEmpty()) {
            password!!.error = "Password is Required"
            password!!.requestFocus()
            return
        }
        mAuth!!.signInWithEmailAndPassword(email_text, password_text)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    val databaseReference =
                        FirebaseDatabase.getInstance().reference.child("Users").child(
                            firebaseUser!!.uid
                        )
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val users: Users? = snapshot.getValue(Users::class.java)
                            try {
                                iPref.putObject(USER_OBJECT, users)
                                iPref.put(KEY_USER_LOGIN, true)
                                progressBar!!.visibility = View.GONE
                                val intent =
                                    Intent(this@MainActivity, DashbaordActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()

                            } catch (e: Exception) {
                                Log.e("TAG", "onComplete: " + e.message)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to login ! please check your credential",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseAuth.getInstance().signOut()
    }
}