package com.xjs.routerwrapper.compiler.feature.transformers;


import com.xjs.routerwrapper.compiler.Constants;
import com.xjs.routerwrapper.compiler.utils.TypeUtils;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author xjs
 *         on  2018/1/25
 *         desc: ArrayList
 */

public class ArrayListTypeTransformImpl extends BaseTypeTransform {
    private String result;
    private Elements elements;

    public ArrayListTypeTransformImpl(Types typesUtils, Elements elements) {
        super(typesUtils);
        this.elements = elements;
    }

    @Override
    public boolean accept(Element element) {
        TypeMirror typeMirror = element.asType();
        if (typeMirror.getKind() == TypeKind.DECLARED && TypeUtils.isSameType(element, ArrayList.class, typesUtils)) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (!typeArguments.isEmpty()) {
                TypeMirror typeMirror1 = typeArguments.get(0);
                if (TypeUtils.isSameType(typesUtils.asElement(typeMirror1), Integer.class, typesUtils)) {
                    result = "IntegerArrayList";
                    return true;
                }
                if (TypeUtils.isSameType(typesUtils.asElement(typeMirror1), String.class, typesUtils)) {
                    result = "StringArrayList";
                    return true;
                }

                if (TypeUtils.isSameType(typesUtils.asElement(typeMirror1), CharSequence.class, typesUtils)) {
                    result = "CharSequenceArrayList";
                    return true;
                }

                try {
                    if (TypeUtils.isSubtype(typeMirror1, Constants.ANDROID_OS_PARCELABLE_CLASS_NAME, typesUtils, elements)) {
                        result = "ParcelableArrayList";
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public CharSequence transform(Element element) {
        return result;
    }
}
