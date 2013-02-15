package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import org.apache.velocity.VelocityContext;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class MapperParams {

    public static final String PACKAGE_NAME_PROP = "packageName";
    public static final String SIMPLE_CLASS_NAME_PROP = "simpleClass";
    public static final String PROTO_CLASS_PROP = "protoClass";
    public static final String FIELDS_PROP = "fields";
    public static final String IS_ENUM_PROP = "isEnum";
    public static final String ENUM_NUMBER_FIELD_PROP = "enumNuberField";

    public static class FieldParams {
        private String type;
        private String genericType;
        private String name;
        private String protoGetter;
        private TypeMirror genericProtoClass;

        public FieldParams(VariableElement element, RoundEnvironment roundEnvironment) {
            type = element.asType().toString();

            if(element.asType() instanceof DeclaredType){
                DeclaredType declaredType = (DeclaredType) element.asType();
                if (!declaredType.getTypeArguments().isEmpty()) {
                    TypeMirror genericMirrorType = declaredType.getTypeArguments().get(0);
                    genericType = genericMirrorType.toString();

                    if (isGenericClassNeedsMapping()) {
                        for (Element el : roundEnvironment.getElementsAnnotatedWith(Mapper.class)) {
                            if (el.asType().equals(genericMirrorType)) {
                                Mapper mapper = el.getAnnotation(Mapper.class);
                                genericProtoClass = setupProtoClass((TypeElement) el);
                            }
                        }
                    }
                }
            }

            name = element.getSimpleName().toString();
            Field field = element.getAnnotation(Field.class);
            if (field.name() == null || field.name().length() == 0) {
                protoGetter = makeGetter(name, type);
            } else {
                protoGetter = makeGetter(field.name(), type);
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

        public boolean isSimpleType() {
            return !type.contains(".");
        }
        
        public boolean isList() {
            return type.startsWith("java.util.List");
        }

        public boolean isByteArray() {
            return type.equals("byte[]");
        }

        public boolean isMappingRequired() {
            return !isSimpleType() && !type.equals("java.lang.String");
        }

        public boolean isGenericClassNeedsMapping() {
            return genericType != null
                    && genericType.contains(".")
                    && !genericType.equals("java.lang.String");
        }
        
        public String getType() {
            return type;
        }

        public String getSetterName() {
            return formatGetSetName("set", name);
        }

        public String getGenericClass(){
            return genericType;
        }

        public String getName(){
            return name;
        }

        public String getProtoClassGetter(){
            return protoGetter;
        }

        public String getProtoClassOfGeneric(){
            return genericProtoClass.toString();
        }
    }

    private TypeMirror protoClass;
    private String simpleClassName;
    private String className;
    private String packageName;
    private boolean isEnum;

    private List<FieldParams> fields = new ArrayList<FieldParams>();

    public MapperParams(TypeElement classElement, RoundEnvironment environment) {
        protoClass = setupProtoClass(classElement);
        setupClassParameters(classElement);
        setupFields(classElement, environment);
    }

    protected static TypeMirror setupProtoClass(TypeElement classElement) {
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
