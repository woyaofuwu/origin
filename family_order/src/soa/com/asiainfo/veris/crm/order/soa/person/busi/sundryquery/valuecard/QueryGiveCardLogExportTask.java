package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.valuecard; 

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

import java.math.BigDecimal;

public class QueryGiveCardLogExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
        IData input = param.subData("cond", true);
        
        String[] inputName=input.getNames(); 
        for(int i=0;i<inputName.length;i++){
        	param.put(inputName[i], input.getString(inputName[i]));
        }
        String eparchyCode=this.getTradeEparchyCode();
        if("".equals(eparchyCode) || eparchyCode==null){
        	eparchyCode="0898";
        }
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode); 
        IDataset dataset = CSAppCall.call("SS.ValueCardMgrSVC.qryGiveCardLogInfos", param); 
        IDataset rtnSet = new DatasetList();
        if(dataset!=null && dataset.size()>0){
        	IDataset downnums = CSAppCall.call("SS.ValueCardMgrSVC.getDownloadNum", param); 
        	String downnum="";
        	if(downnums!=null && downnums.size()>0){
        		downnum=downnums.getData(0).getString("LIMIT_NUM","");
        	}
        	if(dataset.size()>Integer.parseInt(downnum)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "导出数量大于"+downnum+"条，不允许导出！请缩小数据范围后再导出.");
        	}
        	
        	for(int i=0;i<dataset.size();i++){
        		IData info=dataset.getData(i);
        		String AREA_CODE=info.getString("AREA_CODE","");
        		if(!"".equals(AREA_CODE)){  
        			AREA_CODE=StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", AREA_CODE); 
        		}
        		String TOTAL_AMOUNT=info.getString("TOTAL_AMOUNT","");
        		String DEVICE_PRICE=info.getString("DEVICE_PRICE","");
        		String DEVICE_NUM=info.getString("DEVICE_NUM","");
        		if(!"".equals(TOTAL_AMOUNT)){        			
        			BigDecimal bd=new BigDecimal(Double.parseDouble(TOTAL_AMOUNT)/100).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        			TOTAL_AMOUNT=bd.toString();
        		}else{
        			TOTAL_AMOUNT="审批工单配置数据已删除";
        		}
        		if(!"".equals(DEVICE_PRICE) && !"".equals(DEVICE_NUM)){ 
        			BigDecimal bd=new BigDecimal(Double.parseDouble(DEVICE_PRICE)*Double.parseDouble(DEVICE_NUM)/100).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        			DEVICE_PRICE=""+bd.toString();
        		}
        		info.put("AREA_CODE", AREA_CODE);
        		info.put("TOTAL_AMOUNT", TOTAL_AMOUNT);
        		info.put("DEVICE_PRICE", DEVICE_PRICE);
        		rtnSet.add(info);
        		
        	}
        }
        return rtnSet;
    }

}
