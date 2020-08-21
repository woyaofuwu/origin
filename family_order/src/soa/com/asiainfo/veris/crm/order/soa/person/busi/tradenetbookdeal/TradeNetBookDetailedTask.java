
package com.asiainfo.veris.crm.order.soa.person.busi.tradenetbookdeal;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 网厅预约业务处理
 * @author zyz
 *
 */
public class TradeNetBookDetailedTask extends ExportTaskExecutor
{
	static  Logger logger=Logger.getLogger(TradeNetBookDetailedTask.class);
	
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	try {
    		//获取查询条件
        	IData inputData = param.subData("cond", true); 
            inputData.put("BOOK_DATE", inputData.getString("START_DATE"));
            inputData.put("BOOK_END_DATE", inputData.getString("END_DATE"));
            inputData.put("TRADE_ID", inputData.getString("BOOK_ID"));
            inputData.put("TRADE_DEPART_ID", inputData.getString("BOOK_DEPT"));
            
        	IDataset  result = CSAppCall.call("SS.TradeNetBookDealSVC.qryBookInfo",inputData);
        	if(IDataUtil.isNotEmpty(result)){
        		for(int i=0;i<result.size();i++){
        			 String  remark=result.getData(i).getString("REMARK");
        			 String str="";
        			 if(!"".equals(remark)&&remark!=null){
        				 if(remark.indexOf("联系人：") > 0){
        					 //备注模糊处理
        					 String contacts=remark.substring(remark.indexOf("联系人：")+4, remark.length());
        						String replaceStr="";
        						if(contacts.length() > 1){
        							if(contacts.startsWith("0")||contacts.startsWith("1")||contacts.startsWith("2")||
        									contacts.startsWith("3")||contacts.startsWith("4")||contacts.startsWith("5")||
        									contacts.startsWith("6")||contacts.startsWith("7")||contacts.startsWith("8")||
        									contacts.startsWith("9")){
        								//数字
        							    if(contacts.length() >= 3){
        							    	replaceStr=contacts.substring(0, 3)+"********";
        							    }else{
        							    	replaceStr="********";
        							    }
        							}else{
        								//中文
        								replaceStr=contacts.substring(0, 1)+"**";
        							}
        						}else{
        							replaceStr="***";
        						}
            				 str=remark.substring(0, remark.indexOf("联系人："))+"联系人："+replaceStr+"】";
        				 }else{
        					 str=remark;
        				 }
        			 }
        			 result.getData(i).put("REMARK", str);
    				 //预约业务类型
    				 result.getData(i).put("BOOK_TYPE_NAME",
    						 StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA",
    						 new java.lang.String[]{"PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, "PARA_CODE2",
    						 new java.lang.String[]{"195","TRADE_TYPE",result.getData(i).getString("BOOK_TYPE_CODE")}));
    				 //预约受理部门
    				 result.getData(i).put("TRADE_DEPART_NAME",
    						 StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", 
            						 result.getData(i).getString("TRADE_DEPART_ID")));
    				 //地市信息
    				 result.getData(i).put("RSRV_STR1",
    						 StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", 
            						 result.getData(i).getString("RSRV_STR1")));
    				 //工单状态
    				 result.getData(i).put("BOOK_STATUS",
    						 StaticUtil.getStaticValue("NET_BOOK_STATUS",result.getData(i).getString("BOOK_STATUS")));
    				 
    				 //处理员工姓名
    				 result.getData(i).put("TRADE_STAFF_NAME",
    						 StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", 
            						 result.getData(i).getString("TRADE_STAFF_ID")));
    				 //预约订单来源
    				 result.getData(i).put("ORDER_SOURCE_NAME",
    						 StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC",
    						 new java.lang.String[]{"TYPE_ID","DATA_ID"}, "DATA_NAME",
    						 new java.lang.String[]{"TRADENETBOOK_ORDER_SOURCE",result.getData(i).getString("IN_MOD_CODE")}));
    				 //有无来源
    				 result.getData(i).put("RESOURCES_NAME",
    						 StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC",
    						 new java.lang.String[]{"TYPE_ID","DATA_ID"}, "DATA_NAME",
    						 new java.lang.String[]{"TRADENETBOOK_IS_RESOURCES",result.getData(i).getString("RSRV_STR4")}));
    				
    				 
        		}
        	}
        	return result;
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
}
