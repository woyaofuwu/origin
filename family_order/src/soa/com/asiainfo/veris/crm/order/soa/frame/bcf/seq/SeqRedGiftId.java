package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqRedGiftId extends SeqBase
{

    public SeqRedGiftId()
    {
        super("SEQ_REDGIFT_MID", 100);
    }
    
    
    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        fillupFigure(nextval, 5, "0"); // 取初始序列,不足5位前面补 0
        return nextval;
    }
    
    
    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }


}
