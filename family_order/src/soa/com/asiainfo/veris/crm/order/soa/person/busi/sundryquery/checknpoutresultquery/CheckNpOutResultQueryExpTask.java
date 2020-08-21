package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.checknpoutresultquery;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;  
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class CheckNpOutResultQueryExpTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	System.out.println("------mqx---1---CheckNpOutResultQueryExpTask--param="+param);
    	System.out.println("------mqx---1---CheckNpOutResultQueryExpTask--param.subData="+param.subData("cond", true));
    	param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
    	param.put("START_DATE", param.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
    	param.put("FINISH_DATE", param.getString("FINISH_DATE") + SysDateMgr.END_DATE);
    	System.out.println("------mqx---2---CheckNpOutResultQueryExpTask--param="+param);

        IDataset results = CSAppCall.call("SS.CheckNpOutResultQuerySVC.queryCheckNpOut", param);
        
        if(results!=null &&results.size()>0){
        	for(int k=0;k<results.size();k++){
        		String state=results.getData(k).getString("SMS_RESULT");
        		if("0".equals(state)){
        			state="不通过";
        		}else if("1".equals(state)){
        			state="通过";
        		}
        		
        		results.getData(k).put("SMS_RESULT", state);

        	}
        }
        return results;
    } 
}