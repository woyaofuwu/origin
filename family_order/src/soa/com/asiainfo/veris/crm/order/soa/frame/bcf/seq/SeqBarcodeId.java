
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqBarcodeId extends SeqBase
{
    public SeqBarcodeId()
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
        strbuf.append(getSysDate_yyMMdd());
        strbuf.append(fillupFigure(nextval, 6, "0"));
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }
}
