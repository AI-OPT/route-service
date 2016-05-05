package com.ai.slp.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class JsonProtocolConverter implements IProtocolConverter {
    @Override
    public String convert(String template, String convertData) {
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(template);
        JsonObject dataJsonObject = (JsonObject) new JsonParser().parse(convertData);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            jsonObject.add(entry.getKey(), dataJsonObject.get(entry.getKey()));
        }
        return jsonObject.toString();
    }
}
