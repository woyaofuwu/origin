package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class WorkformModiTraceSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static IDataset qryAccetanceperiodChangeByIbsysid(IData param) throws Exception{
		return WorkformModiTraceBean.qryAccetanceperiodChangeByIbsysid(param);
	}

    public static IDataset qryModiTraceByIbsysid(IData param) throws Exception {
        return WorkformModiTraceBean.qryModiTraceByIbsysid(param);
    }
    
    public static IDataset qryModiTraceHByIbsysid(IData param) throws Exception {
        return WorkformModiTraceBean.qryModiTraceHByIbsysid(param);
    }
    
    public static IDataset insertModiTrace(IData param) throws Exception{
   	 IData modiTraceData = new DataMap();
        modiTraceData.put("IBSYSID", param.getString("IBSYSID"));
        modiTraceData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        modiTraceData.put("MAIN_IBSYSID", param.getString("MAIN_IBSYSID"));
        modiTraceData.put("ATTR_CODE", "BUSIFORM_ID");
        modiTraceData.put("ATTR_TYPE", "F");
        modiTraceData.put("ATTR_VALUE", param.getString("BUSIFORM_ID"));
        modiTraceData.put("STAFF_ID", getVisit().getStaffId());
        modiTraceData.put("CITY_CODE",getVisit().getCityCode());
        modiTraceData.put("DEPART_ID", getVisit().getDepartId());
        modiTraceData.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        modiTraceData.put("ACCEPT_DATE", SysDateMgr.getSysDate());
        modiTraceData.put("RSRV_STR1", param.getString("RSRV_STR1"));
        boolean modiTraceResult = WorkformModiTraceBean.insertModiTrace(modiTraceData);
        if(!modiTraceResult) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "插入TF_B_EOP_MODI_TRACE表信息失败！");
        }
        return new DatasetList();
   }
}
