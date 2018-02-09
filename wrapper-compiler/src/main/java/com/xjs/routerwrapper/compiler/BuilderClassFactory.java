package com.xjs.routerwrapper.compiler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xjs.routerwrapper.compiler.feature.TypeTransformer;
import com.xjs.routerwrapper.compiler.utils.Logger;
import com.xjs.routerwrapper.compiler.utils.RouteTypes;
import com.xjs.routerwrapper.compiler.utils.StringUtils;
import com.xjs.routerwrapper.compiler.utils.SubsetUtils;
import com.xjs.routerwrapper.compiler.utils.TypeUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static com.xjs.routerwrapper.compiler.Constants.GENERATE_CLASS_NAME_PREFIX;

/**
 * @author xjs
 *         on  2018/1/13
 *         desc:
 */
@Deprecated
public class BuilderClassFactory {

    private TypeElement classElement;
    private List<Element> fieldElements;
    private Types typeUtils;
    private Logger logger;
    private TypeTransformer typeTransformer;

    private static final String STATIC_BUILDER_METHOD_NAME = "builder";

    public BuilderClassFactory(TypeElement classElement, List<Element> fieldElements,
                               Logger logger, TypeTransformer transformerManager, Types typeUtils) {
        this.classElement = classElement;
        this.fieldElements = fieldElements;
        this.typeTransformer = transformerManager;
        this.logger = logger;
        this.typeUtils = typeUtils;
    }

    /**
     * 生成类文件
     *
     * @param classElement 类元素
     * @param elementList  字段元素
     */
    private JavaFile generateClass(TypeElement classElement, List<Element> elementList) {

        logger("build class > %1$s", classElement.getSimpleName());

        List<Element> mustSetElements = new ArrayList<>();
        List<Element> setElements = new ArrayList<>();

        for (Element element : elementList) {
            Autowired autowired = element.getAnnotation(Autowired.class);
            if (autowired.required()) {
                mustSetElements.add(element);
            } else {
                setElements.add(element);
            }
        }

        List<MethodSpec> builderMethod = createRouteStaticBuilderMethods(classElement, mustSetElements, setElements);
        logger("build createRouteStaticBuilderMethods class > %1$s ", classElement.getSimpleName());

        //构造函数
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        //类名
        TypeSpec classTypeSpec = TypeSpec
                .classBuilder(getClassName(classElement))
                .addModifiers(Modifier.PUBLIC)
                .addMethods(builderMethod)
                .addMethod(constructor)
                .build();

        logger("Builder_ %1$s end <<< ", classElement.getSimpleName());

        return JavaFile.builder(getClassElementPackageName(classElement), classTypeSpec)
                .build();
    }

    /**
     * 创建所有的builder方法
     *
     * @param classElement    ；
     * @param mustSetElements 必须的参数
     * @param setElements     可选的参数
     * @return ；
     */
    private List<MethodSpec> createRouteStaticBuilderMethods(TypeElement classElement, @NonNull List<Element> mustSetElements,
                                                             @Nullable List<Element> setElements) {

        boolean isActivitySubclass = isActivitySubclass(classElement);
        logger.info(String.format("isActivitySubclass->%1$s %2$s", classElement.getQualifiedName(), isActivitySubclass));
        List<MethodSpec> methodSpecList = new ArrayList<>();
        MethodSpec justMustSetParam = null;
        if (isActivitySubclass) {
            justMustSetParam = createActivityStaticBuilderMethod(classElement, mustSetElements, "");
        } else {
            justMustSetParam = createClassStaticBuilderMethod(classElement, mustSetElements, "");
        }
        logger.info(String.format("just must set param->%1$s", classElement.getQualifiedName()));

        methodSpecList.add(justMustSetParam);
        if (setElements != null && !setElements.isEmpty()) {
            Set<int[]> setElementPositionCombinationSet = SubsetUtils.recursionGetCombination(0, setElements.size() - 1);
            if (setElementPositionCombinationSet != null && !setElementPositionCombinationSet.isEmpty()) {
                int times = 1;
                for (int[] positionArray : setElementPositionCombinationSet) {
                    List<Element> elements = new ArrayList<>();
                    elements.addAll(mustSetElements);
                    for (int index : positionArray) {
                        elements.add(setElements.get(index));
                    }
                    MethodSpec builderMethod = null;
                    if (isActivitySubclass) {
                        builderMethod = createActivityStaticBuilderMethod(classElement, elements, String.valueOf(times));
                    } else {
                        builderMethod = createClassStaticBuilderMethod(classElement, elements, String.valueOf(times));
                    }
                    methodSpecList.add(builderMethod);
                    times++;
                }
            }
        }
        return methodSpecList;
    }


