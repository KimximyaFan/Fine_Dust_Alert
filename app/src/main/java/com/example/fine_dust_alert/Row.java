package com.example.fine_dust_alert;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "row", strict = false)
public class Row {

    @Element(name = "GRADE", required = false)
    private String grade;

    @Element(name = "PM10", required = false)
    private int pm10;

    @Element(name = "PM25", required = false)
    private int pm25;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getPm10() {
        return pm10;
    }

    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }
}
