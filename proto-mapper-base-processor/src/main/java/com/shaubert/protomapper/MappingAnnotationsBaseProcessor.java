package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Mapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class MappingAnnotationsBaseProcessor extends AbstractProcessor {

    private VelocityEngine velocityEngine;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "searching for mappers");
            List<ClassParams> paramsList = new ArrayList<ClassParams>();
            for (Element elem : roundEnv.getElementsAnnotatedWith(Mapper.class)) {
                ClassParams mapperParams = new ClassParams((TypeElement) elem, roundEnv, processingEnv);
                logMapper(mapperParams);
                paramsList.add(mapperParams);
                Template template = createMapperTemplate();
                if (template != null) {
                    createMapperClass(template, mapperParams);
                }
            }
            Template template = createProtoMappersTemplate();
            if (template != null) {
                createProtoMappersClass(template, paramsList);
            }
        }
        return false;
    }

    protected VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    private void createProtoMappersClass(Template template, List<ClassParams> mappers) {
        JavaFileObject jfo = null;
        Writer writer = null;
        try {
            jfo = processingEnv.getFiler().createSourceFile(getProtoMappersClassName());

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "creating source file: " + jfo.toUri());

            writer = jfo.openWriter();

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "applying velocity template: " + template.getName());

            List<String> classNames = new ArrayList<String>(mappers.size());
            for (ClassParams mapper : mappers) {
                classNames.add(getMapperClassName(mapper));
            }
            VelocityContext context = new VelocityContext();
            context.put("mappers", classNames);

            template.merge(context, writer);

            writer.close();
        } catch (FilerException fillerEx) {//Attempt to recreate a file for type
            //all mappers should be created in the first round of annotation processing
            //so mapper is already created
            //and we do nothing
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

    protected abstract String getProtoMappersClassName();

    private void createMapperClass(Template template, ClassParams mapperParams) {
        JavaFileObject jfo;
        Writer writer = null;
        try {
            jfo = processingEnv.getFiler().createSourceFile(
                    getMapperClassName(mapperParams));

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

    protected String getMapperClassName(ClassParams mapperParams) {
        return mapperParams.getClassName() + "Mapper";
    }

    private void logMapper(ClassParams params) {
        String message = "mapper found in " + params.getClassName()
                + " with proto class " + params.getProtoClass()
                + " and " + params.getFields().size() + " annotated fields";
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    protected abstract Template createProtoMappersTemplate();

    protected abstract Template createMapperTemplate();

    protected Template createTemplate(String fileName) {
        if (velocityEngine == null) {
            try {
                velocityEngine = setupVelocityEngine();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            return velocityEngine.getTemplate(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected VelocityEngine setupVelocityEngine() throws Exception {
        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());
        VelocityEngine ve = new VelocityEngine(props);
        ve.init();
        return ve;
    }

}
