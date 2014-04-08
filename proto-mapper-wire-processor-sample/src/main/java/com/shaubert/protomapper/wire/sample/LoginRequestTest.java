package com.shaubert.protomapper.wire.sample;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.LoginRequestProtobufDTO;

/**
 * @author Sergey Ryabov
 * Date: 22.04.13
 */
@Mapper(protoClass = LoginRequestProtobufDTO.class)
public class LoginRequestTest extends AbsLoginRequestTest {
    private @Field(name = "passwordHash") String password;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
