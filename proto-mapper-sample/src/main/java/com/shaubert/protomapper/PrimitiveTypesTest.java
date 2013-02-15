package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.Services;

@Mapper(protoClass = Services.PrimitiveTypes.class)
public class PrimitiveTypesTest {
    private @Field String strVal;
    private @Field int intVal;
    private @Field long longVal;
    private @Field float floatVal;
    private @Field double doubleVal;
    private @Field byte[] byteVal;

    public String getStrVal() {
        return strVal;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public long getLongVal() {
        return longVal;
    }

    public void setLongVal(long longVal) {
        this.longVal = longVal;
    }

    public float getFloatVal() {
        return floatVal;
    }

    public void setFloatVal(float floatVal) {
        this.floatVal = floatVal;
    }

    public double getDoubleVal() {
        return doubleVal;
    }

    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public byte[] getByteVal() {
        return byteVal;
    }

    public void setByteVal(byte[] byteVal) {
        this.byteVal = byteVal;
    }
}
