package com.helloyuyu.routerwrapper.compiler.feature.filters;

import com.squareup.javapoet.ClassName;
import com.helloyuyu.routerwrapper.compiler.Constants;
import com.helloyuyu.routerwrapper.compiler.feature.IRouteAnnotationFilter;
import com.helloyuyu.routerwrapper.compiler.utils.TypeUtils;


import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * 跳过实现 IProvider的类
 * @author xjs
 * @date 2018/1/18
 */

public class ProviderFilterImpl implements IRouteAnnotationFilter {

    private Types typeUtils;

    public ProviderFilterImpl(Types types) {
        this.typeUtils = types;
    }

    @Override
    public boolean filter(TypeElement typeElement) {
        return TypeUtils.recursionIsImplements(typeElement, ClassName.bestGuess(Constants.ATOUTER_IPROVIDER_CLASS_NAME), typeUtils);
    }
}
