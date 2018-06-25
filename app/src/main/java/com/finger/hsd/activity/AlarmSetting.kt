package com.finger.hsd.activity

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.finger.hsd.R
import com.finger.hsd.R.id.*
import com.finger.hsd.adapters.CustomAdapter
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.TimeAlarm
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import io.realm.Realm
import kotlinx.android.synthetic.main.alarm_setting.*
import java.util.*

import kotlin.collections.ArrayList


@Suppress("UNREACHABLE_CODE", "TYPEALIAS_EXPANSION_DEPRECATION")
class AlarmSetting : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    val Tag : String  = "AlarmSetting"
    private val milDay = 86400000L
    private var mMinute : Int = 0
    private var mHour : Int = 0
    private var mTime: String? = null
    private var realms : RealmAlarmController?= null
    private lateinit var mCustomAdapter: CustomAdapter
    private lateinit var sessionManager : SessionManager
    private lateinit var mToolbar: Toolbar

    private var timeAlarm : List<TimeAlarm> = ArrayList<TimeAlarm>()
    private  var oldList : ArrayList<String> = ArrayList<String>()

    var checkChange  : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_setting)
//        realms  = RealmAlarmController.with(this)

        mToolbar = findViewById(R.id.toolbar)
        this.setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        mToolbar.setTitleTextColor(Color.WHITE)
        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        sessionManager = SessionManager(applicationContext)
        realms = RealmAlarmController(this)
        realms!!.DatabseLlistAlarm()
        switch_notification.isChecked = sessionManager.get_open_Alarm()

        timeAlarm = realms!!.view_to_dataTimeAlarm()!!

        if (timeAlarm.isNotEmpty()) {
            for (index in timeAlarm.indices) {
                val model = timeAlarm[index]
                if(model.isSelected == true){
                    oldList.add(model.listtime.toString())
                }
            }
        }

//         timeAlarm = mCustomAdapter.getSelectedItem()




        mToolbar.setNavigationOnClickListener {
            if(checkChange){
                DialogChange()
            }else{
                onBackPressed()
            }


        }




        switch_notification.setOnCheckedChangeListener(this)
        btn_save.setOnClickListener(this)

        mCustomAdapter = CustomAdapter(this, timeAlarm)

        list_TimeSetting.adapter = mCustomAdapter

        list_TimeSetting.setOnItemClickListener { adapterView, view, i, l ->
            val model = timeAlarm[i]
            checkChange = true

            val alarmList = TimeAlarm()
            alarmList.listtime  = model.listtime
            alarmList.isSelected = !model.isSelected!!
            realms!!.update_to_timeAlarm(alarmList)

//            timeAlarm[i].isSelected = !model.isSelected!!
            //now update adapter
            mCustomAdapter.updateRecords(timeAlarm)
//            mCustomAdapter.notifyDataSetChanged()

//            mCustomAdapter.updatelist(i,model.isSelected!!)

        }

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_save -> {
             save()
            }
        }
    }

    fun save(){
        val list = mCustomAdapter.getSelectedItem()
        if(list.isNotEmpty()){
            val intent = Intent()
            intent.putExtra("keyAlarm", mTime)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else{
            showToast("Please select any time!")
        }
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            // do something when check is selected
            sessionManager.set_open_Alarm(true)
        } else {
            sessionManager.set_open_Alarm(false)
            //do something when unchecked
        }
    }


//    fun saveDatabase(){
////        val calendars = Calendar.getInstance()
////        calendars.set(Calendar.MINUTE, 0)
////        calendars.set(Calendar.SECOND, 0)
////        val now = Calendar.getInstance()
//        val list = mCustomAdapter.getSelectedItem()
//        if (list.isNotEmpty()) {
//            val sb = StringBuilder()
//            for (index in list.indices) {
//                val model = list[index]
//                sb.append(model.listtime.toString() + "\n")
//                realms!!.update_to_SetAlarm(model.listtime!!.toInt(), true)
////                calendars.set(Calendar.HOUR_OF_DAY, model.listtime!!)
//            }
////            showToast(sb.toString())
//        }
//    }

    private fun DialogChange() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Bạn phải lưu lại thay đổi!")
        mBuilder.setPositiveButton("OK") { dialog, which ->
            // Do something when click the neutral button
            save()
            dialog.cancel()
        }

        // Set the neutral/cancel button click listener

        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            val newAlarm : ArrayList<TimeAlarm> = ArrayList<TimeAlarm>()
            newAlarm.add(TimeAlarm(0,false))
            newAlarm.add(TimeAlarm(1,false))
            newAlarm.add(TimeAlarm(2,false))
            newAlarm.add(TimeAlarm(3,false))
            newAlarm.add(TimeAlarm(4,false))
            newAlarm.add(TimeAlarm(5,false))
            newAlarm.add(TimeAlarm(6,false))
            newAlarm.add(TimeAlarm(7,false))
            newAlarm.add(TimeAlarm(8,false))
            newAlarm.add(TimeAlarm(9,false))
            newAlarm.add(TimeAlarm(10,false))
            newAlarm.add(TimeAlarm(11,false))
            newAlarm.add(TimeAlarm(12,false))
            newAlarm.add(TimeAlarm(13,false))
            newAlarm.add(TimeAlarm(14,false))
            newAlarm.add(TimeAlarm(15,false))
            newAlarm.add(TimeAlarm(16,false))
            newAlarm.add(TimeAlarm(17,false))
            newAlarm.add(TimeAlarm(18,false))
            newAlarm.add(TimeAlarm(19,false))
            newAlarm.add(TimeAlarm(20,false))
            newAlarm.add(TimeAlarm(21,false))
            newAlarm.add(TimeAlarm(22,false))
            newAlarm.add(TimeAlarm(23,false))

            realms!!.storeListToRealm(newAlarm)

            for (j in oldList.indices){
                Log.d("for....  " , " j " + j)
                val model_oldList = oldList[j]
                    realms!!.update_to_SetAlarm(model_oldList.toInt(), true)
            }

//            Log.d("setNeutralButton " , " -------  " + timeAlarm)
//            Log.d("setNeutralButton " , " timeAlarm :  " + timeAlarm  + " \n ========  "  + oldList)
//            for ( i in timeAlarm.indices ){
//                val model_timeAlarm = timeAlarm[i]
//                Log.d("for....  " , " i " + i )
//                //Log.d("setNeutralButton " , " modeltimeAlarm   " + model_timeAlarm.listtime  + "  ========  " )
//
//                for (j in oldList.indices){
//                    Log.d("for....  " , " j " + j)
//                    val model_oldList = oldList[j]
//                    Log.d("setNeutralButton " , " " + model_timeAlarm.listtime  + "  ==   " + model_oldList)
//
//                    if(model_timeAlarm.listtime == model_oldList.toInt()){
//                        realms!!.update_to_SetAlarm(model_timeAlarm.listtime!!.toInt(), true)
//                        Log.d("setNeutralButton " , " true  " + model_timeAlarm.listtime  + "  ==   " + model_oldList.toInt())
//
//                    }else{
//                        realms!!.update_to_SetAlarm(model_timeAlarm.listtime!!.toInt(), false)
//                        Log.d("setNeutralButton " , " false " + model_timeAlarm.listtime  + "  ==   " + model_oldList.toInt())
//                    }
//                }
//            }
            mCustomAdapter.updateRecords(timeAlarm)
            dialog.cancel()
            finish()

        }


        val mDialog = mBuilder.create()
        mDialog.show()
    }


    override fun isDestroyed(): Boolean {
        return super.isDestroyed()
        realms!!.closeRealm()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}


