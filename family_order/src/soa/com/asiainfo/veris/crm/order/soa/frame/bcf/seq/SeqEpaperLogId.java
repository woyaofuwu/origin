
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqEpaperLogId extends SeqBase
{
    public SeqEpaperLogId()
    {
        super("SEQ_EPAPER_LOG_ID", 100);
    }

    /**
     * 日志ID
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
