
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqIbSysIdForUip extends SeqBase
{
    public SeqIbSysIdForUip()
    {
        super("SQ_EC_IBSYSID", 100);
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
        strbuf.append("crmI");
        strbuf.append(getSysDate_yyMMddhh24miss());
        strbuf.append(fillupFigure(nextval, 8, "0"));

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
