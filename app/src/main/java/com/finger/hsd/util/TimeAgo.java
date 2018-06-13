package com.finger.hsd.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.finger.hsd.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by william Smith on 9/27/2017 .
 */

public class TimeAgo {

    private SimpleDateFormat simpleDateFormat, dateFormat;
    private DateFormat timeFormat;
    private Date dateTimeNow;

    @Nullable
    private Context context;

    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final int WEEKS_MILLIS = 7 * DAY_MILLIS;
//    private static final int MONTHS_MILLIS = 4 * WEEKS_MILLIS;
//    private static final int YEARS_MILLIS = 12 * MONTHS_MILLIS;

    @SuppressLint("SimpleDateFormat")
    public TimeAgo() {
        System.out.print(" func TimeAgo");
        simpleDateFormat = new SimpleDateFormat("HH:mm, EEE, MMM d, yyyy" );
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");

        Date now = new Date();
        String sDateTimeNow = simpleDateFormat.format(now);

        try {
            dateTimeNow = simpleDateFormat.parse(sDateTimeNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public TimeAgo locale(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public TimeAgo with(@NonNull SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
        this.dateFormat = new SimpleDateFormat(simpleDateFormat.toPattern().split(" ")[0]);
        this.timeFormat = new SimpleDateFormat(simpleDateFormat.toPattern().split(" ")[1]);
        return this;
    }

    public Boolean TimeNew(Date dayend)
    {
        boolean result;
        Date endDate = dateTimeNow;
        //  time difference in milli seconds
        long different = endDate.getTime() - dayend.getTime();
//        if (context!=null) {
        if (different < HOUR_MILLIS) {
            result = true;
        }else {
            result = false;
        }
//        }
        return result;
    }

    public Boolean TimeNewCommunity(Date dayend)
    {
        boolean result;
        Date endDate = dateTimeNow;
        //  time difference in milli seconds
        long different = endDate.getTime() - dayend.getTime();
//        if (context!=null) {
        if (different < DAY_MILLIS) {
            result = true;
        }else {
            result = false;
        }
//        }
        return result;
    }

    public String getTimeAgo(Date startDate, Context context) {
        System.out.print(startDate + " func gettimeAgo");
        //  date counting is done till todays date
        Date endDate = dateTimeNow;
        //  time difference in milli seconds
        long different = endDate.getTime() - startDate.getTime();

        String timeFromData;
        String pastDate;
        if (context!=null) {
            if (different < MINUTE_MILLIS) {
                return context.getResources().getString(R.string.just_now);
            } else if (different < 2 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.a_min_ago);
            } else if (different < 50 * MINUTE_MILLIS) {
                return different / MINUTE_MILLIS + context.getResources().getString(R.string.mins_ago);
            } else if (different < 90 * MINUTE_MILLIS) {
                return  context.getResources().getString(R.string.a_hour_ago);
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData =  different /60/ MINUTE_MILLIS + " " + context.getResources().getString(R.string.hour_ago);
                return timeFromData;
            } else if (different < 48 * HOUR_MILLIS) {
                return timeFormat.format(startDate)+" "+ context.getResources().getString(R.string.yesterday);
            } else if (different < 7 * DAY_MILLIS) {
                return different / DAY_MILLIS +" " + context.getResources().getString(R.string.days_ago);
            } else if (different < 2 * WEEKS_MILLIS) {
                return different / WEEKS_MILLIS +" "+ context.getResources().getString(R.string.weeks_ago);
//            } else if (different < 3.5 * WEEKS_MILLIS) {
//                return different / WEEKS_MILLIS + " weeks ago";
            } else {
                pastDate = simpleDateFormat.format(startDate);
                return pastDate;
            }
        } else {
            if (different < MINUTE_MILLIS) {
                return context.getResources().getString(R.string.just_now);
            } else if (different < 2 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.a_min_ago);
            } else if (different < 50 * MINUTE_MILLIS) {
                return different / MINUTE_MILLIS + context.getString(R.string.mins_ago);
            } else if (different < 90 * MINUTE_MILLIS) {
                return context.getString(R.string.a_hour_ago);
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = different /60/ MINUTE_MILLIS + " " + context.getString(R.string.hour_ago);
                return timeFromData;
            } else if (different < 48 * HOUR_MILLIS) {
                return timeFormat.format(startDate)+" "+ context.getString(R.string.yesterday);
            } else if (different < 7  * DAY_MILLIS) {
                return different / DAY_MILLIS + context.getString(R.string.days_ago);
            } else if (different < 2 * WEEKS_MILLIS) {
                return different / WEEKS_MILLIS + context.getString(R.string.week_ago);
//            } else if (different < 3.5 * WEEKS_MILLIS) {
//                return different / WEEKS_MILLIS + context.getString(R.string.weeks_ago);
            } else {
                pastDate = simpleDateFormat.format(startDate);
                return pastDate;
            }
        }
    }
}
