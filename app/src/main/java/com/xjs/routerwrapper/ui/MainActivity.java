package com.xjs.routerwrapper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xjs.routerwrapper.R;
import com.xjs.routerwrapper.model.TestBean;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.route_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/test/activity")
                        .withObject("extra_test_bean", new TestBean("Hello arouter!"))
                        .withString("message", "message")
                        .navigation();
            }
        });
    }
}
