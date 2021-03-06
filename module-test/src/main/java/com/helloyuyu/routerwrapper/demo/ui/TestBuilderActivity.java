package com.helloyuyu.routerwrapper.demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.helloyuyu.routerwrapper.demo.R;
import com.helloyuyu.routerwrapper.demo.model.ARouter;
import com.helloyuyu.routerwrapper.demo.model.TestBean;
import com.xjs.routerwrapper.demo.route.Navigator;

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

    @Autowired(name = "test_bean")
    TestBean testBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_builder);
    }
}
