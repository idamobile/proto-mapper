package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Mapper;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

@SupportedAnnotationTypes("com.shaubert.protomapper.annotations.Mapper")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class MappingAnnotationsProcessor extends AbstractProcessor {

    public static final String MAPPER_VM_FILE = "mapper.vm";
    private VelocityEngine velocityEngine;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "searching for mappers");
        for (Element elem : roundEnv.getElementsAnnotatedWith(Mapper.class)) {
            ClassParams mapperParams = new ClassParams((TypeElement) elem, roundEnv);
            logMapper(mapperParams);
            Template template = createTemplate();
            if (template != null) {
                createMapperClass(template, mapperParams);
            }
        }
        return true;
    }

    private void createMapperClass(Template template, ClassParams mapperParams) {
        JavaFileObject jfo = null;
        Writer writer = null;
        try {
            jfo = processingEnv.getFiler().createSourceFile(
                    mapperParams.getClassName() + "Mapper");

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "creating source file: " + jfo.toUri());

            writer = jfo.openWriter();

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "applying velocity template: " + template.getName());

            template.merge(mapperParams.createContext(), writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    private void logMapper(ClassParams params) {
        String message = "mapper found in " + params.getClassName()
                + " with proto class " + params.getProtoClass()
                + " and " + params.getFields().size() + " annotated fields";
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    private Template createTemplate() {
        if (velocityEngine == null) {
            try {
                velocityEngine = setupVelocityEngine();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            return velocityEngine.getTemplate(MAPPER_VM_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private VelocityEngine setupVelocityEngine() throws Exception {
        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());
        VelocityEngine ve = new VelocityEngine(props);
        ve.init();
        return ve;
    }

}
