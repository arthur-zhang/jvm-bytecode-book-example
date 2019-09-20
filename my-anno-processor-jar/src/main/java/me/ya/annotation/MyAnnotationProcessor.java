package me.ya.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"me.ya.annotation.MyBuilder"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class MyAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            // typeElement 表示 MyBuilder 注解
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(typeElement);
            // 包含 MyBuilder 注解的所有字段名和字段类型映射表
            Map<String, String> fieldMap = new HashMap<>();
            for (Element element : annotatedElements) {
                fieldMap.put(element.getSimpleName().toString(), element.asType().toString());
            }
            // 获取到 MyBuilder 注解所在类的全限定名
            String className = ((TypeElement) annotatedElements.iterator().next().getEnclosingElement()).getQualifiedName().toString();
            String packageName = null;
            int lastDot = className.lastIndexOf('.');
            if (lastDot > 0) packageName = className.substring(0, lastDot);
            // 不包含包名的 class 名
            String simpleClassName = className.substring(lastDot + 1);
            try {
                generateBuilder(simpleClassName, packageName, fieldMap);
            } catch (IOException e) {
            }
        }
        return true;
    }

    private void generateBuilder(String simpleClassName, String packageName, Map<String, String> fieldMap) throws IOException {
        String builderClassName = simpleClassName + "Builder";
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);

        StringBuilder sb = new StringBuilder();
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            sb.append(
                "package " + packageName + ";\n" +
                "public class " + builderClassName + " {\n" +
                "    private " + simpleClassName + " object = new " + simpleClassName + "();\n" +
                "\n" +
                "    public " + simpleClassName + " build() {\n" +
                "        return object;\n" +
                "    }\n");
            fieldMap.forEach((methodName, argumentType) -> {
                String setMethodName = "set" + capitalFirstChar(methodName);
                sb.append(
                    "    public " + builderClassName + " " + methodName + "(" + argumentType + " value) {\n" +
                    "        object." + setMethodName + "(value);\n" +
                    "        return this;\n"  +
                    "    }\n");
            });
            sb.append("}\n");
            out.write(sb.toString());
        }
    }

    private static String capitalFirstChar(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}