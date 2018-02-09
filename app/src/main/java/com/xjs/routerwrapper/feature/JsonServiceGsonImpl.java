package com.xjs.routerwrapper.feature;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * @author xjs
 *         on  2018/2/4
 *         desc:
 */

public class JsonServiceGsonImpl implements JsonService {
    private static JsonServiceGsonImpl instance;

    private Gson gson;


    private JsonServiceGsonImpl() {
        gson = new Gson();
    }

    public static JsonServiceGsonImpl getInstance() {
        if (null == instance) {
            synchronized (JsonServiceGsonImpl.class) {
                instance = new JsonServiceGsonImpl();
            }
        }
        return instance;
    }

    @Override
    public String object2Json(Object instance) {
        return gson.toJson(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        try {
            return gson.fromJson(input, clazz);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
