package com.helloyuyu.routerwrapper.compiler.feature.transformers;

import com.helloyuyu.routerwrapper.compiler.Constants;
import com.squareup.javapoet.ClassName;
import com.helloyuyu.routerwrapper.compiler.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * @author xjs
 *         on  2018/1/15
 *         desc:
 */

public class ParcelableTypeTransformImpl extends BaseTypeTransform {

    public ParcelableTypeTransformImpl(Types typesUtils, Elements elements) {
        super(typesUtils, elements);
    }

    @Override
    public boolean accept(Element element) {
        return TypeUtils.isSubtype(element.asType(), Constants.ANDROID_OS_PARCELABLE_CLASS_NAME, typesUtils, elements);
    }

    @Override
    public CharSequence transform(Element element) {
        return "Parcelable";
    }
}
