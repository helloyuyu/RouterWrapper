package com.xjs.routerwrapper;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.xjs.routerwrapper.feature.JsonService;
import com.xjs.routerwrapper.feature.JsonServiceGsonImpl;

import java.lang.reflect.Type;

/**
 * @author xjs
 *         on  2018/2/4
 *         desc:
 */
@Route(path = "/service/json")
public class SerializationServiceImpl implements SerializationService {
    private JsonService jsonService;

    @Override
    public <T> T json2Object(String input, Class<T> clazz) {
        return jsonService.parseObject(input, clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return jsonService.object2Json(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return jsonService.parseObject(input, clazz);
    }

    @Override
    public void init(Context context) {
        this.jsonService = JsonServiceGsonImpl.getInstance();
    }
}
