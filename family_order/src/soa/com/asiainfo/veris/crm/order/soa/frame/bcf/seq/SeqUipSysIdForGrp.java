
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqUipSysIdForGrp extends SeqBase
{
    public SeqUipSysIdForGrp()
    {
        super("SEQ_UIP_SYSID", 100);
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

        StringBuilder strbuf = new StringBuilder();

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            strbuf.append(getSysDate_yyyyMMddhh24miss()); // 取系统时间
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.QHAI))// 陈依要求 青海也需要
        {
            strbuf.append(getSysDate_yyyyMMdd()); // 取系统时间
        }
        else
        {
            strbuf.append(getSysDate_yy9yyMMdd()); // 取9位系统时间，yy9yyMMdd
        }

        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
