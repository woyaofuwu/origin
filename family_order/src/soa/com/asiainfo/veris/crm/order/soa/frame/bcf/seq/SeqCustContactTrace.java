
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: SeqCustContact.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-5-29 下午2:24:34 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-5-29 tangxy v1.0.0 修改原因
 */
public class SeqCustContactTrace extends SeqBase
{
    public SeqCustContactTrace()
    {
        super("SEQ_CUST_CONTACT_TRACE", 1000);
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
        strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }
}
