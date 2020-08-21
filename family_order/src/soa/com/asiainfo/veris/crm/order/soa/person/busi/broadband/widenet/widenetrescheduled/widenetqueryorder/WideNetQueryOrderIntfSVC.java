package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetrescheduled.widenetqueryorder;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 订单查询接口
 * @author zhengkai5
 */
public class WideNetQueryOrderIntfSVC extends CSBizService{
	
	//订单查询
	public IData queryOrderInfo(IData input) throws Exception{
		
		//校验服务号是否传入
    	IDataUtil.chkParam(input, "TRADE_ID");
		
    	IData result = new DataMap();
		input.put("IN_MODE_CODE", getVisit().getInModeCode());

		WideNetQueryOrderIntfBean bean = (WideNetQueryOrderIntfBean) BeanManager.createBean(WideNetQueryOrderIntfBean.class);
		IDataset resultDataset = bean.queryOrderInfo(input);
		String resultCode = "";
		String resultInfo = "";
		if(IDataUtil.isEmpty(resultDataset)){
			resultCode = "-1";
			resultInfo= "该用户没有宽带订单！";
		}else{
			resultCode = "0";
			resultInfo = "返回成功！";
			IData trade = resultDataset.getData(0);
			String tradeId = trade.getString("TRADE_ID");
			String acceptDate = trade.getString("ACCEPT_DATE");
			String tradeTypeCode = trade.getString("TRADE_TYPE_CODE");
			String tradeDepartId = trade.getString("TRADE_DEPART_ID");
			String tradeStaffId = trade.getString("TRADE_STAFF_ID");
			String tradeStaffName = UStaffInfoQry.getStaffNameByStaffId(tradeStaffId);
			String workType = "";    //是否现场施工  ：涉及魔百和开户时  存储在 TF_B_TRADE.RSRV_STR1
			String suggestDate = "";   //预约时间
			String operCode  = "";
			String tradeType = "";
			String productName = "";
			String cityCode = trade.getString("CITY_CODE");
			String isModem = "0";   //是否办理光猫
			String isTopSetBox = "0";  //是否办理魔百合
			IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
			if(!IDataUtil.isEmpty(tradeOthers))
			{
				for (int i = 0; i < tradeOthers.size(); i++) {
					String rsrvValueCode = tradeOthers.getData(i).getString("RSRV_VALUE_CODE");  //获取光猫/魔百和信息
					if("FTTH".equals(rsrvValueCode)||"FTTH_GROUP".equals(rsrvValueCode)){
						isModem = "1";
					}else if("TOPSETBOX".equals(rsrvValueCode)){
						isTopSetBox = "1";
					}
				}
			}
			String isIMS = "0";   //是否IMS固话
			String userId = trade.getString("USER_ID","");
			String userStarLevel = "";
			String custName = trade.getString("CUST_NAME","");
			String WACCTNO = "";     //带KD_
			String serialNumber = "";   //带KD_
			String phone = "";
			String installAddress = ""; //九级地址
			String deviceID = "";    //设备ID
			String rmPortCode = "";  //端口
			String remark = trade.getString("REMARK","");
			String RSRV_STR1 = "";   // 带宽
			String isPayOff = trade.getString("FEE_STATE","");
			String payPhone = "";    //付费号码
			
			if(trade.getString("SERIAL_NUMBER").startsWith("KD_"))
			{
				serialNumber = trade.getString("SERIAL_NUMBER");
				WACCTNO = trade.getString("SERIAL_NUMBER");
				phone = WACCTNO.substring(3);
				payPhone = phone;
			}
			else
			{
				phone = trade.getString("SERIAL_NUMBER");
				WACCTNO = "KD_"+phone;
				serialNumber = "KD_"+phone;
				payPhone = phone;
			}
			String productMode = "";
			if("600".equals(tradeTypeCode) || "606".equals(tradeTypeCode)) //宽带开户
			{
				IDataset products = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
				//如果没有办理宽带产品 or 没有产品变更 ，即查询原有的产品
				if(!IDataUtil.isEmpty(products))
				{
					String productId = products.getData(0).getString("PRODUCT_ID");
					productName = UProductInfoQry.getProductNameByProductId(productId);  //获取产品名称
					productMode = products.getData(0).getString("PRODUCT_MODE");  // productMode
				}else{
					IDataset mainProducts = UserProductInfoQry.queryUserMainProduct(userId);
					if(!IDataUtil.isEmpty(mainProducts))
					{
						String productId = mainProducts.getData(0).getString("PRODUCT_ID");
						productName = UProductInfoQry.getProductNameByProductId(productId);  //获取产品名称
						productMode = mainProducts.getData(0).getString("PRODUCT_MODE");  // productMode
					}
				}
				
				IDataset widenets =TradeWideNetInfoQry.queryTradeWideNet(tradeId);
				if(!IDataUtil.isEmpty(widenets))
				{
					suggestDate = widenets.getData(0).getString("SUGGEST_DATE");  //预约时间
					installAddress = widenets.getData(0).getString("STAND_ADDRESS");  
					deviceID = widenets.getData(0).getString("RSRV_NUM1"); //设备ID
					//rmPortCode = widenets.getData(0).getString("RSRV_NUM1"); //端口
				}
				
				//根据产品ID  查询  速率
				String productId = trade.getString("PRODUCT_ID","");
				IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
	            if(!IDataUtil.isEmpty(forceElements)){    
					for (int j = 0; j < forceElements.size(); j++)
	                {
	                    IData forceElement = forceElements.getData(j);
	                    if ("S".equals(forceElement.getString("OFFER_TYPE")))
	                    {
							IDataset rates = CommparaInfoQry.getCommpara("CSM", "4000",forceElement.getString("OFFER_CODE") , "0898");
							if(!IDataUtil.isEmpty(rates))
							{
								RSRV_STR1 = rates.getData(0).getString("PARA_CODE1","");
								if(!StringUtils.isEmpty(RSRV_STR1)){
									int rate = Integer.parseInt(RSRV_STR1)/1024;
									RSRV_STR1 = String.valueOf(rate)+"M";
								}
							}
	                    }
	                }
	            }
				tradeType = productModeChangeTradeType(productMode);
				
				if("600".equals(tradeTypeCode))
				{
					operCode= "01";
				}
				if("606".equals(tradeTypeCode))
				{
					operCode= "03";
				}
			}else if("4800".equals(tradeTypeCode)){ //魔百和开户
				workType = trade.getString("RSRV_STR1","");
				if (StringUtils.isNotEmpty(trade.getString("PRODUCT_ID")))
	            {
	            	productName = UProductInfoQry.getProductNameByProductId(trade.getString("PRODUCT_ID"));
	            }
				tradeType = "7";
				operCode= "01";
			}
			
			IData data = new DataMap();
			data.put("SUBSCRIBE_ID", tradeId);
			data.put("ACCEPT_DATE", acceptDate);
			data.put("TRADE_DEPART_ID", tradeDepartId);
			data.put("TRADE_STAFF_ID", tradeStaffId);
			data.put("TRADE_STAFF_NAME", tradeStaffName);
			data.put("WORK_TYPE",  workType );    //是否现场施工
			data.put("WORK_START_TIME", suggestDate);
			data.put("WORK_END_TIME", "" );  //预约结束时间
			data.put("CITY_CODE", cityCode);
			data.put("OPER_CODE", operCode);
			data.put("TRADE_TYPE", tradeType);
			data.put("PRODUCT_NAME", productName);
			data.put("IS_MODEM", isModem);
			data.put("IS_STB", isTopSetBox);
			data.put("IS_IMS", isIMS);
			data.put("IMS_NUM", ""); //IMS固话号码 : 当IS_IMS为1时必填
			data.put("USER_ID", userId);
			data.put("USER_STARLEVEL",userStarLevel );  //用户等级 
			data.put("CUST_NAME", custName);
			data.put("PHONE", phone);
			data.put("WACCTNO", WACCTNO);
			data.put("INSTALL_ADDR", installAddress);
			data.put("DEVICE_ID", deviceID);
			data.put("RM_PORT_CODE", rmPortCode );   //端口
			data.put("Remark", remark);
			data.put("SERIAL_NUMBER", serialNumber);
			data.put("RSRV_STR1", RSRV_STR1);  //带宽
			data.put("TRADE_TYPE_CODE", tradeTypeCode);
			data.put("isPayOff", isPayOff);
			data.put("payPhone", payPhone); //付费号码
			
			result.put("DATA", data);
		}
		result.put("X_RESULTCODE", resultCode);
		result.put("X_RESULTINFO", resultInfo);
		result.put("RESULT_CODE",resultCode);
		result.put("RESULT_MESSAGE", resultInfo);
		return result;
	}

	
	public String productModeChangeTradeType(String productMode)
	{	
		String tradeType = "";
		if("07".equals(productMode)) //07：移动GPON/FTTB
		{
			tradeType = "1";
		}
		else if("09".equals(productMode))  //09：ADSL宽带产品
		{
			tradeType = "2";
		}
		else if("11".equals(productMode))  //11：移动FTTH宽带
		{
			tradeType = "3";
		}
		else if("16".equals(productMode))  //16：海南铁通FTTH
		{
			tradeType = "5";
		}
		else if("17".equals(productMode))  //17：海南铁通FTTB
		{
			tradeType = "6";
		}
		else if("13".equals(productMode))  //13：校园宽带
		{
			tradeType = "4";
		}
		return tradeType;
	}
}
