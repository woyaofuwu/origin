
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqMiniGroup extends SeqBase
{
    public SeqMiniGroup()
    {
        super("SEQ_MINI_GROUP", 100);
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
        strbuf.append("900");
        strbuf.append(fillupFigure(nextval, 7, "0"));
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }
}
