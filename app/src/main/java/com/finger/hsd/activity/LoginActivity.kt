package com.finger.hsd.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.finger.hsd.R
import com.finger.hsd.R.id.btn_login
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener {
            startActivity(Intent(this,HorizontalNtbActivity::class.java))
        }

    }
}
