package com.finger.hsd.adapters

import android.content.Context
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
            val product = realm.getProduct(items.id_product!!)
            if (items.type == 0) {

                view.tv_hsd.setVisibility(View.VISIBLE)
                view.im_product_noti.setVisibility(View.VISIBLE)
                view.tv_warning.setVisibility(View.VISIBLE)

                Log.d("aaaaaaa ", "NotificationFragment: " + items.id_product!!)
              //  Log.d("aaaaaaa ", "NotificationFragment: " + product.getNamechanged())

                val expiredTime = product!!.expiretime
                val date = Date(expiredTime)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val days = daysBetween(System.currentTimeMillis(), expiredTime)
                val txtDaystatus = product.namechanged + " " + days + " " + mContext.resources.getString(R.string.days_left)

                view.tv_status.text = txtDaystatus
                val txtExpiredTime = mContext.resources.getString(R.string.expiredtime_notifi) + dateFormat.format(date)
                view.tv_hsd.setText(txtExpiredTime)
                Log.d("aaaaaaa ", "NotificationFragment: " + product.imagechanged)
                val options = RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .priority(Priority.LOW)
                Glide.with(mContext)
                        .load(product.imagechanged)
                        .apply(options)
                        //                        .listener(new RequestListener<Drawable>() {
                        //                            @Override
                        //                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //                               view.progressbarImage.setVisibility(View.GONE);
                        //                                return false;
                        //                            }
                        //
                        //                            @Override
                        //                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //                                view.progressbarImage.setVisibility(View.GONE);
                        //                                return false;
                        //                            }
                        //                        })
                        .into(view.im_product_noti)

            } else if (items.type == 1) {
//                view.tv_hsd.setVisibility(View.GONE)
//                view.im_product_noti.setVisibility(View.GONE)
//                view.tv_warning.setVisibility(View.GONE)
//                //  view.progressbarImage.setVisibility(View.GONE);
//                val txtDaystatus = mContext.resources.getString(R.string.status_count_product_1) + " " + items.countProductExprited + " " +
//                        mContext.resources.getString(R.string.status_count_product_2)
//                view.tv_status.setText(txtDaystatus)
            }

            view.ln_item.setOnClickListener(View.OnClickListener {
                mNotificationListener.onBadgeUpdate(20)
                items.watched = true
                realm.updateNotification(items._id!!)
                view.ln_item.setBackgroundColor(mContext.resources.getColor(R.color.white))

                itemClick.onItemClick(position, product!!)
            })

            view.tv_time.setText(timeAgo.getTimeAgo(Date(Long.parseLong(items.create_at)), mContext))

        }

        fun daysBetween(startDate: kotlin.Long, endDate: kotlin.Long): Int {

            val calendarStart = Calendar.getInstance()
            calendarStart.timeInMillis = startDate
            calendarStart.set(Calendar.HOUR_OF_DAY, 0)
            calendarStart.set(Calendar.MINUTE, 0)
            calendarStart.set(Calendar.SECOND, 0)
            calendarStart.set(Calendar.MILLISECOND, 0)
            val calendarEnd = Calendar.getInstance()
            calendarStart.timeInMillis = endDate
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