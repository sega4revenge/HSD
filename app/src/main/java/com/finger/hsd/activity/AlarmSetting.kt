package com.finger.hsd.activity

import android.app.Activity
import android.app.AlarmManager
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
import com.finger.hsd.adapters.CustomAdapter
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.TimeAlarm
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import io.realm.Realm
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
    private lateinit var mToolbar: Toolbar

    internal var timeAlarm : List<TimeAlarm> = ArrayList<TimeAlarm>()

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
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })

        sessionManager = SessionManager(applicationContext)
         realms = RealmAlarmController(this)

        realms!!.DatabseLlistAlarm()

        switch_notification.isChecked = sessionManager.get_open_Alarm()
        Log.d(Tag, "sessionManager.get_open_Alarm()..." + sessionManager.get_open_Alarm() )

        alarmManager =  getSystemService(Context.ALARM_SERVICE) as AlarmManager

        switch_notification.setOnCheckedChangeListener(this)

        btn_save.setOnClickListener(this)
//        btn_settime.setOnClickListener(this)

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
                realms!!.update_to_SetAlarm(model.listtime!!.toInt() , true)

                calendars.set(Calendar.HOUR_OF_DAY, model.listtime!!)
//                calendars.set(Calendar.DAY_OF_WEEK)
                Log.d(Tag, " curent_Timemili ... " + " ====   time choose model.listtime ==== " + calendars.timeInMillis  + "\n")
//                Log.d(Tag, " curent_Timemili ... " + " ====   Calendar.DAY_OF_MONTH ==== " +Calendar.DAY_OF_MONTH  + "\n"  )
//                Log.d(Tag, " curent_Timemili ... " + " ====   time choose model.listtime ==== " + Calendar.DAY_OF_WEEK  + "\n" )
//                Log.d(Tag, " curent_Timemili ... " + " ====   time choose model.listtime ==== " + Calendar.DAY_OF_WEEK_IN_MONTH  + "\n")
//                Log.d(Tag, " curent_Timemili ... " + " ====   time choose model.listtime ==== " + Calendar.DAY_OF_YEAR + "\n" )

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
//        btn_settime.text = alarmText
    }

    override fun isDestroyed(): Boolean {
        return super.isDestroyed()
        realms!!.closeRealm()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}


