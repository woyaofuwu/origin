
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqBookingId extends SeqBase
{
    public SeqBookingId()
    {
        super("SEQ_BOOKING_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        nextval = fillupFigure(nextval, 3, "0"); // 取初始序列,不足3位前面补 0

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
