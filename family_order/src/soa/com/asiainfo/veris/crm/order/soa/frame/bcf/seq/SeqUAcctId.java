
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SeqUAcctId extends SeqBase
{
	public SeqUAcctId() throws Exception
    {
        super("seq_uacct_id_" + CSBizBean.getTradeEparchyCode(), 100);
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

        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
