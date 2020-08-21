
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqNpJobId extends SeqBase
{
    public SeqNpJobId()
    {
        super("SEQ_NP_JOB_ID", 100);
    }

    /**
     * NP_JOB_ID
     */
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

    public String getNextval(String connName, String arg1) throws Exception
    {
        return getNextval(connName);
    }
}
