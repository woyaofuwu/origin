
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqVpmnSerialForGrp extends SeqBase
{
    public SeqVpmnSerialForGrp()
    {
        super("seq_vpmn_serial", 10);
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
        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足8位前面补 0

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
