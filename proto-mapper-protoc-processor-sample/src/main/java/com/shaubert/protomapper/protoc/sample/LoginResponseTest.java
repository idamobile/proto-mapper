package com.shaubert.protomapper.protoc.sample;

import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import com.shaubert.protomapper.sample.protobuf.Services;

@Mapper(protoClass = Services.LoginResponseProtobufDTO.class)
public class LoginResponseTest {

    private @Field LoginResultTest loginResult;
    private @Field CourierInfoTest courierInfo;

    public LoginResultTest getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResultTest loginResult) {
        this.loginResult = loginResult;
    }

    public CourierInfoTest getCourierInfo() {
        return courierInfo;
    }

    public void setCourierInfo(CourierInfoTest courierInfo) {
        this.courierInfo = courierInfo;
    }
}
