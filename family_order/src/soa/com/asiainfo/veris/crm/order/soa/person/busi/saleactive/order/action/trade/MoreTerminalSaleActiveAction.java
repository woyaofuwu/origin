package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;


/**
 * 信用购机活动受理页面办理活动2、活动3
 */
public class MoreTerminalSaleActiveAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
    	//对于可以办理信用购机的活动   1:勾选了信用购机.0:没勾选信用购机,正常购机活动
        if(!"1".equals(req.getCreditPurchases())){
        	return ;
        }
        String tradeId2 ="";
        String tradeId3 ="";
        
		String productId2 = btd.getRD().getPageRequestData().getString("PRODUCT_ID2");
    	String packageId2 = btd.getRD().getPageRequestData().getString("PACKAGE_ID2");
    	String saleGoodsImei2 = btd.getRD().getPageRequestData().getString("SALEGOODS_IMEI2");
    	String productId3 = btd.getRD().getPageRequestData().getString("PRODUCT_ID3");
    	String packageId3 = btd.getRD().getPageRequestData().getString("PACKAGE_ID3");
    	String saleGoodsImei3 = btd.getRD().getPageRequestData().getString("SALEGOODS_IMEI3");
    	if(StringUtils.isNotBlank(productId2)&&StringUtils.isNotBlank(packageId2)){
    		 IData activeParam = new DataMap();
    		 activeParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
    		 activeParam.put("PRODUCT_ID", productId2);
    		 activeParam.put("PACKAGE_ID", packageId2);
    		 activeParam.put("SALEGOODS_IMEI", saleGoodsImei2);
    		 activeParam.put("NO_TRADE_LIMIT", "TRUE");
    		 activeParam.put("SKIP_RULE", "TRUE");
    		 IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", activeParam);
    		 IData retnData = (result != null && result.size()>0)?result.getData(0):new DataMap();
			 if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
             {
				 tradeId2 = retnData.getString("TRADE_ID");
             }
    	}
    	if(StringUtils.isNotBlank(productId3)&&StringUtils.isNotBlank(packageId3)){
	   		 IData activeParam = new DataMap();
	   		 activeParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
	   		 activeParam.put("PRODUCT_ID", productId3);
	   		 activeParam.put("PACKAGE_ID", packageId3);
	   		 activeParam.put("SALEGOODS_IMEI", saleGoodsImei3);
	   		 activeParam.put("NO_TRADE_LIMIT", "TRUE");
	   		 activeParam.put("SKIP_RULE", "TRUE");
	   		 IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", activeParam);
   		     IData retnData = (result != null && result.size()>0)?result.getData(0):new DataMap();
			 if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
             {
				 tradeId3 = retnData.getString("TRADE_ID");
             }
     	}
    	
    	if(StringUtils.isNotBlank(tradeId2)||StringUtils.isNotBlank(tradeId3)){
    		OtherTradeData otherTD = new OtherTradeData();
            otherTD.setRsrvValueCode("MORE_TERMINAL");
            otherTD.setRsrvValue("信用购机活动受理多终端");
            otherTD.setStartDate(SysDateMgr.getSysTime());
            otherTD.setUserId(btd.getRD().getUca().getUserId());
            otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
            otherTD.setModifyTag("0");
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setRsrvStr2(tradeId2);//活动2办理tradeId
            otherTD.setRsrvStr3(tradeId3);//活动3办理tradeId
            
            otherTD.setRsrvStr20(CSBizBean.getVisit().getStaffId());
            otherTD.setRsrvStr21(CSBizBean.getVisit().getSerialNumber());
            otherTD.setRsrvStr22(CSBizBean.getVisit().getDepartCode());
            otherTD.setRsrvStr23(CSBizBean.getVisit().getDepartName());
            
    		btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
    	}
    	
		
	}

}
