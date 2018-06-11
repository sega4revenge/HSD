package com.finger.hsd.services

import com.finger.hsd.model.User

interface DataListener {

    fun onLoadStarted()
    fun onLoading(persent: Int)
    fun onLoadComplete(user: User)
}
