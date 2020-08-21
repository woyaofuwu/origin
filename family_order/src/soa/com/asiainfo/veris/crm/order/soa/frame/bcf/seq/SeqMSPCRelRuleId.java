
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqMSPCRelRuleId extends SeqBase
{

    public SeqMSPCRelRuleId()
    {
        super("SEQ_MSPC_REL_RULE_ID", 100);
        // TODO Auto-generated constructor stub
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
        strbuf.append(getSysDate_yyyyMMdd());
        strbuf.append(fillupFigure(nextval, 8, "0"));
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }
}