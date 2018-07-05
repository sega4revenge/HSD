package com.finger.hsd.activity

import android.os.Bundle
import com.finger.hsd.BaseActivity
import com.finger.hsd.R


class testImageActivity : BaseActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)




    }

//    private fun fn_profilepic() {
//
//        val params = Bundle()
//        params.putBoolean("redirect", false)
//        params.putString("type", "large")
//        GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "me/picture",
//                params,
//                HttpMethod.GET,
//                GraphRequest.Callback { response ->
//                    Log.e("Response 2", response.toString() + "")
//
//                    try {
//                        val str_facebookimage = response.jsonObject.getJSONObject("data").get("url") as String
//                        Log.e("Picture", str_facebookimage)
//
//                        Glide.with(this@MainActivity).load(str_facebookimage).skipMemoryCache(true).into(iv_image)
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                    tv_name.setText(str_facebookname)
//                    tv_email.setText(str_facebookemail)
//                    tv_dob.setText(str_birthday)
//                    tv_location.setText(str_location)
//                }
//        ).executeAsync()
//    }


}
