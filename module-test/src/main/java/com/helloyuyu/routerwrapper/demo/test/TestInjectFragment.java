package com.helloyuyu.routerwrapper.demo.test;

import android.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * 测试fragment
 *
 * @author xjs
 * @date 2018/2/8
 */
@Route(path = "/inject/test_inject_frag")
public class TestInjectFragment extends Fragment {
    @Autowired(name = "name", required = true)
    String content;
}
