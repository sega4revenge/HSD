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
import com.finger.hsd.model.Sound
import com.finger.hsd.model.TimeAlarm
import java.util.ArrayList
import java.nio.file.Files.size

import java.nio.file.Files.size
import android.support.v7.widget.RecyclerView





class Sound_Adapter : BaseAdapter {


    internal  var activity: Activity
    private  var users: List<Sound>
    private  var inflater: LayoutInflater
    private var lastCheckedPosition = -1

    constructor(activity: Activity, users: List<Sound>) : super() {
        this.activity = activity
        this.users = users
        this.inflater = activity.layoutInflater
    }


    override fun getCount(): Int {
        return users.size
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
            Log.i("JSA", "set Tag for ViewHolder, position: " + position)
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        vh.tvUserName!!.text = users[position].listtime.toString()

        if(users.lastIndex == lastCheckedPosition){

            vh.ivCheckBox!!.setBackgroundResource(R.drawable.checked)

        }else{

            vh.ivCheckBox!!.setBackgroundResource(R.drawable.check)

        }

//        (holder as ItemViewHolder).name.setText(model.getName())
//        if (model.getId() === lastCheckedPosition) {
//            (holder as ItemViewHolder).radioButton.setChecked(true)
//        } else {
//            (holder as ItemViewHolder).radioButton.setChecked(false)
//        }
//        (holder as ItemViewHolder).radioButton.setOnClickListener(View.OnClickListener {
//            lastCheckedPosition = model.getId()
//            notifyItemRangeChanged(0, itemModels.size())
//        })


//        vh.tvContent.text = notesList[position].content

        return view

    }

//    private fun initializeViews(model: Sound, holder: RecyclerView.ViewHolder, position: Int) {
//        (holder as ViewHolder).tvUserName!!.text = model.nameSound
//        if (model.listtime === lastCheckedPosition) {
//            holder.radioButton.setChecked(true)
//        } else {
//            holder.radioButton.setChecked(false)
//        }
//        holder.radioButton.setOnClickListener(View.OnClickListener {
//            lastCheckedPosition = model.getId()
//            notifyItemRangeChanged(0, itemModels.size())
//        })
//
//    }



    fun updateRecords(users: List<Sound>) {
        this.users = users
        notifyDataSetChanged()

    }

    fun getSelectedItem(): List<Sound> {
        val itemModelList = ArrayList<Sound>()
        for (i in users.indices) {
            val itemModel = users[i]
            if (itemModel.isSelected!!) {
                itemModelList.add(itemModel)
            }
        }
        return itemModelList
    }


    private class ViewHolder(view: View?) {
        var tvUserName: TextView? = null
        var ivCheckBox: ImageView? = null

        init {
            tvUserName = view!!.findViewById(R.id.tv_user_name) as TextView
            ivCheckBox = view.findViewById(R.id.iv_check_box) as ImageView

        }
    }
}
