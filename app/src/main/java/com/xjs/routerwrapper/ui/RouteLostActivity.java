package com.xjs.routerwrapper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xjs.routerwrapper.R;

/**
 * @author xjs
 *         desc:降级页面
 */
public class RouteLostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lost);
    }
}
