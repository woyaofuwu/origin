
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqBatchId extends SeqBase
{
    public SeqBatchId()
    {
        super("SEQ_BATCH_ID", 100);
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
        strbuf.append(getSysDate_yyMMdd()); // 取8位系统时间，yyyyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
