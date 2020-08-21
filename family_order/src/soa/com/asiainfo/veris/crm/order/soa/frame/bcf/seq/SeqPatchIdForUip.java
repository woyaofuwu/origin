
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqPatchIdForUip extends SeqBase
{
    public SeqPatchIdForUip()
    {
        super("patchid", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        return getSysDate_yyyyMMddhh24miss();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
