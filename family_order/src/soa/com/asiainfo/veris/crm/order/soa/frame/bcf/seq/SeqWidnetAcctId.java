
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqWidnetAcctId extends SeqBase
{
    public SeqWidnetAcctId()
    {
        super("seq_widenet_acct_id", 100);
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
        if (!ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
            strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMdd
            strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        }
        else
        {
            strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        }

        return strbuf.toString();
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
