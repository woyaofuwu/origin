
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ChoosePhoneQry;

public class ChoosePhoneBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(ChoosePhoneBean.class);

    public IDataset getChoosePhoneInfo(IData params, Pagination pagination) throws Exception
    {
        String startDate = params.getString("START_DATE", "");
        String endDate = params.getString("END_DATE", "");
        String lowValue = params.getString("LOWER_VALUE", "");
        String upValue = params.getString("UPPER_VALUE", "");
        IData param = new DataMap();
        startDate = startDate + SysDateMgr.getFirstTime00000();
        endDate = endDate + SysDateMgr.getEndTime235959();
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        if (StringUtils.isNotBlank(lowValue))
        {
            param.put("LOWER_VALUE", lowValue);
        }
        if (StringUtils.isNotBlank(upValue))
        {
            param.put("UPPER_VALUE", upValue);
        }
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset results = ChoosePhoneQry.getChoosePhoneInfo(param, pagination);
        return results;
    }
}
