
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.biz.util.StaticUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SeqSmsSendId extends SeqBase
{
    public SeqSmsSendId()
    {
        super("seq_smssend_id", 100);
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

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {

            strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMdd

        }
        else
        {

            String smmIdPre2 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
            { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
            { "BMC_SMSID_PRE2", connName });

            if (StringUtils.isEmpty(smmIdPre2))

                CSAppException.apperr(BizException.CRM_BIZ_10, connName);

            strbuf.append(smmIdPre2);// 前2位
            strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd

        }

        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
