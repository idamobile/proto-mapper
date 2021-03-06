package com.shaubert.protomapper.protoc.sample;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.Services;

import java.util.List;

@Mapper(protoClass = Services.Map.class)
public class MapTest {
    private @Field List<EntryTest> entries;

    public List<EntryTest> getEntries() {
        return entries;
    }

    public void setEntries(List<EntryTest> entries) {
        this.entries = entries;
    }
}
