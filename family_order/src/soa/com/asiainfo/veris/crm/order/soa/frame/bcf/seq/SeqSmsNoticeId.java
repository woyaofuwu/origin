package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqSmsNoticeId extends SeqBase{

	public SeqSmsNoticeId()
    {
        super("seq_smssend_id", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(getSysDate_yyyyMMdd());
        strBuf.append(fillupFigure(nextval, 8, "0"));
        
        nextval = strBuf.toString();
        
        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }

}
