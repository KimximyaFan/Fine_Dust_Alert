package com.example.fine_dust_alert;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "RESULT", strict = false)
public class Result {

    @Element(name = "CODE", required = false)
    private String code;

    @Element(name = "MESSAGE", required = false)
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
