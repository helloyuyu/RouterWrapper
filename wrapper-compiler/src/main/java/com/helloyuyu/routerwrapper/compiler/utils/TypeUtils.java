package com.helloyuyu.routerwrapper.compiler.utils;

import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author xjs
 *         on  2018/1/15
 *         desc:
 */

public class TypeUtils {
    /**
     * 是否继承、实现 类接口
     *
     * @param classElement  原类
     * @param impsInterface 继承、实现接口、类
     * @param typeUtils     ；
     * @return ；
     */
    public static boolean recursionIsImplements(TypeElement classElement, TypeName impsInterface, Types typeUtils) {
        if (classElement.asType().getKind() == TypeKind.NONE) {
            return false;
        }

        if (TypeName.get(classElement.asType()).equals(impsInterface)) {
            return true;
        }

        List<? extends TypeMirror> list = classElement.getInterfaces();
        for (TypeMirror interfaceTypeMirror : list) {
            if (TypeName.get(interfaceTypeMirror).equals(impsInterface)) {
                return true;
            } else {
                TypeElement interfaceElement = (TypeElement) typeUtils.asElement(interfaceTypeMirror);
                if (recursionIsImplements(interfaceElement, impsInterface, typeUtils)) {
                    return true;
                }
            }
        }


        if (classElement.getSuperclass().getKind() == TypeKind.NONE) {
            return false;
        } else {
            return recursionIsImplements((TypeElement) typeUtils.asElement(classElement.getSuperclass()), impsInterface, typeUtils);
        }
    }

    public static boolean isSameType(Element element, Class<?> type, Types typeUtils) {
        TypeElement typeElement;
        if (element instanceof TypeElement) {
            typeElement = (TypeElement) element;
        } else {
            typeElement = (TypeElement) typeUtils.asElement(element.asType());
        }
        return typeElement.getQualifiedName().toString().equals(type.getName());
    }

    public static boolean isSubtype(TypeMirror subtypeMirror, CharSequence superTypeClassName, Types types, Elements elements) {
        return types.isSubtype(subtypeMirror, elements.getTypeElement(superTypeClassName).asType());
    }

    public static boolean isSubtype(TypeMirror subtypeMirror, Type superType, Types types, Elements elements) throws Exception {
        return types.isSubtype(subtypeMirror, elements.getTypeElement(superType.getTypeName()).asType());
    }
}
