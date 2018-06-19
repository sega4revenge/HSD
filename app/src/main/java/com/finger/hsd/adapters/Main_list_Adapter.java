package com.finger.hsd.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.finger.hsd.R;
import com.finger.hsd.model.Product_v;

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

    private OnproductClickListener onproductClickListener;

    private  ArrayList<Integer> possitionChange = new ArrayList<>();
    private List<Integer> header =new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    public RequestOptions optionsGlide;
    public Main_list_Adapter(Context mContext, List<Product_v> mProducts, ArrayList<Integer> listheader,OnproductClickListener onproductClickListener) throws ParseException {
        this.mContext = mContext;
        this.mProducts = mProducts;
        this.header = listheader;
        this.onproductClickListener = onproductClickListener;

        String stringToday = sdf.format(new Date());
        Date exToday = sdf.parse(stringToday);
        miliexToday =  exToday.getTime();
        mCountT = new int[mProducts.size()];

        for (int i=0;i<mProducts.size();i++){
            mCountT[i] = 0;
        }
//        picasso =  new Picasso.Builder(mContext).indicatorsEnabled(true)
//                .downloader(new OkHttp3Downloader(MyApplication.Companion.okhttpclient()))
//                .build();
        optionsGlide = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_add_photo)
                .error(R.drawable.ic_back)
                .priority(Priority.LOW);
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
            final Product_v mObject = mProducts.get((position));
            if (holder instanceof HeaderViewHolder) {
                Product_v mObjectHeader = mProducts.get((position+1));
                    if(miliexToday>mObjectHeader.getExpiretime()){
                        ((HeaderViewHolder) holder).mTypeProduct.setText("Expired");
                        ((HeaderViewHolder) holder).mTypeProduct.setTextColor(mContext.getResources().getColor(R.color.viewfinder_laser));
                    }else{

                        long dis = (mProducts.get(position+1).getExpiretime()/86400000 - miliexToday/86400000);
                        if(dis<10 && dis>0){
                            ((HeaderViewHolder) holder).mTypeProduct.setText("Warring Eat Now!!");
                            ((HeaderViewHolder) holder).mTypeProduct.setTextColor(mContext.getResources().getColor(R.color.viewfinder_border));
                        }else if(dis>10){
                            ((HeaderViewHolder) holder).mTypeProduct.setText("Protected!!");
                            ((HeaderViewHolder) holder).mTypeProduct.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                        }

                    }
                    if(mCountT[position] != 0&&((HeaderViewHolder) holder).space.getVisibility() != View.VISIBLE){
                        ((HeaderViewHolder) holder).space.setVisibility(View.VISIBLE);
                    }
                    if(!possitionChange.contains(position))
                    {
                        for (int i=position+1;i<mProducts.size();i++){
                            mCountT[i] = mCountT[i]+1;
                            if(i==mProducts.size()-1+mCount){
                                possitionChange.add(position);
                            }
                        }
                    }



//                    if(mCountT[position] != 0 && ((HeaderViewHolder) holder).space.getVisibility() != View.VISIBLE){
//                        ((HeaderViewHolder) holder).space.setVisibility(View.VISIBLE);
//                    }
//                    if(mCountT[position] == 0 && position ==0 &&((HeaderViewHolder) holder).space.getVisibility() != View.GONE){
//                        ((HeaderViewHolder) holder).space.setVisibility(View.GONE);
//                    }
//                    if(miliexToday>mObject.getExpiretime()){
//                        ((HeaderViewHolder) holder).mTypeProduct.setText("Expired");
//                    }else{
//                        long dis = (mObject.getExpiretime()/86400000 - miliexToday/86400000);
//                        ((HeaderViewHolder) holder).mTypeProduct.setText(dis+" days left");
//                    }
//                    if(!possitionChange.contains(position))
//                    {
//                        for (int i=position+1;i<mProducts.size()+mCount;i++){
//                            mCountT[i] = mCountT[i]+1;
//                            if(i==mProducts.size()-1+mCount){
//                                possitionChange.add(position);
//                            }
//                        }
//                    }


                    ((HeaderViewHolder) holder).mClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String _idDelete = "";
                            List<Product_v> arrID = new ArrayList<Product_v>();
                            for (int i=position+1;i<mProducts.size();i++){
                                arrID.add(mProducts.get(i));
                                if(_idDelete==""){
                                    _idDelete = mProducts.get(i).get_id()+",";
                                }else{
                                    _idDelete =_idDelete+mProducts.get(i).get_id()+",";
                                }
                                try{
                                    if(mProducts.get(i+1).get_id()=="1"){
                                        break;
                                    }
                                }catch (Exception err){
                                    Log.d("Adapter:",err.getMessage());
                                    break;
                                }
                            }
                            if(_idDelete!= ""){
                                onproductClickListener.onproductClickedDelete(_idDelete,arrID);
                            }
                        }
                    });
            } else if (holder instanceof ItemViewHolder) { //Constants.INSTANCE.getIMAGE_URL()+
                    Log.d("IMAGEGGGGGGGGGG",mObject.getImagechanged()+"//");
                    Glide.with(mContext)
                            .load(mObject.getImagechanged())
                            .apply(optionsGlide)
                            .into(((ItemViewHolder) holder).photo_product);

                    ((ItemViewHolder) holder).txt_barcode.setText(mObject.getBarcode());
                    ((ItemViewHolder) holder).txt_detail.setText(mObject.getDescription());
                    ((ItemViewHolder) holder).txt_exdate.setText(getDate(mObject.getExpiretime(), "dd/MM/yyyy"));
                    ((ItemViewHolder) holder).txt_nameproduct.setText(mObject.getNamechanged());

                ((ItemViewHolder) holder).lnItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onproductClickListener.onClickItem(mObject,position);
                    }
                });

                    if(miliexToday>mObject.getExpiretime()){
                        if( ((ItemViewHolder) holder).txt_warring.getText()!="Hết hạn"){
                            ((ItemViewHolder) holder).txt_warring.setText("Hết hạn");
                            ((ItemViewHolder) holder).txt_warring.setTextColor(mContext.getResources().getColor(R.color.white));
                            ((ItemViewHolder) holder).txt_warring.setBackgroundResource(R.drawable.text_warring_itemview);
                        }
                    }else{
                        long dis = (mObject.getExpiretime()/86400000 - miliexToday/86400000);
                        if(((ItemViewHolder) holder).txt_warring.getText()!= dis+" day left"){
                            ((ItemViewHolder) holder).txt_warring.setText(dis+" day left");
                            ((ItemViewHolder) holder).txt_warring.setTextColor(mContext.getResources().getColor(R.color.white));
                        }
                        if(dis>10){
                            ((ItemViewHolder) holder).txt_warring.setBackgroundResource(R.drawable.text_warring_item_at);
                        }else{
                            ((ItemViewHolder) holder).txt_warring.setBackgroundResource(R.drawable.text_warring_item_cb);
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

    @Override
    public int getItemViewType(int position) {
        Product_v mObject = mProducts.get((position));
        if (mObject.getExpiretime()==0&&mObject.getBarcode().isEmpty()&&mObject.getNamechanged().isEmpty())
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

    public  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public  Bitmap decodeSampledBitmapFromResource(String strPath,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, options);
    }


    @Override
    public int getItemCount() {
        return (mProducts.size());
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView photo_product;
        public View divide,divide_shadow;
        public TextView txt_warring;
        public LinearLayout lnItem;
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
            lnItem = v.findViewById(R.id.ln_item);
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
    public interface OnproductClickListener {
        void onproductClickedDelete(String listDelete,List<Product_v> arr);

        void onClickItem(Product_v position,int pos);
    }
}
