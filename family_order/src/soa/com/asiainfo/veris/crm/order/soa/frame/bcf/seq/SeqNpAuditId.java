
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SeqNpAuditId extends SeqBase
{

    public SeqNpAuditId()
    {
        super("SEQ_NP_AUDIT_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        String strIp = CSBizBean.getVisit().getLoginIP();
        if (StringUtils.isNotBlank(strIp))
        {
            int index = strIp.lastIndexOf(".");
            strIp = strIp.substring(index + 1);
        }
        else
        {
            strIp = "188";
        }

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getSysDate_yyyyMMddhh24miss());
        strbuf.append(fillupFigure(strIp, 3, "0"));
        strbuf.append(fillupFigure("5555", 5, "0"));
        strbuf.append(fillupFigure(nextval, 4, "0"));
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String arg0, String arg1) throws Exception
    {

        return getNextval(arg0);

    }

}
