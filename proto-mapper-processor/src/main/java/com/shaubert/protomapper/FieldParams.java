package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Field;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;

public class FieldParams {
    private TypeParams type;
    private String name;
    private String protoGetter;

    public FieldParams(VariableElement element, RoundEnvironment roundEnvironment) {
        type = new TypeParams(element.asType(), roundEnvironment);
        name = element.getSimpleName().toString();
        Field field = element.getAnnotation(Field.class);
        if (field.name() == null || field.name().length() == 0) {
            protoGetter = makeGetter(name, type.getName());
        } else {
            protoGetter = makeGetter(field.name(), type.getName());
        }
    }

    private String makeGetter(String fieldName, String type) {
        if (type.equals("boolean")) {
            return formatGetSetName("is", fieldName);
        } else {
            String res = formatGetSetName("get", fieldName);
            if (type.equals("java.util.List")) {
                res += "List";
            }
            return res;
        }
    }

    private String formatGetSetName(String getOrSet, String fieldName) {
        return getOrSet + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
    }

    public TypeParams getType() {
        return type;
    }

    public String getSetterName() {
        return formatGetSetName("set", name);
    }

    public String getName(){
        return name;
    }

    public String getProtoClassGetter(){
        return protoGetter;
    }
}
