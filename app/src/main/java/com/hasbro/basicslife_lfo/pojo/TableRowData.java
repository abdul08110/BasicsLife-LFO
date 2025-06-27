package com.hasbro.basicslife_lfo.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class TableRowData {
    private String strid;
    private String tmid;
    private String visitdate;
    private String rank;
    private String brand;
    private String qty;
    private String nsv;
    private String soh;
    private String remarks;

    public TableRowData(String strid,String tmid,String visitdate,String rank, String brand, String qty, String nsv, String soh, String remarks) {
        this.strid = strid;
        this.tmid = tmid;
        this.visitdate = visitdate;
        this.rank = rank;
        this.brand = brand;
        this.qty = qty;
        this.nsv = nsv;
        this.soh = soh;
        this.remarks = remarks;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("strid", strid);
        jsonObject.put("tmid", tmid);
        jsonObject.put("visitdate", visitdate);
        jsonObject.put("rank", rank);
        jsonObject.put("brand", brand);
        jsonObject.put("qty", qty);
        jsonObject.put("nsv", nsv);
        jsonObject.put("soh", soh);
        jsonObject.put("remarks", remarks);
        return jsonObject;
    }
}