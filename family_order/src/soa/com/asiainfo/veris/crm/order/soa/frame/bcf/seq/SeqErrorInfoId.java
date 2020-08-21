
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqErrorInfoId extends SeqBase
{
    public SeqErrorInfoId()
    {
        super("SEQ_ERRORINFO_ID", 100);
    }

    /**
     * 错误信息ID
     */
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

    public String getNextval(String connName, String arg1) throws Exception
    {
        return getNextval(connName);
    }
}
