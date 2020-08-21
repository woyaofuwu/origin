
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ChangeSvcRecondsQry;

public class QueryChangeSvcRecondsBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(QueryChangeSvcRecondsBean.class);

    public IDataset getRecondsInfo(IData params, Pagination pagination) throws Exception
    {
        String startDate = params.getString("START_DATE", "");
        String endDate = params.getString("END_DATE", "");
        String changeSVCType = params.getString("CHANGE_SVC_TYPE");
        startDate = startDate + SysDateMgr.getFirstTime00000();
        endDate = endDate + SysDateMgr.getEndTime235959();
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
    	params.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	IDataset results;
		if(StringUtils.equals(changeSVCType,"000")){
			results= ChangeSvcRecondsQry.getAllRecondsInfo(params, pagination);
		}else{
			results = ChangeSvcRecondsQry.getRecondsInfo(params, pagination);
		}
        return results;
    }
}
