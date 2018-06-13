package com.finger.hsd.activity

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.finger.hsd.R
import com.finger.hsd.adapters.CustomAdapter
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.TimeAlarm
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import kotlinx.android.synthetic.main.alarm_setting.*
import java.util.*

import kotlin.collections.ArrayList


@Suppress("UNREACHABLE_CODE")
class AlarmSetting : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    val Tag : String  = "AlarmSetting"
    private val milDay = 86400000L
    private var mMinute : Int = 0
    private var mHour : Int = 0
    private var mTime: String? = null
    internal lateinit var alarmManager: AlarmManager
    private var pending_intent: PendingIntent? = null
    var realms : RealmAlarmController?= null
    lateinit var mCustomAdapter: CustomAdapter
    lateinit var sessionManager : SessionManager

    internal var timeAlarm : List<TimeAlarm> = ArrayList<TimeAlarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_setting)
//        realms  = RealmAlarmController.with(this)
        sessionManager = SessionManager(applicationContext)
         realms = RealmAlarmController(this)

//        realms!!.view_to_database()
//        if (realms==null){
        realms!!.DatabseLlistAlarm()
//        }

        switch_notification.isChecked = sessionManager.get_open_Alarm()
        Log.d(Tag, "sessionManager.get_open_Alarm()..." + sessionManager.get_open_Alarm() )

        alarmManager =  getSystemService(Context.ALARM_SERVICE) as AlarmManager

        switch_notification.setOnCheckedChangeListener(this)

        btn_save.setOnClickListener(this)
        btn_settime.setOnClickListener(this)

        timeAlarm = realms!!.view_to_dataTimeAlarm()!!
        Log.d(Tag, "  sessionManager ......." + realms!!.view_to_dataTimeAlarm())

        mCustomAdapter = CustomAdapter(this, timeAlarm)
        list_TimeSetting.setAdapter(mCustomAdapter)

        list_TimeSetting.setOnItemClickListener { adapterView, view, i, l ->
            val model = timeAlarm.get(i)
            val alarmList = TimeAlarm()
            alarmList.listtime  = model.listtime
            alarmList.isSelected = !model.isSelected!!
            realms!!.update_to_timeAlarm(alarmList)
//            timeAlarm.set(i, model)
            //now update adapter
            mCustomAdapter.updateRecords(timeAlarm)
        }

    }


    override fun onClick(view: View?) {
        when (view!!.getId()) {

            R.id.btn_settime -> {
                // do your code
//                val now = Calendar.getInstance()
//                val tpd = TimePickerDialog.newInstance(
//                        this,
//                        now.get(Calendar.HOUR_OF_DAY),
//                        now.get(Calendar.MINUTE),
//                        true
//                )
//                tpd.isThemeDark = false
//                tpd.show(fragmentManager, "Timepickerdialog")
            }

            R.id.btn_save -> {
                val intent = Intent()
                intent.putExtra("keyAlarm", mTime)
                    saveDatabase()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
            }
        // edit git
            else -> {
            }
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


    fun saveDatabase(){
        val calendars = Calendar.getInstance()
        calendars.set(Calendar.MINUTE, 0)
        calendars.set(Calendar.SECOND, 0)

        val now = Calendar.getInstance()
        val list = mCustomAdapter.getSelectedItem()

        if (list.isNotEmpty()) {

            val sb = StringBuilder()

            for (index in list.indices) {
                val model = list.get(index) as TimeAlarm
                sb.append(model.listtime.toString() + "\n")
                realms!!.save_to_database(model.listtime!!.toInt() , calendars.timeInMillis.toString())

                calendars.set(Calendar.HOUR_OF_DAY, model.listtime!!)
                Log.d(Tag, " curent_Timemili ... " + " ====   time choose model.listtime ==== " + calendars.timeInMillis  + "\n")

                Log.d(Tag, " curent_Timemili ... " + " ====   time choose 3600000L ==== " +(model.listtime!! * 3600000L)  + "\n")

                Log.d(Tag, " curent_Timemili ... " + " ====   time choose (model.listtime!! * 3600000L)==== " + calendars.timeInMillis + (model.listtime!! * 3600000L) + "\n" )

                Log.d(Tag, " curent_Timemili ... " + calendars.timeInMillis  + " ====   time now ==== " + now.timeInMillis  + "\n" )

                Log.d(Tag, " curent_Timemili ... " + " ====   time now ==== " + (now.timeInMillis  - calendars.timeInMillis)  + "\n")

            }
            showToast(sb.toString())
        } else {
            showToast("Please select any time!")
        }
    }

    private fun setAlarmText(alarmText: String) {
        btn_settime.text = alarmText
    }

    override fun isDestroyed(): Boolean {
        return super.isDestroyed()
        realms!!.closeRealm()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}


