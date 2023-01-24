package com.moneyplanner.moneyplanner.activities.register_activity

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moneyplanner.moneyplanner.R
import com.moneyplanner.moneyplanner.model.Users

class RegistrationActivity : AppCompatActivity() {
    var name: EditText? = null
    var gender: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var confrimpassword: EditText? = null
    var genderRadio: RadioGroup? = null
    var userTypeRadioGroup: RadioGroup? = null
    var male: RadioButton? = null
    var female: RadioButton? = null
    var customer: RadioButton? = null
    var serviceProvider: RadioButton? = null
    var ProfileImage: ImageView? = null
    var storage: FirebaseStorage? = null
    var Registerbtn: Button? = null
    var Signbtn: Button? = null
    private var mAuth: FirebaseAuth? = null
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    var progressBar: ProgressBar? = null
    var selectedgender: String? = null
    var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        FirebaseApp.initializeApp(this)
        initView()
        userRegistration()

    }

    private fun userRegistration() {
        Signbtn!!.setOnClickListener { onBackPressed() }
        Registerbtn!!.setOnClickListener(View.OnClickListener {
            try {
                val name_emp = name!!.text.toString().trim { it <= ' ' }
                //                    String gender_emp = radiogenderButton.getText().toString().trim();
                val email_emp = email!!.text.toString().trim { it <= ' ' }
                val password_emp = password!!.text.toString().trim { it <= ' ' }
                val confirm_pass_emp = confrimpassword!!.text.toString().trim { it <= ' ' }

                //                    Toast.makeText(RegisterationActivity.this, ""+gender_emp, Toast.LENGTH_SHORT).show();
                if (name_emp.isEmpty()) {
                    name!!.error = "name is Required"
                    name!!.requestFocus()
                    return@OnClickListener
                }
                if (email_emp.isEmpty()) {
                    email!!.error = "email is Required"
                    email!!.requestFocus()
                    return@OnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_emp).matches()) {
                    email!!.error = "Please Provide Valid Email"
                    email!!.requestFocus()
                    return@OnClickListener
                }
                if (password_emp.isEmpty()) {
                    password!!.error = "password is Required"
                    password!!.requestFocus()
                    return@OnClickListener
                }
                if (confirm_pass_emp.isEmpty()) {
                    confrimpassword!!.error = "confirm password is Required"
                    confrimpassword!!.requestFocus()
                    return@OnClickListener
                }
                if (male!!.isChecked) {
                    selectedgender = male!!.text.toString().trim { it <= ' ' }
                }
                if (female!!.isChecked) {
                    selectedgender = female!!.text.toString().trim { it <= ' ' }
                }
                if (customer!!.isChecked) {
                    userType = customer!!.text.toString().trim { it <= ' ' }
                }
                if (serviceProvider!!.isChecked) {
                    userType = serviceProvider!!.text.toString().trim { it <= ' ' }
                }
                if (password_emp == confirm_pass_emp) {
                    registerEmployee(
                        name_emp,
                        selectedgender.toString(),
                        email_emp,
                        userType!!,
                        password_emp,
                        confirm_pass_emp
                    )
                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Password must be 6 character and Should match with confirm password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TAG", "onClick: " + e.message)
            }
        })

    }

    private fun registerEmployee(
        employeename: String,
        employeeGender: String,
        employeeEmail: String,
        userType: String,
        employeePassword: String,
        Employeeconfirmpassword: String
    ) {
        Log.e("TAG", "gender: $employeeGender")
        if (employeePassword == Employeeconfirmpassword) {
            progressBar!!.visibility = View.VISIBLE
            mAuth!!.createUserWithEmailAndPassword(employeeEmail, Employeeconfirmpassword)
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e("TAG", "createUserWithEmail:success")
                        val user = mAuth!!.currentUser
                        rootNode = FirebaseDatabase.getInstance()
                        val currentUserId = mAuth!!.currentUser!!.uid
                        reference = rootNode!!.getReference("Users").child(currentUserId)
                        val currentUser = Users(id =currentUserId,
                        email = employeeEmail,
                        gendar = employeeGender,
                        status = false,
                        userName = employeename,
                        userType = userType,
                        verified = true)
                        reference!!.setValue(currentUser).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressBar!!.visibility = View.GONE
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Registered ! you can now Login ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackPressed()
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Failed to registered ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar!!.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.e("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@RegistrationActivity, "Authentication failed. User already Exists",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar!!.visibility = View.GONE
                    }
                }
        }
    }

    private fun initView() {
        name = findViewById(R.id.UserName)
        genderRadio = findViewById(R.id.radioGrp)
        userTypeRadioGroup = findViewById(R.id.radio_group_user_type)
        male = findViewById(R.id.radioM)
        female = findViewById(R.id.radioF)
        customer = findViewById(R.id.customer_Rbtn)
        serviceProvider = findViewById(R.id.service_provider_Rbtn)
        email = findViewById(R.id.email_et_r)
        password = findViewById(R.id.password_et_r)
        confrimpassword = findViewById(R.id.confirm_password_et)
        Registerbtn = findViewById(R.id.btn_register_)
        progressBar = findViewById(R.id.progressR)
        Signbtn = findViewById(R.id.sign_in_Btn)
        ProfileImage = findViewById(R.id.profile_image_users)
        mAuth = FirebaseAuth.getInstance()
    }
}