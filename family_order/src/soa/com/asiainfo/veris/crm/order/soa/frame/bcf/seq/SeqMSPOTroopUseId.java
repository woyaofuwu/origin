
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;


public class SeqMSPOTroopUseId extends SeqBase
{
    public SeqMSPOTroopUseId()
    {
        super("SEQ_MSPO_TROOP_USE_ID", 100);
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
        strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
    	String nextval = getNextval(connName);

        return nextval;
    }
}
