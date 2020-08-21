
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.group.querygroupinfo.GroupUserForOweInfosQry;

public class GroupUserForOweInfoExport extends ExportTaskExecutor
{

	@Override
    public IDataset executeExport(IData inParam, Pagination pagination) throws Exception
    {
		
		String grpCityCode = inParam.getString("cond_GRP_CITYCODE");
		if(StringUtils.isBlank(grpCityCode))
		{
			CSAppException.apperr(GrpException.CRM_GRP_713,"集团归属市县不能为空!");
		}
		
        IDataset dataset = new DatasetList();
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", inParam.getString("cond_CUST_MANAGER_ID"));
        param.put("GROUP_ID", inParam.getString("cond_GROUP_ID"));
        param.put("CITY_CODE", inParam.getString("cond_GRP_CITYCODE"));
        String eparchyCode = getTradeEparchyCode();
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        dataset = GroupUserForOweInfosQry.expGrpUserForOweInfo(param, pagination);
        
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for (int i = 0, len = dataset.size(); i < len; i++)
            {
        		IData info = dataset.getData(i);
        		String grpUserId = info.getString("USER_ID","");
        		String custManagerId = info.getString("CUST_MANAGER_ID","");
        		String cityCode = info.getString("CITY_CODE","");
        		
        		info.put("PRODUCT_OWE", "0");
        		if(StringUtils.isNotBlank(grpUserId))
        		{        			
        			IData grpAcctInfo = UcaInfoQry.qryAcctInfoByUserIdForGrp(grpUserId);
        			if(IDataUtil.isNotEmpty(grpAcctInfo))
                    {
        	            info.put("ACCT_ID", grpAcctInfo.getString("ACCT_ID",""));
                    }
        			
        			IData acctInfos = AcctCall.getGrpUserOweInfoByUserId(grpUserId);;
        			
        			if(IDataUtil.isNotEmpty(acctInfos))
        			{
        				String oweFee = "0";
        				oweFee = acctInfos.getString("OWE_FEE","0");
        				if(StringUtils.isBlank(oweFee))
        				{
        					oweFee = "0";
        				}
        				info.put("PRODUCT_OWE", oweFee);
        				info.put("PRODUCT_START_CYCLE", acctInfos.getString("USER_OWE_BEGIN_DATE",""));
        				info.put("PRODUCT_END_CYCLE", acctInfos.getString("USER_OWE_END_DATE",""));
        			}
        		}
        		
        		if(StringUtils.isNotBlank(custManagerId))
        		{
        			String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custManagerId);
        			info.put("CUST_MANAGER_NAME", manageName);
        		}
        		
        		if(StringUtils.isNotBlank(cityCode))
        		{
        			String cityName = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", cityCode);
        			info.put("AREA_NAME", cityName);
        		}
            }
        }
        
        return dataset;
    }
    
}
