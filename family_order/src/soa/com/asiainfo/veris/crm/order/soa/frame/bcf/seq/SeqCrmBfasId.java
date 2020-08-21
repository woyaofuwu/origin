
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqCrmBfasId extends SeqBase
{
    public SeqCrmBfasId()
    {
        super("SEQ_CRM_BFAS_ID", 1000);
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
        strbuf.append("20");
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String connName, String epachyCode) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();

        strbuf.append(getOrdernoByEpachy(epachyCode)); // 获取地域编码序号，不足两位前面补9
        strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd

        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

}
