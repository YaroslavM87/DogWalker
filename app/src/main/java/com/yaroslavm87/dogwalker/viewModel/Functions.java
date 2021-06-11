package com.yaroslavm87.dogwalker.viewModel;

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class Functions {

    public static String parseMillsToDate(long millsToParse) {

        String date;

        if(millsToParse == 0L) {
            date = "Еще не гулял(а)";

        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy в HH:mm", Locale.ENGLISH);

            date = formatter.format(millsToParse);
        }

        return date;
    }

    public static void setColorToViewsDependingOnLastTimeWalk(long lastTimeWalkExact, View ...views) {

        long currentTimeExact = System.currentTimeMillis();
        long currentTimeInFullDays = currentTimeExact - (currentTimeExact % Constants.PERIOD_ONE_DAY);
        long lastTimeWalkInFullDays = lastTimeWalkExact - (lastTimeWalkExact % Constants.PERIOD_ONE_DAY);
        long timeDifference = currentTimeInFullDays - lastTimeWalkInFullDays;

        if(lastTimeWalkExact == 0L || timeDifference >= Constants.PERIOD_FIVE_DAYS) {

            for(View v : views) {
                v.setBackgroundResource(Constants.COLOR_DOG_DID_NOT_WALK_THIS_WEEK);
            }

        } else {

            if(timeDifference >= Constants.PERIOD_THREE_DAYS) {

                for(View v : views) {
                    v.setBackgroundResource(Constants.COLOR_DOG_WALKED_THIS_WEEK);
                }

            } else {

                for(View v : views) {
                    v.setBackgroundResource(Constants.COLOR_DOG_WALKED_RECENTLY);
                }
            }
        }
    }
}