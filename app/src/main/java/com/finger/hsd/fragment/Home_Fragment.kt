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
import com.finger.hsd.activity.Scanner_Barcode_Activity
import com.finger.hsd.adapters.Main_list_Adapter
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.*
import kotlinx.android.synthetic.main.not_found_product.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList




class Home_Fragment : Fragment(),Main_list_Adapter.OnproductClickListener,RealmController.updateData{
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
    private var numLoading = 0
    private var mRefresh:SwipeRefreshLayout? = null
    private var mPositionEX = -1
    private var mPositionWaring = -1
    private var mPositionProtect = -1
    private var mDialogProgress: Dialog? = null
    private var mDialogProgressDelete:Dialog? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        Log.d("REALMCONTROLLER","getDatagetDatagetDatagetData ")
        getData()
    }

    fun newInstance(info: String): Home_Fragment {
        val args = Bundle()
        val fragment = Home_Fragment()
        args.putString("info", info)
        fragment.setArguments(args)
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
     //   Toast.makeText(activity,arguments?.getString("searchkey")+"/123/",Toast.LENGTH_SHORT).show()
        if(arguments != null&& !arguments?.isEmpty!!){
            var mSearch = arguments?.getString("searchkey")
            searchKey(mSearch.toString())
        }

        mView = inflater.inflate(R.layout.fragment_blank, container, false)
        return mView
    }
     fun searchKey(key :String){
        listheader?.clear()
        mPositionEX = -1
        mPositionProtect = -1
        mPositionWaring = -1

        listProduct = myRealm?.getlistProductLike(key)
        if(key == ""){
            loadData()
        }else
        if(listProduct != null && listProduct?.size!! > 0) {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val stringToday = sdf.format(Date())
            val exToday = sdf.parse(stringToday)
            var miliexToday: Long = exToday.getTime()

            listProduct?.sortWith(Comparator(fun(a: Product_v, b: Product_v): Int {
                if (a.expiretime < b.expiretime)
                    return -1
                if (a.expiretime > b.expiretime)
                    return 1
                return 0
            }))
            var listData = ArrayList<Product_v>()
            for (i in 0 until  listProduct!!.size) {
                Log.d("SEARCHHHHHHHHHH",listProduct?.get(i)?.description+"//vvv")
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
                            myRealm?.deletelistproduct(arrID,this@Home_Fragment)
                          //  Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
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
        Log.d("REALMCONTROLLER","//zzzzzzzzzzz//showDialogNotFound//")
        var mDialog: Dialog? = null
        var dialog = AlertDialog.Builder(activity)
        val mView:View = View.inflate(activity,R.layout.not_found_product,null)
        dialog.setView(mView)
        var create = mView.lin_create
        dialog.setCancelable(false)
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
        mPositionEX = -1
        mPositionProtect = -1
        mPositionWaring = -1
        getDataFromServer()
    }
    override fun onupdateProduct(type: Int,product:Product_v) {
        Log.d("REALMCONTROLLER",type.toString()+"//")
        if(type==1){
            Log.d("REALMCONTROLLER","update")
            listheader?.clear()
            mPositionEX = -1
            mPositionProtect = -1
            mPositionWaring = -1
            loadData()
        }else  if(type==0){
            myRealm?.addProductWithNonImage(product,this)
        }

    }
    override fun onproductClickedDelete(listDelete: String?, arrID : List<Product_v>) {
        showDialogDelete(listDelete,arrID)
    }
    override fun onClickItem(product_v: Product_v,pos : Int) {
        Toast.makeText(activity,product_v.namechanged+"//"+product_v.description+"//"+pos,Toast.LENGTH_SHORT).show()
    }


    private fun getData(){
        if(ConnectivityChangeReceiver.isConnected()){
            if(myRealm?.getlistProductOffline()!= null && (myRealm?.getlistProductOffline()?.size!! > 0)){
                val arrDataNotSync = myRealm?.getlistProductOffline()
                numLoading = arrDataNotSync?.size!!
                showDialog("Đang đồng bộ dữ liệu...")
                for(i in 0 until arrDataNotSync.size){
                    Log.d("REALMCONTROLLE",arrDataNotSync.get(i).imagechanged+"//"+arrDataNotSync.get(i).namechanged+"//"+arrDataNotSync.get(i)!!.expiretime+"//"+arrDataNotSync.get(i)!!.description+"//"+arrDataNotSync.get(i)!!.barcode)
                    addProductOfflinetoServer(arrDataNotSync.get(i))
                }
               // loadData()
            }else{
                Log.d("REALMCONTROLLER","ConnectivityChangeReceiver ")
                getDataFromServer()
            }
        }else{
            if(myRealm?.getlistProduct()!!.size==0){
                showDialogNotFound()
            }else{
                loadData()
            }
        }
    }
    fun getDataFromServer(){
        Log.d("REALMCONTROLLER","getDataFromServer")
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.getAllProductInGroup("5b21d1f9fe313f03da828118")?.enqueue(object: Callback<Result_Product>{
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                if(myRealm?.getlistProduct()!!.size==0){
                    showDialogNotFound()
                }else{
                    loadData()
                }
                Toast.makeText(activity,"Error! \n message:"+t?.message, Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                            if(response.code()==200){
                                if(response.body().listProduct.size>0){
                            Log.d("REALMCONTROLLER",response.body().listProduct.size.toString()+"//listProduct")
                            showDialog("Đang đồng bộ dữ liệu...")
                            myRealm?.updateorCreateListProduct(response.body().listProduct,this@Home_Fragment)
                        }else if(myRealm?.getlistProduct()!!.size==0){
                            showDialogNotFound()
                        }
                    }
                }else{
                    Toast.makeText(activity,"Error! \n message:"+response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
    fun addProductOfflinetoServer(mProduct_v: Product_v){
        val mediaFile = File(mProduct_v.imagechanged)

        val requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), CompressImage.compressImage(mediaFile,activity?.applicationContext!!))
        val photoproduct =
                MultipartBody.Part.createFormData("image", mediaFile.getName(), requestFile)

        val nameproduct =
                RequestBody.create(MediaType.parse("multipart/form-data"), mProduct_v.namechanged)
        val hsd_ex =
                RequestBody.create(MediaType.parse("multipart/form-data"),mProduct_v.expiretime.toString())
        val detail =
                RequestBody.create(MediaType.parse("multipart/form-data"),mProduct_v.description)
        val barcodenum =
                RequestBody.create(MediaType.parse("multipart/form-data"),mProduct_v.barcode)

        val iduser =
                RequestBody.create(MediaType.parse("multipart/form-data"),"5b21d1f9fe313f03da828118")
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.addProduct(nameproduct,barcodenum,hsd_ex,detail,iduser,photoproduct)?.enqueue(object: Callback<Result_Product>{
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                numLoading--
                Toast.makeText(activity,"Error! \n message:"+t?.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    if(response?.code()==200){
                        response?.body().product.imagechanged = mProduct_v.imagechanged
                        response?.body().product.isDelete = false
                        response?.body().product.isSyn = true
                        response?.body().product.barcode = response?.body().product.producttype_id.barcode
                        myRealm?.deleteProductFromDevice(mProduct_v,this@Home_Fragment)
                        myRealm?.addProduct(response?.body().product)
                        mDialogProgress?.dismiss()
                        numLoading--
                        if(numLoading==0){
                            loadData()
                        }
                    }
                }else{
                    numLoading--
                    Toast.makeText(activity,"Error! Code:"+response?.code()+"\n message:"+response?.message(),Toast.LENGTH_SHORT).show()
                }
            }

        })
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppIntent.REQUEST_DETAIL_PRODUCT) {
            if (resultCode == AppIntent.RESULT_UPDATE_ITEM) {



                var extras = data!!.extras
                if(extras !=null){
                   var product =  extras.getSerializable("product_v") as Product_v

                }

            }
        }
    }

}
