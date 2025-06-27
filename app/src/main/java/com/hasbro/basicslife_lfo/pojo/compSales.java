package com.hasbro.basicslife_lfo.pojo;

public class compSales {

        private final String rank;
        private final String brand;
        private final String qty;
        private final String nsv;

        private final String soh;
        private final String remarks;

        public compSales(String rank,String brand, String qty,String nsv,String soh,String remarks) {

            this.rank = rank;
            this.brand = brand;
            this.qty = qty;
            this.nsv = nsv;
            this.soh = soh;
            this.remarks = remarks;

        }
        public String getrank() {
            return rank;
        }
        public String getbrand() {
            return brand;
        }

        public String getqty() {

            return qty;
        }
        public String getnsv() {

            return nsv;
        }
        public String getsoh() {

            return soh;
        }
        public String getremarks() {

            return remarks;
        }


}
