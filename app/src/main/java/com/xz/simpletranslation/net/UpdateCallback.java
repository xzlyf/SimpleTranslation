package com.xz.simpletranslation.net;

public interface UpdateCallback {

    void finish(String...values);
    void error(Exception e);

}
