
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqScoreRuleId extends SeqBase
{
    public SeqScoreRuleId()
    {
        super("SEQ_SCORE_RULEID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