    /**
     * new RouteBuilderWrapper(ARouter.getInstance().build("///")
     * .withInt("_progress", progress)
     * .withString("_password", password))
     *
     * @param elements parameter element
     * @return ;
     */
    private MethodSpec createActivityStaticBuilderMethod(@NonNull TypeElement classElement, List<Element> elements, String builderMethodNameSuffix) {
        List<ParameterSpec> parameterSpecList = new ArrayList<>();
        String routePath = classElement.getAnnotation(Route.class).path();
        //ARouter.getInstance().build("/path")
        CodeBlock.Builder arouterParamCodeBlockBuilder = CodeBlock.builder().add(
                "$N.getInstance().build($S)"
                , Constants.AROUTER_QUALIFIED_CLASS_NAME
                , routePath);
        for (Element element : elements) {
            String parameterName = replaceStartWith_m_privateField(element.getSimpleName().toString());
            //(XXX xx)
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(element.asType())
                    , parameterName)
                    .build();
            parameterSpecList.add(parameterSpec);
            //.withXxx("key",value)
            arouterParamCodeBlockBuilder.add(".with$N($S,$N)"
                    , typeTransformer.transform(element)
                    , getParamKey(element)
                    , parameterName);
        }
        // new RouteBuilderWrapper(ARoute.getInstance().build("path").withXxx()...)
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .add("return new $N(", Constants.ROUTE_BUILDER_WRAPPER_CLASS_NAME)
                .add(arouterParamCodeBlockBuilder.build())
                .add(");");

        return MethodSpec.methodBuilder(STATIC_BUILDER_METHOD_NAME + builderMethodNameSuffix)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameters(parameterSpecList)
                .addCode(codeBlockBuilder.build())
                .returns(ClassName.bestGuess(Constants.ROUTE_BUILDER_WRAPPER_CLASS_NAME))
                .build();
    }

    private MethodSpec createClassStaticBuilderMethod(@NonNull TypeElement classElement, @NonNull List<Element> elements, String builderMethodNameSuffix) {
        List<ParameterSpec> parameterSpecList = new ArrayList<>();
        String routePath = classElement.getAnnotation(Route.class).path();
        // ARouter.getInstance()
        CodeBlock.Builder aRouterParamCodeBlockBuilder = CodeBlock.builder().add(
                "$N.getInstance().build($S)"
                , Constants.AROUTER_QUALIFIED_CLASS_NAME
                , routePath);
        for (Element element : elements) {
            String parameterName = replaceStartWith_m_privateField(element.getSimpleName().toString());
            // (XXX xxx)
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(element.asType())
                    , parameterName)
                    .build();
            parameterSpecList.add(parameterSpec);
            //.withXxx("key",value)
            aRouterParamCodeBlockBuilder.add(".with$N($S,$N)"
                    , typeTransformer.transform(element)
                    , getParamKey(element)
                    , parameterName);
        }
        //.navigation 方法;
        aRouterParamCodeBlockBuilder.add(".navigation()");
        // return ARouter.getInstance().build("path").navigation(代码;


        CodeBlock returnCodes = CodeBlock.builder()
                .add("return ($N)", classElement.getQualifiedName().toString())
                .add(aRouterParamCodeBlockBuilder.build())
                .add(";")
                .build();

        return MethodSpec.methodBuilder(STATIC_BUILDER_METHOD_NAME + builderMethodNameSuffix)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameters(parameterSpecList)
                .addCode(returnCodes)
                .returns(ClassName.bestGuess(classElement.getQualifiedName().toString()))
                .build();

    }

    /**
     * 判断是否继承Activity
     * android.app.Activity 或者 android.support.v7.app.AppCompatActivity 的
     *
     * @param element ；
     * @return ；
     */
    private boolean isActivitySubclass(@NonNull TypeElement element) {
        return TypeUtils.recursionIsImplements(element, ClassName.bestGuess(RouteTypes.ACTIVITY.getClassName()), typeUtils)
                || TypeUtils.recursionIsImplements(element, ClassName.bestGuess(RouteTypes.SUPPORT_ACTIVITY.getClassName()), typeUtils);
    }


    private String getParamKey(Element element) {
        Autowired autowired = element.getAnnotation(Autowired.class);
        return autowired.name();
    }

    private String replaceStartWith_m_privateField(String fieldName) {
        //匹配 类似于 mUserName这样的
        String regex = "^m[A-Z]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fieldName);
        if (matcher.find()) {
            return StringUtils.firstCharacterToLow(fieldName.substring(1));
        }
        return fieldName;
    }


    private static String getClassName(TypeElement element) {
        return GENERATE_CLASS_NAME_PREFIX + element.getSimpleName();
    }

    private static String getClassElementPackageName(TypeElement classElement) {
        return ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
    }

    private JavaFile build() {
        return generateClass(classElement, fieldElements);
    }

    private void logger(String format, Object... objects) {
        logger(String.format(format, objects));
    }

    private void logger(CharSequence character) {
        logger.info(character);

    }

    public static JavaFile create(TypeElement classElement, List<Element> fieldElements, Logger logger, TypeTransformer transformerManager, Types typeUtils) {
        return new BuilderClassFactory(classElement, fieldElements, logger, transformerManager, typeUtils).build();
    }

}

