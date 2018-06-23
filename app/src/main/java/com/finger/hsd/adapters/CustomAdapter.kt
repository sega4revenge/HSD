package com.finger.hsd.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.finger.hsd.R
import com.finger.hsd.model.TimeAlarm
import java.util.ArrayList


class CustomAdapter : BaseAdapter {


    internal  var activity: Activity
    private  var listAlarm: List<TimeAlarm>
    private  var inflater: LayoutInflater

    constructor(activity: Activity, listAlarm: List<TimeAlarm>) : super() {
        this.activity = activity
        this.listAlarm = listAlarm
        this.inflater = activity.layoutInflater
    }


    override fun getCount(): Int {
        return listAlarm.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val vh: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_view_item, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
            Log.i("JSA", "set Tag for ViewHolder, position: $position")
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        vh.tvUserName!!.text = listAlarm[position].listtime.toString()
        if(listAlarm[position].isSelected!!){
            vh.ivCheckBox!!.setBackgroundResource(R.drawable.checked)
        }else{
            vh.ivCheckBox!!.setBackgroundResource(R.drawable.check)
        }


//        vh.tvContent.text = notesList[position].content

        return view

    }
//    fun updatelist(i : Int, check : Boolean) {
//
//        listAlarm[i].isSelected
//
//        notifyDataSetChanged()
//    }

    fun updateRecords(listAlarm : List<TimeAlarm>) {
        this.listAlarm = listAlarm
        notifyDataSetChanged()

    }

    fun getSelectedItem(): List<TimeAlarm> {
        val itemModelList = ArrayList<TimeAlarm>()
        for (i in listAlarm.indices) {
            val itemModel = listAlarm[i]
            if (itemModel.isSelected!!) {
                itemModelList.add(itemModel)
            }
        }
        return itemModelList
    }

//    internal inner class ViewHolder {
//
//        var tvUserName: TextView? = null
//        var ivCheckBox: ImageView? = null
//
//    }

    private class ViewHolder(view: View?) {
        var tvUserName: TextView? = null
        var ivCheckBox: ImageView? = null

        init {
            tvUserName = view!!.findViewById(R.id.tv_user_name) as TextView
            ivCheckBox = view.findViewById(R.id.iv_check_box) as ImageView

        }
    }
}
