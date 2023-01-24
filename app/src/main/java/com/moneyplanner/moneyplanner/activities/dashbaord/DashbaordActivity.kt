package com.moneyplanner.moneyplanner.activities.dashbaord

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moneyplanner.moneyplanner.R
import com.moneyplanner.moneyplanner.activities.select_month_planning_activity.SelectMonthForPlanningActvitiy
import com.moneyplanner.moneyplanner.helpers.PrefImpl
import com.moneyplanner.moneyplanner.helpers.USER_OBJECT
import com.moneyplanner.moneyplanner.model.Cash
import com.moneyplanner.moneyplanner.model.Users
import java.text.SimpleDateFormat
import java.util.*

class DashbaordActivity : AppCompatActivity() {
    lateinit var cashEt: EditText
    lateinit var insertBtn: Button
    lateinit var addDetails: Button
    lateinit var logout: Button
    lateinit var progressBar: ProgressBar
    lateinit var iPref: PrefImpl
    private var mAuth: FirebaseAuth? = null
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    lateinit var monthSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashbaord)
        cashEt = findViewById(R.id.total_cost)
        insertBtn = findViewById(R.id.insert_btn)
        addDetails = findViewById(R.id.add_details)
        logout = findViewById(R.id.logOut)
        progressBar = findViewById(R.id.progress_bar)

        iPref = PrefImpl(this)
        mAuth = FirebaseAuth.getInstance()
        val datePickerET = findViewById<EditText>(R.id.date_picker)

        datePickerET.setOnClickListener {
            val newCalendar = Calendar.getInstance()
            val startDatePickerDialog = DatePickerDialog(
                this, object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        val newDate = Calendar.getInstance()
                        newDate[year, monthOfYear] = dayOfMonth
                        datePickerET.setText(SimpleDateFormat("MM-yyyy").format(newDate.time))
                    }
                }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            startDatePickerDialog.show()
        }


        insertBtn.setOnClickListener {
            val totalCashStr = cashEt.text.toString()
            if (totalCashStr.isEmpty() && datePickerET.text.isEmpty()) {
                cashEt.error = "Please select date and Cash ! "
                datePickerET.error = "Please select date and Cash ! "

            } else {
                progressBar.visibility = View.VISIBLE
                val user: Users = iPref.getObject(USER_OBJECT, Users::class.java)!!

                val userId = user.id
                val userName = user.userName
                val userEmail = user.email
                val userCash = totalCashStr.toString()

                val cash = Cash(
                    id = userId,
                    userName = userName,
                    email = userEmail,
                    totalCash = userCash,
                    moneyIn = "0",
                    moneyOut = "0",
                    date = datePickerET.text.toString(),
                    savings = "0",
                    investments = "0",
                    salary = "0",
                    generals = "0"
                )
                insertCashOfTheUser(cash)
            }
        }



        addDetails.setOnClickListener {

            val intent = Intent(this@DashbaordActivity, SelectMonthForPlanningActvitiy::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        logout.setOnClickListener {

            showLogoutDialog()
        }
    }

    private fun insertCashOfTheUser(cash: Cash) {
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode!!.getReference("UserCash").child(cash.id.toString())
            .child(cash.date.toString())
        reference!!.setValue(cash).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar!!.visibility = View.GONE
                Toast.makeText(
                    this@DashbaordActivity,
                    "Total Cash Added ! Congratulations",
                    Toast.LENGTH_SHORT
                ).show()
                val intent =
                    Intent(this@DashbaordActivity, SelectMonthForPlanningActvitiy::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } else {
                progressBar!!.visibility = View.GONE
                Toast.makeText(
                    this@DashbaordActivity,
                    "Total Cash Not Added  ! Try Again  ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }


    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this@DashbaordActivity, R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.custm_alert_logout, null)
        val btnOK = view.findViewById<Button>(R.id.btn_ok)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        builder.setView(view)
        btnOK.setOnClickListener {


            iPref.clear()
            this@DashbaordActivity.moveTaskToBack(true);
            this@DashbaordActivity.finish();
        }
        btnCancel.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

}