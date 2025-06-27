package com.hasbro.basicslife_lfo.pojo;

public class SellThroughItem {
    private String itemCode, season, design, colour, fit_name, collar;
    private int mrp, soldQty, stockQty;
    private double sellThrough, grn_qty,  dis_qty, mrp_value, net_sale, discount_value, dispercen, rateofsale, noddays;

    public SellThroughItem(String itemCode, String season, int mrp, int soldQty, int stockQty,
                           double sellThrough, String design, String colour, String fit_name,
                           String collar, double grn_qty, double dis_qty, double mrp_value,
                           double net_sale, double discount_value, double dispercen, double rateofsale,
                           double noddays) {
        this.itemCode = itemCode;
        this.season = season;
        this.mrp = mrp;
        this.soldQty = soldQty;
        this.stockQty = stockQty;
        this.sellThrough = sellThrough;
        this.design = design;
        this.colour = colour;
        this.fit_name = fit_name;
        this.collar = collar;
        this.grn_qty = grn_qty;
        this.dis_qty = dis_qty;
        this.mrp_value = mrp_value;
        this.net_sale = net_sale;
        this.discount_value = discount_value;
        this.dispercen = dispercen;
        this.rateofsale = rateofsale;
        this.noddays = noddays;

    }

    public String getItemCode() { return itemCode; }
    public String getSeason() { return season; }
    public int getMrp() { return mrp; }
    public int getSoldQty() { return soldQty; }
    public int getStockQty() { return stockQty; }
    public double getSellThrough() { return sellThrough; }

    public String getDesign() { return design; }
    public String getColour() { return colour; }
    public String getFit_name() { return fit_name; }
    public String getCollar() { return collar; }

    public double getGrn_qty() { return grn_qty; }

    public double getDis_qty() { return dis_qty; }
    public double getMrp_value() { return mrp_value; }
    public double getNet_sale() { return net_sale; }
    public double getDiscount_value() { return discount_value; }

    public double getDispercen() { return dispercen; }
    public double getRateofsale() { return rateofsale; }
    public double getNoddays() { return noddays; }

    // Method to generate the image URL using itemCode
    public String getImageUrl() {
        return "https://bl-pim.s3.ap-southeast-1.amazonaws.com/web/1000x1500/f/" + itemCode + "F.jpg";
    }
}
