package com.helloyuyu.routerwrapper.compiler;

import android.support.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.helloyuyu.routerwrapper.compiler.feature.TypeTransformer;
import com.helloyuyu.routerwrapper.compiler.utils.CollectionUtils;
import com.helloyuyu.routerwrapper.compiler.utils.Logger;
import com.helloyuyu.routerwrapper.compiler.utils.RouteTypes;
import com.helloyuyu.routerwrapper.compiler.utils.StringUtils;
import com.helloyuyu.routerwrapper.compiler.utils.SubsetUtils;
import com.helloyuyu.routerwrapper.compiler.utils.TypeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.helloyuyu.routerwrapper.compiler.Constants.FACADE_PACKAGE;
import static com.helloyuyu.routerwrapper.compiler.Constants.GENERATE_CLASS_NAME_PREFIX;
import static com.helloyuyu.routerwrapper.compiler.Constants.POSTCARD_SIMPLE_CLASS_NAME;

/**
 * @author xjs
 *         on  2018/1/13
 *         desc: 生成模块的所有路由界面RouterServer文件 和 被Route注解的Fragment的静态调用的文件
 */

public class RouterWrapperClassGenerater {

    private Logger logger;
    private Types typeUtils;
    private String outputPackage;
    private Elements elementUtils;
    private TypeTransformer typeTransformer;
    private Map<TypeElement, List<Element>> elementListMap;
    private static final String ROUTER_WRAPPER_NAME = "RouterServer";

    public RouterWrapperClassGenerater(String outputPackage, Types typeUtils, Logger logger, TypeTransformer typeTransformer, @NonNull Map<TypeElement, List<Element>> elementListMap, Elements elementUtils) {
        this.outputPackage = outputPackage;
        this.typeUtils = typeUtils;
        this.logger = logger;
        this.typeTransformer = typeTransformer;
        this.elementListMap = elementListMap;
        this.elementUtils = elementUtils;
    }

    public void write(Filer filer) throws IOException {
        for (JavaFile javaFile : createClasses()) {
            javaFile.writeTo(filer);
        }
    }

    /**
     * 构建生成文件
     *
     * @return ；
     */
    private Set<JavaFile> createClasses() {
        Set<JavaFile> javaFiles = new HashSet<>();
        List<MethodSpec> routeMethodSpecList = new LinkedList<>();
        Map<String, Integer> duplicationNameMap = new HashMap<>(elementListMap.size());

        for (Map.Entry<TypeElement, List<Element>> entry : elementListMap.entrySet()) {
            //重名加前缀 _mx  x为重复的次数
            String methodNamePrefix = "";
            TypeElement classElement = entry.getKey();
            String className = classElement.getSimpleName().toString();
            if (duplicationNameMap.containsKey(className)) {
                int times = duplicationNameMap.get(className) + 1;
                methodNamePrefix = String.format("_m%1$s", times);
                duplicationNameMap.put(className, times);
            } else {
                duplicationNameMap.put(className, 1);
            }
            //如果Route注解的是Activity添加方法，如果是其他类(也只支持Fragment参数注入)则创建JavaFile
            if (isActivitySubclass(classElement)) {
                routeMethodSpecList.addAll(getActivitySubclassMethodSpecs(classElement, entry.getValue(), methodNamePrefix));
            } else {
                javaFiles.add(createInjectFragmentJavaFile(classElement, entry.getValue()));
            }
        }
        TypeSpec routerServerClassTypeSpec = TypeSpec
                .classBuilder(ROUTER_WRAPPER_NAME)
                .addMethods(routeMethodSpecList)
                .addJavadoc("模块路由表")
                .addModifiers(Modifier.PUBLIC)
                .build();

        javaFiles.add(JavaFile.builder(outputPackage, routerServerClassTypeSpec).build());
        return javaFiles;
    }

    /**
     * 创建 作为参数注入的 fragment 的文件
     *
     * @return ；
     */
    private JavaFile createInjectFragmentJavaFile(TypeElement classElement, List<Element> fieldElement) {

        List<MethodSpec> builderMethod = getClassBuilderMethodSpecs(classElement, fieldElement);
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
        return JavaFile.builder(getClassElementPackageName(classElement), classTypeSpec)
                .build();
    }


    /**
     * 构建 activity 子类的方法
     *
     * @param typeElement  ；
     * @param fieldElement ；
     * @return ；
     */
    private List<MethodSpec> getActivitySubclassMethodSpecs(TypeElement typeElement, List<Element> fieldElement, String methodNamePrefix) {
        if (fieldElement.isEmpty()) {
            MethodSpec methodSpec = createRouteActivityStaticMethod(typeElement, fieldElement, methodNamePrefix, "");
            return Collections.singletonList(methodSpec);
        }
        List<List<Element>> possibleCombinations = getParamsPossibleCombinations(fieldElement);
        List<MethodSpec> methodSpecList = new ArrayList<>(possibleCombinations.size());
        for (int i = 0; i < possibleCombinations.size(); i++) {
            String methodNameSuffix = i == 0 ? "" : String.valueOf(i);
            MethodSpec methodSpec = createRouteActivityStaticMethod(typeElement, possibleCombinations.get(i), methodNamePrefix, methodNameSuffix);
            methodSpecList.add(methodSpec);
        }
        return methodSpecList;
    }

