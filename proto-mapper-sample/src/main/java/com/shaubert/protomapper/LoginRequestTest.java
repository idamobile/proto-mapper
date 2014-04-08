package com.shaubert.protomapper;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.Services;

/**
 * @author Sergey Ryabov
 * Date: 22.04.13
 */
@Mapper(protoClass = Services.LoginRequestProtobufDTO.class)
public class LoginRequestTest extends AbsLoginRequestTest {
    private @Field(name = "passwordHash") String password;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
