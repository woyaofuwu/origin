
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqIntegralAcctId extends SeqBase
{

    public SeqIntegralAcctId()
    {
        super("SEQ_INTEGRAL_ACCT_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
        	StringBuilder strbuf = new StringBuilder();
            strbuf.append(getSysDate_yyyyMMdd());
            strbuf.append(fillupFigure(nextval, 8, "0"));
            nextval = strbuf.toString();
		}

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }

}
