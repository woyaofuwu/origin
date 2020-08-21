
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;


public class SeqRealIdPush extends SeqBase
{
    public SeqRealIdPush()
    {
        super("seq_real_id", 100);
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
        
        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {

        return getNextval(connName);
    }
}
