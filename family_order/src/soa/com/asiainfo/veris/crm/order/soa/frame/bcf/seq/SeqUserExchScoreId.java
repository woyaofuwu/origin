package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;


public class SeqUserExchScoreId extends SeqBase
{
    public SeqUserExchScoreId()
    {
        super("SEQ_USER_EXCHANGESCORE_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        fillupFigure(nextval, 8, "0"); // 取初始序列,不足8位前面补 0
        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
