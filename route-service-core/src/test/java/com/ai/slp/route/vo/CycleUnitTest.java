package com.ai.slp.route.vo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by jackieliu on 16/7/13.
 */
public class CycleUnitTest {
    private static Logger logger = LoggerFactory.getLogger(CycleUnitTest.class);
    @Test
    public void buildNextInvalidTimeStamp(){
        //当前开始时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH,31);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        Timestamp startTime = new Timestamp(calendar.getTimeInMillis());
        logger.info("now is :{}",startTime.toString());
        //两天
        int cycleNum = 22;
        Timestamp timestamp = CycleUnit.buildNextInvalidTimeStamp(startTime,cycleNum,CycleUnit.DAY);
        logger.info("{} day cycle:{}\r",cycleNum,timestamp.toString());

        //周
        cycleNum = 2;
        timestamp = CycleUnit.buildNextInvalidTimeStamp(startTime,cycleNum,CycleUnit.WEEK);
        logger.info("{} week cycle:{}\r",cycleNum,timestamp.toString());

        //月
        cycleNum = 8;
        timestamp = CycleUnit.buildNextInvalidTimeStamp(startTime,cycleNum,CycleUnit.MONTH);
        logger.info("{} month cycle:{}\r",cycleNum,timestamp.toString());

        //季度
        calendar.set(Calendar.MONTH,Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH,6);
        startTime = new Timestamp(calendar.getTimeInMillis());
        logger.info("For QUARTER now is :{}",startTime.toString());
        cycleNum = 1;
        timestamp = CycleUnit.buildNextInvalidTimeStamp(startTime,cycleNum,CycleUnit.QUARTER);
        logger.info("{} QUARTER cycle:{}\r",cycleNum,timestamp.toString());
    }
}
