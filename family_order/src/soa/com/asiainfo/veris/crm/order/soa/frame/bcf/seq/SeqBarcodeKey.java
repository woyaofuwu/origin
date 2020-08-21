
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqBarcodeKey extends SeqBase
{
    public SeqBarcodeKey()
    {
        super("SEQ_BARCODE_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();        
        strbuf.append(fillupFigure(nextval, 14, "0"));
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }
}
