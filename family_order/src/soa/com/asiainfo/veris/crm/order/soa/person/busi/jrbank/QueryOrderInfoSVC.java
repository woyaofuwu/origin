package com.asiainfo.veris.crm.order.soa.person.busi.jrbank;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.HttpSvcTool;

/**
 * @CREATED
 */
public class QueryOrderInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 7416263045210187376L;

    
    public IDataset queryResultsInfo(IData data) throws Exception{
    	
    	//调用银行接口查询数据
    	IData params = new DataMap();
    	
    	params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		params.put("START_DATE", data.getString("START_DATE"));
		params.put("END_DATE", data.getString("END_DATE"));
		params.put("STATUS", data.getString("TRADE_STATUS"));
		params.put("INTF_CODE", data.getString("ORDER_NUMBER"));//交易编码
		params.put("X_TRANS_CODE", "UIP_QUERYLOG");
    	IDataset resultDatas = HttpSvcTool.sendHttpData(params, "UIP_QUERYLOG");
    	/*if(IDataUtil.isEmpty(resultDatas)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用银行日志查询接口失败！");
    	}*/
    	if(IDataUtil.isNotEmpty(resultDatas)){
    		if(!"0".equals(resultDatas.getData(0).getString("X_RESULTCODE"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口调用失败！"+resultDatas.getData(0).getString("X_RESULTINFO"));
    		}
    	}
    	String bankSets = resultDatas.getData(0).getString("RESULT_SET");
    	
    	if(StringUtils.isEmpty(bankSets)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "无数据！");
    	}
    	
    	IData bankData = queryAllSignBanks();
    	IDataset orderDataSet = new DatasetList();
    	for(int i=0;i<resultDatas.size();i++){
//    		IData bankTempData = bankSet.getData(i);
    		String bankTempDataString = resultDatas.getData(i).getString("RESULT_SET");
    		IData bankTempData = new DataMap(bankTempDataString);
    		
			IData temp = new DataMap();
			temp.put("UIPSYSID", bankTempData.getString("UIPSYSID"));
			if("0".equals(bankTempData.getString("ACTION_CODE",""))){
				temp.put("ACTION_CODE", "发起");
			}else if("1".equals(bankTempData.getString("ACTION_CODE",""))){
				temp.put("ACTION_CODE", "落地");
			}else{
				temp.put("ACTION_CODE", "未知");
			}
			String reqSys = bankTempData.getString("REQ_SYS","");
			temp.put("REQ_SYS", reqSys);
			temp.put("REQ_SYS_NAME", bankData.getString(reqSys,"未知"));
			temp.put("REQ_DATE", bankTempData.getString("REQ_DATE"));
			String rcvSys = bankTempData.getString("RCV_SYS","");
			temp.put("RCV_SYS", bankData.getString(rcvSys,"未知"));
			temp.put("RCV_DATE", bankTempData.getString("RCV_DATE"));
			if("01".equals(bankTempData.getString("USER_ID_TYPE",""))){
				temp.put("USER_ID_TYPE", "手机号码");
			}else if("02".equals(bankTempData.getString("USER_ID_TYPE",""))){
				temp.put("USER_ID_TYPE", "飞信号");
			}else if("03".equals(bankTempData.getString("USER_ID_TYPE",""))){
				temp.put("USER_ID_TYPE", "宽带用户");
			}else if("04".equals(bankTempData.getString("USER_ID_TYPE",""))){
				temp.put("USER_ID_TYPE", "EMAIL");
			}
			temp.put("SERIAL_NUMBER", bankTempData.getString("SERIAL_NUMBER"));
			if("0".equals(bankTempData.getString("STATUS",""))){
				temp.put("STATUS", "成功");
			}else if("1".equals(bankTempData.getString("STATUS",""))){
				temp.put("STATUS", "正在处理");
			}else if("2".equals(bankTempData.getString("STATUS",""))){
				temp.put("STATUS", "失败");
			}else if("3".equals(bankTempData.getString("STATUS",""))){
				temp.put("STATUS", "超时");
			}
			
			//取银行的系统编码
			if("0001".equals(bankTempData.getString("REQ_SYS",""))){
				temp.put("RECV_BANK", bankTempData.getString("RCV_SYS",""));
			}else{
				temp.put("RECV_BANK", bankTempData.getString("REQ_SYS",""));
			}
			temp.put("REQ_TRANS_ID", bankTempData.getString("REQ_TRANS_ID",""));
			orderDataSet.add(temp);
		}
    	
    	return orderDataSet;
    }
    
    private IData queryAllSignBanks() throws Exception{
    	IData bankData = new DataMap();
    	IDataset bankDataInfo = CommparaInfoQry.getCommByParaAttr("CSM", "339", getTradeEparchyCode());
    	for(int i=0,s=bankDataInfo.size();i<s;i++){
    		bankData.put(bankDataInfo.getData(i).getString("PARAM_CODE"), bankDataInfo.getData(i).getString("PARAM_CODE"));
    	}
    	bankData.put("0001", "中国移动");
    	return bankData;
    }
    
}
