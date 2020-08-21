
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqSimCardNo extends SeqBase
{
    public SeqSimCardNo()
    {
        super("SEQ_SIM_CARD", 100);
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

        strbuf.append(fillupFigure(nextval, 10, "0")); // 取初始序列,不足10位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }
}
