
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqChnlAcctTrigId extends SeqBase
{
    public SeqChnlAcctTrigId()
    {
        super("SEQ_CHNL_ACCT_TRIGID", 100);
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

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMdd
        }

        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
