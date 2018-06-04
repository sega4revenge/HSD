package com.finger.hsd.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finger.hsd.MyApplication;
import com.finger.hsd.R;
import com.finger.hsd.model.Product;
import com.finger.hsd.model.Product_v;
import com.finger.hsd.util.Constants;
import com.finger.hsd.util.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main_list_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Product_v> mProducts = new ArrayList<>();
    private static final String TAG = Main_list_Adapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private long miliexToday = 0;
    private int mCount = 0;
    private  int[] mCountT;
    private String st = "";
    private  ArrayList<Integer> possitionChange = new ArrayList<>();
    private List<Integer> header =new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    BitmapFactory.Options options;
    private Picasso picasso;

    public Main_list_Adapter(){}

    public Main_list_Adapter(Context mContext, List<Product_v> mProducts, int mCount) throws ParseException {
        this.mContext = mContext;
        this.mProducts = mProducts;
        this.mCount = mCount;


        header.add(0);
        String stringToday = sdf.format(new Date());
        Date exToday = sdf.parse(stringToday);
        miliexToday =  exToday.getTime();
        mCountT = new int[mProducts.size()+mCount];

        for (int i=0;i<mProducts.size()+mCount;i++){
            mCountT[i] = 0;
        }
        picasso =  new Picasso.Builder(mContext).indicatorsEnabled(true)
                .downloader(new OkHttp3Downloader(MyApplication.Companion.okhttpclient()))
                .build();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_header_listmain, parent, false);
            HeaderViewHolder viewHorder = new HeaderViewHolder(view);
            return viewHorder;
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_listmain, parent, false);
            ItemViewHolder viewHorder = new ItemViewHolder(view);
            return viewHorder;
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            Product_v mObject = mProducts.get((position-mCountT[position]));
            if (holder instanceof HeaderViewHolder) {
                    if(mCountT[position] != 0 && ((HeaderViewHolder) holder).space.getVisibility() != View.VISIBLE){
                        ((HeaderViewHolder) holder).space.setVisibility(View.VISIBLE);
                    }
                    if(mCountT[position] == 0 && position ==0 &&((HeaderViewHolder) holder).space.getVisibility() != View.GONE){
                        ((HeaderViewHolder) holder).space.setVisibility(View.GONE);
                    }
                    if(miliexToday>mObject.getExpiretime()){
                        ((HeaderViewHolder) holder).mTypeProduct.setText("Expired");
                    }else{
                        long dis = (mObject.getExpiretime()/86400000 - miliexToday/86400000);
                        ((HeaderViewHolder) holder).mTypeProduct.setText(dis+" days left");
                    }
                    if(!possitionChange.contains(position))
                    {
                        for (int i=position+1;i<mProducts.size()+mCount;i++){
                            mCountT[i] = mCountT[i]+1;
                            if(i==mProducts.size()-1+mCount){
                                possitionChange.add(position);
                            }
                        }
                    }
                    ((HeaderViewHolder) holder).mClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String _idDelete = "";
                            for (int i=position+1;i<mProducts.size()+mCount;i++){
                                if(_idDelete==""){
                                    _idDelete = mProducts.get(i-mCountT[i]).getDescription();
                                }else{
                                    _idDelete =_idDelete +" , "+mProducts.get(i-mCountT[i]).getDescription();
                                }
                                if((i+2) < mCountT.length && mCountT[i]!=mCountT[i+2]){
                                 break;
                                }
                            }
                        }
                    });
            } else if (holder instanceof ItemViewHolder) {
                     picasso.load(Constants.INSTANCE.getIMAGE_URL()+mObject.getImagechanged()).error(R.drawable.ic_calendar)
                             .placeholder(R.drawable.ic_add_photo).resize(80,80).into(((ItemViewHolder) holder).photo_product);
                    ((ItemViewHolder) holder).txt_barcode.setText(mObject.getBarcode());
                    ((ItemViewHolder) holder).txt_detail.setText(mObject.getDescription());
                    ((ItemViewHolder) holder).txt_exdate.setText(getDate(mObject.getExpiretime(), "dd/MM/yyyy"));
                    ((ItemViewHolder) holder).txt_nameproduct.setText(mObject.getNamechanged());
                    //&& miliexToday<mProducts.get(position+1-mCountT[position]).getExpiretime()
                    if((position) != (mProducts.size()+mCount-1) && !getDate(mObject.getExpiretime(), "dd/MM/yyyy").equals(getDate(mProducts.get(position+1-mCountT[position]).getExpiretime(), "dd/MM/yyyy")) ){
                        ((ItemViewHolder) holder).divide.setVisibility(View.GONE);
                        header.add(position+1);
                    }
                    if(miliexToday>mObject.getExpiretime()){
                        ((ItemViewHolder) holder).txt_warring.setText("Cảnh báo");
                        ((ItemViewHolder) holder).txt_warring.setBackgroundResource(R.drawable.text_warring_itemview);
                    }else{
                        long dis = (mObject.getExpiretime()/86400000 - miliexToday/86400000);
                        if(dis<30){
                            ((ItemViewHolder) holder).txt_warring.setText("Hãy sủ dụng ngay");
                            ((ItemViewHolder) holder).txt_warring.setBackgroundResource(R.drawable.text_warring_item_at);
                        }else{
                            ((ItemViewHolder) holder).txt_warring.setText("An toàn");
                            ((ItemViewHolder) holder).txt_warring.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                            ((ItemViewHolder) holder).txt_warring.setBackgroundResource(R.drawable.text_warring_att);
                        }

                    }

            }



    }
    public String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        boolean check = false;
        for(int i=0;i<header.size();i++){
            if(position == header.get(i)){
                check = true;
                break;
            }
        }
        return check;
    }

    @Override
    public int getItemCount() {
        return (mProducts.size()+mCount);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView photo_product;
        public View divide,divide_shadow;
        public TextView txt_warring;
        public TextView txt_barcode,txt_exdate,txt_detail,txt_nameproduct,txt_group;
        public ItemViewHolder(View v) {
            super(v);
            photo_product = v.findViewById(R.id.photo_product);
            txt_exdate = v.findViewById(R.id.txt_ex);
            txt_barcode = v.findViewById(R.id.txt_barcode);
            txt_detail = v.findViewById(R.id.txt_detail);
            txt_nameproduct = v.findViewById(R.id.txt_nameproduct);
            txt_group = v.findViewById(R.id.txt_namegroup);
            divide = v.findViewById(R.id.divide);
            divide_shadow = v.findViewById(R.id.divide_shadow);
            txt_warring =  v.findViewById(R.id.txt_warring);
        }


    }
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView mTypeProduct;
        public TextView mClear;
        public View space;
        public HeaderViewHolder(View v) {
            super(v);
            mTypeProduct = v.findViewById(R.id.txt_type_list);
            mClear = v.findViewById(R.id.txt_delete_list);
            space = v.findViewById(R.id.space);
        }

    }

}
