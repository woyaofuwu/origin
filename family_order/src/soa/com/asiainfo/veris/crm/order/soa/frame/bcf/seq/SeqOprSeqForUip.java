
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqOprSeqForUip extends SeqBase
{
    public SeqOprSeqForUip()
    {
        super("SQ_EC_OPRSEQ", 100);
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
        strbuf.append(getSysDate_yyyyMMddhh24miss());
        strbuf.append(nextval);

        return strbuf.toString();
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
