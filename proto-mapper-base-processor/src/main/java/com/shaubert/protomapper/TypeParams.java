package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Mapper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class TypeParams {
    private String type;
    private List<TypeParams> genericTypes = new ArrayList<TypeParams>();
    private String protoClass;

    public TypeParams(TypeMirror type, RoundEnvironment roundEnvironment) {
        this.type = type.toString();

        if(type instanceof DeclaredType){
            DeclaredType declaredType = (DeclaredType) type;
            if (!declaredType.getTypeArguments().isEmpty()) {
                for (TypeMirror genericMirrorType : declaredType.getTypeArguments()) {
                    genericTypes.add(new TypeParams(genericMirrorType, roundEnvironment));
                }
            }
        }

        if (isMappingRequired()) {
            for (Element el : roundEnvironment.getElementsAnnotatedWith(Mapper.class)) {
                if (el.asType().equals(type)) {
                    Mapper mapper = el.getAnnotation(Mapper.class);
                    protoClass = ClassParams.getProtoClass((TypeElement) el).toString();
                }
            }
        }
    }

    public String getName() {
        return type;
    }

    public List<TypeParams> getGenerics(){
        return genericTypes;
    }

    public String getProtoClass(){
        return protoClass;
    }

    public boolean isPrimitiveType() {
        return !type.contains(".");
    }

    public boolean isList() {
        return type.startsWith("java.util.List");
    }

    public boolean isByteArray() {
        return type.equals("byte[]");
    }

    public boolean isMappingRequired() {
        return !isPrimitiveType() && !isSimpleTypeWrapper() && !type.equals("java.lang.String");
    }

    private boolean isSimpleTypeWrapper() {
        if (type.equals("java.lang.Byte")) {
            return true;
        }
        if (type.equals("java.lang.Short")) {
            return true;
        }
        if (type.equals("java.lang.Integer")) {
            return true;
        }
        if (type.equals("java.lang.Long")) {
            return true;
        }
        if (type.equals("java.lang.Float")) {
            return true;
        }
        if (type.equals("java.lang.Double")) {
            return true;
        }
        if (type.equals("java.lang.Char")) {
            return true;
        }
        if (type.equals("java.lang.Boolean")) {
            return true;
        }
        return false;
    }

}
