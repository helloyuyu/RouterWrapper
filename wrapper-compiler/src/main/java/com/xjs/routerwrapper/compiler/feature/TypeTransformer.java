package com.xjs.routerwrapper.compiler.feature;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.javapoet.TypeName;
import com.xjs.routerwrapper.compiler.feature.transformers.ArrayListTypeTransformImpl;
import com.xjs.routerwrapper.compiler.feature.transformers.ArrayTypeTransformImpl;
import com.xjs.routerwrapper.compiler.feature.transformers.BoxPrimitiveTypeTransformImp;
import com.xjs.routerwrapper.compiler.feature.transformers.ParcelableTypeTransformImpl;
import com.xjs.routerwrapper.compiler.feature.transformers.PrimitiveTypeTransformImpl;
import com.xjs.routerwrapper.compiler.feature.transformers.SerializableTypeTransformImpl;
import com.xjs.routerwrapper.compiler.feature.transformers.StringTypeTransformImpl;
import com.xjs.routerwrapper.compiler.utils.Logger;


import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author xjs
 *         on  2018/1/14
 *         desc:
 */

public class TypeTransformer {

    private Logger logger;
    private Types typeUtils;
    private Elements elements;
    private Set<ITypeTransform> typeTransformSet;

    public static TypeTransformer create(Logger logger, Types types, Elements elements) {
        return create(logger, types, elements, null);
    }

    public static TypeTransformer create(Logger logger, Types types, Elements elements, Set<ITypeTransform> typeTransformList) {
        return new TypeTransformer(logger, types, elements, typeTransformList);

    }


    private TypeTransformer(@NonNull Logger logger, @NonNull Types typeUtils, Elements elements, @Nullable Set<ITypeTransform> typeTransformList) {
        if (typeTransformList != null) {
            this.typeTransformSet = typeTransformList;
        } else {
            this.typeTransformSet = new LinkedHashSet<>();
        }
        this.logger = logger;
        this.typeUtils = typeUtils;
        this.elements = elements;
        init();
    }


    public CharSequence transform(Element target) {
        logger.info(target.getSimpleName() + "->transformType:" + TypeName.get(target.asType()).toString());
        for (ITypeTransform typeTransform : typeTransformSet) {
            if (typeTransform.accept(target)) {
                return typeTransform.transform(target);
            }
        }
        return "Object";
    }

    private void init() {
        typeTransformSet.add(new PrimitiveTypeTransformImpl());
        typeTransformSet.add(new BoxPrimitiveTypeTransformImp());
        typeTransformSet.add(new StringTypeTransformImpl());
        typeTransformSet.add(new ArrayListTypeTransformImpl(typeUtils, elements));
        typeTransformSet.add(new SerializableTypeTransformImpl(typeUtils));
        typeTransformSet.add(new ParcelableTypeTransformImpl(typeUtils));
        typeTransformSet.add(new ArrayTypeTransformImpl(typeUtils));
    }
}
