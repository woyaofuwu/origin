
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class UndoHwIMobileDeviceModifyStateAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByRelationTradeId(mainTrade.getString("TRADE_ID"));
    	if (IDataUtil.isEmpty(tradeSaleActive)){
            return;
        }
    	
    	String updateTime = "";
    	String updateTimeTemp = "";
    	String rsrvStr22 = "";
    	String rsrvStr22Temp = "";
    	String productId = "";
    	String productIdTemp = "";
    	
    	for(int index = 0, size = tradeSaleActive.size(); index < size; index++)
    	{
    		updateTimeTemp = tradeSaleActive.getData(index).getString("UPDATE_TIME");//更新时间
    		rsrvStr22Temp = tradeSaleActive.getData(index).getString("RSRV_STR22","");//IPHOIONE6 IMEI卡号（该活动特别处理）
    		productIdTemp = tradeSaleActive.getData(index).getString("PRODUCT_ID");
    		
    		if(updateTimeTemp.compareTo(updateTime) > 0)
    		{
    			updateTime = updateTimeTemp;
    			rsrvStr22 = rsrvStr22Temp;
    			productId = productIdTemp;
    		}
    	}
    	
    	if(!"69900858".equals(productId) || StringUtils.isBlank(rsrvStr22)){
    		return;
    	}
    	
    	IData param = new DataMap();
    	param.put("RES_NO", rsrvStr22);//终端串号 不为空
    	param.put("REMARK", "返销该iphone6营销活动");
    	param.put("PARA_VALUE1", mainTrade.getString("SERIAL_NUMBER"));//购机用户的手机号码 不为空
    	param.put("PARA_VALUE4", mainTrade.getString("USER_ID"));//存入TF_R_TERMINAL_SALE_TRADE_DTL的USER_ID字段,用户编码
    	param.put("PARA_VALUE5", mainTrade.getString("CANCEL_DATE"));//存入TF_R_TERMINAL_SALE_TRADE的CANCEL_TIME字段,返销时间
    	param.put("PRODUCT_ID", tradeSaleActive.first().getString("PRODUCT_ID"));//产品编码 不为空
    	param.put("PACKAGE_ID", tradeSaleActive.first().getString("PACKAGE_ID"));//包编码 不为空
    	param.put("X_CHOICE_TAG", "2");//2-iphone销售退货 0-终端销售 1-终端销售退货 不为空 
    	IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(param);
    	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//华为测接口文档有误，0为成功，其他失败
    		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
    		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    		}
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为IPHONE6裸机后合约办理完工接口失败");
    	}
    }

}
