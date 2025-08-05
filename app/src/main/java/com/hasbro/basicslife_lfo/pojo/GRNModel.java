package com.hasbro.basicslife_lfo.pojo;

import java.io.Serializable;

public class GRNModel implements Serializable {

    private String poNo;
    private String invoiceNo;
    private String invoiceQty;
    private String lrNo;
    private String grnQty;
    private String grnDate;
    private String grnNo;

    public GRNModel(String poNo, String invoiceNo, String invoiceQty,
                    String lrNo, String grnQty, String grnDate, String grnNo) {
        this.poNo = poNo;
        this.invoiceNo = invoiceNo;
        this.invoiceQty = invoiceQty;
        this.lrNo = lrNo;
        this.grnQty = grnQty;
        this.grnDate = grnDate;
        this.grnNo = grnNo;
    }

    // Getters and setters
    public String getPoNo() { return poNo; }
    public void setPoNo(String poNo) { this.poNo = poNo; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public String getInvoiceQty() { return invoiceQty; }
    public void setInvoiceQty(String invoiceQty) { this.invoiceQty = invoiceQty; }

    public String getLrNo() { return lrNo; }
    public void setLrNo(String lrNo) { this.lrNo = lrNo; }

    public String getGrnQty() { return grnQty; }
    public void setGrnQty(String grnQty) { this.grnQty = grnQty; }

    public String getGrnDate() { return grnDate; }
    public void setGrnDate(String grnDate) { this.grnDate = grnDate; }

    public String getGrnNo() { return grnNo; }
    public void setGrnNo(String grnNo) { this.grnNo = grnNo; }
}
