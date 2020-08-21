
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * FTTB用户迁移提速活动
 */
public class WidenetMoveFTTBActiveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serial_number = mainTrade.getString("SERIAL_NUMBER");
        String user_id = mainTrade.getString("USER_ID");
        String new_product_id = mainTrade.getString("PRODUCT_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String cancelTag = mainTrade.getString("CANCEL_TAG");
		IDataset otherList = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, "FTTH");
    	IDataset widenetList = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
    	String moveTradeId = "";
		if(orderId!=null&&!"".equals(orderId)){
			IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
        	if(IDataUtil.isNotEmpty(tradeInfos)){
        		for(int j=0;j<tradeInfos.size();j++){
        			if("606".equals(tradeInfos.getData(j).getString("TRADE_TYPE_CODE")))
        				moveTradeId = tradeInfos.getData(j).getString("TRADE_ID","");
        		}
        	}
		}
		
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(moveTradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
        	CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }
    	String finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
    	//新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(new_product_id);
        if (Integer.valueOf(new_rate) < 102400){//带宽小于100M的
        	boolean isFTTH = false;
        	if(!otherList.isEmpty() && otherList.size()>0){
				for (int i = 0; i < otherList.size(); i++) {
					IData data = otherList.getData(i);
	        		String modifyTag = data.getString("MODIFY_TAG");
	        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
	        			isFTTH = true;
	        		}
				}
			}
        	
        	if(serial_number.startsWith("KD_")){
        		serial_number = serial_number.substring(3);
        	}
        	IData param=new DataMap();
        	param.put("SERIAL_NUMBER", serial_number);
        	//查询宽带账号的原设备ID
        	IDataset userDeviceIdInfos = CSAppCall.call("PB.AddressManageSvc.queryDeviceByAccount", param);
        	if(IDataUtil.isNotEmpty(userDeviceIdInfos)){
        		String deviceId = userDeviceIdInfos.getData(0).getString("DEVICE_ID");
        		IData param1=new DataMap();
        		param1.put("DEVICE_CODE", deviceId);
        		param1.put("REMOVE_TAG", "0");
        		//宽带账户的原设备ID在高价值小区的
        		IDataset infos = CSAppCall.call("SS.GardenDeviceInfoSVC.qryGardenDeviceInfo", param1);
        		if(IDataUtil.isNotEmpty(infos) && isFTTH){
        			if(!widenetList.isEmpty() && widenetList.size()>0){
        				for (int i = 0; i < widenetList.size(); i++) {
        					IData data = widenetList.getData(i);
        					String modifyTag = data.getString("MODIFY_TAG");
        	        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
        	        		    IData input=new DataMap();
        	        		    IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 5890, "ZZZZ");
        	        		    if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
        	                        String product_Id = ((IData) comparas.get(0)).getString("PARAM_CODE");
        	                        String package_Id = ((IData) comparas.get(0)).getString("PARA_CODE1");
        	                        input.put("SERIAL_NUMBER", serial_number);
            			        	input.put("PRODUCT_ID", product_Id);
            			        	input.put("PACKAGE_ID", package_Id);
            			        	input.put("NO_TRADE_LIMIT", "TRUE");
            			        	
            			        	CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
        	                    }
              				    
        	        		    
        	        		}
        				}
        			}
        		}
        	}
        }
    }
}
