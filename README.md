Java Proto Mapper
=================


Ligthweight generation of protobuf <-> bean mapper classes. Based on annotation processing. It depends directly on the generated Java code from:

* google [protoc compiler] (https://code.google.com/p/protobuf/downloads/list) (tested on versions 2.4.1 and 2.5.0)
* square [wire compiler] (https://github.com/square/wire) (tested on version 1.3.3)

Usage Example
------------
To get generated mapper of java bean <-> protobuf message/enum you have to annotate your java bean class with `@Mapper(protoClass = CorrespondingProtobuf.class)` annonation. Also you have to annonate required for mapping fields with `@Field` annotation. Optionally you could provide `name` value if a field name declared in a java bean differs from such in the protocol definition. Also add `optional` value if a field is optional in the protocol.

For example, suppose we generated `Services.java` with protoc (or `Result.java`, `Entry.java` and `Map.java` with wire) in package `com.model.protobuf.dto` from the following protobuf protocol:

        enum Result {
            OK = 1;
            WRONG_CREDENTIALS = 2;
            BLOCKED_ACCOUNT = 3;
        }

        message Entry {
            required string key = 1;
            optional string value = 2;
        }

        message Map {
            repeated Entry entries = 1;
        }

Also we defined three Java bean classes. Now add `@Mapper` and `@Field` annotations to them: 

        //@Mapper(protoClass = com.model.protobuf.dto.Result.class) - for wire
        @Mapper(protoClass = com.model.protobuf.dto.Services.Result.class)
        public enum Result {
            OK(1),
            WRONG_CREDENTIALS(2),
            BLOCKED_ACCOUNT(3);
        
            @Field
            public final int code;
        
            private Result(int code) {
                this.code = code;
            }
        }
        
Note: enum must have one public or package field with `@Field` annotation. Values of that field must corresponds to values from protocol.

For `Map` and `Entry`:

       //@Mapper(protoClass = com.model.protobuf.dto.Entry.class) - for wire
       @Mapper(protoClass = com.model.protobuf.dto.Services.Entry.class)
       public class Entry {
       
           private @Field String key;
           private @Field String value;
       
           public String getKey() {
               return key;
           }
       
           public void setKey(String key) {
               this.key = key;
           }
       
           public String getValue() {
               return value;
           }
       
           public void setValue(String value) {
               this.value = value;
           }
       }
       
       
       //@Mapper(protoClass = com.model.protobuf.dto.Map.class) - for wire
       @Mapper(protoClass = com.model.protobuf.dto.Services.Map.class)
       public class Map {
           private @Field List<Entry> entries;
       
           public List<Entry> getEntries() {
               return entries;
           }
       
           public void setEntries(List<Entry> entries) {
               this.entries = entries;
           }
       }

Output for protoc
-----------------
After processing this classes Proto-Mapper will generate for `ResultMapper.java`:

       public class ResultMapper
           implements com.shaubert.protomapper.protoc.ProtoMappers.Mapper<Result, Services.Result> {
       
           @Override
           public Result mapFromProto(InputStream stream) throws IOException {
               throw new UnsupportedOperationException("Can not parse enum class from InputStream");
           }

           @Override
           public Result mapFromProto(Services.Result protoClass) {
               for (Result result : Result.values()) {
                   if (result.code == protoClass.getNumber()) {
                       return result;
                   }
               }
               throw new IllegalArgumentException("Unable to parse Result from: "
                       + protoClass);
           }

           @Override
           public Services.Result mapToProto(Result modelClass) {
               for (Services.Result result : Services.Result.values()) {
                   if (result.getNumber() == modelClass.code) {
                       return result;
                   }
               }
               throw new IllegalArgumentException("Unable to convert " + modelClass
                       + " to " + Services.Result.class);
           }
       
           @Override
           public Class<Result> getDataClass() {
               return Result.class;
           }
       
           @Override
           public Class<Services.Result> getProtoClass() {
               return Services.Result.class;
           }
       
       }

`EntryMapper.java`:

       public class EntryMapper
           implements com.shaubert.protomapper.protoc.ProtoMappers.Mapper<Entry, Services.Entry> {
       
           @Override
           public Entry mapFromProto(InputStream stream) throws IOException {
               Services.Entry protoClass =
                       Services.Entry.parseFrom(stream);
               return mapFromProto(protoClass);
           }
       
           @Override
           public Entry mapFromProto(Services.Entry protoClass) {
               Entry result = new Entry();
               result.setKey(protoClass.getKey());
               result.setValue(protoClass.getValue());
               return result;
           }
       
           @Override
           public Services.Entry mapToProto(Entry modelClass) {
               Services.Entry.Builder result =
                       Services.Entry.newBuilder();
               result.setKey(modelClass.getKey());
               result.setValue(modelClass.getValue());
               return result.build();
           }
       
           @Override
           public Class<Entry> getDataClass() {
               return Entry.class;
           }
       
           @Override
           public Class<Services.Entry> getProtoClass() {
               return Services.Entry.class;
           }
       
       }

And `MapMapper.java`:

       public class MapMapper
           implements com.shaubert.protomapper.protoc.ProtoMappers.Mapper<Map, Services.Map> {
       
           @Override
           public Map mapFromProto(InputStream stream) throws IOException {
               Services.Map protoClass =
                       Services.Map.parseFrom(stream);
               return mapFromProto(protoClass);
           }

           @Override
           public Map mapFromProto(Services.Map protoClass) {
               Map result = new Map();
               List<Entry> entries = new ArrayList<Entry>();
               {
                   EntryMapper mapper = new EntryMapper();
                   for (Services.Entry el : protoClass.getEntriesList()) {
                       entries.add(mapper.mapFromProto(el));
                   }
               }
               result.setEntries(entries);
               return result;
           }
       
           @Override
           public Services.Map mapToProto(Map modelClass) {
               Services.Map.Builder result =
                       Services.Map.newBuilder();
               {
                   EntryMapper mapper = new EntryMapper();
                   for (Entry el : modelClass.getEntries()) {
                       result.addEntries(mapper.mapToProto(el));
                   }
               }
               return result.build();
           }
       
           @Override
           public Class<Map> getDataClass() {
               return Map.class;
           }
       
           @Override
           public Class<Services.Map> getProtoClass() {
               return Services.Map.class;
           }
       
       }

Also you will get `com.shaubert.protomapper.protoc.ProtoMappers`:

       public class ProtoMappers {

           public interface Mapper<DataClass, ProtoClass> {
               DataClass mapFromProto(InputStream inputStream) throws IOException;

               DataClass mapFromProto(ProtoClass protoClass);

               ProtoClass mapToProto(DataClass dataClass);

               Class<DataClass> getDataClass();

               Class<ProtoClass> getProtoClass();
           }

           protected List<Mapper<?, ?>> mappers = new ArrayList<Mapper<?, ?>>();

           private static ProtoMappers instance;

           public static ProtoMappers getInstance() {
               if (instance == null) {
                   instance = new ProtoMappers();
               }
               return instance;
           }

           public ProtoMappers() {
               mappers.add(new com.shaubert.protomapper.ResultMapper());
               mappers.add(new com.shaubert.protomapper.EntryMapper());
               mappers.add(new com.shaubert.protomapper.MapMapper());
           }

           @SuppressWarnings("unchecked")
           public <DataClass, ProtoClass> Mapper<DataClass, ProtoClass> getMapper(
                   Class<DataClass> dataClass, Class<ProtoClass> protoClass) {
               for (Mapper<?, ?> mapper : mappers) {
                   if (mapper.getDataClass().equals(dataClass)
                           && mapper.getProtoClass().equals(protoClass)) {
                       return (Mapper<DataClass, ProtoClass>) mapper;
                   }
               }
               return null;
           }

           @SuppressWarnings("unchecked")
           public <DataClass, ProtoClass> Mapper<DataClass, ProtoClass> getFirstFromData(Class<DataClass> dataClass) {
               for (Mapper<?, ?> mapper : mappers) {
                   if (mapper.getDataClass().equals(dataClass)) {
                       return (Mapper<DataClass, ProtoClass>) mapper;
                   }
               }
               return null;
           }

           @SuppressWarnings("unchecked")
           public <DataClass, ProtoClass> Mapper<DataClass, ProtoClass> getFirstFromProto(Class<ProtoClass> protoClass) {
               for (Mapper<?, ?> mapper : mappers) {
                   if (mapper.getProtoClass().equals(protoClass)) {
                       return (Mapper<DataClass, ProtoClass>) mapper;
                   }
               }
               return null;
           }

           @SuppressWarnings("unchecked")
           public static AbstractMessageLite mapToMessage(Object data) {
               ProtoMappers mappers = getInstance();
               Mapper mapper = mappers.getFirstFromData(data.getClass());
               throwIfMapperNotFound(mapper, data.getClass());
               return (AbstractMessageLite) mapper.mapToProto(data);
           }

           @SuppressWarnings("unchecked")
           public static <T> T mapFromProto(Object proto) {
               ProtoMappers mappers = getInstance();
               Mapper mapper = mappers.getFirstFromProto(proto.getClass());
               throwIfMapperNotFound(mapper, proto.getClass());
               return (T) mapper.mapFromProto(proto);
           }

           @SuppressWarnings("unchecked")
           public static <T> T mapFromProtoByProtoClass(InputStream inputStream, Class<?> protoClass) throws IOException {
               ProtoMappers mappers = getInstance();
               Mapper mapper = mappers.getFirstFromProto(protoClass);
               throwIfMapperNotFound(mapper, protoClass);
               return (T) mapper.mapFromProto(inputStream);
           }

           @SuppressWarnings("unchecked")
           public static <T> T mapFromProtoByDataClass(InputStream inputStream, Class<?> dataClass) throws IOException {
               ProtoMappers mappers = getInstance();
               Mapper mapper = mappers.getFirstFromData(dataClass);
               throwIfMapperNotFound(mapper, dataClass);
               return (T) mapper.mapFromProto(inputStream);
           }

           private static void throwIfMapperNotFound(Object mapper, Class<?> forClass) {
               if (mapper == null) {
                   throw new NullPointerException("mapper for " + forClass + " not found");
               }
           }
       }

Output for wire
-----------------
`ResultMapper.java`:

       public class ResultMapper
           implements ProtoMappers.Mapper<Result, com.model.protobuf.dto.Result> {

           @Override
           public Result mapFromProto(InputStream stream) throws IOException {
               throw new UnsupportedOperationException("Can not parse enum class from InputStream, "
                       + "call mapFromProto(protoClass) instead");
           }

           @Override
           public Result mapFromProto(com.model.protobuf.dto.Result protoClass) {
               for (LoginResultTest result :
                       LoginResultTest.values()) {
                   if (result.code == protoClass.getValue()) {
                       return result;
                   }
               }
               throw new IllegalArgumentException("Unable to parse LoginResultTest from: "
                       + protoClass);
           }

           @Override
           public com.model.protobuf.dto.Result mapToProto(LoginResultTest modelClass) {
               for (com.model.protobuf.dto.Result result :
                       com.model.protobuf.dto.Result.values()) {
                   if (result.getValue() == modelClass.code) {
                       return result;
                   }
               }
               throw new IllegalArgumentException("Unable to convert " + modelClass
                       + " to " + com.model.protobuf.dto.Result.class);
           }

           @Override
           public Class<Result> getDataClass() {
               return Result.class;
           }

           @Override
           public Class<com.model.protobuf.dto.Result> getProtoClass() {
               return com.model.protobuf.dto.Result.class;
           }

       }

`EntryMapper.java`:

       public class EntryMapper
           implements ProtoMappers.Mapper<Entry, com.model.protobuf.dto.Entry> {
       
           @Override
           public Entry mapFromProto(InputStream stream) throws IOException {
               Wire wire = ProtoMappers.get().getWire();
               com.model.protobuf.dto.Entry protoClass =
                       wire.parseFrom(stream, com.model.protobuf.dto.Entry.class);
               return mapFromProto(protoClass);
           }
       
           @Override
           public Entry mapFromProto(com.model.protobuf.dto.Entry protoClass) {
               Entry result = new Entry();
               result.setKey(protoClass.key);
               result.setValue(protoClass.value);
               return result;
           }
       
           @Override
           public com.model.protobuf.dto.Entry mapToProto(Entry modelClass) {
               com.model.protobuf.dto.Entry.Builder result =
                       new com.model.protobuf.dto.Entry.Builder();
               result.key(modelClass.getKey());
               result.value(modelClass.getValue());
               return result.build();
           }
       
           @Override
           public Class<Entry> getDataClass() {
               return Entry.class;
           }
       
           @Override
           public Class<com.model.protobuf.dto.Entry> getProtoClass() {
               return com.model.protobuf.dto.Entry.class;
           }
       }

And `MapMapper.java`:

       public class MapMapper
           implements ProtoMappers.Mapper<Map, com.model.protobuf.dto.Map> {
       
           @Override
           public Map mapFromProto(InputStream stream) throws IOException {
               Wire wire = ProtoMappers.get().getWire();
               com.model.protobuf.dto.Map protoClass =
                       wire.parseFrom(stream, com.model.protobuf.dto.Map.class);
               return mapFromProto(protoClass);
           }
       
           @Override
           public Map mapFromProto(com.model.protobuf.dto.Map protoClass) {
               Map result = new Map();
               java.util.List<com.shaubert.protomapper.wire.sample.EntryTest> entries = new java.util.ArrayList<com.shaubert.protomapper.wire.sample.EntryTest>();
               {
                   com.shaubert.protomapper.wire.sample.EntryTestMapper mapper = new com.shaubert.protomapper.wire.sample.EntryTestMapper();
                   for (com.model.protobuf.dto.Entry el : protoClass.entries) {
                       entries.add(mapper.mapFromProto(el));
                   }
               }
               result.setEntries(entries);
               return result;
           }
       
           @Override
           public com.model.protobuf.dto.Map mapToProto(Map modelClass) {
               com.model.protobuf.dto.Map.Builder result =
                       new com.model.protobuf.dto.Map.Builder();
               {
                   java.util.List<com.model.protobuf.dto.Entry> entries =
                           new java.util.ArrayList<com.model.protobuf.dto.Entry>(modelClass.getEntries().size());
                   com.shaubert.protomapper.wire.sample.EntryTestMapper mapper = new com.shaubert.protomapper.wire.sample.EntryTestMapper();
                   for (com.shaubert.protomapper.wire.sample.EntryTest el : modelClass.getEntries()) {
                       entries.add(mapper.mapToProto(el));
                   }
                   result.entries(entries);
               }
               return result.build();
           }
       
           @Override
           public Class<Map> getDataClass() {
               return Map.class;
           }
       
           @Override
           public Class<com.model.protobuf.dto.Map> getProtoClass() {
               return com.model.protobuf.dto.Map.class;
           }
       }

Also you will get `com.shaubert.protomapper.wire.ProtoMappers`:

       public class ProtoMappers {

           public interface Mapper<DataClass, ProtoClass> {
               DataClass mapFromProto(InputStream inputStream) throws IOException;

               DataClass mapFromProto(ProtoClass protoClass);

               ProtoClass mapToProto(DataClass dataClass);

               Class<DataClass> getDataClass();

               Class<ProtoClass> getProtoClass();
           }

           protected List<Mapper<?, ?>> mappers = new ArrayList<Mapper<?, ?>>();
           private Wire wire;

           private static ProtoMappers instance;

           public static synchronized ProtoMappers get() {
               if (instance == null) {
                   instance = new ProtoMappers();
               }
               return instance;
           }

           public ProtoMappers() {
               wire = new Wire();
               mappers.add(new com.shaubert.protomapper.wire.sample.ResultMapper());
               mappers.add(new com.shaubert.protomapper.wire.sample.EntryMapper());
               mappers.add(new com.shaubert.protomapper.wire.sample.MapMapper());
           }

           public Wire getWire() {
               return wire;
           }

           @SuppressWarnings("unchecked")
           public <DataClass, ProtoClass> Mapper<DataClass, ProtoClass> getMapper(
                   Class<DataClass> dataClass, Class<ProtoClass> protoClass) {
               for (Mapper<?, ?> mapper : mappers) {
                   if (mapper.getDataClass().equals(dataClass)
                           && mapper.getProtoClass().equals(protoClass)) {
                       return (Mapper<DataClass, ProtoClass>) mapper;
                   }
               }
               return null;
           }

           @SuppressWarnings("unchecked")
           public <DataClass, ProtoClass> Mapper<DataClass, ProtoClass> getFirstFromData(Class<DataClass> dataClass) {
               for (Mapper<?, ?> mapper : mappers) {
                   if (mapper.getDataClass().equals(dataClass)) {
                       return (Mapper<DataClass, ProtoClass>) mapper;
                   }
               }
               return null;
           }

           @SuppressWarnings("unchecked")
           public <DataClass, ProtoClass> Mapper<DataClass, ProtoClass> getFirstFromProto(Class<ProtoClass> protoClass) {
               for (Mapper<?, ?> mapper : mappers) {
                   if (mapper.getProtoClass().equals(protoClass)) {
                       return (Mapper<DataClass, ProtoClass>) mapper;
                   }
               }
               return null;
           }

           @SuppressWarnings("unchecked")
           public static Message mapToMessage(Object data) {
               ProtoMappers mappers = get();
               Mapper mapper = mappers.getFirstFromData(data.getClass());
               throwIfMapperNotFound(mapper, data.getClass());
               return (Message) mapper.mapToProto(data);
           }

           @SuppressWarnings("unchecked")
           public static <T> T mapFromProto(Object proto) {
               ProtoMappers mappers = get();
               Mapper mapper = mappers.getFirstFromProto(proto.getClass());
               throwIfMapperNotFound(mapper, proto.getClass());
               return (T) mapper.mapFromProto(proto);
           }

           @SuppressWarnings("unchecked")
           public static <T> T mapFromProtoByProtoClass(InputStream inputStream, Class<?> protoClass) throws IOException {
               ProtoMappers mappers = get();
               Mapper mapper = mappers.getFirstFromProto(protoClass);
               throwIfMapperNotFound(mapper, protoClass);
               return (T) mapper.mapFromProto(inputStream);
           }

           @SuppressWarnings("unchecked")
           public static <T> T mapFromProtoByDataClass(InputStream inputStream, Class<?> dataClass) throws IOException {
               ProtoMappers mappers = get();
               Mapper mapper = mappers.getFirstFromData(dataClass);
               throwIfMapperNotFound(mapper, dataClass);
               return (T) mapper.mapFromProto(inputStream);
           }

           private static void throwIfMapperNotFound(Object mapper, Class<?> forClass) {
               if (mapper == null) {
                   throw new NullPointerException("mapper for " + forClass + " not found");
               }
           }
       }

Licence
=======
  
             Copyright 2013 iDa Mobile.
        
           Licensed under the Apache License, Version 2.0 (the "License");
           you may not use this file except in compliance with the License.
           You may obtain a copy of the License at
        
               http://www.apache.org/licenses/LICENSE-2.0
        
           Unless required by applicable law or agreed to in writing, software
           distributed under the License is distributed on an "AS IS" BASIS,
           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           See the License for the specific language governing permissions and
           limitations under the License.
