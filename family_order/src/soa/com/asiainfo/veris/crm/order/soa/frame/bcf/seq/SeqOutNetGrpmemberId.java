
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqOutNetGrpmemberId extends SeqBase
{
    public SeqOutNetGrpmemberId()
    {
        super("SEQ_OUTNET_GRPMEMBER_ID", 100);
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
