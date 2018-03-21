package com.helloyuyu.routerwrapper.compiler;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.auto.service.AutoService;
import com.helloyuyu.routerwrapper.compiler.feature.RouteAnnotationFilter;
import com.helloyuyu.routerwrapper.compiler.feature.TypeTransformer;
import com.helloyuyu.routerwrapper.compiler.utils.Logger;
import com.helloyuyu.routerwrapper.compiler.utils.RegexUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    private Filer filer;
    private Logger logger;
    private Types typeUtils;
    private Elements elementUtils;
    private RouteAnnotationFilter routeAnnotationFilter;
    private String outputPackageName = null;

    private static final String MESSAGE_TAG = "BuilderCoder";

    public static final String SUPPORT_OPTION_OUTPUT_PACKAGE_NAME = "optionsOutputPackageName";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv.getMessager();
        processingEnv.getLocale();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        logger = new Logger(processingEnv.getMessager());
        routeAnnotationFilter = RouteAnnotationFilter.create(typeUtils);
        Map<String, String> options = processingEnv.getOptions();
        if (null == options || options.isEmpty() || !options.containsKey(SUPPORT_OPTION_OUTPUT_PACKAGE_NAME)) {
            StringBuilder message = new StringBuilder();
            message.append("Miss 'optionsOutputPackageName' options, at 'write.gradle' like \n");
            message.append("javaCompileOptions {\n");
            message.append("    annotationProcessorOptions {\n");
            message.append("        arguments = [optionsOutputPackageName: com.xjs.routerwrapper.demo]\n");
            message.append("    }\n");
            message.append("}");
            logger.error(message);
            throw new RuntimeException("Annotation:Compiler miss 'optionsOutputPackageName' option, for more information, look at gradle log.");
        }
        outputPackageName = options.get(SUPPORT_OPTION_OUTPUT_PACKAGE_NAME);
        if (!RegexUtils.isPackageName(outputPackageName)) {
            throw new RuntimeException(String.format("Annotation:compiler wrong package name input,at 'write.gradle' file  arguments = [optionsOutputPackageName: %1$s]\n", outputPackageName));
        }
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>(4);
        supportedTypes.add(Route.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> optionsSet = new HashSet<>();
        optionsSet.add(SUPPORT_OPTION_OUTPUT_PACKAGE_NAME);
        return optionsSet;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        logger.info("process start >>>>");
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);

        Map<TypeElement, List<Element>> elementListMap = findFiledAndIntegration(elements);
        if (!elementListMap.isEmpty()) {
            try {
                generate(elementListMap);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("process error" + e.getMessage());
            }
            logger.info("process end <<<<");
        }

        return false;
    }

    private Map<TypeElement, List<Element>> findFiledAndIntegration(Set<? extends Element> classElements) {
        Map<TypeElement, List<Element>> elementListMap = new HashMap<>();
        for (Element element : classElements) {
            TypeElement classElement = (TypeElement) element;
            logger.info(String.format("filter->%1$s", classElement.getQualifiedName()));
            boolean isSkip = routeAnnotationFilter.filter(classElement);
            logger.info(String.format("filter<-%1$s", String.valueOf(isSkip)));
            if (isSkip) {
                continue;
            }
            List<? extends Element> elements = elementUtils.getAllMembers(classElement);
            elementListMap.put(classElement, new ArrayList<>());
            for (Element memberElement : elements) {
                if (memberElement.getKind() == ElementKind.FIELD) {
                    if (memberElement.getAnnotation(Autowired.class) != null) {
                        elementListMap.get(classElement).add(memberElement);
                    }
                }
            }
        }
        return elementListMap;
    }

    private void generate(Map<TypeElement, List<Element>> elementListMap) throws IOException {
        logger.info("class size:" + elementListMap.size());
        new RouterWrapperClassGenerator(outputPackageName, typeUtils, logger, TypeTransformer.create(logger, typeUtils, elementUtils), elementListMap, elementUtils)
                .write(filer);
    }


}
