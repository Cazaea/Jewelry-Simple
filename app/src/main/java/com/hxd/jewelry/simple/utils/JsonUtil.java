package com.hxd.jewelry.simple.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * json数据处理工具类
 * Created by Cazaea on 2017/6/21.
 */

public class JsonUtil {
    /**
     * @param mData 原数据
     * @param array 新数据
     * @return 拼接后的总数据
     */
    public static JSONArray spliceJson(JSONArray mData, JSONArray array) {
        StringBuilder builder = new StringBuilder();
        try {
            int len = mData.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) mData.get(i);
                if (i == len - 1)
                    builder.append(obj1.toString());
                else
                    builder.append(obj1.toString()).append(",");
            }
            len = array.length();
            if (len > 0)
                builder.append(",");
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array.get(i);
                if (i == len - 1)
                    builder.append(obj1.toString());
                else
                    builder.append(obj1.toString()).append(",");
            }
            builder.insert(0, "[").append("]");
            return new JSONArray(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
