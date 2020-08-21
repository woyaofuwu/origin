
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.NetChooseUserQry;

public class QueryNetChooseUserBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(QueryNetChooseUserBean.class);

    public IDataset queryNetChooseUserInfo(IData params, Pagination pagination) throws Exception
    {
        String startDate = "";
        String endDate = "";
        IData param = new DataMap();
        String openDay = params.getString("NETCHOOSE_OPEN_DAY", "");
        if (StringUtils.isBlank(openDay))
        {
            startDate = SysDateMgr.getSysDate() + SysDateMgr.getFirstTime00000();
            endDate = SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();
        }
        else
        {
            startDate = openDay + SysDateMgr.getFirstTime00000();
            endDate = openDay + SysDateMgr.getEndTime235959();
        }
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset results = NetChooseUserQry.queryNetChooseUserInfo(param, pagination);
        IData result = new DataMap();
        String productName = "";
        if(IDataUtil.isNotEmpty(results)){
        	for (int i = 0; i < results.size(); i++) {
				result = results.getData(i);
				productName = UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID",""));
				result.put("PRODUCT_NAME", productName);
			}
        }
        return results;
    }
}
