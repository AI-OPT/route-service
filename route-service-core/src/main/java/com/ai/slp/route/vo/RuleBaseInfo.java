package com.ai.slp.route.vo;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 路由规则基本信息
 * Created by xin on 16-4-28.
 */
public class RuleBaseInfo implements Serializable {
    //时段类型
    private TimeType timeType;
    //规则项类型
    private RuleItem ruleItem;
    //周期单位
    private CycleUnit cycleUnit;
    //生效时间
    private Timestamp validateTime;
    private int cycleValue;
    //最小阀值
    private float minQuantity;
    //最大阀值
    private float maxQuantity;
    //失效时间
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
