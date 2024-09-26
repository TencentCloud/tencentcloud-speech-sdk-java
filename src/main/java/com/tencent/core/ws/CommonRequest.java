package com.tencent.core.ws;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Request 基类
 */
public class CommonRequest {
    /**
     * 扩展参数
     */
    private Map<String, Object> extendParam;
    public Map<String, Object> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, Object> extendParam) {
        this.extendParam = extendParam;
    }

    /**
     * 用于暂未支持参数设置,需要初始化extendParam，非线程安全
     *
     * @param key   字段名
     * @param value 字段值
     */
    public void set(String key, Object value) {
        if (this.extendParam == null) {
            this.extendParam = new HashMap<>();
        }
        this.extendParam.put(key, value);
    }

    public Map<String, Object> toTreeMap() {
        Map<String, Object> sortedMap = new TreeMap<>();
        // 获取对象的所有字段
        Field[] fields = this.getClass().getDeclaredFields();
        // 遍历字段并将它们添加到 TreeMap 中
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (!field.getName().equals("extendParam") && field.get(this) != null &&
                        field.getAnnotation(SerializedName.class) != null) {
                    sortedMap.put(field.getAnnotation(SerializedName.class).value(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (this.extendParam != null) {
            for (String key : this.extendParam.keySet()) {
                sortedMap.put(key, this.extendParam.get(key));
            }
        }
        return sortedMap;
    }
}
