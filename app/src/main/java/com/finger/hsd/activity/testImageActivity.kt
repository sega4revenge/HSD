package com.finger.hsd.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.finger.hsd.R
import java.io.File
import java.text.DateFormatSymbols
import java.util.*




class testImageActivity : AppCompatActivity() {



    val calendar: Calendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_put_barcode_layout)

        val monthsList = ArrayList<String>()
        val months = DateFormatSymbols().getMonths()
        for (i in months.indices) {
            val month = months[i]
            println("month = $month")
            monthsList.add(months[i])
        }



    }

//    fun completeRefresh() {
//        sync.clearAnimation()
//        refreshItem.setActionView(null)
//    }
//    fun refresh() {
//        /* Attach a rotating ImageView to the refresh item as an ActionView */
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val iv = inflater.inflate(R.layout.animation_layout, null) as ImageView
//
//        val rotation = AnimationUtils.loadAnimation(this, R.anim.sync_animation)
//        rotation.repeatCount = Animation.INFINITE
//        iv.startAnimation(rotation)
//
//        refreshItem.setActionView(iv)
//
//        //TODO trigger loading
//    }

    fun UploadImage(idProduct: String, file: File) {



    }

}
