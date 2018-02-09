package com.xjs.routerwrapper.compiler.feature;

import com.xjs.routerwrapper.compiler.feature.filters.ProviderFilterImpl;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * @author xjs
 *         on  2018/1/20
 *         desc: 过滤哪些@Route()注解不用处理的
 */

public class RouteAnnotationFilter {
    private Types typeUtils;
    private Set<IRouteAnnotationFilter> filterSet;

    private RouteAnnotationFilter(Types typeUtils) {
        this.typeUtils = typeUtils;
        this.filterSet = new HashSet<>();
        initFilters();
    }

    public boolean filter(TypeElement classElement) {
        for (IRouteAnnotationFilter filter : filterSet) {
            if (filter.filter(classElement)) {
                return true;
            }
        }
        return false;
    }

    public static RouteAnnotationFilter create(Types typeUtils) {
        return new RouteAnnotationFilter(typeUtils);
    }

    private void initFilters() {
        filterSet.add(new ProviderFilterImpl(typeUtils));
    }
}
