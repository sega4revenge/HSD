package com.finger.hsd.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment

import com.finger.hsd.model.Product

import java.util.ArrayList

import io.realm.Realm

class RealmController(application: Context) {
    val realm: Realm

    init {
        realm = Realm.getDefaultInstance()
    }

    //Refresh the realm istance
    fun refresh() {

        realm.refresh()
    }

    //clear all objects from Champion.class


    //find all objects in the Champion.class
    fun getlistProduct(): ArrayList<Product> {

        return realm.copyFromRealm(realm.where(Product::class.java).findAll()) as ArrayList<Product>
    }

    //query a single item with the given id
    fun getProduct(id: String): Product? {

        return realm.where(Product::class.java).equalTo("_id", id).findFirst()
    }

    companion object {

        var instance: RealmController? = null
            private set

        fun with(fragment: Fragment): RealmController {

            if (instance == null) {
                instance = RealmController(fragment.activity!!.application)
            }
            return instance as RealmController
        }

        fun with(activity: Activity): RealmController {

            if (instance == null) {
                instance = RealmController(activity.application)
            }
            return instance as RealmController
        }

        fun with(application: Application): RealmController {

            if (instance == null) {
                instance = RealmController(application)
            }
            return instance as RealmController
        }

        fun with(context: Context): RealmController {

            if (instance == null) {
                instance = RealmController(context)
            }
            return instance as RealmController
        }
    }

    //check if Champion.class is empty


    //query example

}
