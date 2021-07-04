package com.yaroslavm87.dogwalker.view;

public class WalkRecordListItem {

    private int yearTitle;
    private int monthTitle;
    private int[] setOfDays;
    private Integer[] walkDays;
    private boolean isTitle = false;

    public int getYearTitle() {
        return yearTitle;
    }

    public int getMonthTitle() {
        return monthTitle;
    }

    public int[] getSetOfDays() {
        return setOfDays;
    }

    public Integer[] getWalkDays() {
        return walkDays;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setYearTitle(int yearTitle) {
        this.yearTitle = yearTitle;
        isTitle = true;
    }

    public void setMonthTitle(int title) {
        this.monthTitle = title;
        isTitle = true;
    }

    public void setAllDaysInMonth(int[] setOfDays) {
        this.setOfDays = setOfDays;
    }

    public void setWalkDays(Integer[] walkDays) {
        this.walkDays = walkDays;
    }
}
