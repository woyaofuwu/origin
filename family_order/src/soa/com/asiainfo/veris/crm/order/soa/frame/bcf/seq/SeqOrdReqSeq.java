
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public class SeqOrdReqSeq extends SeqBase
{
    public SeqOrdReqSeq()
    {
        super("ORD_REQ_SEQ", 100);
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
        strbuf.append(SysDateMgr.getSysDate("yyyyMMddHHmmss")); // 取14位系统时间，yyMMdd
        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        String nextval = getNextval(connName);

        return nextval;
    }
}
