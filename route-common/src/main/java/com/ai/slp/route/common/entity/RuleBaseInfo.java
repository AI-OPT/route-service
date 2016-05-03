package com.ai.slp.route.common.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by xin on 16-4-28.
 */
public class RuleBaseInfo implements Serializable {
    private TimeType timeType;
    private RuleItem ruleItem;
    private CycleUnit cycleUnit;
    private Timestamp validateTime;
    private int cycleValue;
    private float minQuantity;
    private float maxQuantity;
    private Timestamp invalidateTime;


    public RuleBaseInfo(ResultSet resultSet) throws SQLException {
        ruleItem = RuleItem.convert(resultSet.getString("ROUTE_RULE_ITEM"));
        timeType = TimeType.convert(resultSet.getString("TIME_TYPE"));
        validateTime = resultSet.getTimestamp("BEGIN_DATE");
        if (timeType == TimeType.CYCLE) {
            cycleUnit = CycleUnit.convert(resultSet.getString("CYCLE_UNIT"));
            cycleValue = resultSet.getInt("CYCLE_VALUE");

            invalidateTime = generateNextInvalidateTime();
        } else if (timeType == TimeType.SELF_DEFINED) {
            invalidateTime = resultSet.getTimestamp("END_DATE");
        }
        minQuantity = resultSet.getFloat("MIN_QUANTITY");
        maxQuantity = resultSet.getFloat("MAX_QUANTITY");
    }

    public Timestamp generateNextInvalidateTime() {
        Timestamp startDate = validateTime;
        Timestamp nextInvalidateTime = startDate;
        while (true) {
            nextInvalidateTime = CycleUnit.buildNextInvalidTimeStamp(nextInvalidateTime, cycleValue, cycleUnit);
            if (nextInvalidateTime.after(new Date(System.currentTimeMillis()))) {
                break;
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(nextInvalidateTime);
                calendar.add(Calendar.SECOND, 1);
                nextInvalidateTime = new Timestamp(calendar.getTimeInMillis());
            }
        }
        return nextInvalidateTime;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public float getMinQuantity() {
        return minQuantity;
    }

    public float getMaxQuantity() {
        return maxQuantity;
    }

    public RuleItem getRuleItem() {
        return ruleItem;
    }

    public CycleUnit getCycleUnit() {
        return cycleUnit;
    }

    public Timestamp getValidateTime() {
        return validateTime;
    }

    public int getCycleValue() {
        return cycleValue;
    }

    public Timestamp getInvalidateTime() {
        return invalidateTime;
    }
}
