
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqWidenetBookOrderId extends SeqBase
{
    public SeqWidenetBookOrderId()
    {
        super("SEQ_WIDENET_BOOK_ORDERID", 100);
    }

    /**
     * 业务流水号
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

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
