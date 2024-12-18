package com.example.fine_dust_alert;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ListAvgOfSeoulAirQualityService", strict = false)
public class AirQualityResponse {

    @Element(name = "list_total_count", required = false)
    private int listTotalCount;

    @Element(name = "RESULT", required = false)
    private Result result;

    @ElementList(name = "row", inline = true, required = false)
    private List<Row> rows;

    public int getListTotalCount() {
        return listTotalCount;
    }

    public void setListTotalCount(int listTotalCount) {
        this.listTotalCount = listTotalCount;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}

