//package com.finger.hsd.common
//
//import android.app.DialogFragment
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.finger.hsd.R
//import com.finger.hsd.activity.DetailProductActivity
//
//class DialogAllFragment : DialogFragment() {
//
//    companion object {
//        fun newInstance(title: String): DialogFragment {
//            var fragment = DialogFragment()
//            var args = Bundle()
//            args.putString("title", title)
//            args.putInt("from_delete", 1)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    var type : Int = 0
//
//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        var view = inflater!!.inflate(R.layout.fragment_dialog_all, container!!,false)
//        var title = arguments.getString("title")
//
//         type = arguments.getInt("from_delete")
//
//        return view
//    }
//
//    fun cancel( view: View){
//        if(type==1)
//
//    }
//    fun ok(view: View){
//        if(type==1)
//
//    }
////
////    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
////
////
////        var title = arguments.getString("title")
////
////        return AlertDialog.Builder(activity)
////                .setIcon(R.mipmap.ic_launcher)
////                .setTitle(title)
////
////                .setPositiveButton("OK",
////                        DialogInterface.OnClickListener() { dialogInterface, i ->
////                            Toast.makeText(activity, "Ok", Toast.LENGTH_SHORT).show()
////                            (activity as DetailProductActivity ).onPositiveDelete()
////                        })
////                .setNegativeButton("Cancel",
////                        DialogInterface.OnClickListener() {dialogInterface, i ->
////                            Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show()
////                        })
////                .create()
////    }
//}