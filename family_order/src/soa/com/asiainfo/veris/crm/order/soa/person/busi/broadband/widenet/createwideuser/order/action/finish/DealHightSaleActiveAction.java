package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class DealHightSaleActiveAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
        String tradeId = mainTrade.getString("TRADE_ID");
        String flg = mainTrade.getString("RSRV_STR10");//前台是否办理高价值小区活动标志
        System.out.println("办理高价值小区活动标志"+flg);
        
        if("1".equals(flg)){
        	 //根据配置对应主产品用户开宽带后台办理活动（REQ201810180010 关于高价值小区摘牌行动方案的开发需求）
        	String serialNumberKd= mainTrade.getString("SERIAL_NUMBER").replace("KD_", "");
        	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumberKd);
        	if(IDataUtil.isNotEmpty(userInfo)){
        		//String user_id = userInfo.getString("USER_ID");
            	//IDataset productInfo= UserProductInfoQry.queryMainProductNow(user_id);
    			IDataset commparaInfos9923 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9923",null,null);
    			if(IDataUtil.isNotEmpty(commparaInfos9923)){
    		        IDataset infos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
    		        if(infos != null&&infos.size()>0)
    		        {
    		        		String deviceId = infos.getData(0).getString("RSRV_NUM1");
        		        	IData params=new DataMap();
        		        	params.put("DEVICE_ID", deviceId);
        		        	if(UserOtherInfoQry.IsHightDevice(params))
        		        	{
        		        		//1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：TTADSL海南铁通FTTH，6：海南铁通FTTB
        			            String wideTypeCode = infos.getData(0).getString("RSRV_STR2");
        			            if (!"3".equals(wideTypeCode))
        			            {
        			            	return;
        			            }
        			            IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        			            if(IDataUtil.isNotEmpty(tradeProductInfos)){
        			            	String wideNetPro = tradeProductInfos.getData(0).getString("PRODUCT_ID");
        			            	String rat = WideNetUtil.getWidenetProductRate(wideNetPro);
        			            	if(StringUtils.isNotBlank(rat)){
        	                        	if(Integer.parseInt(rat)<102400){
        	                        		return;
        	                        	}
        	                        	
        	                        }else{
        	                        	return;
        	                        }
        			            }
        			            
        		    	 		 IData saleactiveData = new DataMap();
        		    	         saleactiveData.put("SERIAL_NUMBER",serialNumberKd);
        		    	         saleactiveData.put("PRODUCT_ID", commparaInfos9923.getData(0).getString("PARA_CODE1"));
        		    	         saleactiveData.put("PACKAGE_ID", commparaInfos9923.getData(0).getString("PARA_CODE2"));
        		    	         saleactiveData.put("NO_TRADE_LIMIT", "TRUE");
        		    	         saleactiveData.put("SKIP_RULE", "TRUE");
        		    		     CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
        		    		     
        		        	}
    		        	
    		        }
    			}
        	}
        }

    }

}
