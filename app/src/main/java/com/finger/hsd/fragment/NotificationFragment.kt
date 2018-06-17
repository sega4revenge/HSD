package com.finger.hsd.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.finger.hsd.BaseFragment
import com.finger.hsd.R
import com.finger.hsd.activity.DetailProductActivity
import com.finger.hsd.adapters.NotificationAdapterKotlin
import com.finger.hsd.common.Prefs
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.Mylog
import kotlinx.android.synthetic.main.fragment_notification.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BlankFragment2.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BlankFragment2.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NotificationFragment : BaseFragment(), NotificationAdapterKotlin.ItemClickListener {

    // TODO: Rename and change types of parameters

    private var mView: View? = null

    private lateinit var mNotifiAdapter: NotificationAdapterKotlin
    private lateinit var listitem: ArrayList<Notification>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val pageToDownload: Int = 0
    private var realm: RealmController? = null
    private var prefs: Prefs? = null
    var mNotificationBadgeListener: NotificationBadgeListener? = null
    var mContext: Context? = null

    fun newInstance(info: String): NotificationFragment {
        val args = Bundle()
        val fragment = NotificationFragment()
        args.putString("info", info)
        fragment.setArguments(args)
        return fragment
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mView = inflater.inflate(R.layout.fragment_notification, container, false)

        realm = RealmController(activity?.application!!)
        prefs = Prefs(activity);
        mContext = activity

        initViews(mView!!)
        setRealmAdapter()

        mView!!.im_clear.setOnClickListener(View.OnClickListener {
            showProgress()
            realm!!.setWatchedNotification()
            hideProgress()

        })
        return mView
    }

    fun setRealmAdapter() {
        listitem = realm!!.getListNotification()!!

        mNotifiAdapter = NotificationAdapterKotlin(mContext!!, listitem, realm!!, mNotificationBadgeListener!!, this)
        mView!!.recycler_notification.adapter = mNotifiAdapter
        mNotifiAdapter.notifyDataSetChanged()


        //activity!!.startService(Intent(activity, NotificationService::class.java))
    }

    fun addOneNotification() {

        var notification = Notification()
        var user = realm!!.getUser("5b06897ebb966e07b4fbd91a")!!
        notification._id = System.currentTimeMillis().toString()
//        notification.iduser = user
        notification.create_at = System.currentTimeMillis().toString()
        var product = Product_v()
        product = realm!!.getProduct("5b17f6daea81a8639de962ea")!!

        notification.type = 0 // 1 single, 2 multi
        notification.watched = false;
        realm!!.addNotification(notification)
        listitem.add(0, notification)

        mView!!.recycler_notification.scrollToPosition(0)
        mNotifiAdapter.notifyDataSetChanged()

    }

    private fun initViews(mView: View) {

        listitem = java.util.ArrayList<Notification>()
        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
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
                var extras = data!!.extras

                var position = data.getIntExtra("position", -1)
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
        // startActivityForResult(intent, AppIntent.REQUEST_NOTIFICATION)

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

                    }
                    else {
                        //======== hàm này để load lại toàn bộ item khi homeFragment chỉnh sửa item.
//                        listitem.clear()
//                        listitem = realm!!.getListNotification()!
                        val idProduct = bundle.getString("id_product")
                        for(i in 0.. listitem.size -1){
                            var item = listitem[i]
                            if(item.id_product.equals(idProduct)){
                                mNotifiAdapter.notifyItemChanged(i)
                                break
                            }
                        }

                        Mylog.d("communition  BroadcastReceiver  chay chua chau")

                    }
                }else if(bundle.getBoolean("deleteItem")){
                    if (!bundle.getBoolean("reloadItem")) {
                        Mylog.d("communition  BroadcastReceiver  chay chua chau")
                        val position = bundle.getInt("position")
                        var item = listitem.get(position)
                        listitem.remove(item)
                        mNotifiAdapter.notifyDataSetChanged()

                    }else{
                        val idProduct = bundle.getInt("id_product")
                        val item = mNotifiAdapter.listItem.iterator()

                        while (item.hasNext()) {
                            var value = item.next()
                            if (value.id_product!!.equals(idProduct)) {
                                item.remove()

                            }
                        }
                        listitem.clear()
                        listitem = realm!!.getListNotification()!!
                        mNotifiAdapter.notifyDataSetChanged()

                    }
//============hàm này để khi có  thông báo là nó sẽ add vào list notification = alarm thông báo
                } else if (bundle.getBoolean("addnotification")) {
                    var notification = Notification()
                    notification._id = System.currentTimeMillis().toString()
                    notification.create_at = System.currentTimeMillis().toString()
                    notification.type = 0 // 1 single, 2 multi
                    notification.watched = false;

                    var checkNewOrOldNotification = realm!!.addNotification(notification)
                    if (checkNewOrOldNotification) {
                        val item = mNotifiAdapter.listItem.iterator()

                        while (item.hasNext()) {
                            var value = item.next()
                            if (value.id_product.equals(notification.id_product)) {
                                item.remove()
                            }
                        }
                        listitem.add(0, notification)
                        mView!!.recycler_notification.adapter = mNotifiAdapter
                        mView!!.recycler_notification.scrollToPosition(0)
                        mNotifiAdapter.notifyDataSetChanged()
                    } else {
                        listitem.add(0, notification)
                        mView!!.recycler_notification.adapter = mNotifiAdapter
                        mView!!.recycler_notification.scrollToPosition(0)
                        mNotifiAdapter.notifyDataSetChanged()
                    }

                }
            }
        }
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
