package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqEopAttr extends SeqBase {

    public SeqEopAttr() {
        super("SEQ_EOP_ATTR", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception {
        String nextval = nextval(connName);

        if (nextval == null) {
            return "";
        }
        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception {
        return getNextval(connName);
    }

}