    /**
     * ARouter 现在只是fragment支持参数注入
     *
     * @param typeElement  ；
     * @param fieldElement ；
     * @return ；
     */
    private List<MethodSpec> getClassBuilderMethodSpecs(TypeElement typeElement, List<Element> fieldElement) {
        if (fieldElement.isEmpty()) {
            MethodSpec methodSpec = createClassStaticBuilderMethod(typeElement, fieldElement, "");
            return Collections.singletonList(methodSpec);
        }
        List<List<Element>> possibleCombinations = getParamsPossibleCombinations(fieldElement);
        List<MethodSpec> methodSpecList = new ArrayList<>(possibleCombinations.size());
        for (int i = 0; i < possibleCombinations.size(); i++) {
            String methodNameSuffix = i == 0 ? "" : String.valueOf(i);
            MethodSpec methodSpec = createClassStaticBuilderMethod(typeElement, possibleCombinations.get(i), methodNameSuffix);
            methodSpecList.add(methodSpec);
        }
        return methodSpecList;
    }

    /**
     * 获取注入参数所有可能组合情况
     *
     * @param fieldElementList route 类中所有依赖注入的参数element
     * @return 注入参数所有可能组合情况
     */
    private List<List<Element>> getParamsPossibleCombinations(List<Element> fieldElementList) {

        List<Element> choosableElements = new ArrayList<>();
        for (int i = 0; i < fieldElementList.size(); i++) {
            Element element = fieldElementList.get(i);
            Autowired autowired = element.getAnnotation(Autowired.class);
            if (!autowired.required()) {
                fieldElementList.remove(i);
                i--;
                choosableElements.add(element);
            }
        }
        List<List<Element>> resultList = new ArrayList<>(2 ^ choosableElements.size());
        resultList.add(fieldElementList);

        if (CollectionUtils.isNotEmpty(choosableElements)) {
            Set<int[]> combinationSet = SubsetUtils.recursionGetCombination(0, choosableElements.size() - 1);
            if (CollectionUtils.isNotEmpty(combinationSet)) {
                for (int[] positionArray : combinationSet) {
                    List<Element> elements = new ArrayList<>(fieldElementList.size() + positionArray.length);
                    elements.addAll(fieldElementList);
                    for (int index : positionArray) {
                        elements.add(choosableElements.get(index));
                    }
                    resultList.add(elements);
                }
            }
        }
        return resultList;
    }


