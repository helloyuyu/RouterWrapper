package com.xjs.routerwrapper.demo.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.xjs.routerwrapper.demo.R;
import com.xjs.routerwrapper.demo.model.TestBean;

import java.util.Map;

/**
 * 这是类的描述
 *
 * @author xjs
 * @date 2018/2/9 10:09
 */
@Route(path = "/dome/test/test_java_doc")
public class TestJavaDocActivity extends AppCompatActivity {

    @Autowired(required = true, desc = "消息")
    String message;
    @Autowired(required = true, desc = "测试bean")
    TestBean testBean;
    @Autowired(required = true)
    float tataFloat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_java_doc);
        ARouter.getInstance().inject(this);
        TextView printTv = findViewById(R.id.print_tv);
        String msg = "message:" + message + "\n" +
                "testbean:" + testBean.toString() + "\n" +
                "tataFloat:" + tataFloat;
        printTv.setText(msg);
    }
}
