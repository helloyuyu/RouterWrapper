package com.xjs.routerwrapper.compiler.feature.filters;

import com.squareup.javapoet.ClassName;
import com.xjs.routerwrapper.compiler.Constants;
import com.xjs.routerwrapper.compiler.feature.IRouteAnnotationFilter;
import com.xjs.routerwrapper.compiler.utils.TypeUtils;


import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * @author xjs
 *         on 2018/1/18
 *         desc:
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
