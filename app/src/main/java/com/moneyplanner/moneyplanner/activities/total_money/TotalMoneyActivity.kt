package com.moneyplanner.moneyplanner.activities.total_money

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.moneyplanner.moneyplanner.R
import com.moneyplanner.moneyplanner.helpers.PrefImpl
import com.moneyplanner.moneyplanner.helpers.USER_OBJECT
import com.moneyplanner.moneyplanner.model.Cash
import com.moneyplanner.moneyplanner.model.Users

class TotalMoneyActivity : AppCompatActivity() {
    lateinit var totalBalance: TextView
    lateinit var totalMoneyIn: TextView
    lateinit var totalMoneyOut: TextView
    lateinit var totalMoneyLeft: TextView
    lateinit var incomeBtn: TextView
    lateinit var expenseBtn: TextView
    lateinit var progressbar: ProgressBar
    lateinit var iPref: PrefImpl
    lateinit var selectedDate: String
    var rootNode: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_money)
        initViews()
        getCurrentMonthData()
        addIncome()
    }


    private fun initViews() {
        totalBalance = findViewById(R.id.total_balance)
        totalMoneyIn = findViewById(R.id.total_money_in)
        totalMoneyOut = findViewById(R.id.total_money_out)
        totalMoneyLeft = findViewById(R.id.total_money_left)
        incomeBtn = findViewById(R.id.income_btn)
        expenseBtn = findViewById(R.id.expense_btn)
        progressbar = findViewById(R.id.progress_bar)
        iPref = PrefImpl(this)
        selectedDate = intent.getStringExtra("SELECTED_DATE").toString()
    }


    fun getCurrentMonthData() {
        val user: Users = iPref.getObject(USER_OBJECT, Users::class.java)!!
        val UserRef =
            FirebaseDatabase.getInstance().reference.child("UserCash").child(user.id.toString())
                .child(
                    selectedDate
                )

        UserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userCash: Cash = dataSnapshot.getValue(Cash::class.java)!!
                Log.e("TAG", "userCash: " + userCash)
                totalBalance.text = userCash.totalCash
                totalMoneyIn.text = userCash.moneyIn
                totalMoneyOut.text = userCash.moneyOut
                totalMoneyLeft.text = userCash.totalCash

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    private fun addIncome() {
        incomeBtn.setOnClickListener {
            val user: Users = iPref.getObject(USER_OBJECT, Users::class.java)!!

            val builder = AlertDialog.Builder(
                this,
                androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog
            )
                .create()
            val view = layoutInflater.inflate(R.layout.custom_alert_dialog_for_adding, null)
            val salaryEt = view.findViewById<EditText>(R.id.salary)
            val savingsET = view.findViewById<EditText>(R.id.savings)
            val investmentsEt = view.findViewById<EditText>(R.id.investments)
            val generalEt = view.findViewById<EditText>(R.id.general)
            val addDetails = view.findViewById<Button>(R.id.add_details)
            val Dismiss = view.findViewById<Button>(R.id.dismiss)
            builder.setView(view)


            addDetails.setOnClickListener {
                val savings = savingsET.text.toString()
                val investments = investmentsEt.text.toString()
                val salary = salaryEt.text.toString()
                val generals = generalEt.text.toString()

                var incomeMoney = 0.0
                incomeMoney =
                    savings.toDouble() + investments.toDouble() + salary.toDouble() + generals.toDouble()
                var myTotalIncome = 0.0
                myTotalIncome = incomeMoney + totalBalance.text.toString().toDouble()


                val cash = Cash(
                    id = user.id,
                    userName = user.userName,
                    email = user.email,
                    totalCash = myTotalIncome.toString(),
                    moneyIn = incomeMoney.toString(),
                    moneyOut = totalMoneyOut.text.toString(),
                    date = selectedDate,
                )


                rootNode = FirebaseDatabase.getInstance()
                reference = rootNode!!.getReference("UserCash").child(cash.id.toString())
                    .child(cash.date.toString())
                reference!!.setValue(cash).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        getCurrentMonthData()
                        progressbar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Income Calculated ! ",
                            Toast.LENGTH_SHORT
                        ).show()

                        builder.dismiss()

                    } else {
                        progressbar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Total Cash Not Added  ! Try Again  ",
                            Toast.LENGTH_SHORT
                        ).show()
                        builder.dismiss()

                    }
                }

            }
            Dismiss.setOnClickListener {
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }
        expenseBtn.setOnClickListener {
            val user: Users = iPref.getObject(USER_OBJECT, Users::class.java)!!

            val builder = AlertDialog.Builder(
                this,
                androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog
            )
                .create()
            val view = layoutInflater.inflate(R.layout.custom_layout_for_expenses, null)
            val clothesEt = view.findViewById<EditText>(R.id.clothes)
            val eatingoutET = view.findViewById<EditText>(R.id.eating_out)
            val transportEt = view.findViewById<EditText>(R.id.transportations)
            val groceryEt = view.findViewById<EditText>(R.id.grocerry)
            val addDetailsExp = view.findViewById<Button>(R.id.add_details_e)
            val DismissExp = view.findViewById<Button>(R.id.dismiss_e)
            builder.setView(view)


            addDetailsExp.setOnClickListener {

                val transport = transportEt.getText().toString()
                val grocerry = groceryEt.getText().toString()
                val eatingout = eatingoutET.getText().toString()
                val clothes = clothesEt.getText().toString()

                var totalExpense = 0.0
                totalExpense =
                    transport.toDouble() + grocerry.toDouble() + eatingout.toDouble() + clothes.toDouble()

                var myTotalMoneyLeft = 0.0
                myTotalMoneyLeft = totalBalance.text.toString().toDouble() - totalExpense

                val cash = Cash(
                    id = user.id,
                    userName = user.userName,
                    email = user.email,
                    totalCash = myTotalMoneyLeft.toString(),
                    moneyIn = totalMoneyIn.text.toString(),
                    moneyOut = totalExpense.toString(),
                    date = selectedDate,

                    )

                rootNode = FirebaseDatabase.getInstance()
                reference = rootNode!!.getReference("UserCash").child(cash.id.toString())
                    .child(cash.date.toString())
                reference!!.setValue(cash).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        getCurrentMonthData()
                        progressbar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Expense Calculated ! ",
                            Toast.LENGTH_SHORT
                        ).show()

                        builder.dismiss()

                    } else {
                        progressbar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Total Cash Not Added  ! Try Again  ",
                            Toast.LENGTH_SHORT
                        ).show()
                        builder.dismiss()

                    }
                }

            }
            DismissExp.setOnClickListener {
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }
    }

}