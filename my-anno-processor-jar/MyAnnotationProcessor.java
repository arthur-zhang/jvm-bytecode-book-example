package me.ya.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

/**
 * Created By Arthur Zhang at 2019/9/13
 */
//@SupportedAnnotationTypes({"me.ya.annotation.MyAnnotation"})
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyAnnotationProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        for (Element e : roundEnv.getElementsAnnotatedWith(MyAnnotation.class)) {
            if (e.getKind() != ElementKind.FIELD) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.WARNING,
                        "Not a field", e);
                continue;
            }
            String name = capitalize(e.getSimpleName().toString());
            TypeElement clazz = (TypeElement) e.getEnclosingElement();
            try {
                JavaFileObject f = processingEnv.getFiler().createSourceFile(clazz.getQualifiedName() + "Extras");
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Creating " + f.toUri());
                Writer w = f.openWriter();
                try {
                    PrintWriter pw = new PrintWriter(w);
                    pw.println("package " + clazz.getEnclosingElement().getSimpleName() + ";");
                    pw.println("public abstract class " + clazz.getSimpleName() + "Extras {");
                    pw.println("    protected " + clazz.getSimpleName() + "Extras() {}");
                    TypeMirror type = e.asType();
                    pw.println("    /** Handle something. */");
                    pw.println("    protected final void handle" + name
                                       + "(" + type + " value) {");
                    pw.println("        System.out.println(value);");
                    pw.println("    }");
                    pw.println("}");
                    pw.flush();
                } finally {
                    w.close();
                }
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        x.toString());
            }
        }
        return true;
    }

    private static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
