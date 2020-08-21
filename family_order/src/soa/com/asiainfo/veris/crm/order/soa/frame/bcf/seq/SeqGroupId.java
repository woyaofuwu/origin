
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SeqGroupId extends SeqBase
{
    public SeqGroupId()
    {
        super("seq_group_id", 100);
    }

    /**
     * 集团编码
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
        strbuf.append(getAreaCodeAfterTwo(CSBizBean.getVisit().getStaffEparchyCode())); // 获取地域编码后两位
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
