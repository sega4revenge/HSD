package com.finger.hsd.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.finger.hsd.R
import com.finger.hsd.activity.AlarmReceiver
import com.finger.hsd.activity.AlarmSetting
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.File
import java.util.*


class FragmentProfile : Fragment(), View.OnClickListener {


    lateinit var sessionManager: SessionManager


    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    var realms: RealmAlarmController? = null
    // alarm

    private val milDay = 86400000L
    private var mMinute: Int = 0
    private var mHour: Int = 0
    private var mTime: String? = null
    internal lateinit var alarmManager: AlarmManager
    private var pending_intent: PendingIntent? = null

    internal var a = ""
    internal var nameSound = ""
    internal var mMediaPlayer: MediaPlayer? = null
    var res: Resources? = null
    lateinit var soundSession: String
    private var cheked : Int? = null

    lateinit var txtview: TextView
    lateinit var txtViewSound: TextView
    fun newInstance(info: String): FragmentProfile {
        val args = Bundle()
        val fragment = FragmentProfile()
        args.putString("info", info)
        fragment.setArguments(args)
        return fragment
    }

    private val listItems = arrayOf("off","suri_big_robot", "suri_chipmunk", "suri_creature", "suri_death",
            "suri_deep", "suri_grand", "suri_helium", "suri_robot", "suri_squirrel")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_profile, container, false)
        val v = inflater.inflate(R.layout.fragment_profile, null)

        sessionManager = SessionManager(v.context)
        txtview = v.txt_viewExpiry
        txtViewSound = v.txt_ViewSound

        res  =resources

//        btn_yes = v.btn_yes
//        btn_no = v.btn_no

        realms = RealmAlarmController.with(this)
        realms!!.DatabseLlistAlarm()

        // get user
        var listUser =  realms!!.getUser()
        for (index in listUser!!.indices) {
            val model = listUser.get(index)
            v.phone_user.text = model.phone
        }
        // dem so luong san pham
        var number = realms!!.numberproduct()
        if(number>0){
            v.txt_numberProduct.text  = number.toString()
        }else{
            v.txt_numberProduct.text  = "0"
        }

        v.txt_Expiry.setOnClickListener {
            val intent = Intent(activity, AlarmSetting::class.java)
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
        }

        v.txt_sound.setOnClickListener(this)
        v.txt_share.setOnClickListener(this)
        v.send_feedback.setOnClickListener(this)

//        v.export_file.setOnClickListener(this)

        Log.d("FragmentProfile", "list... " + listUser)
        Log.d("FragmentProfile", "sessionManager... " + sessionManager.get_open_Alarm())

        if (sessionManager.get_open_Alarm()) {
            val list = realms!!.view_to_dataTimeAlarm()
            var output: String? = ""
            for (index in list!!.indices) {
                val model = list.get(index)
                if (model.isSelected!!) {
//                    SettingAlarm(model.listtime!!.toInt(), true)
                    if (!output.equals("")) {
                        output += ", "
                    }
                    output += model.listtime.toString() + "h"

                }
//                else{
//
//                    SettingAlarm(model.listtime!!.toInt(), false)
//                }
            }
            setAlarmText(output!!)

        } else {

            setAlarmText("Not in use")

        }

       setSoundText(sessionManager.get_Sound())
//        txt_sound.setOnClickListener({
//        })

        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        realms!!.closeRealm()
    }

    override fun onClick(p0: View?) {
        when (p0!!.getId()) {
            R.id.txt_sound -> {
                for (i in listItems.indices){
                    if(sessionManager.get_Sound().equals(listItems.get(i))){
                        cheked = i
                    }
                }

                showDialogSound(cheked!!)
            }
            R.id.txt_share -> {
                    val message = ("\n" + "I want invite you to join me on HSD. Please click the link to download. "+" https://play.google.com/store/apps/details?id=com.finger.suri&hl=vi")
                    shareAction(message)
            }
            R.id.send_feedback -> {
                sendEmail()
            }

//            R.id.export_file->{
//
//            }

        }

    }

