package com.shaubert.protomapper.wire.sample;

import com.shaubert.protomapper.annotations.Field;

/**
 * @author Sergey Ryabov
 * Date: 22.04.13
 */
public class AbsLoginRequestTest {
    private @Field String login;

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
}
