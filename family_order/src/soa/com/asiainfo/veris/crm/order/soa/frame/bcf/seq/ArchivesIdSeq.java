package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class ArchivesIdSeq extends SeqBase
{
    public ArchivesIdSeq()
    {
        super("SEQ_ARCHIVES_ID", 100);
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

    @Override
    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
