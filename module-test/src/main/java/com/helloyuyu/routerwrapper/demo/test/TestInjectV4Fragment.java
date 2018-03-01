package com.helloyuyu.routerwrapper.demo.test;

import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

/**
 *
 * 测试类
 * @author xjs
 * @date 2018/2/8
 */
@Route(path = "/inject/test_inject_v4frag")
public class TestInjectV4Fragment extends Fragment {
    /**
     * content
     */
    @Autowired(name = "name", required = true)
    String content;
}
