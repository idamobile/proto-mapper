package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.Services;

@Mapper(protoClass = Services.LoginResponseProtobufDTO.Result.class, isEnum = true)
public enum LoginResultTest {
    OK(1),
    WRONG_CREDENTIALS(2),
    BLOCKED_ACCOUNT(3);

    @Field
    public final int code;

    private LoginResultTest(int code) {
        this.code = code;
    }
}
