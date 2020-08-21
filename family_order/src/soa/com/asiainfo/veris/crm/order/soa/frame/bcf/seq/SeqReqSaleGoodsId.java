
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqReqSaleGoodsId extends SeqBase
{
    public SeqReqSaleGoodsId()
    {
        super("SEQ_REQ_SALE_GOODS", 100);
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
        strbuf.append(getOrderno());
        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
