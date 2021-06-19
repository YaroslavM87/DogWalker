package com.yaroslavm87.dogwalker.viewModel;

import android.view.View;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Functions {

    public static String parseMillsToDate(long millsToParse, String datePattern) {

        String result;

        Locale russian = new Locale("ru");

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern, russian);

        DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
        String[] newMonths = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        //dateFormatSymbols.setMonths(newMonths);



                //(SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, russian);
        //formatter.setDateFormatSymbols(dateFormatSymbols);

        //Date date = new Date("dd MMM yyyy", millsToParse);

        if(millsToParse == 0L) {
            result = "Еще не гулял(а)";

        } else {
            //formatter = new SimpleDateFormat("dd MMM yyyy", russian);

            result = formatter.format(millsToParse);
        }

        return result;
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