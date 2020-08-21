
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqPbssBizProdInstId extends SeqBase
{
    public SeqPbssBizProdInstId()
    {
        super("seq_pbss_biz_prodinstid", 100);
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
        strbuf.append("9"); // 编码规则：9+三位省编码+ 14位流水（定长）。湖南前4位是9731
        strbuf.append(ProvinceUtil.getProvinceCodeGrpCorp());
        strbuf.append(getSysDate_yyMMdd()); // 系统时间，yyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
