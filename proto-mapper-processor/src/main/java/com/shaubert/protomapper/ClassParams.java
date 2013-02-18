package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import org.apache.velocity.VelocityContext;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class ClassParams {

    public static final String PACKAGE_NAME_PROP = "packageName";
    public static final String SIMPLE_CLASS_NAME_PROP = "simpleClass";
    public static final String PROTO_CLASS_PROP = "protoClass";
    public static final String FIELDS_PROP = "fields";
    public static final String IS_ENUM_PROP = "isEnum";
    public static final String ENUM_NUMBER_FIELD_PROP = "enumNuberField";

    private TypeMirror protoClass;
    private String simpleClassName;
    private String className;
    private String packageName;
    private boolean isEnum;

    private List<FieldParams> fields = new ArrayList<FieldParams>();

    public ClassParams(TypeElement classElement, RoundEnvironment environment) {
        protoClass = getProtoClass(classElement);
        setupClassParameters(classElement);
        setupFields(classElement, environment);
    }

    protected static TypeMirror getProtoClass(TypeElement classElement) {
        Mapper mapper = classElement.getAnnotation(Mapper.class);
        try {
            mapper.protoClass();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        return null;
    }

    private void setupFields(TypeElement classElement, RoundEnvironment roundEnvironment) {
        for (Element e : classElement.getEnclosedElements()) {
            if (e.getAnnotation(Field.class) != null) {
                fields.add(new FieldParams((VariableElement) e, roundEnvironment));
            }
        }
    }

    private void setupClassParameters(TypeElement classElement) {
        Mapper mapper = classElement.getAnnotation(Mapper.class);
        Element enclosingElement = classElement.getEnclosingElement();
        while (!(enclosingElement instanceof PackageElement)) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        PackageElement packageElement = (PackageElement) enclosingElement;
        packageName = packageElement.getQualifiedName().toString();
        className = classElement.getQualifiedName().toString();
        simpleClassName = classElement.getSimpleName().toString();
        isEnum = mapper.isEnum();
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public TypeMirror getProtoClass() {
        return protoClass;
    }

    public List<FieldParams> getFields() {
        return fields;
    }

    public VelocityContext createContext() {
        VelocityContext vc = new VelocityContext();

        vc.put(PACKAGE_NAME_PROP, packageName);
        vc.put(SIMPLE_CLASS_NAME_PROP, simpleClassName);
        vc.put(PROTO_CLASS_PROP, protoClass);

        vc.put(IS_ENUM_PROP, isEnum);
        if (!isEnum) {
            vc.put(FIELDS_PROP, fields);
        } else {
            FieldParams params = fields.get(0);
            vc.put(ENUM_NUMBER_FIELD_PROP, params.getName());
        }

        return vc;
    }
}
