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
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        // 当前时间也算在周期内，所以需要自减
        value--;
        switch (unit) {
            //天
            case DAY: {
//                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + value);
                calendar.add(Calendar.DAY_OF_MONTH,value);
                break;
            }
            //周
            case WEEK: {
                calendar.add(Calendar.WEEK_OF_YEAR,value);
                int initDay = calendar.getFirstDayOfWeek();
                calendar.set(Calendar.DAY_OF_WEEK, initDay);//设置为周开始时间
                calendar.add(Calendar.DAY_OF_WEEK, 6);//加六天到周末
                break;
            }
            //月
            case MONTH: {
                calendar.add(Calendar.MONTH, value+1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            }
            //季度
            case QUARTER: {
                //获取当前所属季度
                int quarterNumber = getQuarter(calendar.get(Calendar.MONTH));
                //判断是否进行加年操作
                calendar.add(Calendar.YEAR, (quarterNumber + value ) / 4);
                //获取季度最后月份并加1,用户后续最后一天获取
                calendar.set(Calendar.MONTH, getQuarterInMonth((quarterNumber + value) % 4)+1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);//设置月份的第一天
                calendar.add(Calendar.DAY_OF_MONTH, -1);//获取月份的最后一天
                break;
            }
            //年
            case YEAR: {
                calendar.add(Calendar.YEAR, value);
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);//设置为一年最后一月
                calendar.set(Calendar.DAY_OF_MONTH, 31);//设置12月最后一天
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
