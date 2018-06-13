package com.finger.hsd.activity

import android.app.AlarmManager
import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.finger.hsd.R
import com.finger.hsd.adapters.CustomAdapter
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.Sound
import com.finger.hsd.model.TimeAlarm
import kotlinx.android.synthetic.main.alarm_setting.*
import kotlinx.android.synthetic.main.sound_setting_layout.*

class SoundSetting  : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {


    var realms : RealmAlarmController?= null
    lateinit var mCustomAdapter: CustomAdapter
    lateinit var sessionManager : SessionManager

    internal var listSound : List<Sound> = ArrayList<Sound>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_setting_layout)

        sessionManager = SessionManager(applicationContext)
        realms = RealmAlarmController(this)

//        realms!!.view_to_database()
//        if (realms==null){
//        realms!!.DatabseLlistSound()
//        }
        switch_sound.isChecked = sessionManager.get_open_Alarm()

        switch_sound.setOnCheckedChangeListener(this)
        btn_save.setOnClickListener(this)

//        listSound = realms!!.view_to_dataSound()!!

//        Log.d("", "  sessionManager ......." + realms!!.view_to_dataSound())

//        mCustomAdapter = CustomAdapter(this, listSound)
//        list_TimeSetting.setAdapter(mCustomAdapter)

    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

    }

    override fun onClick(p0: View?) {



    }

}