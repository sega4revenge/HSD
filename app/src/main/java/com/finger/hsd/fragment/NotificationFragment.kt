package com.finger.hsd.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finger.hsd.BaseFragment
import com.finger.hsd.R
import com.finger.hsd.activity.DetailProductActivity
import com.finger.hsd.adapters.NotificationAdapter
import com.finger.hsd.common.Prefs
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.services.NotificationService
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
class NotificationFragment : BaseFragment(), NotificationAdapter.itemSeenClick {

    // TODO: Rename and change types of parameters

    private var mView : View? = null
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mNotifiAdapter : NotificationAdapter
    private lateinit var listitem : List<Notification>
    private val pageToDownload : Int = 0
    private var realm: RealmController? = null
    private var prefs : Prefs? = null
    var mNotificationBadgeListener: NotificationBadgeListener? = null

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
        mRecyclerView = mView!!.findViewById(R.id.recycler_notification)
        listitem = ArrayList()
        realm = RealmController(activity?.application!!)
        prefs = Prefs(activity);

        initViews()
        setRealmAdapter()

        mView!!.im_clear.setOnClickListener(View.OnClickListener {
            showProgress()
            realm!!.setWatchedNotification()
            hideProgress()

        })
        return mView
    }

    fun setRealmAdapter(){
        listitem = realm!!.getListNotification()!!

    //    Mylog.d("aaaaaaa NotificationFragment: "+listitem.get(0).id_product +" type: "+listitem.get(0).type)

        mNotifiAdapter = NotificationAdapter(activity, listitem, mRecyclerView, mNotificationBadgeListener, this )
        mRecyclerView.adapter = mNotifiAdapter
        mNotifiAdapter.notifyDataSetChanged()

        activity!!.startService(Intent(activity, NotificationService::class.java))
    }

    fun addOneNotification() {

        var notification = Notification()
        var user = realm!!.getUser("5b06897ebb966e07b4fbd91a")!!
        notification._id = System.currentTimeMillis().toString()
//        notification.iduser = user
        notification.create_at = System.currentTimeMillis().toString()
        var product = Product_v()
        product = realm!!.getProduct("5b17f6daea81a8639de962ea")!!
        if (product != null) {
            notification.type = 1 // 1 single, 2 multi
            notification.watched = false;
            realm!!.addNotification(notification)

            mRecyclerView.scrollToPosition(0)
        } else {
            Mylog.d("aaaaaaaaa " + null)
        }
    }

    private fun initViews() {
        mRecyclerView.setHasFixedSize(true)

        val mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.setNestedScrollingEnabled(false)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mRecyclerView.setNestedScrollingEnabled(false)
        mRecyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mRecyclerView.setItemAnimator(DefaultItemAnimator())

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



//        mNotifiAdapter.setOnLoadMoreListener(object : NotificationAdapter.OnLoadMoreListener {
//            override fun onLoadmore() {
//                listitem.add
//                mNotifiAdapter.notifyItemInserted(listitem.size - 1)
//                if (listitem.size >= 10) {
//                    Log.d("pageToDownload ", "onLoadmore communi: $pageToDownload")
//                    ++pageToDownload
//                    Log.d("pageToDownload ", "onLoadmore communi: $pageToDownload")
//                  //  mCommunityPresenter.RepquestCommunityeSuri(pageToDownload)
//                }
//            }
//        })



    }

    interface NotificationBadgeListener {
        fun onBadgeUpdate(value: Int)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == AppIntent.REQUEST_NOTIFICATION){
            if(resultCode == AppIntent.RESULT_DETAIL_PRODUCT){
                var extras = data!!.extras

                if(extras !=null){
                    var product =  extras.getSerializable("product_v") as Product_v
                    var position = extras.getInt("position")
                    var item = listitem.get(position)

                    item.namechanged = product.namechanged
                    item.expiredtime = product.expiretime

                    mNotifiAdapter.notifyItemChanged(position)


                }
            }
        }
    }

    override fun onItemSeenClick(position: Int, product: Product_v) {

        val intent = Intent(activity, DetailProductActivity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("id_product", product._id)
        activity!!.startActivityForResult(intent, AppIntent.REQUEST_NOTIFICATION)


    }

}
