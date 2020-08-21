
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;

public final class AsynDealVisitUtil
{

    /**
     * 设置visit信息
     * 
     * @param iData
     * @throws Exception
     */
    public static void dealVisitInfo(IData iData) throws Exception
    {
        BizVisit visit = CSBizBean.getVisit();

        if (StringUtils.isBlank(visit.getCityCode()) && StringUtils.isNotBlank(iData.getString("TRADE_CITY_CODE")))
        {
            visit.setCityCode(iData.getString("TRADE_CITY_CODE"));
        }
        if (StringUtils.isBlank(visit.getDepartCode()) && StringUtils.isNotBlank(iData.getString("TRADE_DEPART_ID")))
        {
            visit.setDepartCode(iData.getString("TRADE_DEPART_ID"));
        }
        if (StringUtils.isBlank(visit.getDepartId()) && StringUtils.isNotBlank(iData.getString("TRADE_DEPART_ID")))
        {
            visit.setDepartId(iData.getString("TRADE_DEPART_ID"));
        }
        if (StringUtils.isBlank(visit.getStaffId()) && StringUtils.isNotBlank(iData.getString("TRADE_STAFF_ID")))
        {
            visit.setStaffId(iData.getString("TRADE_STAFF_ID"));
        }
        if (StringUtils.isBlank(visit.getStaffEparchyCode()) && StringUtils.isNotBlank(iData.getString("TRADE_EPARCHY_CODE")))
        {
            visit.setStaffEparchyCode(iData.getString("TRADE_EPARCHY_CODE"));
        }
        if (StringUtils.isBlank(visit.getSerialNumber()) && StringUtils.isNotBlank(iData.getString("TRADE_SERIAL_NUMBER")))
        {
            visit.setSerialNumber(iData.getString("TRADE_SERIAL_NUMBER"));
        }

    }
}
