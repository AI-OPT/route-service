package com.ai.slp.route.common.entity;

import java.util.Map;

/**
 * 路由规则类型
 * Created by xin on 16-4-28.
 */
public enum RuleItem {
    //金额 订单量
    AMOUNT("amount"), ORDERCOUNT("orderCount");

    String fieldName;

    RuleItem(String fieldName) {
        this.fieldName = fieldName;
    }

    public Float fetchTestValue(Map<String, Float> dataMap) {
        float result = 0;
        switch (this) {
            case AMOUNT: {
                result = dataMap.get(fieldName);
                break;
            }
            case ORDERCOUNT: {
                result = 1F;
                break;
            }
            default: {
                throw new RuntimeException("Failed to find the ruleItem[" + this + "]");
            }
        }

        return result;
    }

    public static RuleItem convert(String route_rule_item) {
        char value = route_rule_item.charAt(0);
        switch (value) {
            case 'A':
                return AMOUNT;
            case 'V':
                return ORDERCOUNT;
            default: {
                throw new RuntimeException("Failed to find the ruleItem[" + route_rule_item + "]");
            }
        }
    }

    public String getFieldName() {
        return fieldName;
    }
}
