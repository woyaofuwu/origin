package com.asiainfo.veris.crm.order.soa.group.idcline.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;

public class IdcToCheckInFinishAction implements ITradeFinishAction {
	private final static Logger logger = Logger.getLogger(IdcToCheckInFinishAction.class);
	 public void executeAction(IData mainTrade) throws Exception
	    {
	    	String tradeId = mainTrade.getString("TRADE_ID");
	    	String userId = mainTrade.getString("USER_ID");
	    	//这里给iDC平台发送报文
	    	logger.info("In [IdcToEsopDataFinishAction] >>> IdcCheckSVC-tradeId:"+tradeId);
	    	IDataset extInfoList=TradeExtInfoQry.getTradeExtForEsop(tradeId);
	    	if(IDataUtil.isNotEmpty(extInfoList)&&IDataUtil.isNotEmpty(extInfoList.first())){
	    		IData extInfoData=extInfoList.first();
	    		String ibsysId=extInfoData.getString("ATTR_VALUE");
   			IData params = new DataMap();
   			params.put("IBSYSID", ibsysId);
   			IDataset returnList=CSAppCall.call("SS.IdcCheckSVC.idcCheckin", params);//归档
	            if(IDataUtil.isNotEmpty(returnList)&&IDataUtil.isNotEmpty(returnList.first())&&"0".equals(returnList.first().getString("Status",""))){
	            	//成功
	            }else{
	                CSAppException.apperr(CrmCommException.CRM_COMM_103, "执行归档程序异常！");
	            }

	    	}else{
	    		//报错
               CSAppException.apperr(CrmCommException.CRM_COMM_103, "表数据tf_b_trade_ext为空，请检查！");

	    	}
	    }
}
