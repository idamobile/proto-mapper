package com.shaubert.protomapper.wire.sample;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.PrimitiveTypesLists;

import java.util.List;

@Mapper(protoClass = PrimitiveTypesLists.class)
public class PrimitiveTypeListsTest {
    private @Field List<String> strEntries;
    private @Field List<Integer> intEntries;
    private @Field(name = "longEntries") List<Long> longVals;
    private @Field List<Float> floatEntries;
    private @Field List<Double> doubleEntries;
    private @Field List<byte[]> byteEntries;
    private @Field List<Boolean> boolEntries;

    public List<String> getStrEntries() {
        return strEntries;
    }

    public void setStrEntries(List<String> strEntries) {
        this.strEntries = strEntries;
    }

    public List<Integer> getIntEntries() {
        return intEntries;
    }

    public void setIntEntries(List<Integer> intEntries) {
        this.intEntries = intEntries;
    }

    public List<Long> getLongVals() {
        return longVals;
    }

    public void setLongVals(List<Long> longVals) {
        this.longVals = longVals;
    }

    public List<Float> getFloatEntries() {
        return floatEntries;
    }

    public void setFloatEntries(List<Float> floatEntries) {
        this.floatEntries = floatEntries;
    }

    public List<Double> getDoubleEntries() {
        return doubleEntries;
    }

    public void setDoubleEntries(List<Double> doubleEntries) {
        this.doubleEntries = doubleEntries;
    }

    public List<byte[]> getByteEntries() {
        return byteEntries;
    }

    public void setByteEntries(List<byte[]> byteEntries) {
        this.byteEntries = byteEntries;
    }

    public List<Boolean> getBoolEntries() {
        return boolEntries;
    }

    public void setBoolEntries(List<Boolean> boolEntries) {
        this.boolEntries = boolEntries;
    }
}
