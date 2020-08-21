
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqMSPHChnlParamId extends SeqBase
{

    public SeqMSPHChnlParamId()
    {
        super("SEQ_MSPH_CHNL_PARAM_ID", 100);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getSysDate_yyyyMMdd());
        strbuf.append(fillupFigure(nextval, 8, "0"));
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return getNextval(s);
    }
}
