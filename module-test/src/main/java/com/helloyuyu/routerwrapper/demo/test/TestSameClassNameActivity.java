package com.helloyuyu.routerwrapper.demo.test;

import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.helloyuyu.routerwrapper.demo.model.TestBean;
import com.xjs.routerwrapper.demo.route.Navigator;

import java.util.ArrayList;

/**
 * Initializes the processor with the processing environment by
 * setting the {@code processingEnv} field to the value of the
 * {@code processingEnv} argument.  An {@code
 * IllegalStateException} will be thrown if this method is called
 * more than once on the same object.
 *
 * @author xjs
 * @date 2018/2/8
 */
@Route(path = "/test/test_same_name_1")
public class TestSameClassNameActivity extends AppCompatActivity {
    @Autowired( name = "show_message")
    String              message;
    @Autowired( name = "account_password")
    String              password;
    @Autowired( name = "test_bean_1")
    TestBean            testBean2;

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
