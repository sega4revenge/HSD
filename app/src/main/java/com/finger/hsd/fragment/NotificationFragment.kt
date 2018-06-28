package com.finger.hsd.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.finger.hsd.BaseFragment
import com.finger.hsd.R
import com.finger.hsd.activity.DetailProductActivity
import com.finger.hsd.adapters.NotificationAdapterKotlin
import com.finger.hsd.manager.RealmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_notification.view.*
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match





class NotificationFragment : BaseFragment(), NotificationAdapterKotlin.ItemClickListener {

    // TODO: Rename and change types of parameters

    private var mView: View? = null

    private lateinit var mNotifiAdapter: NotificationAdapterKotlin
    private lateinit var listitem: ArrayList<Notification>
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var realm: RealmController? = null

    var mNotificationBadgeListener: NotificationBadgeListener? = null
    var mContext: Context? = null
    var session : SessionManager? = null

//    fun newInstance(info: String): NotificationFragment {
//        val args = Bundle()
//        val fragment = NotificationFragment()
//        args.putString("info", info)
//        fragment.setArguments(args)
//        return fragment
//    }

    override fun onAttach(context: Activity) {
        super.onAttach(context)
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(context)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttachToContext(context)

    }

    fun onAttachToContext(context: Context) {
        if (context is NotificationBadgeListener) {
            mNotificationBadgeListener = context

        }
    }
    var temp: Int = 0
    var listNotWatch = ArrayList<Notification>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mView = inflater.inflate(R.layout.fragment_notification, container, false)

        realm = RealmController(activity?.application!!)

        mContext = activity

        initViews(mView!!)
        session = SessionManager(activity!!)
        setRealmAdapter()

        val strNotiCount = realm!!.countNotification().toString() + " " +activity!!.resources.getString(R.string.notification_not_see)
        mView!!.count_notification.text = strNotiCount

