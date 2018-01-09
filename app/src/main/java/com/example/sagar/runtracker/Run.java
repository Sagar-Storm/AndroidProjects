package com.example.sagar.runtracker;

import java.util.Date;

/**
 * Created by sagar on 10/7/17.
 */

public class Run {
    private Date mStartDate ;
    private long mId ;

    public Run() {
        mId = -1;
        mStartDate = new Date();
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate){
        mStartDate = startDate;
    }

    public int getDurationSeconds(long endMillis) {
        return (int) ((endMillis - mStartDate.getTime())) / 1000 ;
    }

    public static String formatDuration(int durationSeconds) {
        int seconds = durationSeconds % 60;
        int minutes = ((durationSeconds - seconds) / 60 ) % 60;
        int hours = (durationSeconds - (minutes * 60) - seconds) / 60 ;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
