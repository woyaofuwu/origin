
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqDummyUserId extends SeqBase
{
    public SeqDummyUserId()
    {
        super("SEQ_DUMMY_USER", 1000);
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

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
