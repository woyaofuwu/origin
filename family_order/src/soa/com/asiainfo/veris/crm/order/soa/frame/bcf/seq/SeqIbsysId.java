
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqIbsysId extends SeqBase
{
    public SeqIbsysId()
    {
        super("SEQ_GRPBIZ_IBSYSID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMddhh24mmss
        strbuf.append(fillupFigure(nextval, 4, "0")); // 取初始序列,不足4位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
