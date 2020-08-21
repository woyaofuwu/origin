package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAccessAcctInfoQry;

public class PayDredgeSmartNetworkAction implements ITradeFinishAction{
	
	protected static Logger log = Logger.getLogger(PayDredgeSmartNetworkAction.class);
	
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		
		IData datainfo = UserAccessAcctInfoQry.qrySynOrderInfoByTradeId(tradeId).getData(0);
		
		IData param = new DataMap();
    	param.put("OPER_NUMB", datainfo.getString("OPER_NUMB"));
    	param.put("OPER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	param.put("BIZ_TYPE", datainfo.getString("BIZ_TYPE"));
    	param.put("NEW_LIST_NO", datainfo.getString("NEW_LIST_NO"));
    	param.put("DATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	param.put("WORK_LIST_NO", datainfo.getString("TRADE_ID"));
    	param.put("LIST_STATE", "1");
    	param.put("PAY_AMOUNT", mainTrade.getInt("RSRV_STR1")*100);//营业费用  Lizj总费用存在str1字段，单位：元
    	param.put("PAY_STATE", "1");//0：待支付 1：已支付
    	//if("1".equals( mainTrade.getString("FEE_STATE")) && "0".equals(CSBizBean.getVisit().getInModeCode()))
    	if(mainTrade.getInt("RSRV_STR1")*100>0)
    	{
        	param.put("PAY_TYPE", "05");//01：CRM/BOSS  02：和家亲APP 03：网上营业厅 04：掌上营业厅 05：营业前台 09：其他
        	param.put("PAY_CHANNEL", "2");//0：支付宝 1：微信 2:现金 3：其他 4：话费（只有省CRM侧支持话费支付sssss）支付状态payState为1时必填
        	param.put("PAY_NO", mainTrade.getString("RSRV_STR7"));//bd.getMainTradeData().setRsrvStr7(payNo);
    	}

    	String sysdate = SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss:SSS");
		String payTime = ((sysdate.replace("-", "")).replace(":", "")).replace(" ", "");
    	param.put("PAY_TIME", payTime);
    	param.put("BIZ_VERSION", datainfo.getString("BIZ_VERSION"));
    	
    	param.put("SP_ID", datainfo.getString("SP_ID"));
    	param.put("BIZ_CODE", datainfo.getString("BIZ_CODE"));
    	param.put("CAMPAIGN_ID", datainfo.getString("CAMPAIGN_ID"));
    	param.put("TOTAL_PRICE", mainTrade.getInt("RSRV_STR1")*100);
    	param.put("TOTAL_DISCOUNT", "100");//折扣
    	param.put("REAL_TOTAL_PRICE", mainTrade.getInt("RSRV_STR1")*100);

    	Dao.insert("TI_INTELLIGENTNET_PAY", param,Route.getJourDbDefault());
    	
//    	IData dataQry = UserAccessAcctInfoQry.qrytiIntelligentnetEvaluate(datainfo).getData(0);
//    	IDataset tradeOthers = TradeOtherInfoQry.getGrpOtherByTrade(datainfo.getString("NEW_LIST_NO"), Route.CONN_CRM_CG, null);
//    	IDataset payDetails = new DatasetList();
//    	for(int i=0;i<tradeOthers.size();i++){
//    		String paraCode4 = tradeOthers.getData(i).getString("RSRV_STR4");
//    		String paraCode6 = tradeOthers.getData(i).getString("RSRV_STR6");
//    		IData commparaInfos9221 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9921",paraCode4,null).first(); 
//    		String paraCode1 = commparaInfos9221.getString("RSRV_STR1");
//    		String paraCode2 = commparaInfos9221.getString("RSRV_STR4","0");
//    		if("1".equals(paraCode1)||"2".equals(paraCode1)){
//    			int num = Integer.parseInt(paraCode2);
//    			for(int j=0;j<num;j++){
//    				IData payDetail = new DataMap(); 
//    	        	payDetail.put("ITEM_NUM", dataQry.getString("RSRV_STR1"));
//    	        	payDetail.put("TYPE", "");
//    	        	payDetail.put("NAME", "");
//    	        	payDetail.put("PRICE", "");
//    	        	payDetail.put("REAL_PRICE", "");
//    	        	payDetail.put("INT_COUNT", "");
//    			}
//    			
//    			
//    		}  		
//    		
//    	}
    	
    	IDataset payDetails = new DatasetList();
    	IData payDetail = new DataMap(); 
    	if("0".equals(CSBizBean.getVisit().getInModeCode()))
    	{
    		IDataset others = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
    		if(IDataUtil.isNotEmpty(others)){
    			boolean flag =false;
    			int moneyALL =0;
    			for(int i=0;i<others.size();i++){
    				IData other = others.getData(i);
    				
    				//RSRV_STR6：3对应页面第三大类即服务
    				if("3".equals(other.getString("RSRV_STR6"))){
    					payDetail.put("ITEM_NUM", "06"); //网关：01路由器：02电力猫：03POE面板：04POE交换机：05服务：06网线：07 其他：10
                    	payDetail.put("TYPE", "1");//0:设备 1：服务 9：其它
                    	payDetail.put("PRICE", other.getString("RSRV_NUM1"));
                    	payDetail.put("REAL_PRICE", other.getString("RSRV_NUM1"));
                    	payDetail.put("INT_COUNT", "1");
                    	payDetails.add(payDetail);
    				}else{
    					moneyALL = moneyALL + Integer.parseInt(other.getString("RSRV_NUM1","0"));
    					flag = true ;
    					
    				}
    			}
    			if(flag){
    				IData para = new DataMap();
    				para.put("TRADE_ID", tradeId);
    				IDataset resInfos = HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.queryZnzwTermInfoByTradeId",para);;//海波给接口返回设备信息
    				if(IDataUtil.isNotEmpty(resInfos)){
    					for(int i=0;i<resInfos.size();i++){
    						IData resInfo = resInfos.getData(i);
    						String type = deviceType(resInfo.getString(""));
    						payDetail.put("ITEM_NUM",type);
                        	payDetail.put("TYPE", "2");
                        	payDetail.put("NAME", resInfo.getString("RES_SKU_NAME","资源传空"));
                         	payDetail.put("MANUF", resInfo.getString("SUPPLIER_NAME","资源传空"));
                        	payDetail.put("MODEL", resInfo.getString("IMEI","资源传空"));
                        	payDetail.put("PRICE", moneyALL);
                        	payDetail.put("REAL_PRICE", moneyALL);
                        	payDetail.put("INT_COUNT", "1");
                        	payDetails.add(payDetail);
    					}
    				}
    				
    			}
    		}
    	}
    	
    	//if("1".equals( mainTrade.getString("FEE_STATE")))
    	if( mainTrade.getInt("RSRV_STR1")*100>0 )
    	{
    		//IBossCall.支付信息同步
        	/*IBossCall.payInfoSyn(param.getString("OPER_NUMB"), param.getString("BIZ_TYPE"), param.getString("OPER_TIME"), param.getString("NEW_LIST_NO"),
        			"3", param.getString("WORK_LIST_NO"), param.getString("PAY_STATE"), param.getString("PAY_TYPE"), param.getString("PAY_CHANNEL"), param.getString("PAY_NO"), 
        			param.getString("PAY_TIME"), btd.getMainTradeData().getRsrvStr3(), "100", btd.getMainTradeData().getOperFee(), param.getString("PAY_AMOUNT"),
        			"前台支付", reqData.getSPID(), reqData.getBizCode(), reqData.getCampaign_id(), param.getString("BIZ_VERSION"), null);*/
        	IBossCall.payInfoSyn(param.getString("OPER_NUMB"), param.getString("BIZ_TYPE"), param.getString("OPER_TIME"), param.getString("NEW_LIST_NO"),
        			"3", param.getString("WORK_LIST_NO"), param.getString("PAY_STATE"), param.getString("PAY_TYPE"), param.getString("PAY_CHANNEL"), param.getString("PAY_NO"), 
        			param.getString("PAY_TIME"), param.getString("TOTAL_PRICE"), param.getString("TOTAL_DISCOUNT"), param.getString("REAL_TOTAL_PRICE"), param.getString("PAY_AMOUNT"),
        			"前台支付", param.getString("SP_ID"), param.getString("BIZ_CODE"), param.getString("CAMPAIGN_ID"), param.getString("BIZ_VERSION"), payDetails);
    	}
               
	}
	
	public String deviceType(String name){
		
		if(StringUtils.isBlank(name)){
			return "10";
		}
		if(name.contains("网关")){
			return "01";
		}else if(name.contains("路由器")){
			return "02";
		}else if(name.contains("电力猫")){
			return "03";
		}else if(name.contains("面板")){
			return "04";
		}else if(name.contains("交换机")){
			return "05";
		}else if(name.contains("服务")){
			return "06";
		}else if(name.contains("网线")){
			return "07";
		}
		
		return "10";
	}
}
