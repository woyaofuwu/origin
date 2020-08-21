
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.biz.service.BizRoute;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SeqBroadBandId extends SeqBase
{

    public SeqBroadBandId()
    {
        super("SEQ_BROADBAND_ID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        String routId = BizRoute.getRouteId();

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(routId.substring(1)); // 获取路由地州编码后三位

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            strbuf.append(CSBizBean.getVisit().getCityCode().substring(2)); // 获取员工CITY_CODE后两位
        }
        else
        {
            strbuf.append(StringUtils.lowerCase(CSBizBean.getVisit().getCityCode().substring(2))); // 获取员工CITY_CODE后两位
        }

        strbuf.append(fillupFigure(nextval, 7, "0")); // 取初始序列,不足7位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    public String getNextval(String connName, String epachyCode) throws Exception
    {
        return null;
    }
}
