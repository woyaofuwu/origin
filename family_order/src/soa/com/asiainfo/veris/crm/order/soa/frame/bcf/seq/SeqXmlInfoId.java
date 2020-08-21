
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqXmlInfoId extends SeqBase
{
    public SeqXmlInfoId()
    {
        super("SEQ_XML_INFO_ID", 100);
    }

    /**
     * 落地报文序列ID
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
