package com.finger.hsd.model;

public class Date_Ex {
    private long DateMili;
    private String Date;

    public Date_Ex(long dateMili, String date){
        DateMili = dateMili;
        Date = date;
    }
    public long getDateMili() {
        return DateMili;
    }

    public void setDateMili(long dateMili) {
        DateMili = dateMili;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