        mView!!.im_clear.setOnClickListener({
//            showProgress()
             listNotWatch = realm!!.listNotWatch()
            temp = 0
            if(listNotWatch.size >=0 && !listNotWatch.isEmpty() && temp < listNotWatch.size) {
                processWatchedNotification(listNotWatch[temp])
            }else{
//                sucess
                showSnack(R.string.clear_sucess,R.id.notification_root)
            }

//            hideProgress()

        })
        return mView
    }

    fun processWatchedNotification(notification: Notification){

            if (ConnectivityChangeReceiver.isConnected()) {
                updateNotficationOnServer(notification, 2)
            } else {

                temp++
                realm!!.realm.executeTransaction({
                    notification.isSync = false
                    notification.watched = true

                })
                if(listNotWatch.size >=0 && !listNotWatch.isEmpty() && temp < listNotWatch.size) {
                    processWatchedNotification(listNotWatch[temp])
                }else{
                    // sucess
                    showSnack(R.string.watched_all, R.id.notification_root)
                }
            }

    }

    fun setRealmAdapter() {
        listitem = realm!!.getListNotification()!!

        mNotifiAdapter = NotificationAdapterKotlin(mContext!!, listitem, realm!!, mNotificationBadgeListener!!, this)
        mView!!.recycler_notification.adapter = mNotifiAdapter
        mNotifiAdapter.notifyDataSetChanged()

        if (listitem.size<1){
            (mView!!.findViewById(R.id.ln_not_data) as LinearLayout).visibility = View.VISIBLE
        }else{
            (mView!!.findViewById(R.id.ln_not_data) as LinearLayout).visibility = View.GONE
        }
        //activity!!.startService(Intent(activity, NotificationService::class.java))
    }


    private fun initViews(mView: View) {

        listitem = java.util.ArrayList<Notification>()
        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        mView.recycler_notification.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        // mNotifiAdapter = NotificationAdapterKotlin(mContext!! ,listitem, realm!!, mNotificationBadgeListener!!, this )

        mView.recycler_notification.layoutManager = layoutManager

//        mRecyclerView.setHasFixedSize(true)
//
//        val mLayoutManager = LinearLayoutManager(activity)
//        mRecyclerView.setLayoutManager(mLayoutManager)
//        mRecyclerView.setNestedScrollingEnabled(false)
//        mRecyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
//        mRecyclerView.setItemAnimator(DefaultItemAnimator())

        // create an empty adapter and add it to the recycler view
//        listitem = realm!!.getListInfomation()
//        if(listitem!=null && !listitem.isEmpty()){

//        mNotifiAdapter.notifyDataSetChanged()


//        }else{
//            Mylog.d("aaaaaa: không có dữ liệu ")
//        }

//        mRecyclerView.addOnItemTouchListener(RecyclerTouchListener(activity, mRecyclerView, object : RecyclerTouchListener.ClickListener {
//            override fun onClick(view: View, position: Int) {
//                val infomationNoti = listitem.get(position)
//                infomationNoti.watched = true
//                val item = Notification()
//                item._id = position.toString()
//                item.watched = true
//                realm!!.addNotification(item)
//                Toast.makeText(getApplicationContext(), infomationNoti._id+ " is selected!", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onLongClick(view: View, position: Int) {
//
//            }
//        }))


//


    }

    interface NotificationBadgeListener {
        fun onBadgeUpdate(value: Int)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppIntent.REQUEST_NOTIFICATION) {
            if (resultCode == AppIntent.RESULT_DETAIL_PRODUCT) {
                val extras = data!!.extras

                val position = data.getIntExtra("position", -1)
                if (extras != null) {
                    Mylog.d("aaaaaaaaa " + position)
                    mNotifiAdapter.notifyItemChanged(position)

                }
            }
        }
    }

    override fun onItemClick(position: Int, product: Product_v) {
        val intent = Intent(activity, DetailProductActivity::class.java)
        intent.putExtra("checkNotification", true)
        intent.putExtra("position", position)
        intent.putExtra("id_product", product._id)

        Mylog.d("aaaaaaaaaa "+product._id)

        // startActivityForResult(intent, AppIntent.REQUEST_NOTIFICATION)
        val notification = realm!!.getOneNotification(product._id!!)
        if (ConnectivityChangeReceiver.isConnected()) {
            updateNotficationOnServer(notification!!, 2)
        } else {
            realm!!.realm.executeTransaction( {
                notification!!.isSync = false
                notification.watched = true

            })
            val strCountNoti =  realm!!.countNotification().toString() +  activity!!.resources.getString(R.string.notification_not_see)
            mView!!.count_notification.text = strCountNoti
        }
        startActivity(intent)
    }


    var appendChatScreenMsgReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            Mylog.d("communition  BroadcastReceiver chay ngay di")

            if (bundle != null) {
                if (bundle.getBoolean("updateItem")) {
                    //======= hàm này để load lại item vs position đã có
                    if (!bundle.getBoolean("reloaditem")) {

                        val position = bundle.getInt("position")

                        mNotifiAdapter.notifyItemChanged(position)
                        Mylog.d("communition  BroadcastReceiver  $position")

                    } else {
                        //======== hàm này để load lại toàn bộ item khi homeFragment chỉnh sửa item.
//                        listitem.clear()
//                        listitem = realm!!.getListNotification()!
                        val idProduct = bundle.getString("id_product")
                        for (i in 0..listitem.size - 1) {
                            val item = listitem[i]
                            if (item.id_product.equals(idProduct)) {
                                mNotifiAdapter.notifyItemChanged(i)
                                break
                            }
                        }

                        Mylog.d("communition  BroadcastReceiver  chay chua chau")

                    }
                    val strCountNoti =  realm!!.countNotification().toString() +  activity!!.resources.getString(R.string.notification_not_see)
                    mView!!.count_notification.text = strCountNoti
                } else if (bundle.getBoolean("deleteItem")) {
                    if (!bundle.getBoolean("reloadItem")) {
                        Mylog.d("communition  BroadcastReceiver  chay chua chau")
                        val position = bundle.getInt("position")
                        val item = listitem.get(position)
                        listitem.remove(item)
                        mNotifiAdapter.notifyDataSetChanged()

                    } else {

                        listitem.clear()
                        setRealmAdapter()

                    }
                    val strCountNoti =  realm!!.countNotification().toString() +  activity!!.resources.getString(R.string.notification_not_see)
                    mView!!.count_notification.text = strCountNoti
//============hàm này để khi có  thông báo là nó sẽ add vào list notification = alarm thông báo
                } else if (bundle.getBoolean("addnotification")) {

                    Mylog.d("aaaaaaaaaa da chay broadcash")
                    listitem.clear()
                    setRealmAdapter()
                    val strCountNoti =  realm!!.countNotification().toString() +  activity!!.resources.getString(R.string.notification_not_see)
                    mView!!.count_notification.text = strCountNoti

                    mNotificationBadgeListener!!.onBadgeUpdate(session!!.getCountNotification())

                }

 //========= hàm này để update lại thông báo khi xóa listProduct từ homeFragment
                else if (bundle.getBoolean("deleteListProduct")){
                    listitem.clear()
                    setRealmAdapter()
                    val strCountNoti =  realm!!.countNotification().toString() +  activity!!.resources.getString(R.string.notification_not_see)
                    mView!!.count_notification.text = strCountNoti
                }
            }
        }
    }

    fun updateNotficationOnServer(notification: Notification, type : Int) {
        Mylog.d("ttttttttt idproduct: "+notification.id_product)
        val user = realm!!.getUser()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("id_product", notification.id_product)
            jsonObject.put("idUser", user!!._id)
            jsonObject.put("type", notification.type)
            jsonObject.put("watched", true)
            jsonObject.put("status_expired", notification.status_expiry)
            jsonObject.put("time", notification.create_at)
        } catch (e: Exception) {
            Mylog.d("Error " + e.message)
        }
        Rx2AndroidNetworking.post(Constants.URL_UPDATE_NOTIFICATION)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("", " bytesSent : $bytesSent")
                    Log.d("", " bytesReceived : $bytesReceived")
                    Log.d("", " isFromCache : $isFromCache")
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response>() {
                    override fun onComplete() {

                    }

                    override fun onNext(t: Response?) {
                        Mylog.d("onsucess")
                        if (type == 2){

                            temp++
                            realm!!.realm.executeTransaction( {
                                notification.isSync = true
                                notification.watched = true

                            })
                            if(listNotWatch.size >=0 && !listNotWatch.isEmpty() && temp < listNotWatch.size) {
                                processWatchedNotification(listNotWatch[temp])
                            }else{
                                // success
                            }
                        }else {
                            realm!!.realm.executeTransaction( {
                                notification.isSync = true
                                notification.watched = false
                            })
                        }
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())
                        if (type==2){

                            temp++
                            realm!!.realm.executeTransaction({
                                notification.isSync = false
                                notification.watched = true

                            })
                            if(listNotWatch.size >=0 && !listNotWatch.isEmpty() && temp < listNotWatch.size) {
                                processWatchedNotification(listNotWatch[temp])
                            }else{
                                //sucess
                            }
                        }else {
                            realm!!.realm.executeTransaction( {
                                notification.isSync = false
                                notification.watched = true

                            })
                        }



                    }

                })

    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(this.appendChatScreenMsgReceiver)


    }

    override fun onResume() {
        super.onResume()
        activity!!.registerReceiver(this.appendChatScreenMsgReceiver, IntentFilter(AppIntent.ACTION_UPDATE_ITEM))

    }

}