//    private fun ExportToExcel(){
//
//        val workbook = XSSFWorkbook()
//        val createHelper = workbook.getCreationHelper()
//
//        val sheet = workbook.createSheet("Customers")
//
//        val headerFont = workbook.createFont()
//        headerFont.setBold(true)
//        headerFont.setColor(IndexedColors.BLUE.getIndex())
//
//        val headerCellStyle = workbook.createCellStyle()
//        headerCellStyle.setFont(headerFont)
//
//        // Row for Header
//        val headerRow = sheet.createRow(0)
//
//        // Header
//
//
//        for (col in COLUMNs.indices) {
//            val cell = headerRow.createCell(col)
//            cell.setCellValue(COLUMNs[col])
//            cell.setCellStyle(headerCellStyle)
//        }
//
//        // CellStyle for Age
//        val ageCellStyle = workbook.createCellStyle()
//        ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"))
//
//        var rowIdx = 1
//        for (customer in customers) {
//            val row = sheet.createRow(rowIdx++)
//            row.createCell(0).setCellValue(customer.id)
//            row.createCell(1).setCellValue(customer.name)
//            row.createCell(2).setCellValue(customer.address)
//            val ageCell = row.createCell(3)
//            ageCell.setCellValue(customer.age.toDouble())
//            ageCell.setCellStyle(ageCellStyle)
//        }
//
//        val fileOut = FileOutputStream("customers.xlsx")
//        workbook.write(fileOut)
//        fileOut.close()
//        workbook.close()
//    }

    private fun sendEmail() {
        val filename = "contacts_sid.vcf"
        val filelocation = File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename)
        val path = Uri.fromFile(filelocation)
        val emailIntent = Intent(Intent.ACTION_SEND)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("conghuancse@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) { // Activity.RESULT_OK
                val returnSettingAlarm = data?.getStringExtra("keyAlarm")
                alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                // Session manager == true (mo)
                if (sessionManager.get_open_Alarm()) {

                    val list = realms!!.view_to_dataTimeAlarm()
                    var output: String? = ""
                    for (index in list!!.indices) {

                        val model = list.get(index)

                        if (model.isSelected!!) {
                            SettingAlarm(model.listtime!!.toInt(), true)
                            if (!output.equals("")) {
                                output += ", "
                            }
                            output += model.listtime.toString() + "h"

                        } else {
                            SettingAlarm(model.listtime!!.toInt(), false)
                            Log.d("FragmentProfile","model..false  ====    " + model.listtime)
                        }


                    }

                    setAlarmText(output!!)

                } else {
                    val list = realms!!.view_to_dataTimeAlarm()
                    for (index in list!!.indices) {
                        val model = list.get(index)
                        if (model.isSelected!!) {
                            SettingAlarm(model.listtime!!.toInt(), false)
                        }
                    }
                    setAlarmText("Not in use")
                }
                realms!!.closeRealm()
            }
        }
    }

    private fun SettingAlarm(hour: Int, boolean: Boolean) {

        val calendars = Calendar.getInstance()
        val now = Calendar.getInstance()
        val myIntent = Intent(context, AlarmReceiver::class.java)
        myIntent.putExtra("extra", "yes")
        calendars.set(Calendar.MINUTE, 0)
        calendars.set(Calendar.SECOND, 0)
        calendars.set(Calendar.HOUR_OF_DAY, hour)

        Log.d("FragmentProfile", " 1 calendars.timeInMillis+milDay ====>>>>    " +   calendars.timeInMillis)

        if(calendars.timeInMillis < now.timeInMillis){

//            calendars.timeInMillis+milDay
            calendars.set(Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR+1)
            Log.d("FragmentProfile", " 2 DAY_OF_WEEK ====>>>>    " +   calendars.timeInMillis)
        }

        Log.d("FragmentProfile", "  calendars.timeInMillis+milDay ====>>>>    " +   calendars.timeInMillis+milDay + " =====  " + hour)

        if (boolean) {
            Log.d("FragmentProfile", "boolean..true  ====>>>>    " + boolean + " =====  " + hour)
            pending_intent = PendingIntent.getBroadcast(activity, hour, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars.timeInMillis, milDay, pending_intent)

        } else {
            Log.d("FragmentProfile", "boolean..false  ====>>>>>>    " + boolean + " =====  " + hour)
            pending_intent = PendingIntent.getBroadcast(activity, hour, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.cancel(pending_intent)


        }
    }

    private fun setAlarmText(alarmText: String) {
        txtview.text = alarmText
    }


    private fun setSoundText(soundText: String) {

        if (soundText != "off") {
//            setSoundText(sessionManager.get_Sound())
            txtViewSound.text = soundText

        } else {

            txtViewSound.text = resources.getString(R.string.not_in_use)
        }

    }

    //    @SuppressLint("InflateParams")
    private fun showDialogSound(id :Int) {
        var sound : String?  = null
        var soundOff : Boolean? = false

        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle("Choose an item")

        mBuilder.setSingleChoiceItems(listItems, id ) { dialogInterface, i ->
            Log.d(" choose ... ", " Choose an item " + listItems[i])

            if(listItems[i]  != "off"){
                sound = listItems[i]
                soundOff = true
                val soundId = res!!.getIdentifier(listItems[i], "raw", activity!!.packageName)
                if (mMediaPlayer != null) {
                    mMediaPlayer!!.release()
                }
                mMediaPlayer = MediaPlayer.create(activity, soundId)
                mMediaPlayer!!.start()
            }else{
                sound = "off"
                soundOff = false
            }





        }
        mBuilder.setPositiveButton("OK") { dialog, which ->
            // Do something when click the neutral button
            if (mMediaPlayer != null && soundOff!!) {
                mMediaPlayer!!.release()
            }

            if(sound!=null){
                    sessionManager.set_Sound(sound!!)
                    setSoundText(sessionManager.get_Sound())
            }

            dialog.cancel()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            if (mMediaPlayer != null) {
                mMediaPlayer!!.release()
            }
            dialog.cancel()
        }


        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun shareAction(message: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(share, resources.getString(R.string.title_share)))
    }

}




