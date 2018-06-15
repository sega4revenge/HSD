package com.finger.hsd.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.finger.hsd.R;
import com.finger.hsd.fragment.NotificationFragment;
import com.finger.hsd.manager.RealmController;
import com.finger.hsd.model.Notification;
import com.finger.hsd.model.Product_v;
import com.finger.hsd.util.TimeAgo;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter {
    // implements Preview.PreviewListener
    private List<Notification> feedItems;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    RealmController realm;

    private int visibleThreshold;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    Context mContext;
    private itemSeenClick itemClickListener;
    NotificationFragment.NotificationBadgeListener mNotificationListener;

    public NotificationAdapter(Activity activity, List<Notification> listNotification, RecyclerView mRecyclerView,
                               NotificationFragment.NotificationBadgeListener mNotificationlistener, itemSeenClick itemClickListener) {
        this.feedItems = listNotification;
        this.mContext = activity;
        this.mNotificationListener = mNotificationlistener;
        this.itemClickListener = itemClickListener;
      //  this.itemClick = itemClick;
        realm = new RealmController(activity);
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    visibleThreshold = linearLayoutManager.getChildCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadmore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadmore();
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    @Override
    public int getItemViewType(int position) {

        return feedItems.get(position) != null ? VIEW_ITEM : VIEW_PROG;

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            vh = new MyHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_progress, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final Notification information = feedItems.get(position);

        if (viewHolder instanceof MyHolder) {
            TimeAgo timeAgo = new TimeAgo();
            final MyHolder myHolder = (MyHolder) viewHolder;

            //đã xem
            if(information.getWatched()){
                myHolder.clickitemlayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }else{
                myHolder.clickitemlayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dim));
            }
            // kiểu thông báo: type == 1: kiểu single thông báo sản phẩm hết hạng
            // type == 2: thông báo có bao nhiêu sản phẩm hết hạn
            final Product_v  product = realm.getProduct(information.getId_product());
            if(information.getType() == 0){
                myHolder.txtHsd.setVisibility(View.VISIBLE);
                myHolder.imgInfo.setVisibility(View.VISIBLE);
                myHolder.tvWarning.setVisibility(View.VISIBLE);
                // day expiredtime
                Log.d("aaaaaaa ", "NotificationFragment: "+information.getId_product());


                Log.d("aaaaaaa ", "NotificationFragment: "+product.getImagechanged());
                long expiredTime = product.getExpiretime();
                Date date = new Date(expiredTime);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                int days = daysBetween(System.currentTimeMillis(), expiredTime);

                String txtDaystatus = product.getNamechanged() +" "+days+" " + mContext.getResources().getString(R.string.days_left);

                myHolder.tvStatus.setText(txtDaystatus);
                String txtExpiredTime = mContext.getResources().getString(R.string.expiredtime_notifi) + dateFormat.format(date);
                myHolder.txtHsd.setText(txtExpiredTime);

             //   myHolder.progressbarImage.setVisibility(View.VISIBLE);
                File file = new File(product.getImagechanged());
                Uri imageUri = Uri.fromFile(file);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .priority(Priority.LOW);
                Glide.with(mContext)
                        .load(product.getImagechanged())
                        .apply(options)
//                        .listener(new RequestListener<Drawable>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                               myHolder.progressbarImage.setVisibility(View.GONE);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                myHolder.progressbarImage.setVisibility(View.GONE);
//                                return false;
//                            }
//                        })
                        .into(myHolder.imgInfo);

            }else if(information.getType() == 1){
                myHolder.txtHsd.setVisibility(View.GONE);
                myHolder.imgInfo.setVisibility(View.GONE);
                myHolder.tvWarning.setVisibility(View.GONE);
              //  myHolder.progressbarImage.setVisibility(View.GONE);
                String txtDaystatus =  mContext.getResources().getString(R.string.status_count_product_1) +" "+ information.getCountProductExprited()+" "+
                        mContext.getResources().getString(R.string.status_count_product_2);
                myHolder.tvStatus.setText(txtDaystatus);
            }

            ((MyHolder) viewHolder).clickitemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNotificationListener.onBadgeUpdate(20);
                    information.setWatched(true);
                    realm.updateNotification(information.get_id());
                    myHolder.clickitemlayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));


                    itemClickListener.onItemSeenClick(position, product);
                }
            });
            myHolder.txtTime.setText(timeAgo.getTimeAgo(new Date(Long.parseLong(information.getCreate_at())), mContext));

        } else {
            if (feedItems.size() >= 10) {
                ((ProgressViewHolder) viewHolder).progressBar.setVisibility(View.VISIBLE);
                ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
            } else {
                ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(false);
                ((ProgressViewHolder) viewHolder).progressBar.setVisibility(View.GONE);
            }
        }
    }


    //
    // xử lý ngày -> trả về số ngày
    public int  daysBetween(long startDate,  long endDate) {

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(startDate);
        calendarStart.set(Calendar.HOUR_OF_DAY, 0);
        calendarStart.set(Calendar.MINUTE, 0);
        calendarStart.set(Calendar.SECOND, 0);
        calendarStart.set(Calendar.MILLISECOND, 0);
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.setTimeInMillis(endDate);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        calendarEnd.set(Calendar.MINUTE, 0);
        calendarEnd.set(Calendar.SECOND, 0);
        calendarEnd.set(Calendar.MILLISECOND, 0);

        long  start = calendarStart.getTimeInMillis();
       long end = calendarEnd.getTimeInMillis();

        return (int) java.util.concurrent.TimeUnit.MILLISECONDS.toDays((end - start));
    }
    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView txtTime;
        TextView tvStatus;
        TextView txtHsd;
        TextView tvWarning;
        ImageView imgInfo;
        public LinearLayout clickitemlayout;
//        ProgressBar progressbarImage;


        public MyHolder(View rowView) {
            super(rowView);

            clickitemlayout = rowView.findViewById(R.id.ln_item);
            txtTime =  rowView.findViewById(R.id.tv_time);
            tvStatus =  rowView.findViewById(R.id.tv_status);
            txtHsd =  rowView.findViewById(R.id.tv_hsd);
            tvWarning = rowView.findViewById(R.id.tv_warning);
            imgInfo =  rowView.findViewById(R.id.im_product_noti);
//            progressbarImage =  rowView.findViewById(R.id.progress_notification);


        }
    }


    class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_more);

        }
    }

    public  interface itemSeenClick {
         void onItemSeenClick(int position, Product_v product);
    }

}
