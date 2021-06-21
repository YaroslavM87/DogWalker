package com.yaroslavm87.dogwalker.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Tools {

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

    @SuppressLint("ResourceAsColor")
    public static void setColorTextToViewsDependingOnLastTimeWalk(long lastTimeWalkExact, TextView...views) {

        long currentTimeExact = System.currentTimeMillis();
        long currentTimeInFullDays = currentTimeExact - (currentTimeExact % Constants.PERIOD_ONE_DAY);
        long lastTimeWalkInFullDays = lastTimeWalkExact - (lastTimeWalkExact % Constants.PERIOD_ONE_DAY);
        long timeDifference = currentTimeInFullDays - lastTimeWalkInFullDays;

        if(lastTimeWalkExact == 0L || timeDifference >= Constants.PERIOD_FIVE_DAYS) {

            for(TextView v : views) {
                v.setTextColor(Constants.COLOR_DOG_DID_NOT_WALK_THIS_WEEK);
            }

        } else {

            if(timeDifference >= Constants.PERIOD_THREE_DAYS) {

                for(TextView v : views) {
                    v.setTextColor(Constants.COLOR_DOG_WALKED_THIS_WEEK);
                }

            } else {

                for(TextView v : views) {
                    v.setTextColor(Constants.COLOR_DOG_WALKED_RECENTLY);
                }
            }
        }
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

//    public static void displayImageRound(final Context ctx, final ImageView img, @DrawableRes int drawable) {
//        try {
//            Glide.with(ctx).load(drawable).asBitmap().centerCrop().into(new BitmapImageViewTarget(img) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    img.setImageDrawable(circularBitmapDrawable);
//                }
//            });
//        } catch (Exception e) {
//        }
//    }
}