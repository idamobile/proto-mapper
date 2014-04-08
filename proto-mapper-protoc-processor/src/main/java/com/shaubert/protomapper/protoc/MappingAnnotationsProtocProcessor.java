package com.shaubert.protomapper.protoc;

import com.shaubert.protomapper.MappingAnnotationsBaseProcessor;
import org.apache.velocity.Template;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes("com.shaubert.protomapper.annotations.Mapper")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class MappingAnnotationsProtocProcessor extends MappingAnnotationsBaseProcessor {

    public static final String MAPPER_VM_FILE = "mapper.vm";
    public static final String PROTO_MAPPERS_VM_FILE = "mapper-map.vm";

    @Override
    protected String getProtoMappersClassName() {
        return "com.shaubert.protomapper.protoc.ProtoMappers";
    }

    @Override
    protected Template createProtoMappersTemplate() {
        return createTemplate(PROTO_MAPPERS_VM_FILE);
    }

    @Override
    protected Template createMapperTemplate() {
        return createTemplate(MAPPER_VM_FILE);
    }

}