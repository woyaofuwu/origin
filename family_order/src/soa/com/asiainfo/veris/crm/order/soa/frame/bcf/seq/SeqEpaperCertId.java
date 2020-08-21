
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqEpaperCertId extends SeqBase
{
    public SeqEpaperCertId()
    {
        super("SEQ_EPAPE_CER_ID", 100);
    }

    /**
     * 印章管理员证书ID
     */
    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        
        if (ProvinceUtil.isProvince(ProvinceUtil.SHXI)) {
			
        	StringBuilder strbuf = new StringBuilder();
        	strbuf.append(getSysDate_yyyyMMdd()); // 取8位系统时间，yyyyMMdd
        	strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        	nextval = strbuf.toString();
		}

        return nextval;
    }

    public String getNextval(String connName, String arg1) throws Exception
    {
        return getNextval(connName);
    }
}
