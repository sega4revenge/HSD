package com.finger.hsd.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.afollestad.materialdialogs.MaterialDialog
import com.finger.hsd.R
import com.finger.hsd.activity.DetailProductActivity
import com.finger.hsd.activity.Scanner_Barcode_Activity
import com.finger.hsd.adapters.Main_list_Adapter
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.ApiUtils
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.Mylog
import com.finger.hsd.util.RetrofitService
import kotlinx.android.synthetic.main.not_found_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private var mDialogProgress: Dialog? = null
    private var mDialogProgressDelete:Dialog? = null
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
        Mylog.d("aaaaaaaa jaja "+ myRealm!!.countNotification())

    }

    override fun onResume() {
        super.onResume()
        if(mCount>0){
            mCount =0
        }
    }
    private fun showDialog(title: String) {
        val m = MaterialDialog.Builder(activity!!)
                .content(title)
                .cancelable(false)
                .progress(true, 0)
        mDialogProgress = m.show()
    }
    fun showDialogDelete(listDelete: String?, arrID : List<Product_v>){
        val dialogdelete = MaterialDialog.Builder(activity!!)
                .title("Bạn muốn xóa toàn bộ sản phẩm này?")
                .content("Nhấn OK để xóa toàn bộ sản phẩm này")
                .cancelable(false)
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive { dialog, which ->
                    showDialog("Đang xóa sản phẩm...")
                    mRetrofitService = ApiUtils.getAPI()
                    mRetrofitService?.deleteGroup(listDelete!!)?.enqueue(object: Callback<Result_Product>{
                        override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                            Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                            if(response?.isSuccessful!!){
                                if(response?.code()==200){
                                    myRealm?.deletelistproduct(arrID,this@Home_Fragment)
                                }
                            }else{
                                Toast.makeText(activity,"Error! \n message:"+response?.message(), Toast.LENGTH_SHORT).show()
                            }
                            mDialogProgressDelete?.dismiss()
                        }

                    })
                }

        mDialogProgressDelete = dialogdelete.build()
        mDialogProgressDelete?.show()
    }
    fun showDialogNotFound(){
        var mDialog: Dialog? = null
        var dialog = AlertDialog.Builder(activity)
        val mView:View = View.inflate(activity,R.layout.not_found_product,null)
        dialog.setView(mView)
        var create = mView.lin_create
        create.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val i = Intent(activity,Scanner_Barcode_Activity::class.java)
                startActivity(i)
                mDialog?.dismiss()
            }
        })
        mDialog = dialog.create()
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()
    }
    override fun onupdate() {
        Log.d("REALMCONTROLLER","update")
        listheader?.clear()
        mPositionEX = -1
        mPositionProtect = -1
        mPositionWaring = -1
        loadData()
    }

    override fun onupdateDelete() {
        mDialogProgress?.dismiss()
        listProduct?.clear()
        mCount =0
        getData()
    }

    override fun onproductClickedDelete(listDelete: String?, arrID : List<Product_v>) {
        showDialogDelete(listDelete,arrID)

    }



    private fun getData(){

        loadData()
//
//            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
//                if(response?.isSuccessful!!){
//                    if(response?.code()==200){
//                        if(response?.body().listProduct.size>0){
//                            Log.d("REALMCONTROLLER",response?.body().listProduct.size.toString()+"//listProduct")
//                            showDialog("Đang đồng bộ dữ liệu...")
//                            myRealm?.updateorCreateListProduct(response?.body().listProduct,this@Home_Fragment)
//                        }else if(myRealm?.getlistProduct()!!.size==0){
//                            showDialogNotFound()
//                        }
//                    }
//                }else{
//                    Toast.makeText(activity,"Error! \n message:"+response?.message(), Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        })
//        mRetrofitService = ApiUtils.getAPI()
//        mRetrofitService?.getAllProductInGroup("5b14b582c040310f42d8e0ee")?.enqueue(object: Callback<Result_Product>{
//            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
//                if(myRealm?.getlistProduct()!!.size==0){
//                    showDialogNotFound()
//                }else{
//                    loadData()
//                }
//                Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
//                if(response?.isSuccessful!!){
//                    if(response?.code()==200){
//                        if(response?.body().listProduct.size>0){
//                            Log.d("REALMCONTROLLER",response?.body().listProduct.size.toString()+"//listProduct")
//                            showDialog("Đang đồng bộ dữ liệu...")
//                            myRealm?.updateorCreateListProduct(activity!!, response?.body().listProduct,this@Home_Fragment)
//                        }else if(myRealm?.getlistProduct()!!.size==0){
//                            showDialogNotFound()
//                        }
//                    }
//                }else{
//                    Toast.makeText(activity,"Error! \n message:"+response?.message(), Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == AppIntent.REQUEST_DETAIL_PRODUCT){
            if(resultCode == AppIntent.RESULT_UPDATE_ITEM){

                var product_v: Product_v = data!!.getSerializableExtra("product_v") as Product_v

                val position = data!!.getIntExtra("position", -1)

                if (position != -1){
                    var st = ""
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    val stringToday = sdf.format(Date())
                    val exToday = sdf.parse(stringToday)
                    var miliexToday:Long = exToday.getTime()
                    var check_hethan = false


                    Mylog.d("aaaaaaaaa "+product_v.namechanged +" imae: "+product_v.imagechanged + " expriredTime: "+product_v.expiretime + " position: "+position)

                    if(st.indexOf(getDate(product_v.expiretime,"dd/MM/yyyy"))>0){
                    }else{
                        if(miliexToday<product_v.expiretime){
                            st = st +" "+ getDate(product_v.expiretime,"dd/MM/yyyy")
                            mCount++
                        }else{
                            if(!check_hethan){
                                st = st +" "+ getDate(product_v.expiretime,"dd/MM/yyyy")
                                check_hethan = true
                                mCount++
                            }
                        }
                    }
                    mAdapter!!.notifyItemChanged(position)

                    listProduct?.sortWith(Comparator(fun(a: Product_v, b: Product_v): Int {
                        if (a.expiretime<b.expiretime)
                            return -1
                        if (a.expiretime>b.expiretime)
                            return 1
                        return 0
                    }))

                }

            }else if (resultCode == AppIntent.RESULT_DELETE_ITEM){
               var product_v = data!!.getSerializableExtra("product_v")
                var position = data!!.getIntExtra("position", -1)
                listProduct!!.remove(product_v)
                mAdapter!!.notifyItemRemoved(position)
            }
        }
    }

    fun loadData(){
        listProduct = myRealm?.getlistProduct()
        if(listProduct != null && listProduct?.size!! > 0) {

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
                 if(mPositionEX==-1 && dis<=0){
                     mPositionEX = i
                     listData.add(Product_v("1","","",0,"",""))
                     listheader?.add(i)
                 }
                 if(mPositionWaring==-1 && dis<10 && dis>0){
                     mPositionWaring = i
                     listheader?.add(i)
                     listData.add(Product_v("1","","",0,"",""))
                 }
                 if(mPositionProtect==-1 && dis>10){
                     mPositionProtect = i
                     listheader?.add(i)
                     listData.add(Product_v("1","","",0,"",""))
                 }
                 listData.add(listProduct!!.get(i))
             }

            mAdapter = Main_list_Adapter(activity, listData, listheader, this)
            mRec?.adapter = mAdapter
            mDialogProgress?.dismiss()
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

    override fun onClickItem(position: Int) {
        var product = listProduct!!.get(position)

        var intent = Intent(activity, DetailProductActivity::class.java )
            intent.putExtra("id_product", product._id)
        intent.putExtra("position", position)

        startActivityForResult(intent, AppIntent.REQUEST_DETAIL_PRODUCT)

    }

}
