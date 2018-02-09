package com.xjs.routerwrapper.demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.xjs.routerwrapper.demo.R;
import com.xjs.routerwrapper.demo.model.ARouter;

/**
 * @author xjs
 */
@Route(path = "/test/test_route", name = "测试路由的")
public class TestBuilderActivity extends AppCompatActivity {

    /**
     * 消息
     */
    @Autowired(name = "message_string", desc = "消息", required = true)
    String message;
    /**
     * 年龄
     */
    @Autowired(name = "age_int", desc = "年龄", required = true)
    int age;
    @Autowired(required = true, desc = "测试bean", name = "arouter")
    ARouter aRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_builder);
    }
}
