package com.finger.hsd.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.finger.hsd.R
import com.finger.hsd.model.Product_v
import java.text.SimpleDateFormat
import java.util.*

class MainListAdapterKotlin:
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private val mContext: Context
    private var mProducts = ArrayList<Product_v>()
    private val TAG = Main_list_Adapter::class.java.simpleName
    private val  TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private var miliexToday: Long = 0
    private val mCount = 0
    private var mCountT: IntArray? = null

    private val onproductClickListener: OnproductClickListener
    var optionsGlide: RequestOptions
    private val possitionChange = ArrayList<Int>()
    private var header = ArrayList<Int>()
    internal var sdf = SimpleDateFormat("dd/MM/yyyy")

    constructor(mContext: Context, mProducts: ArrayList<Product_v>,
                listheader: ArrayList<Int>, onproductClickListener: OnproductClickListener){

        this.mContext = mContext
        this.onproductClickListener = onproductClickListener
        this.header = listheader
        this.mProducts = mProducts
        val stringToday = sdf.format(Date())
        val exToday = sdf.parse(stringToday)
        miliexToday = exToday.time
        mCountT = IntArray(mProducts.size)
        for (i in mProducts.indices) {
            mCountT!![i] = 0
        }

        optionsGlide = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_add_photo)
                .error(R.drawable.ic_back)
    }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview_header_listmain, parent, false)
            return HeaderViewHolder(view)
        } else if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview_listmain, parent, false)
            return ItemViewHolder(view)
        }
        throw RuntimeException("No match for $viewType.")
    }

    override fun getItemViewType(position: Int): Int {
        val mObject = mProducts[position]
        return if (mObject.expiretime == 0L && mObject.barcode!!.isEmpty() && mObject.namechanged!!.isEmpty())
            TYPE_HEADER else TYPE_ITEM
    }


    override fun getItemCount(): Int {
        return mProducts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mObject = mProducts[position]
        if (holder is HeaderViewHolder) run {
            val mObjectHeader = mProducts[position + 1]
            if (miliexToday > mObjectHeader.expiretime) {
                holder.mTypeProduct.text = "Expired"
                holder.mTypeProduct.setTextColor(mContext.resources.getColor(R.color.red))
            } else {

                val dis = mProducts[position + 1].expiretime / 86400000 - miliexToday / 86400000
                if (dis < 10 && dis > 0) {
                    holder.mTypeProduct.text = "Warring Eat Now!!"
                    holder.mTypeProduct.setTextColor(mContext.resources.getColor(R.color.viewfinder_border))
                } else if (dis > 10) {
                    holder.mTypeProduct.text = "Protected!!"
                    holder.mTypeProduct.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
                }

            }
            if (mCountT!![position] != 0 && holder.space.visibility != View.VISIBLE) {
                holder.space.visibility = View.VISIBLE
            }
            if (!possitionChange.contains(position)) {
                for (i in position + 1 until mProducts.size) {
                    mCountT!![i] = mCountT!![i] + 1
                    if (i == mProducts.size - 1 + mCount) {
                        possitionChange.add(position)
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


            holder.mClear.setOnClickListener {
                var _idDelete = ""
                val arrID = ArrayList<Product_v>()
                for (i in position + 1 until mProducts.size) {
                    arrID.add(mProducts[i])
                    if (_idDelete === "") {
                        _idDelete = mProducts[i]._id + ","
                    } else {
                        _idDelete = _idDelete + mProducts[i]._id + ","
                    }
                    try {
                        if (mProducts[i + 1]._id === "1") {
                            break
                        }
                    } catch (err: Exception) {
                        Log.d("Adapter:", err.message)
                        break
                    }

                }
                if (_idDelete !== "") {
                    onproductClickListener.onproductClickedDelete(_idDelete, arrID)
                }
            }
        } else if (holder is ItemViewHolder) { //Constants.INSTANCE.getIMAGE_URL()+

            Glide.with(mContext)
                    .load(mObject.imagechanged)
                    .apply(optionsGlide)
                    .into(holder.photo_product)

            holder.txt_barcode.text = mObject.barcode
            holder.txt_detail.text = mObject.description
            holder.txt_exdate.setText(getDate(mObject.expiretime, "dd/MM/yyyy"))
            holder.txt_nameproduct.text = mObject.namechanged

            holder.lnItem.setOnClickListener { onproductClickListener.onClickItem(mObject, position) }

            if (miliexToday > mObject.expiretime) {
                if (holder.txt_warring.text !== "HẾT HẠN") {
                    holder.txt_warring.text = "HẾT HẠN"
                    holder.txt_warring.setTextColor(mContext.resources.getColor(R.color.white))
                    holder.txt_warring.setBackgroundResource(R.drawable.text_warring_itemview)
                }
            } else {
                val dis = mObject.expiretime / 86400000 - miliexToday / 86400000
                if (holder.txt_warring.text !== dis.toString() + " DAYS LEFT") {
                    holder.txt_warring.text = dis.toString() + " DAYS LEFT"
                    holder.txt_warring.setTextColor(mContext.resources.getColor(R.color.white))
                    holder.txt_warring.setBackgroundResource(R.drawable.text_warring_item_at)
                }

            }

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

    inner class ItemViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        var photo_product: ImageView
        var divide: View
        var divide_shadow:View
        var txt_warring: TextView
        var lnItem: LinearLayout
        var txt_barcode: TextView
        var txt_exdate:TextView
        var txt_detail:TextView
        var txt_nameproduct:TextView
        var txt_group:TextView
      init{

            photo_product = v.findViewById(R.id.photo_product)
            txt_exdate = v.findViewById<TextView>(R.id.txt_ex)
            txt_barcode = v.findViewById(R.id.txt_barcode)
            txt_detail = v.findViewById<TextView>(R.id.txt_detail)
            txt_nameproduct = v.findViewById<TextView>(R.id.txt_nameproduct)
            txt_group = v.findViewById<TextView>(R.id.txt_namegroup)
            divide = v.findViewById(R.id.divide)
            divide_shadow = v.findViewById<View>(R.id.divide_shadow)
            txt_warring = v.findViewById(R.id.txt_warring)
            lnItem = v.findViewById(R.id.ln_item)
        }
    }

    inner class HeaderViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

        var mTypeProduct: TextView
        var mClear: TextView
        var space: View
        init {

            mTypeProduct = v.findViewById(R.id.txt_type_list)
            mClear = v.findViewById(R.id.txt_delete_list)
            space = v.findViewById(R.id.space)
        }
    }

    interface OnproductClickListener {
        fun onproductClickedDelete(listDelete: String, arr: List<Product_v>)

        fun onClickItem(position: Product_v, pos: Int)
    }

}