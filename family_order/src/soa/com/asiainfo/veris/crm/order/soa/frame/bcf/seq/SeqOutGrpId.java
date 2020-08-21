
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqOutGrpId extends SeqBase
{
    public SeqOutGrpId()
    {
        super("seq_out_group_id", 100);
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
        strbuf.append("20"); // 取系统时间前两位，yyyyMMdd; 未来77年不变，故写死20
        strbuf.append(fillupFigure(nextval, 8, "0").substring(2)); // 取初始序列,不足8位前面补 0,再截去前两位
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
