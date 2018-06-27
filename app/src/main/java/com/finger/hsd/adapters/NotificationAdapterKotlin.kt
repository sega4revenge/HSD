package com.finger.hsd.adapters

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.finger.hsd.R
import com.finger.hsd.fragment.NotificationFragment
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.util.Mylog
import com.finger.hsd.util.TimeAgo
import kotlinx.android.synthetic.main.item_notification.view.*
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapterKotlin(val mContext: Context, val listItem: ArrayList<Notification>,
                                val realm: RealmController, val mNotificationListener: NotificationFragment.NotificationBadgeListener,
                                val itemClick: ItemClickListener) :
        RecyclerView.Adapter<NotificationAdapterKotlin.ViewHolder>() {


//    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
//        return LayoutInflater.from(mContext).inflate(layoutRes, this, attachToRoot)
//    }
    val options = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.photo_unvailable)
        .error(R.drawable.photo_unvailable)
        .priority(Priority.LOW)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflatedView= LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)

        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(listItem.get(position), mContext, realm, itemClick, mNotificationListener, position)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        //2
        //private var view: View = v

        fun bindItems(items: Notification, mContext: Context, realm: RealmController, itemClick: ItemClickListener,
                      mNotificationListener: NotificationFragment.NotificationBadgeListener, position: Int) {
            val timeAgo = TimeAgo()

            //đã xem
            if (items.watched!!) {
                view.ln_item.setBackgroundColor(mContext.getResources().getColor(R.color.white))
            } else {
                view.ln_item.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dim))
            }
            Mylog.d("aaaaaaa "+ " type: "+items.type)
            if (items.type == 0) {
                val product = realm.getProduct(items.id_product!!)
                if(product !=null){
                    view.tv_hsd.setVisibility(View.VISIBLE)
                    view.im_product_noti.setVisibility(View.VISIBLE)
//                view.tv_warning.setVisibility(View.GONE)
//                    view.progress_notification.visibility = View.VISIBLE
//                    view.img.visibility = View.VISIBLE


                    Log.d("aaaaaaa ", "NotificationFragment: " + items.id_product!!)
                    //  Log.d("aaaaaaa ", "NotificationFragment: " + product.getNamechanged())

                    val expiredTime = product.expiretime


                    val date = Date(expiredTime)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    val days = daysBetween(System.currentTimeMillis(), expiredTime)


                   // val txtDaystatus = " " + days + " " + mContext.resources.getString(R.string.days_left)
                    if (items.status_expiry.equals("expired")) {
                        view.tv_name_status.setTextColor(R.drawable.roundedtext_red)
                        if(days == 0) {
                            view.tv_name_status.text = "Sản phẩm " + product.namechanged!!.toString() + " đã hết hạn " + "hôm nay" + " ngày"
                        }else{
                            view.tv_name_status.text = "Sản phẩm " + product.namechanged!!.toString() + " đã hết hạn " + Math.abs(days) + " ngày"

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            view.tv_name_status.setTextColor(mContext.resources.getColor(R.color.red, null))
                        }else{
                            view.tv_name_status.setTextColor(mContext.resources.getColor(R.color.red))
                        }
                    } else {

                        view.tv_name_status.text = "Sản phẩm "+product.namechanged!!.toString() + "sắp sẽ hết hạn trong "+ Math.abs(days)+" ngày"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            view.tv_name_status.setTextColor(mContext.resources.getColor(R.color.orange, null))
                        }else{
                            view.tv_name_status.setTextColor(mContext.resources.getColor(R.color.orange))
                        }
                    }
//                    view.tv_status.text = txtDaystatus
                    val txtExpiredTime = mContext.resources.getString(R.string.expiredtime_notifi) + dateFormat.format(date)
                    view.tv_hsd.setText(txtExpiredTime)

                    Mylog.d("aaaaaaa " + " type: " + items.type + "NotificationFragment: " + product.imagechanged)

                    Glide.with(mContext)
                            .load(product.imagechanged)
                            .apply(options)
                            .into(view.im_product_noti)
                    view.ln_item.setOnClickListener(View.OnClickListener {
                        mNotificationListener.onBadgeUpdate(realm.countNotification() - 1)

                        view.ln_item.setBackgroundColor(mContext.resources.getColor(R.color.white))

                        itemClick.onItemClick(position, product)
                    })
                }else{
                    Mylog.d("aaaaaaaaaa type = 0")
                    view.tv_hsd.setVisibility(View.GONE)
                    view.im_product_noti.setVisibility(View.GONE)
                    view.tv_name_status.text = mContext.resources.getString(R.string.product_was_deleted)
//                    view.progress_notification.visibility = View.GONE
//                    view.img.visibility = View.GONE
                }

            } else if (items.type == 3) {
                Mylog.d("aaaaaaaaaa type = 3")
                view.tv_hsd.setVisibility(View.GONE)
                view.im_product_noti.setVisibility(View.GONE)
                view.tv_name_status.text = mContext.resources.getString(R.string.wellcome_hsd)
//                view.progress_notification.visibility = View.GONE
//                view.img.visibility = View.VISIBLE


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(mContext)
                            .load(mContext.resources.getDrawable(R.drawable.ic_launcher_hsd, null))
                            .apply(options)
                            .into(view.im_product_noti)
                }else{
                    Glide.with(mContext)
                            .load(mContext.resources.getDrawable(R.drawable.ic_launcher_hsd))
                            .apply(options)
                            .into(view.im_product_noti)
                }

                //  view.progressbarImage.setVisibility(View.GONE);
//                val txtDaystatus = mContext.resources.getString(R.string.status_count_product_1) +
//                        mContext.resources.getString(R.string.status_count_product_2)
//                view.tv_status.setText(txtDaystatus)
            }


            view.tv_time.setText(timeAgo.getTimeAgo(Date(Long.parseLong(items.create_at)), mContext))

        }

        fun daysBetween(currentDay: kotlin.Long, expiredTime: kotlin.Long): Int {

            // ngayf hien tai
            val calendarStart = Calendar.getInstance()
            calendarStart.timeInMillis = currentDay
            calendarStart.set(Calendar.HOUR_OF_DAY, 0)
            calendarStart.set(Calendar.MINUTE, 0)
            calendarStart.set(Calendar.SECOND, 0)
            calendarStart.set(Calendar.MILLISECOND, 0)
            // ngay han su dung
            val calendarEnd = Calendar.getInstance()
            calendarEnd.timeInMillis = expiredTime
            calendarEnd.set(Calendar.HOUR_OF_DAY, 0)
            calendarEnd.set(Calendar.MINUTE, 0)
            calendarEnd.set(Calendar.SECOND, 0)
            calendarEnd.set(Calendar.MILLISECOND, 0)

            val start = calendarStart.timeInMillis
            val end = calendarEnd.timeInMillis

            return java.util.concurrent.TimeUnit.MILLISECONDS.toDays(end - start).toInt()
        }

    }

    interface ItemClickListener {
        fun onItemClick(position: Int, product: Product_v)
    }
}