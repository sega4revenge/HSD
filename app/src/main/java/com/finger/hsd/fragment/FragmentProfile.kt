package com.finger.hsd.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.finger.hsd.BaseFragment
import com.finger.hsd.R
import com.finger.hsd.activity.AlarmReceiver
import com.finger.hsd.activity.AlarmSetting
import com.finger.hsd.activity.LoginActivity
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.User
import com.finger.hsd.util.Mylog
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.File
import java.util.*


class FragmentProfile : BaseFragment(), View.OnClickListener  {

    lateinit var sessionManager: SessionManager
    private val SECOND_ACTIVITY_REQUEST_CODE = 99
    var realms: RealmAlarmController? = null
    // alarm
    private val milDay = 86400000L

    internal lateinit var alarmManager: AlarmManager
    private var pending_intent: PendingIntent? = null

    internal var a = ""

    internal var mMediaPlayer: MediaPlayer? = null
    var res: Resources? = null

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


    private val listItems = arrayOf("off", "dingdong", "kakaotalk","triangle", "waterdrop","arp", "consequence", "filling",
            "good", "whistle", "oringz", "plucky","impressed", "spring", "successful", "tethys", "calling",
             "wish", "informed")

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

        var user: User? = realms!!.getSingleUser()
        if(user!!.type_login == 1) {
            v.tv_type.text = "Facebook"
        }else if(user!!.type_login == 2){
            v.tv_type.text = "Google"
        }else{
            v.tv_type.text = activity!!.resources.getString(R.string.hsd_account)
        }
        // get user
        var listUser =  realms!!.getUser()
        for (index in listUser.indices) {
            val model = listUser.get(index)
            val countCharac = model.phone!!.length
            val str = model.phone!!.substring(countCharac - 4)
            v.phone_user.text = "* * * * * "+str
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
        v.log_out.setOnClickListener(this)

//        v.txt2_Expiry.setOnClickListener(this)
        v.rateApp.setOnClickListener(this)

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

            setAlarmText(activity!!.resources.getString(R.string.not_in_use))

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
                val message = ("\n" +activity!!.resources.getString(R.string.share_app_invite) +" "+" https://play.google.com/store/apps/details?id=com.finger.hsd&hl=vi")
                shareAction(message)
                }
            R.id.send_feedback -> {
                sendEmailFeedback()
            }
//            R.id.export_file->{
//
//            }
            R.id.log_out ->{
                DialogLogout()
            }
//            R.id.txt_FAQs->{
////                loadWebpage()
//            }
            R.id.rateApp->{
                    rate()
            }

        }
    }


    private fun rate(){
//        showProgress()
//        val fixedRateTimer = fixedRateTimer(name = "hello-timer",
//                initialDelay = 100, period = 100) {
//            println("hello world!")
//        }
//        try {
//            Thread.sleep(10000)
//        } finally {
            val uri = Uri.parse("market://details?id=com.finger.hsd")
            Log.i("URI RATE APP", uri.toString())
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.finger.hsd&hl=vi")))
            }
//            fixedRateTimer.cancel()
//            println("finish!")
//            hideProgress()
//
//        }



    }

//    fun loadWebpage() {
//        webview.loadUrl("")
//        val uri: Uri
//        try {
//            uri = buildUri(uriText.text.toString())
//            webview.loadUrl(uri.toString())
//        } catch(e: UnsupportedOperationException) {
//            e.printStackTrace()
//        }
//    }

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

    private fun sendEmailFeedback() {
        val subject = activity!!.resources.getString(R.string.feedback)
        val mailto = "mailto:conghuancse@gmail.com" +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode("")
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse(mailto)
        hideProgress()
        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    private fun sendEmail(message : String) {
        val filename = "contacts_sid.vcf"
        val filelocation = File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename)
        val path = Uri.fromFile(filelocation)
        val emailIntent = Intent(Intent.ACTION_SEND)
        // set the type to 'email'
//        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("conghuancse@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")

        emailIntent.putExtra(Intent.EXTRA_TEXT, message)
//        startActivity(Intent.createChooser(emailIntent, "Send email..."))
        emailIntent.data = Uri.parse("mailto:$to")
        startActivity(emailIntent)
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
                    setAlarmText(activity!!.resources.getString(R.string.not_in_use))
                }
                realms!!.closeRealm()
            }
        }
    }

    private fun SettingAlarm(hour: Int, boolean: Boolean) {

        val calendars = Calendar.getInstance()
        val now = Calendar.getInstance()

        if(calendars.timeInMillis < now.timeInMillis){
            calendars.set(Calendar.DAY_OF_MONTH,  calendars.get(Calendar.DAY_OF_MONTH)+1)
        }

        val myIntent = Intent(context, AlarmReceiver::class.java)
        myIntent.putExtra("extra", "yes")
        calendars.set(Calendar.MINUTE, 0)
        calendars.set(Calendar.SECOND, 0)
        calendars.set(Calendar.HOUR_OF_DAY, hour)

        if (boolean) {
//            Log.d("FragmentProfile", "boolean..true  ====>>>>    " + boolean + " =====  " + hour)
            pending_intent = PendingIntent.getBroadcast(activity, hour, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars.timeInMillis, milDay, pending_intent)

        } else {
//            Log.d("FragmentProfile", "boolean..false  ====>>>>>>    " + boolean + " =====  " + hour)
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

    private fun DialogLogout() {
        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle(activity!!.resources.getString(R.string.you_want_logout))
        mBuilder.setPositiveButton(activity!!.resources.getString(R.string.ok)) { dialog, which ->
            // Do something when click the neutral button


            val product = realms!!.getDataProduct()
            if(product!=null && !product.isEmpty()) {
                for (i in 0..product.size - 1) {
                    val namePassive = Uri.parse(product[i].imagechanged)
//                                        val namePassive = product!!.imagechanged
                    var myDir = File(namePassive.path)

                    if (myDir.exists()) {

                        myDir.delete()
                    } else {

                    }
                }

                realms!!.deleteAllData()
                if(realms!=null){
                    sessionManager.setLogin(false)
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity!!.finish()
                }

            }else{
                realms!!.deleteAllData()
                if(realms!=null){
                    sessionManager.setLogin(false)
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity!!.finish()
                }

            }




            dialog.cancel()
        }

        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton(activity!!.resources.getString(R.string.cancel)) { dialog, which ->
            // Do something when click the neutral button-
            dialog.cancel()
        }


        val mDialog = mBuilder.create()
        mDialog.show()
    }
    //    @SuppressLint("InflateParams")
    private fun showDialogSound(id :Int) {
        var sound : String?  = null
        var soundOff : Boolean? = false

        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle(activity!!.resources.getString(R.string.choose_an_item))

        mBuilder.setSingleChoiceItems(listItems, id ) { _, i ->
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
        mBuilder.setPositiveButton(activity!!.resources.getString(R.string.ok)) { dialog, which ->
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
        mBuilder.setNeutralButton(activity!!.resources.getString(R.string.cancel)) { dialog, which ->
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




