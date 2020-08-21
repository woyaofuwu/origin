
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqCustRequId extends SeqBase
{
    public SeqCustRequId()
    {
        super("SEQ_REQU_CUST_REQU_ID", 100);
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
        strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMdd
        strbuf.append(ProvinceUtil.getProvinceCodeGrpCorp()); // 获取省份代码
        strbuf.append("0"); // //0表示中心库
        strbuf.append(fillupFigure(nextval, 9, "0")); // 取初始序列,不足9位前面补 0

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
    	String nextval = getNextval(connName);

        return nextval;
    }
}
