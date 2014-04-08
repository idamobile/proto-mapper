package com.shaubert.protomapper.wire.sample;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.LoginResponseProtobufDTO;

@Mapper(protoClass = LoginResponseProtobufDTO.Result.class)
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
