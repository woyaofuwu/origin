package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class DealSaleActiveHightAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        
      //根据配置对应主产品用户开宽带后台办理活动（REQ201810180010 关于高价值小区摘牌行动方案的开发需求）
    	String serialNumberKd= mainTrade.getString("SERIAL_NUMBER").replace("KD_", "");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumberKd);
    	if(IDataUtil.isNotEmpty(userInfo)){
    		String user_id = userInfo.getString("USER_ID");
        	IDataset productInfo= UserProductInfoQry.queryMainProductNow(user_id);
    	
    		if(IDataUtil.isNotEmpty(productInfo)){
    			String proId=productInfo.getData(0).getString("PRODUCT_ID");
    			IDataset commparaInfos9922 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9922",proId,null);
    			if(IDataUtil.isNotEmpty(commparaInfos9922)){
    		        IDataset infos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
    		        if(infos != null&&infos.size()>0)
    		        {
    		        	String deviceId = infos.getData(0).getString("RSRV_NUM1");
    		        	IData params=new DataMap();
    		        	params.put("DEVICE_ID", deviceId);
    		        	if(UserOtherInfoQry.IsHightDevice(params))
    		        	{
    		    	 		 IData saleactiveData = new DataMap();
    		    	         saleactiveData.put("SERIAL_NUMBER",serialNumberKd);
    		    	         saleactiveData.put("PRODUCT_ID", commparaInfos9922.getData(0).getString("PARA_CODE1"));
    		    	         saleactiveData.put("PACKAGE_ID", commparaInfos9922.getData(0).getString("PARA_CODE2"));
    		    	         saleactiveData.put("NO_TRADE_LIMIT", "TRUE");
    		    		     CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
    		    		     
    		        	}
    		        }
    			}
    		}
    	}
        

    }
		
	

}