    /**
     * @param classElement     activity class element
     * @param elements         parameter element
     * @param methodNameSuffix 方法名后缀为了避免可选参数类型重复
     * @param methodNamePrefix 方法名前缀避免类名重复
     * @return ;
     */
    private MethodSpec createRouteActivityStaticMethod(@NonNull TypeElement classElement, List<Element> elements, String methodNamePrefix, String methodNameSuffix) {
        List<ParameterSpec> parameterSpecList = new ArrayList<>();
        String routePath = classElement.getAnnotation(Route.class).path();
        //ARouter.getInstance().build("/path")
        ClassName aRouter = ClassName.get(Constants.AROUTER_LAUNCHER_PACKAGE, Constants.AROUTER_SIMPLE_CLASS_NAME);
        CodeBlock.Builder arouterParamCodeBlockBuilder = CodeBlock.builder().add(
                "return $T.getInstance().build($S)", aRouter, routePath);
        // 注释文档
        CodeBlock.Builder javaDocBuilder = createClassJavaDoc(classElement);

        for (Element element : elements) {
            String parameterName = replaceStartWith_m_Field(element.getSimpleName().toString());
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(element.asType())
                    , parameterName)
                    .build();
            parameterSpecList.add(parameterSpec);
            //.withXxx("key",value)
            arouterParamCodeBlockBuilder.add(".with$N($S,$N)"
                    , typeTransformer.transform(element)
                    , getParamKey(element)
                    , parameterName);
            javaDocBuilder.add(createFieldJavaDoc((VariableElement) element));
        }
        arouterParamCodeBlockBuilder.add(";");
        String methodName;
        if (StringUtils.isNotEmpty(methodNamePrefix)) {
            methodName = methodNamePrefix + classElement.getSimpleName().toString() + methodNameSuffix;
        } else {
            methodName = StringUtils.firstCharacterToLow(classElement.getSimpleName().toString()) + methodNameSuffix;
        }


        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameters(parameterSpecList)
                .addJavadoc(javaDocBuilder.build())
                .addCode(arouterParamCodeBlockBuilder.build())
                .returns(ClassName.get(FACADE_PACKAGE, POSTCARD_SIMPLE_CLASS_NAME))
                .build();
    }

    private MethodSpec createClassStaticBuilderMethod(@NonNull TypeElement classElement, @NonNull List<Element> elements, String builderMethodNameSuffix) {
        List<ParameterSpec> parameterSpecList = new ArrayList<>();
        String routePath = classElement.getAnnotation(Route.class).path();
        // ARouter.getInstance()
        ClassName aRouter = ClassName.get(Constants.AROUTER_LAUNCHER_PACKAGE, Constants.AROUTER_SIMPLE_CLASS_NAME);

        CodeBlock.Builder paramCodeBlockBuilder = CodeBlock.builder().add(
                "$T.getInstance().build($S)", aRouter, routePath);
        for (Element element : elements) {
            String parameterName = replaceStartWith_m_Field(element.getSimpleName().toString());
            // (XXX xxx)
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(element.asType())
                    , parameterName)
                    .build();
            parameterSpecList.add(parameterSpec);
            //.withXxx("key",value)
            paramCodeBlockBuilder.add(".with$N($S,$N)"
                    , typeTransformer.transform(element)
                    , getParamKey(element)
                    , parameterName);
        }
        //.navigation 方法;
        paramCodeBlockBuilder.add(".navigation();");
        // return ARouter.getInstance().build("path").navigation(代码;

        ClassName classType = ClassName.bestGuess(classElement.getQualifiedName().toString());

        CodeBlock returnCodes = CodeBlock.builder()
                .add("return ($T)", classType)
                .add(paramCodeBlockBuilder.build())
                .build();

        return MethodSpec.methodBuilder("builder" + builderMethodNameSuffix)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameters(parameterSpecList)
                .addCode(returnCodes)
                .returns(ClassName.bestGuess(classElement.getQualifiedName().toString()))
                .build();
    }


    private CodeBlock.Builder createClassJavaDoc(TypeElement typeElement) {
        Route route = typeElement.getAnnotation(Route.class);
        String routeDesc = route.name();
        String classJavaDoc = elementUtils.getDocComment(typeElement);
        String linkDoc = String.format("{@link %1$s}", typeElement.getQualifiedName().toString());
        return CodeBlock.builder().add("路由描述: $N\n\n$N\n$N\n", routeDesc, classJavaDoc, linkDoc);
    }

    /**
     * 获取被{@code Autowired}注解的字段注释
     *
     * @param element field
     * @return ;
     */
    private CodeBlock createFieldJavaDoc(VariableElement element) {
        Autowired autowired = element.getAnnotation(Autowired.class);
        String paramDesc = autowired.desc();
        if ("No desc.".equals(paramDesc)) {
            paramDesc = elementUtils.getDocComment(element);
            if (paramDesc == null || "".equals(paramDesc)) {
                paramDesc = element.getSimpleName().toString();
            }
        }
        return CodeBlock.of("@param $N $N\n", element.getSimpleName(), paramDesc);
    }


    /**
     * 判断是否继承Activity
     * android.app.Activity 或者 android.support.v7.app.AppCompatActivity
     *
     * @param element ；
     * @return ；
     */
    private boolean isActivitySubclass(@NonNull TypeElement element) {
        return TypeUtils.recursionIsImplements(element, ClassName.bestGuess(RouteTypes.ACTIVITY.getClassName()), typeUtils)
                || TypeUtils.recursionIsImplements(element, ClassName.bestGuess(RouteTypes.SUPPORT_ACTIVITY.getClassName()), typeUtils);
    }

    private String replaceStartWith_m_Field(String fieldName) {
        //匹配 类似于 mUserName这样的
        String regex = "^m[A-Z]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fieldName);
        if (matcher.find()) {
            return StringUtils.firstCharacterToLow(fieldName.substring(1));
        }
        return fieldName;
    }

    /**
     * 获取 ARouter.getInstance().withString("key","value") 中的key
     *
     * @param element field element
     * @return ;
     */
    private String getParamKey(Element element) {
        Autowired autowired = element.getAnnotation(Autowired.class);
        if (StringUtils.isNotEmpty(autowired.name())) {
            return autowired.name();
        } else {
            VariableElement variableElement = (VariableElement) element;
            return variableElement.getSimpleName().toString();
        }
    }

    private static String getClassName(TypeElement element) {
        return GENERATE_CLASS_NAME_PREFIX + element.getSimpleName();
    }

    private static String getClassElementPackageName(TypeElement classElement) {
        return ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
    }
}

