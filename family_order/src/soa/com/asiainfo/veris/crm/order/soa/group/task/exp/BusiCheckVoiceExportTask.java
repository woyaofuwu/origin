package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class BusiCheckVoiceExportTask extends ExportTaskExecutor{

	@Override
	public IDataset executeExport(IData inParam, Pagination pg) throws Exception {
		IDataset busiInfos = CSAppCall.call("SS.BusiCheckVoiceSVC.queryWorkformInfos", inParam);
		for(int i=0;i<busiInfos.size();i++){
			IData busiInfo = busiInfos.getData(i);
			//1、查询地州
			String cityCode = busiInfo.getString("CITY_CODE");
			String cityName =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
			    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
			    	{ "CUST_CITY_CODE",cityCode }); //查询市县
			busiInfo.put("CITY_NAME", cityName);
			//2、查询客户经理名称
			IData param = new DataMap();
			param.put("STAFF_ID", busiInfo.getString("STAFF_ID"));
			IData staffInfo = CSAppCall.callOne("SS.StaffDeptInfoQrySVC.getStaffInfo", param);
		    if(DataUtils.isNotEmpty(staffInfo)){
		    	busiInfo.put("STAFF_NAME", staffInfo.getString("STAFF_NAME"));
		    }
		    //3、存模板
		    busiInfo.put("BUSI_TYPE",  busiInfo.getString("BPM_TEMPLET_ID"));
		    String groupId =  busiInfo.getString("GROUP_ID");
		    param.put("GROUP_ID", groupId);
		    IData group = CSAppCall.callOne("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", param);
		    String custMgrId = group.getString("CUST_MANAGER_ID");
		    busiInfo.put("CUST_MANAGER_ID", custMgrId);
		    String custMgrName =  "";
	        if (StringUtils.isNotEmpty(custMgrId))
	        {
	        	 param.put("CUST_MANAGER_ID", custMgrId);
	        	 IData managerInfo =CSAppCall.callOne("CS.CustManagerInfoQrySVC.qryCustManagerInfoById", param); 
	        	 custMgrName = managerInfo.getString("CUST_MANAGER_NAME");
	        }
	        busiInfo.put("CUST_MANAGER_NAME", custMgrName); 
		    
		}
		return busiInfos;
	}

}
