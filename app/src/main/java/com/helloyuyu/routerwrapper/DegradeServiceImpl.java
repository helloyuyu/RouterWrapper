package com.helloyuyu.routerwrapper;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;

/**
 * @author xjs
 *         on 2018/2/7
 *         desc:
 */
@Route(path = "/service/degrade_impl")
public class DegradeServiceImpl implements DegradeService {
    private Context context;

    @Override
    public void onLost(Context context, Postcard postcard) {
        Intent intent = new Intent();
        intent.setAction("com.xjs.routerwrapper.action.degradepage");
        if (context == null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }
}
