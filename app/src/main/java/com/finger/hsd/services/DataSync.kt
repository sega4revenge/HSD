package com.finger.hsd.services

import android.content.Context
import android.os.AsyncTask

import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.User

class DataSync(internal var mContext: Context, internal var dataListener: DataListener) : AsyncTask<User, Int, User>() {

    internal var realm: RealmController = RealmController(mContext)


    override fun doInBackground(vararg users: User): User {

        var user: User? = null
        if (user != null) {
            user = users[0]
        }

        return addDataInRealm(user)
    }

    override fun onPreExecute() {
        dataListener.onLoadStarted()
    }

     override fun onProgressUpdate(vararg values: Int?) {
        dataListener.onLoading(values[0]!!)
    }

    override fun onPostExecute(user: User) {
        dataListener.onLoadComplete(user)
    }

    private fun addDataInRealm(user: User?): User {
        val total = user!!.describeContents()
        var current = 0
        val percent: Int
        realm = RealmController(mContext)
        realm.addUser(user!!)
        percent = (current / total.toFloat() * 100f).toInt()
        publishProgress(percent)
        return user

    }
}
