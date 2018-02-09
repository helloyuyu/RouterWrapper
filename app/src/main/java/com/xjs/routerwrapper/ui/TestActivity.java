package com.xjs.routerwrapper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.xjs.routerwrapper.R;
import com.xjs.routerwrapper.model.TestBean;

/**
 * @author xjs
 *         on  2018/2/4
 *         desc:
 */
@Route(path = "/app/activity")
public class TestActivity extends Activity {
    @Autowired(required = true, name = "extra_test_bean")
    TestBean testBean;
    @Autowired()
    String message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_test);
        ARouter.getInstance().inject(this);
        TextView textView = findViewById(R.id.params_message_tv);
        textView.setText(message);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/unexist_group/unexist_path").withString("", "hello degrade service!").navigation();
            }
        });
    }
}
