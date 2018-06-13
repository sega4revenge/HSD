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
        setContentView(R.layout.test_layout)

        val monthsList = ArrayList<String>()
        val months = DateFormatSymbols().getMonths()
        for (i in months.indices) {
            val month = months[i]
            println("month = $month")
            monthsList.add(months[i])
        }



    }



    fun UploadImage(idProduct: String, file: File) {



    }

}
