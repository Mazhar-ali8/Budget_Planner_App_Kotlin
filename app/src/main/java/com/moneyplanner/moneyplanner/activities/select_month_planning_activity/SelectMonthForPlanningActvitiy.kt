package com.moneyplanner.moneyplanner.activities.select_month_planning_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.moneyplanner.moneyplanner.R
import com.moneyplanner.moneyplanner.activities.total_money.TotalMoneyActivity
import com.moneyplanner.moneyplanner.helpers.PrefImpl
import com.moneyplanner.moneyplanner.helpers.USER_OBJECT
import com.moneyplanner.moneyplanner.model.Cash
import com.moneyplanner.moneyplanner.model.Users


class SelectMonthForPlanningActvitiy : AppCompatActivity() {
    lateinit var monthSpinner:Spinner
    lateinit var addDetailsButton: Button
    lateinit var user: Users
    lateinit var iPref: PrefImpl
    var allUserCash: ArrayList<Cash> = ArrayList<Cash>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_month_for_planning_actvitiy)
        iPref = PrefImpl(this)
        user= iPref.getObject(USER_OBJECT, Users::class.java)!!
        monthSpinner=findViewById(R.id.month_details_spinner)
        addDetailsButton=findViewById(R.id.add_details_selected_month)
        getData()
    }

        private fun getData() {
            val dates: ArrayList<String> = ArrayList()

            val UserRef =
            FirebaseDatabase.getInstance().reference.child("UserCash").child(user.id.toString())
        UserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allUserCash.clear()
                for (snapshot in dataSnapshot.children) {
                    val usersCash: Cash? = snapshot.getValue(Cash::class.java)
                    allUserCash.add(usersCash!!)
                }

                for (helper in allUserCash) {
                    dates.add(helper.date.toString())
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    applicationContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    dates
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                monthSpinner.setAdapter(adapter)
                addDetailsButton.setOnClickListener {
                    val selectedDate: String = monthSpinner.getSelectedItem().toString()
                    Log.e("TAG", "selectedDate1:$selectedDate ", )

                    if(selectedDate.isNullOrEmpty()){
                        Toast.makeText(
                            this@SelectMonthForPlanningActvitiy,
                            "Please Select Month first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else{
                        val intent = Intent(this@SelectMonthForPlanningActvitiy, TotalMoneyActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("SELECTED_DATE",selectedDate)
                        startActivity(intent)
                    }
                }







            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}