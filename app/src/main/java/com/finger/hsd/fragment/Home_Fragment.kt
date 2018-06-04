package com.finger.hsd.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.finger.hsd.R
import com.finger.hsd.adapters.Main_list_Adapter
import com.finger.hsd.model.Product
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.ApiUtils
import com.finger.hsd.util.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList




class Home_Fragment : Fragment(){
    private var mCount  = 0
    private var mView: View? = null
    private var mRec: RecyclerView? = null
    private var mAdapter: Main_list_Adapter? = null
    private var mRetrofitService: RetrofitService? = null
    private var listProduct:ArrayList<Product_v>? = ArrayList<Product_v>()
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
    }

    override fun onResume() {
        super.onResume()
        if(mCount>0){
            mCount =0
        }
    }

    private fun getData(){
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.getAllProductInGroup("5b0bb59da2c5e873632215a2")?.enqueue(object: Callback<Result_Product>{
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    if(response?.code()==200){
                        Log.d("response----------",response?.body().listProduct.get(0)._id)
                        listProduct = response?.body().listProduct
                        loadData()
                    }
                }else{
                    Toast.makeText(activity,"Error! \n message:"+response?.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
    fun loadData(){
        var st = ""
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val stringToday = sdf.format(Date())
        val exToday = sdf.parse(stringToday)
        var miliexToday:Long = exToday.getTime()
        var check_hethan = false

        for(i in listProduct!!){
            if(st.indexOf(getDate(i.expiretime,"dd/MM/yyyy"))>0){
            }else{
                if(miliexToday<i.expiretime){
                    st = st +" "+ getDate(i.expiretime,"dd/MM/yyyy")
                    mCount++
                }else{
                    if(!check_hethan){
                        st = st +" "+ getDate(i.expiretime,"dd/MM/yyyy")
                        check_hethan = true
                        mCount++
                    }
                }
            }
        }
        listProduct?.sortWith(Comparator(fun(a: Product_v, b: Product_v): Int {
            if (a.expiretime<b.expiretime)
                return -1
            if (a.expiretime>b.expiretime)
                return 1
            return 0
        }))
        var mLayoutManager =  LinearLayoutManager(activity)
        mRec?.layoutManager = mLayoutManager
        mRec?.setHasFixedSize(true)
        mAdapter = Main_list_Adapter(activity,listProduct,mCount)
        mRec?.adapter = mAdapter
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
