
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqScoreAcctIdCG extends SeqBase
{
    public SeqScoreAcctIdCG()
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

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getOrderno()); // 获取地域编码序号，不足两位前面补9
        strbuf.append(getSysDate_9yMMdd()); // 取6位系统时间，9yMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
