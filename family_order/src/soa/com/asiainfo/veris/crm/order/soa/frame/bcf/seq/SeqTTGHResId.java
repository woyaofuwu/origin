package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqTTGHResId extends SeqBase
{
    public SeqTTGHResId()
    {
        super("SEQ_TTGH_RES_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(getSysDate_yyyyMMdd());
        strBuf.append(fillupFigure(nextval, 8, "0"));
        nextval = strBuf.toString();
        return nextval;
    }

    @Override
    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
