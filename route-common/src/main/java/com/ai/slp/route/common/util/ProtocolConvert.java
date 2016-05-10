package com.ai.slp.route.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class ProtocolConvert {

    public static String convert(String template, String requestData) {
        JsonObject dataJson = (JsonObject) new JsonParser().parse(requestData);
        JsonObject templateJson = (JsonObject) new JsonParser().parse(template);
        JsonObject result = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : templateJson.entrySet()) {
            JsonElement temp = dataJson.get(entry.getKey());
            if (temp == null)
                throw new RuntimeException("Cannot find the value of key[" + entry.getKey() + "]");

            result.add(entry.getKey(), temp);
        }

        return result.toString();
    }
}
