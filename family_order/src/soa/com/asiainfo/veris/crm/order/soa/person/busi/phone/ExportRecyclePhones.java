package com.asiainfo.veris.crm.order.soa.person.busi.phone;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportRecyclePhones extends ExportTaskExecutor{

	@Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
		IData data = input.subData("cond", true);        
        
        IData checkParam=new DataMap();
        checkParam.put("START_AGENT_NO", data.getString("START_AGENT_NO",""));
        checkParam.put("END_AGENT_NO", data.getString("END_AGENT_NO",""));
        
        checkParam.put("STAFF_DEPART_ID", data.getString("FORE_STAFF_DEPART_ID",""));  
        checkParam.put("STAFF_CITY_CODE", data.getString("FORE_STAFF_CITY_CODE",""));
        checkParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset checkResult = CSAppCall.call("CS.UserInfoQrySVC.checkUserRightStartEndDepart", checkParam);
        IData checkResultData=checkResult.getData(0);
        
        String checkResultCode=checkResultData.getString("QUERY_RESULT_CODE","");
        
        
        if(checkResultCode.equals("0")){
            IData param=new DataMap();
            param.put("IS_EXTEND_TIME", data.getString("IS_EXTEND_TIME",""));
            param.put("START_AGENT_NO", data.getString("START_AGENT_NO",""));
            param.put("END_AGENT_NO", data.getString("END_AGENT_NO",""));
            param.put("DEPART_KIND_CODE", data.getString("DEPART_KIND_CODE",""));            
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
            param.put("START_DATE", data.getString("START_DATE"));
            param.put("END_DATE", data.getString("END_DATE"));
            
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            
            return CSAppCall.call("SS.BackUserExtendSVC.exportDataByFile", param);
        }else{
        	return new DatasetList();
        }
        
    }
	
}
