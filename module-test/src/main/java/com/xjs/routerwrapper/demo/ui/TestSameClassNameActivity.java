package com.xjs.routerwrapper.demo.ui;

import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.xjs.routerwrapper.demo.model.TestBean;

import java.util.List;

/**
 * @author xjs
 *         on 2018/2/8
 *         desc:
 */
@Route(path = "/test/test_same_name_2",name = "测试同名activity")
public class TestSameClassNameActivity extends AppCompatActivity {
    @Autowired(required = true)
    String message;
    @Autowired(required = true)
    String password;
    @Autowired(required = true)
    TestBean testBean;
    @Autowired(required = true)
    TestBean testBean2;
    @Autowired(required = true, name = "testBeanList", desc = "列表")
    List<TestBean> testBeanList;
}
