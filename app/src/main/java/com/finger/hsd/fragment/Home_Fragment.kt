package com.finger.hsd.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.facebook.FacebookSdk
import com.finger.hsd.R
import com.finger.hsd.adapters.Main_list_Adapter
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Product
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.ApiUtils
import com.finger.hsd.util.Constants
import com.finger.hsd.util.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList




class Home_Fragment : Fragment(),Main_list_Adapter.OnproductClickListener,RealmController.updateData{
    override fun onupdateProduct(type: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mCount  = 0
    private var checkRefresh  = false
    private var mView: View? = null
    private var mRec: RecyclerView? = null
    private var mAdapter: Main_list_Adapter? = null
    private var mRetrofitService: RetrofitService? = null
    private var listProduct:ArrayList<Product_v>? = ArrayList<Product_v>()
    private var listheader:ArrayList<Int>? = ArrayList<Int>()
    private var mLayoutManager:LinearLayoutManager? =null
    private var myRealm: RealmController? = null
    private var mRefresh:SwipeRefreshLayout? = null
    private var mPositionEX = -1
    private var mPositionWaring = -1
    private var mPositionProtect = -1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        getData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mView = inflater.inflate(R.layout.fragment_blank, container, false)
        return mView
    }
    private fun initView() {
        mRec = mView?.findViewById(R.id.rec)
     //   mRefresh = mView?.findViewById(R.id.refresh)
        mLayoutManager =  LinearLayoutManager(activity)
        mRec?.layoutManager = mLayoutManager
        mRec?.setHasFixedSize(true)
        myRealm = RealmController(activity?.application!!)
//        mRefresh?.setOnRefreshListener {
//            if(!checkRefresh)
//            {
//                checkRefresh = true
//                getData()
//            }
//        }

    }

    override fun onResume() {
        super.onResume()
        if(mCount>0){
            mCount =0
        }
    }
    override fun onupdate() {
        Log.d("REALMCONTROLLER","update")
        loadData()
    }

    override fun onproductClickedDelete(listDelete: String?) {
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.deleteGroup(listDelete!!)?.enqueue(object: Callback<Result_Product>{
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    if(response?.code()==200){
                        listProduct?.clear()
                        mCount =0
                        getData()
                    }
                }else{
                    Toast.makeText(activity,"Error! \n message:"+response?.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
    private fun getData(){
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.getAllProductInGroup("5b0bb59da2c5e873632215a2")?.enqueue(object: Callback<Result_Product>{
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                loadData()
                Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    if(response?.code()==200){
                        Log.d("//////////",response?.body().listProduct.size.toString()+"//")
                        myRealm?.updateorCreateListProduct(response?.body().listProduct,this@Home_Fragment)

                    }
                }else{
                    Toast.makeText(activity,"Error! \n message:"+response?.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
    fun loadData(){
        listProduct = myRealm?.getlistProduct()
        if(listProduct != null && listProduct?.size!! > 0) {

//            var st = ""
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val stringToday = sdf.format(Date())
            val exToday = sdf.parse(stringToday)
            var miliexToday: Long = exToday.getTime()
//            var check_hethan = false
//
//            for (i in listProduct!!) {
//                if (st.indexOf(getDate(i.expiretime, "dd/MM/yyyy")) > 0) {
//                } else {
//                    if (miliexToday < i.expiretime) {
//                        st = st + " " + getDate(i.expiretime, "dd/MM/yyyy")
//                        mCount++
//                    } else {
//                        if (!check_hethan) {
//                            st = st + " " + getDate(i.expiretime, "dd/MM/yyyy")
//                            check_hethan = true
//                            mCount++
//                        }
//                    }
//                }
//            }
            listProduct?.sortWith(Comparator(fun(a: Product_v, b: Product_v): Int {
                if (a.expiretime < b.expiretime)
                    return -1
                if (a.expiretime > b.expiretime)
                    return 1
                return 0
            }))
             var listData = ArrayList<Product_v>()
             for (i in 0 until  listProduct!!.size) {
                 var dis = listProduct!!.get(i).getExpiretime() / 86400000 - miliexToday / 86400000
                 Log.d("///////////", dis.toString()+"//"+getDate(listProduct!!.get(i).expiretime,"dd/MM/yyyy"))
                 if(mPositionEX==-1 && dis<=0){
                     mPositionEX = i
                     listData.add(Product_v("","",0,"",""))
                     listheader?.add(i)
                 }
                 if(mPositionWaring==-1 && dis<10 && dis>0){
                     mPositionWaring = i
                     listheader?.add(i)
                     listData.add(Product_v("","",0,"",""))
                 }
                 if(mPositionProtect==-1 && dis>10){
                     mPositionProtect = i
                     listheader?.add(i)
                     listData.add(Product_v("","",0,"",""))
                 }
                 listData.add(listProduct!!.get(i))
             }
            for (i in 0 until  listheader!!.size) {
                Log.d("///////////", listheader!![i].toString())
            }
            mAdapter = Main_list_Adapter(activity, listData, listheader, this)
            mRec?.adapter = mAdapter
        }
    }
    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}
