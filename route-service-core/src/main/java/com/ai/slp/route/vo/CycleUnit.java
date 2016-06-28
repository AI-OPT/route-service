package com.ai.slp.route.vo;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by xin on 16-4-27.
 */
public enum CycleUnit {
    DAY("D"), WEEK("W"), MONTH("M"), QUARTER("Q"), YEAR("Y");

    private String value;

    CycleUnit(String value) {
        this.value = value;
    }

    /**
     * 生成下一次失效时间
     * @param startDate
     * @param value
     * @param unit
     * @return
     */
    public static Timestamp buildNextInvalidTimeStamp(Timestamp startDate, int value, CycleUnit unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 当前时间也算在周期内，所以需要自减
        value--;
        switch (unit) {
            case DAY: {
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + value);
                break;
            }
            case WEEK: {
                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear + value);
                int initDay = calendar.getFirstDayOfWeek();
                calendar.set(Calendar.DAY_OF_WEEK, initDay);
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                break;
            }
            case MONTH: {
                int month = calendar.get(Calendar.MONTH);
                calendar.set(Calendar.MONTH, month + value);
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DATE, 1);
                calendar.add(Calendar.DATE, -1);
                break;
            }
            case QUARTER: {
                int quarterNumber = getQuarter(calendar.get(Calendar.MONTH));
                calendar.add(Calendar.YEAR, (quarterNumber + value ) / 4);
                calendar.set(Calendar.MONTH, getQuarterInMonth((quarterNumber + value) % 4));
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DATE, 1);
                calendar.add(Calendar.DATE, -1);
                break;
            }
            case YEAR: {
                calendar.setTime(startDate);
                int year = calendar.get(Calendar.YEAR);
                calendar.set(Calendar.YEAR, year + value);
                calendar.set(Calendar.DAY_OF_MONTH, 31);
                calendar.set(Calendar.MONTH, 11);
                break;
            }

        }

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return new Timestamp(calendar.getTimeInMillis());
    }

    private static int getQuarterInMonth(int quarterNumber) {
        int[] months = new int[]{2, 5, 8, 11};
        return months[quarterNumber];
    }

    /**
     * 确认月份所属季度
     * @param month
     * @return
     */
    private static int getQuarter(int month) {
        if (month >= 0 && month <= 2)
            return 0;
        else if (month >= 3 && month <= 5)
            return 1;
        else if (month >= 6 && month <= 8)
            return 2;
        else
            return 3;
    }

    public static CycleUnit convert(String cycle_unit) {
        switch (cycle_unit) {
            case "D":
                return DAY;
            case "W":
                return WEEK;
            case "M":
                return MONTH;
            case "Q":
                return QUARTER;
            case "Y":
                return YEAR;
            default:
                throw new RuntimeException("Can not find cycleUnit[" + cycle_unit + "]");
        }
    }
}
