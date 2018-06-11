package com.finger.hsd.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finger.hsd.R
import com.finger.hsd.adapters.Main_list_Adapter
import com.finger.hsd.model.Product
import java.text.ParseException
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BlankFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BlankFragment : Fragment() ,View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mCount  = 0
    private var mView:View? = null
    private var mRec:RecyclerView? = null
    private var mAdapter:Main_list_Adapter? = null
    private var ListProduct:ArrayList<Product>? = null
    private var arrType:ArrayList<Int>? = null
    private var mLayoutManager:LinearLayoutManager? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//                var view: View = inflater.inflate(R.layout.fragment_blank, container, false)
//
//                val imageView: ImageView = view.findViewById(R.id.imageView)
//
//
//
//                imageView.setOnClickListener({
////                    val intent = Intent(activity, testImageActivity::class.java)
////                    intent.putExtra("id_product","5b0b7093304e8e55e9a28617")
////                    startActivity(intent)
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                        val intent = Intent(activity, DetailProductActivity::class.java)
//                        intent.putExtra("id_product","5b0b7093304e8e55e9a28617")
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
//                    }else{
//                        val intent = Intent(activity, DetailProductActivity::class.java)
//                        intent.putExtra("id_product","5b0b7093304e8e55e9a28617")
//                        startActivity(intent)
//                    }

        mView = inflater.inflate(R.layout.fragment_blank, container, false)
        return mView
    }
    private fun initView() {
        mRec = mView?.findViewById(R.id.rec)
    }

    override fun onResume() {
        super.onResume()
        if(mCount>0){
            mCount =0
        }
    }




    fun converTypeDate(date: String): Long {
        var timeInMilliseconds: Long = 0
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val mDate = sdf.parse(date)
            timeInMilliseconds = mDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return timeInMilliseconds
    }
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
