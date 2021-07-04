package com.yaroslavm87.dogwalker.viewModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.yaroslavm87.dogwalker.view.WalkRecordListItem;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class Tools {

    private static String LOG_TAG = "myLogs";;


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

    public static void setColorTextToViewsDependingOnLastTimeWalk(Context ctx, long lastTimeWalkExact, TextView...views) {

        long currentTimeExact = System.currentTimeMillis();
        long currentTimeInFullDays = currentTimeExact - (currentTimeExact % Constants.PERIOD_ONE_DAY);
        long lastTimeWalkInFullDays = lastTimeWalkExact - (lastTimeWalkExact % Constants.PERIOD_ONE_DAY);
        long timeDifference = currentTimeInFullDays - lastTimeWalkInFullDays;

        if(lastTimeWalkExact == 0L || timeDifference >= Constants.PERIOD_FIVE_DAYS) {

            for(TextView v : views) {
                v.setTextColor(ctx.getColor(Constants.COLOR_DOG_DID_NOT_WALK_THIS_WEEK));
            }

        } else {

            if(timeDifference >= Constants.PERIOD_THREE_DAYS) {

                for(TextView v : views) {
                    v.setTextColor(ctx.getColor(Constants.COLOR_DOG_WALKED_THIS_WEEK));
                }

            } else {

                for(TextView v : views) {
                    v.setTextColor(ctx.getColor(Constants.COLOR_DOG_WALKED_RECENTLY));
                }
            }
        }
    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        Window window = act.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(act.getResources().getColor(color));
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static ArrayList<WalkRecordListItem> generateWalkCalendar(long[] walkTimestamps) {
        final long DAY = 86400000L;

        ArrayList<WalkRecordListItem> calendar = new ArrayList<>();

        long firstTs = walkTimestamps[0];
        long lastTs = walkTimestamps[walkTimestamps.length - 1];

        int[] allMonthDaysInYStart = getDaysForEachMonthInYear(firstTs);
        int[] allMonthDaysInYFinish = getDaysForEachMonthInYear(lastTs);

        int startMonth = getMonth(firstTs);
        long globalStart = getMomentOfStartMonth(firstTs);

        int finishMonth = getMonth(lastTs);
        long globalFinish = getMomentOfStartMonth(lastTs) + (allMonthDaysInYFinish[finishMonth - 1] * DAY);

        int curIterYearNumber = getYear(firstTs);
        int curIterMonthNumber = startMonth;
        long curIterMonthStart = globalStart;
        int curIterWeekDay = getWeekDay(curIterMonthStart);
        int[] curIterAllMonthDaysInY = allMonthDaysInYStart;
        long curIterMonthLength = curIterAllMonthDaysInY[startMonth - 1] * DAY;
        ArrayList<Integer> curIterWalkDaysInMonth = new ArrayList<>();

        do{
            WalkRecordListItem titleItem = new WalkRecordListItem();

            // add item as a month title
            titleItem.setYearTitle(curIterYearNumber);
            titleItem.setMonthTitle(curIterMonthNumber);
            calendar.add(titleItem);

            // add item as set of days for current month
            WalkRecordListItem contentItem = new WalkRecordListItem();
            contentItem.setAllDaysInMonth(matchMonthAndWeekDays_dayNumber(
                    curIterAllMonthDaysInY[curIterMonthNumber - 1],
                    curIterWeekDay));

            // set a list of walks in current month
            curIterWalkDaysInMonth.clear();
            for (long walk : walkTimestamps) {
                if (
                        walk >= curIterMonthStart
                        && walk < curIterMonthStart + curIterMonthLength
                ) {
                    curIterWalkDaysInMonth.add(getDay(walk));
                }
            }
            contentItem.setWalkDays(
                    curIterWalkDaysInMonth.toArray(new Integer[0])
            );
            calendar.add(contentItem);

            // set variables for next iteration
            // next month number
            curIterMonthNumber = curIterMonthNumber + 1;
            if (curIterMonthNumber > 12) {
                curIterYearNumber = getYear(curIterMonthStart + curIterMonthLength);
                curIterMonthNumber = 1;
                // if next year get new set of days
                curIterAllMonthDaysInY = getDaysForEachMonthInYear(curIterMonthStart + curIterMonthLength);
            }
            // next month start moment
            curIterMonthStart = curIterMonthStart + curIterMonthLength;
            // next month length
            curIterMonthLength = curIterAllMonthDaysInY[curIterMonthNumber - 1] * DAY;
            // next month week day start number
            curIterWeekDay = getWeekDay(curIterMonthStart);

        } while (curIterMonthStart < globalFinish);

        return calendar;
    }

    public static void printCalendar(ArrayList<WalkRecordListItem> calendar) {
        Log.d(LOG_TAG,"--------------------------------");
        for (WalkRecordListItem item : calendar) {
            if (item.isTitle()) {
                Log.d(LOG_TAG,"");
                switch (item.getMonthTitle()) {
                    case 1:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"January");
                        break;
                    case 2:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"February");
                        break;
                    case 3:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"March");
                        break;
                    case 4:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"April");
                        break;
                    case 5:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"May");
                        break;
                    case 6:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"June");
                        break;
                    case 7:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"July");
                        break;
                    case 8:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"August");
                        break;
                    case 9:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"September");
                        break;
                    case 10:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"October");
                        break;
                    case 11:
                        System.out.println();
                        Log.d(LOG_TAG,"November");
                        break;
                    case 12:
                        Log.d(LOG_TAG,"");
                        Log.d(LOG_TAG,"December");
                        break;
                }

            } else {
                int counter = 1;
                String msg = "";

                for (int day : item.getSetOfDays()) {

                    if (day == 7) {

                        for (int w : item.getWalkDays()) {
                            if (counter == w) {
                                msg = "w";
                                break;
                            }
                        }

                        Log.d(LOG_TAG, counter + msg);
                        msg = "";
                        counter++;
                        Log.d(LOG_TAG,"");

                    } else if (day > 0) {

                        for (int w : item.getWalkDays()) {
                            if (counter == w) {
                                msg = "w";
                                break;
                            }
                        }

                        Log.d(LOG_TAG, counter + msg);
                        msg = "";
                        counter++;

                    } else {
                        Log.d(LOG_TAG, msg);
                    }
                }
            }
        }
        Log.d(LOG_TAG,"--------------------------------");
    }


    public static boolean isMomentInLeapY(long moment) {
        final long FOUR_YEAR_PERIOD = 126230400000L;  // 365 + 365 + 366 + 365
        final long COMMON_YEAR_PERIOD = 31536000000L; // 365
        final long LEAP_YEAR_PERIOD = 31622400000L;   // 366

        long from4YPeriodEnd = moment % FOUR_YEAR_PERIOD;
        return from4YPeriodEnd >= COMMON_YEAR_PERIOD * 2 && from4YPeriodEnd < COMMON_YEAR_PERIOD * 2 + LEAP_YEAR_PERIOD;
    }

    public static long getMomentOfStartYear(long moment) {
        final long FOUR_YEAR_PERIOD = 126230400000L;
        final long COMMON_YEAR_PERIOD = 31536000000L;
        final long LEAP_YEAR_PERIOD = 31622400000L;

        long from4YPeriodEnd = moment % FOUR_YEAR_PERIOD;
        long endOf4YPeriod = moment - from4YPeriodEnd;
        long yearStart;

        if (isMomentInLeapY(moment)) {
            yearStart = (COMMON_YEAR_PERIOD * 2) + endOf4YPeriod;
        } else {
            if ((from4YPeriodEnd - (COMMON_YEAR_PERIOD * 2)) > 0) {
                yearStart = (COMMON_YEAR_PERIOD * 2) + LEAP_YEAR_PERIOD + endOf4YPeriod;
            } else {
                yearStart =
                        (from4YPeriodEnd - COMMON_YEAR_PERIOD) > 0 ? COMMON_YEAR_PERIOD + endOf4YPeriod
                                : endOf4YPeriod;
            }
        }
        return yearStart;
    }

    public static int getYear(long moment) {
        final long FOUR_YEAR_PERIOD = 126230400000L;
        final long COMMON_YEAR_PERIOD = 31536000000L; // 365
        final int YEARS_IN_FOUR_YEAR_PERIOD = 4;
        final int YEARS_BEFORE_SECOND_YEAR_IN_FOUR_YEAR_PERIOD = 1;
        final int YEARS_BEFORE_LEAP_YEAR_IN_FOUR_YEAR_PERIOD = 2;
        final int YEARS_BEFORE_LAST_YEAR_IN_FOUR_YEAR_PERIOD = 3;
        final int START_YEAR = 1970;

        long from4YPeriodEnd = moment % FOUR_YEAR_PERIOD;
        int yrsTill4YEnd = (int) (((moment - from4YPeriodEnd) / FOUR_YEAR_PERIOD) * YEARS_IN_FOUR_YEAR_PERIOD);
        int currentY;
        if (isMomentInLeapY(moment)) {
            currentY = START_YEAR + yrsTill4YEnd + YEARS_BEFORE_LEAP_YEAR_IN_FOUR_YEAR_PERIOD;
        } else {
            if ((from4YPeriodEnd - COMMON_YEAR_PERIOD * 2) > 0) {
                currentY = START_YEAR + yrsTill4YEnd + YEARS_BEFORE_LAST_YEAR_IN_FOUR_YEAR_PERIOD;
            } else {
                currentY =
                        (from4YPeriodEnd - COMMON_YEAR_PERIOD) > 0 ?
                                START_YEAR + yrsTill4YEnd + YEARS_BEFORE_SECOND_YEAR_IN_FOUR_YEAR_PERIOD
                                : START_YEAR + yrsTill4YEnd;
            }
        }
        return currentY;
    }

    public static int[] getDaysForEachMonthInYear(long moment) {
        int[] daySet = {31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        daySet[1] = isMomentInLeapY(moment) ? 29 : 28;
        return daySet;
    }

    public static long getMomentOfStartMonth(long moment) {
        long yearStart = getMomentOfStartYear(moment);
        int[] daySet = getDaysForEachMonthInYear(moment);
        final long DAY_PERIOD = 86400000L;
        long monthStart = yearStart;
        for (int amDays : daySet) {
            long nextMonth = DAY_PERIOD * amDays;
            if (monthStart + nextMonth > moment)
                break;
            monthStart = monthStart + nextMonth;
        }
        return monthStart;
    }

    public static int getMonth(long moment) {
        long start = getMomentOfStartYear(moment);
        long monthStart = getMomentOfStartMonth(moment);
        final long DAY = 86400000L;
        int[] daySet = getDaysForEachMonthInYear(moment);

        int month = 0;
        do {
            if (month == 12)
                break;
            start = start + (daySet[month] * DAY);
            ++month;
        } while (start <= monthStart);
        return month;
    }

    public static long getMomentOfStartDay(long moment) {
        long monthStart = getMomentOfStartMonth(moment);
        final long DAY_PERIOD = 86400000L;
        long dayStart = monthStart;
        for (int day = 1; day <= 31; day++) {
            if (dayStart + DAY_PERIOD > moment)
                break;
            dayStart = dayStart + DAY_PERIOD;
        }
        return dayStart;
    }

    public static int getDay(long moment) {
        long start = getMomentOfStartMonth(moment);
        long dayStart = getMomentOfStartDay(moment);
        final long DAY = 86400000L;

        int day = 0;
        do {
            start = start + DAY;
            ++day;
        } while (start <= dayStart);
        return day;
    }

    public static int getWeekDay(long moment) {
        final long DAY = 86400000L;
        final int GLOBAL_WEEK_DAY_START = 4;
        int currentWeekDay;

        long beginningOfDay = moment - (moment % DAY);
        currentWeekDay = (int)((beginningOfDay / DAY) % 7) + GLOBAL_WEEK_DAY_START;
        if (currentWeekDay > 7) {
            currentWeekDay = currentWeekDay % 7;
        }
        return currentWeekDay;
    }

    public static int[] matchMonthAndWeekDays_dayNumber(int amDaysInMonth, int wdToStartFrom) {
        int prevMonthDays = wdToStartFrom - 1;
        int nextMonthDays = (7 - ((prevMonthDays + amDaysInMonth) % 7) == 7) ?
                0 : 7 - ((prevMonthDays + amDaysInMonth) % 7);
        int amOfCells = prevMonthDays + amDaysInMonth + nextMonthDays;
        int[] weekDaysInMonth = new int[amOfCells];
        for (int i = 0; i < wdToStartFrom - 1; i++) {
            weekDaysInMonth[i] = 0;
        }
        int dayCounter = 1;
        for (int i = wdToStartFrom - 1; i < amDaysInMonth + prevMonthDays; i++) {
            weekDaysInMonth[i] = dayCounter++;
        }
        for (int i = amDaysInMonth + prevMonthDays; i < weekDaysInMonth.length; i++) {
            weekDaysInMonth[i] = 0;
        }
        return weekDaysInMonth;
    }

    public static int[] matchMonthAndWeekDays_weekDayNumber(int amDaysInMonth, int wdToStartFrom) {
        int prevMonthDays = wdToStartFrom - 1;
        int nextMonthDays = (7 - ((prevMonthDays + amDaysInMonth) % 7) == 7) ?
                0 : 7 - ((prevMonthDays + amDaysInMonth) % 7);
        int amOfCells = prevMonthDays + amDaysInMonth + nextMonthDays;
        int[] weekDaysInMonth = new int[amOfCells];
        for (int i = 0; i < wdToStartFrom - 1; i++) {
            weekDaysInMonth[i] = 0;
        }
        for (int i = wdToStartFrom - 1; i < amDaysInMonth + prevMonthDays; i++) {
            weekDaysInMonth[i] = wdToStartFrom;
            wdToStartFrom = (wdToStartFrom == 7 ? 1 : ++wdToStartFrom);
        }
        for (int i = amDaysInMonth + prevMonthDays; i < weekDaysInMonth.length; i++) {
            weekDaysInMonth[i] = 0;
        }
        return weekDaysInMonth;
    }

    // as default adds timezone offset to formatted string date
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

        if(millsToParse <= 1000L) {
            result = "0";

        } else {
            //formatter = new SimpleDateFormat("dd MMM yyyy", russian);

            result = formatter.format(millsToParse);
        }

        return result;
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