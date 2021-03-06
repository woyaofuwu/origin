
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqPotradeIdForGrp extends SeqBase
{
    public SeqPotradeIdForGrp()
    {
        super("seq_potrade_id", 100);
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
        strbuf.append(getSysDate_yy9yyMMdd()); // 取9位系统时间，yy9yyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
