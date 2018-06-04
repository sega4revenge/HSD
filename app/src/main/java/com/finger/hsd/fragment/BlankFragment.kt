package com.finger.hsd.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finger.hsd.R
import com.finger.hsd.adapters.Main_list_Adapter
import com.finger.hsd.model.Product
import com.finger.hsd.model.Product_v
import io.reactivex.internal.util.ArrayListSupplier
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

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
        getData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

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

    private fun getData(){
    /*    val ListProduct = java.util.ArrayList<Product_v>()
        val pro = Product_v("name1", "bacode1", "30/05/2018", "sdf","")
        ListProduct.add(pro)
        val pro2 = Product_v("name2", "bacode2", "30/05/2018", "12sdfd3","")
        ListProduct.add(pro2)
        val pro3 = Product_v("name3", "bacode3", "22/05/2018", "sdfsdf","")
        ListProduct.add(pro3)
        val pro4 = Product_v("name4", "bacode4", "22/05/2018", "sdfsd","")
        ListProduct.add(pro4)
        val pro5 = Product_v("name5", "bacode5", "28/05/2018", "cxvx","")
        ListProduct.add(pro5)
        val pro6 = Product_v("name6", "bacode6", "28/05/2018", "verg","")
        ListProduct.add(pro6)
        val pro7 = Product_v("name7", "bacode7", "21/05/2018", "rghrh","")
        ListProduct.add(pro7)
        val pro8 = Product_v("name8", "bacode8", "21/05/2018", "12umy3","")
        ListProduct.add(pro8)
        val pro9 = Product_v("name9", "bacode9", "29/05/2018", "jmujmujm","")
        ListProduct.add(pro9)
        val pro10 = Product_v("name10", "bacode10", "29/05/2018", "jkjk","")
        ListProduct.add(pro10)
        val pro11 = Product_v("name11", "bacode11", "29/05/2018", "gnf","")
        ListProduct.add(pro11)

        val pro12 = Product_v("name12", "bacode12", "18/06/2018", "sdf","")
        ListProduct.add(pro12)
        val pro13 = Product_v("name13", "bacode13", "14/06/2018", "12sdfd3","")
        ListProduct.add(pro13)
        val pro14 = Product_v("name15", "bacode14", "13/06/2018", "sdfsdf","")
        ListProduct.add(pro14)
        val pro15 = Product_v("name15", "bacode15", "12/06/2018", "sdfsd","")
        ListProduct.add(pro15)
        val pro16 = Product_v("name16", "bacode16", "18/06/2018", "cxvx","")
        ListProduct.add(pro16)
        val pro17 = Product_v("name17", "bacode17", "21/06/2018", "verg","")
        ListProduct.add(pro17)
        val pro18 = Product_v("name18", "bacode18", "27/06/2018", "rghrh","")
        ListProduct.add(pro18)
        val pro19 = Product_v("name19", "bacode19", "29/06/2018", "12umy3","")
        ListProduct.add(pro19)
        val pro20 = Product_v("name20", "bacode20", "24/06/2018", "jmujmujm","")
        ListProduct.add(pro20)
        val pro21 = Product_v("name21", "bacode21", "25/06/2018", "jkjk","")
        ListProduct.add(pro21)
        val pro22 = Product_v("name22", "bacode22", "27/06/2018", "gnf","")
        ListProduct.add(pro22)

        var st = ""
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val stringToday = sdf.format(Date())
        val exToday = sdf.parse(stringToday)
        var miliexToday:Long = exToday.getTime()
        var check_hethan = false

        for(i in ListProduct!!){
            if(st.indexOf(i.getmDateEX())>0){
            }else{
              if(miliexToday<converTypeDate(i.getmDateEX())){
                  st = st +" "+ i.getmDateEX()
                  mCount++
              }else{
                  if(!check_hethan){
                      st = st +" "+ i.getmDateEX()
                      check_hethan = true
                      mCount++
                  }
              }
            }
        }

        ListProduct.sortWith(Comparator(fun(a:Product_v, b:Product_v): Int {
            if (converTypeDate(a.getmDateEX())<converTypeDate(b.getmDateEX()))
                return -1
            if (converTypeDate(a.getmDateEX())>converTypeDate(b.getmDateEX()))
                return 1
            return 0
        }))
        var mLayoutManager =  LinearLayoutManager(activity)
        mRec?.layoutManager = mLayoutManager
        mRec?.setHasFixedSize(true)
        mAdapter = Main_list_Adapter(activity,ListProduct,mCount)
        mRec?.adapter = mAdapter
 */
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
