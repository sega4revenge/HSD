package com.finger.hsd.presenter

import com.finger.hsd.common.getObservable
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

class DetailProductPresenter : getObservable{


    val disposable = CompositeDisposable()

      val  Ipresenter : IDetailProductPresenterView;

    constructor(IpresenterView : IDetailProductPresenterView){

        this.Ipresenter = IpresenterView

    }

// xóa sản phẩm
    fun processDeleteProduct(idProduct : String){
        try {
            jsonObject.put("id_product", idProduct)
        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(getJsonObjectObservable(Constants.URL_DELETE_PRODUCT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSONObject>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: JSONObject?) {
                        Ipresenter.onSucess(t!!, 222)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())
                        Ipresenter.onError(222)
                    }

                }))
    }



    //update thông báo trước ngày
    fun processDayBefore(idProduct : String, day :Int?){
        Mylog.d("aaaaaaa "+idProduct+ ", day: "+day)
        try {
            jsonObject.put("id_product", idProduct)
            jsonObject.put("day", day)

        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(getJsonObjectObservable(Constants.URL_CHANGE_DAY_BEFORE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSONObject>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: JSONObject?) {
                        Ipresenter.onSucess(t!!, 333)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())
                        Ipresenter.onError(333)
                    }

                }))
    }

    // update sản phẩm
    fun processInfomationUpdate(idProduct : String, name: String?, expiredtime : String?, description: String?){
        try {
            jsonObject.put("id_product", idProduct)
            jsonObject.put("nameproduct", name)
            jsonObject.put("hsd_ex", expiredtime)
            jsonObject.put("description", description)
        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(getJsonResponseObservable(Constants.URL_UPDATE_INFOMATION_PRODUCT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: Response?) {
                        Ipresenter.onSucess(t!!.product!!, 333)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())
                        Ipresenter.onError(333)
                    }

                }))
    }

    interface IDetailProductPresenterView{
        fun onSucess(response : Product_v, type: Int)
        fun onSucess(response: JSONObject,type: Int)
        fun onError (typeError : Int)
    }
}