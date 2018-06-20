package com.finger.hsd.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import android.widget.RadioButton
import com.finger.hsd.BaseActivity
import com.finger.hsd.R
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Product_v
import com.finger.hsd.presenter.DetailProductPresenter
import com.finger.hsd.presenter.DetailProductPresenter.IDetailProductPresenterView
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_choose_day_notification.*
import org.json.JSONObject



class ChooseDayNotification: BaseActivity(), IDetailProductPresenterView{
    override fun onSucess(response: Product_v, type: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private lateinit var mRbCustom : RadioButton
    lateinit  var idProduct: String
    var realm: RealmController? = null

    var days : Int =0
    var dayIntent : Int =0
    lateinit var mToolbar : Toolbar

    private lateinit var  presenter: DetailProductPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_day_notification)

        realm = RealmController(this)
        idProduct = intent.getStringExtra("id_product")

        dayIntent = intent.getIntExtra("day_before", 0)

        presenter = DetailProductPresenter(this)

        mToolbar = findViewById(R.id.toolbar)
        this.setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        mToolbar.setTitleTextColor(Color.GRAY)
        mToolbar.setSubtitleTextColor(Color.GRAY)
        mToolbar.setNavigationIcon(R.drawable.ic_move_through)
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })

        if(dayIntent == 1){
            rb_oneday.isChecked
            rb_oneday.isChecked = true

        }else if (dayIntent == 3){
            rb_three.isChecked
            rb_three.isChecked = true
        }else if(dayIntent == 7){
            rb_week.isChecked
            rb_week.isChecked = true
        }else if(dayIntent == 30){
            rb_month.isChecked
            rb_month.isChecked = true
        }else {
            val texDay = resources.getString(R.string.custome_day)+" ("+dayIntent+" "+resources.getString(R.string.days) + ")"
            rb_custom.text =  texDay
            rb_custom.isChecked
            rb_custom.isChecked = true
        }

        rg.setOnCheckedChangeListener { radioGroup, i ->

            if(i == R.id.rb_oneday){
                days= 1
            }else if (i==R.id.rb_three){
                days = 3
            }else if (i== R.id.rb_week){
                days = 7
            }else if (i== R.id.rb_month){
                days = 30
            }else if (i== R.id.rb_custom){
                showDialogCustomDay(idProduct)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_choose_day_before, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.item_save) {
            if(days != dayIntent) {
                if (ConnectivityChangeReceiver.isConnected()) {
                    showProgress()
                    presenter.processDayBefore(idProduct, days)
                } else {
                    updateToRealm()
                }
            }else{
                finish()
            }
            return true
        }
        else if (item.itemId == R.id.home) {
            finish()
            return true
        }
        else return super.onOptionsItemSelected(item)

    }

    fun updateToRealm(){
        var product = realm!!.getProduct(idProduct)
        realm!!.realm.executeTransaction(Realm.Transaction {
            product!!.daybefore = days
            product!!.isSyn = false
        })

        Mylog.d("aaaaaaaaaa check: " + realm!!.getProduct(idProduct)!!.daybefore)
        hideProgress()
        val intent = Intent()
        intent.putExtra(Constants.DATA_DAY_BEFORE, days)
        setResult(Constants.RESULT_DAY_BEFORE, intent)
        finish()
    }

    var dayIndex: Int = 0

    private fun showDialogCustomDay(idProduct: String) {
        // dialog declare
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_choose_day_before)

        val data = resources.getStringArray(R.array.choose_days_before)

       val numberPicker: NumberPicker = dialog.findViewById(R.id.number_picker)
        numberPicker.minValue = 0
        numberPicker.maxValue = data.size - 1
        numberPicker.displayedValues = data


        numberPicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->

            dayIndex = newVal
        }

        val btnDialog_cancel = dialog.findViewById(R.id.bt_cancel) as Button
        val dialog_ok = dialog.findViewById(R.id.bt_ok) as Button
        // handle envent click button
        btnDialog_cancel.setOnClickListener( {

            dialog.dismiss()

        })

        dialog_ok.setOnClickListener({
            val textDay = resources.getString(R.string.custome_day)+" ("+data[dayIndex]+")"
            days = dayIndex+1
            rb_custom.text =  textDay
            dialog.dismiss()

        })
        dialog.show()
    }

    override fun onSucess(response: JSONObject, type: Int) {
        hideProgress()
        var product = realm!!.getProduct(idProduct)
        realm!!.realm.executeTransaction(Realm.Transaction {
            product!!.daybefore = days
            product.isSyn = true
        })

        val intent = Intent()
        intent.putExtra(Constants.DATA_DAY_BEFORE, days)
        setResult(Constants.RESULT_DAY_BEFORE, intent)
        finish()
    }

    override fun onError(typeError: Int) {
        updateToRealm()
    }
}