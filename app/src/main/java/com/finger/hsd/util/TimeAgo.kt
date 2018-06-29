package com.finger.hsd.util


import android.annotation.SuppressLint
import android.content.Context

import com.finger.hsd.R

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by william Smith on 9/27/2017 .
 */

class TimeAgo
//    private static final int MONTHS_MILLIS = 4 * WEEKS_MILLIS;
//    private static final int YEARS_MILLIS = 12 * MONTHS_MILLIS;

@SuppressLint("SimpleDateFormat")
constructor() {

    private var simpleDateFormat: SimpleDateFormat? = null
    private var dateFormat: SimpleDateFormat? = null
    private var timeFormat: DateFormat? = null
    private var dateTimeNow: Date? = null

    private var context: Context? = null

    init {
        print(" func TimeAgo")
        simpleDateFormat = SimpleDateFormat("HH:mm, EEE, MMM d, yyyy")
        dateFormat = SimpleDateFormat("dd/MM/yyyy")
        timeFormat = SimpleDateFormat("HH:mm")

        val now = Date()
        val sDateTimeNow = simpleDateFormat!!.format(now)

        try {
            dateTimeNow = simpleDateFormat!!.parse(sDateTimeNow)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    fun locale(context: Context): TimeAgo {
        this.context = context
        return this
    }

    @SuppressLint("SimpleDateFormat")
    fun with(simpleDateFormat: SimpleDateFormat): TimeAgo {
        this.simpleDateFormat = simpleDateFormat
        this.dateFormat = SimpleDateFormat(simpleDateFormat.toPattern().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        this.timeFormat = SimpleDateFormat(simpleDateFormat.toPattern().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        return this
    }

    fun TimeNew(dayend: Date): Boolean? {
        val result: Boolean
        val endDate = dateTimeNow
        //  time difference in milli seconds
        val different = endDate!!.time - dayend.time
        //        if (context!=null) {
        result = different < HOUR_MILLIS
        //        }
        return result
    }

    fun TimeNewCommunity(dayend: Date): Boolean? {
        val result: Boolean
        val endDate = dateTimeNow
        //  time difference in milli seconds
        val different = endDate!!.time - dayend.time
        //        if (context!=null) {
        result = different < DAY_MILLIS
        //        }
        return result
    }

    fun getTimeAgo(startDate: Date, context: Context?): String {
        print(startDate.toString() + " func gettimeAgo")
        //  date counting is done till todays date
        val endDate = dateTimeNow
        //  time difference in milli seconds
        val different = endDate!!.time - startDate.time

        val timeFromData: String
        val pastDate: String
        if (context != null) {
            if (different < MINUTE_MILLIS) {
                return context.resources.getString(R.string.just_now)
            } else if (different < 2 * MINUTE_MILLIS) {
                return context.resources.getString(R.string.a_min_ago)
            } else if (different < 50 * MINUTE_MILLIS) {
                return (different / MINUTE_MILLIS).toString() + context.resources.getString(R.string.mins_ago)
            } else if (different < 90 * MINUTE_MILLIS) {
                return context.resources.getString(R.string.a_hour_ago)
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = (different / 60 / MINUTE_MILLIS.toLong()).toString() + " " + context.resources.getString(R.string.hour_ago)
                return timeFromData
            } else if (different < 48 * HOUR_MILLIS) {
                return timeFormat!!.format(startDate) + " " + context.resources.getString(R.string.yesterday)
            } else if (different < 7 * DAY_MILLIS) {
                return (different / DAY_MILLIS).toString() + " " + context.resources.getString(R.string.days_ago)
            } else if (different < 2 * WEEKS_MILLIS) {
                return (different / WEEKS_MILLIS).toString() + " " + context.resources.getString(R.string.weeks_ago)
                //            } else if (different < 3.5 * WEEKS_MILLIS) {
                //                return different / WEEKS_MILLIS + " weeks ago";
            } else {
                pastDate = simpleDateFormat!!.format(startDate)
                return pastDate
            }

        } else {

            if (different < MINUTE_MILLIS) {
                return context!!.resources.getString(R.string.just_now)
            } else if (different < 2 * MINUTE_MILLIS) {
                return context!!.resources.getString(R.string.a_min_ago)
            } else if (different < 50 * MINUTE_MILLIS) {
                return (different / MINUTE_MILLIS).toString() + context!!.getString(R.string.mins_ago)
            } else if (different < 90 * MINUTE_MILLIS) {
                return context!!.getString(R.string.a_hour_ago)
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = (different / 60 / MINUTE_MILLIS.toLong()).toString() + " " + context!!.getString(R.string.hour_ago)
                return timeFromData
            } else if (different < 48 * HOUR_MILLIS) {
                return timeFormat!!.format(startDate) + " " + context!!.getString(R.string.yesterday)
            } else if (different < 7 * DAY_MILLIS) {
                return (different / DAY_MILLIS).toString() + context!!.getString(R.string.days_ago)
            } else if (different < 2 * WEEKS_MILLIS) {
                return (different / WEEKS_MILLIS).toString() + context!!.getString(R.string.week_ago)
                //            } else if (different < 3.5 * WEEKS_MILLIS) {
                //                return different / WEEKS_MILLIS + context.getString(R.string.weeks_ago);
            } else {
                pastDate = simpleDateFormat!!.format(startDate)
                return pastDate
            }
        }
    }

    companion object {

        val SECOND_MILLIS = 1000
        val MINUTE_MILLIS = 60 * SECOND_MILLIS
        val HOUR_MILLIS = 60 * MINUTE_MILLIS
        val DAY_MILLIS = 24 * HOUR_MILLIS
        val WEEKS_MILLIS = 7 * DAY_MILLIS
    }
}
