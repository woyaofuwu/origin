
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqPreSmsSendId extends SeqBase
{

    public SeqPreSmsSendId()
    {
        super("seq_smssend_id", 100);
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
        strbuf.append(getSysDate_yyyyMMddhh24miss()); // 取系统时间，yyyyMMddhh24miss
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }

}
