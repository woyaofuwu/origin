
package com.asiainfo.veris.crm.order.soa.group.cargroup;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GrpWlwDiscntRebateApplyQry
{
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IData queryWlwApprovalNoByEcId(IData param) throws Exception
    {
        IDataset resultInfos = Dao.qryByCode("TF_O_DISCNT_APPLY", "SEL_WLW_APPROVALNO_BY_ECID", param, Route.CONN_CRM_CEN);
        if(resultInfos != null && resultInfos.size() > 0)
        {
        	return resultInfos.getData(0);
        }
        return null;
    }
	
	
}
