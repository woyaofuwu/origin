package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;


public class BindWideNetCustBaseOperFeeAction implements ITradeFinishAction{

	public void executeAction(IData mainTrade) throws Exception
    {
		
		String isNewTopsetbox=mainTrade.getString("RSRV_STR2","");
		
		if(isNewTopsetbox.equals("1")){		//如果是开户
			String serialNumber=mainTrade.getString("SERIAL_NUMBER");
			String wdSerialNumber="KD_"+serialNumber;			
			
			
			IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wdSerialNumber);
			if(IDataUtil.isNotEmpty(wideInfos)){	//如果宽带没有完工
				IDataset configDiscnts=CommparaInfoQry.getCommNetInfo("CSM", "3012", "free_discnt");
				String freeDiscnt=null;
				if(IDataUtil.isNotEmpty(configDiscnts)){
					freeDiscnt=configDiscnts.getData(0).getString("PARA_CODE1","3000002");
				}else{
					freeDiscnt="3000002";
				}
				
				//在tf_b_trade表里intf_id中添加TF_B_TRADE_DISCNT,才能执行优惠信息
				String intfId=mainTrade.getString("INTF_ID","");
				if(intfId.indexOf("TF_B_TRADE_DISCNT,")==-1){
					TradeInfoQry.updateTradeIntfId(mainTrade.getString("TRADE_ID"), "TF_B_TRADE_DISCNT,");
				}
				
				//给用户绑定一个首免套餐，等宽带完工的时候，再终止首免
				IData freeDiscntData=new DataMap();
				freeDiscntData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
				freeDiscntData.put("USER_ID",mainTrade.getString("USER_ID"));
				freeDiscntData.put("USER_ID_A","-1");
				freeDiscntData.put("PACKAGE_ID","-1");
				freeDiscntData.put("PRODUCT_ID","-1");
				freeDiscntData.put("DISCNT_CODE",freeDiscnt);
				freeDiscntData.put("SPEC_TAG","0");
				freeDiscntData.put("INST_ID",SeqMgr.getInstId());
				freeDiscntData.put("START_DATE",mainTrade.getString("ACCEPT_DATE"));
				freeDiscntData.put("END_DATE",SysDateMgr.END_DATE_FOREVER);
				freeDiscntData.put("MODIFY_TAG",BofConst.MODIFY_TAG_ADD);
				freeDiscntData.put("UPDATE_TIME",mainTrade.getString("UPDATE_TIME"));
				freeDiscntData.put("UPDATE_STAFF_ID",mainTrade.getString("UPDATE_STAFF_ID"));
				freeDiscntData.put("UPDATE_DEPART_ID",mainTrade.getString("UPDATE_DEPART_ID"));
				freeDiscntData.put("REMARK","魔百和开户首免套餐，在宽带完工时结束");
				
				TradeDiscntInfoQry.saveDiscntTradeForTopSetOpen(freeDiscntData);
				
				//新增定价计划订单项
				insertTradePricePlanData(mainTrade, freeDiscntData);
				
			}else{  
				//宽带已经完工魔百和才完工，则根据是否有相应活动进行绑定首免套餐
				//（1）有营销活动的用户，25日前完工，免费优惠绑定到月底。
				//（2）有营销活动的用户，25日后完工，免费优惠绑定到下月月底。
				//（3）无营销活动的用户，不进行绑定。
				
				if(saleActvieHave(mainTrade)){
					
					//获取当前时间，并验证当前是25号之前，还是之后
					//REQ201803260008开户首月免费优化:取消25号的判断
					/*boolean isBefore25=true;
					String curDay=SysDateMgr.getCurDay();
				
					if(curDay.compareTo("25")>=0){
						isBefore25=false;
					}*/
				
					IDataset configDiscnts=CommparaInfoQry.getCommNetInfo("CSM", "3012", "free_discnt");
					String freeDiscnt=null;
					if(IDataUtil.isNotEmpty(configDiscnts)){
						freeDiscnt=configDiscnts.getData(0).getString("PARA_CODE1","3000002");
					}else{
						freeDiscnt="3000002";
					}
				
					//在tf_b_trade表里intf_id中添加TF_B_TRADE_DISCNT,才能执行优惠信息
					String intfId=mainTrade.getString("INTF_ID","");
					if(intfId.indexOf("TF_B_TRADE_DISCNT,")==-1){
						TradeInfoQry.updateTradeIntfId(mainTrade.getString("TRADE_ID"), "TF_B_TRADE_DISCNT,");
					}
				
					//给用户绑定一个首免套餐，等宽带完工的时候，再终止首免
					IData freeDiscntData=new DataMap();
					freeDiscntData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
					freeDiscntData.put("USER_ID",mainTrade.getString("USER_ID"));
					freeDiscntData.put("USER_ID_A","-1");
					freeDiscntData.put("PACKAGE_ID","-1");
					freeDiscntData.put("PRODUCT_ID","-1");
					freeDiscntData.put("DISCNT_CODE",freeDiscnt);
					freeDiscntData.put("SPEC_TAG","0");
					freeDiscntData.put("INST_ID",SeqMgr.getInstId());
					freeDiscntData.put("START_DATE",mainTrade.getString("ACCEPT_DATE"));
					
					//REQ201803260008开户首月免费优化:取消25号的判断
					freeDiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
					/*if(isBefore25){	//如果是25号前，就是本月底失效
						freeDiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
					}else{	//如果是25号后，就是下月底失效
						freeDiscntData.put("END_DATE", SysDateMgr.getNextMonthLastDate());
					}*/
					freeDiscntData.put("MODIFY_TAG",BofConst.MODIFY_TAG_ADD);
					freeDiscntData.put("UPDATE_TIME",mainTrade.getString("UPDATE_TIME"));
					freeDiscntData.put("UPDATE_STAFF_ID",mainTrade.getString("UPDATE_STAFF_ID"));
					freeDiscntData.put("UPDATE_DEPART_ID",mainTrade.getString("UPDATE_DEPART_ID"));
					freeDiscntData.put("REMARK","魔百和开户首免套餐，根据有没有魔百和活动进行绑定！");
				
					TradeDiscntInfoQry.saveDiscntTradeForTopSetOpen(freeDiscntData);
					
					//新增定价计划订单项
					insertTradePricePlanData(mainTrade, freeDiscntData);
				
				}
			}
			
		}
		
    }
	
	public boolean saleActvieHave(IData mainTrade) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", mainTrade.getString("USER_ID", ""));
		
		//未完工判断
		IDataset tradeFreeDiscnts = SaleActiveInfoQry.queryFreeDiscntByUserSaleActive(param);
		if(IDataUtil.isNotEmpty(tradeFreeDiscnts) && tradeFreeDiscnts.size() >0)
		{
			return true;
		}
		//资料判断
		IDataset userFreeDiscnts = SaleActiveInfoQry.queryFreeDiscntByTradeSaleActive(param);
		if(IDataUtil.isNotEmpty(userFreeDiscnts) && userFreeDiscnts.size() >0)
		{
			return true;
		}
		
		return  false;
	}
	
	/**
	 * 新增定价计划订单项
	 * @param mainTrade
	 * @param disnctData
	 * @throws Exception
	 * @author yuyj3
	 */
	private void insertTradePricePlanData(IData mainTrade, IData disnctData) throws Exception
    {
	    IDataset pricePlans = UpcCall.qryPricePlanInfoByOfferId(disnctData.getString("DISCNT_CODE"), BofConst.ELEMENT_TYPE_CODE_DISCNT);
        
	    if(IDataUtil.isEmpty(pricePlans))
	    {
            return;
        }
	    
	    boolean isInsert = false;
	    
        for(Object obj : pricePlans)
        {
            IData pricePlan = (IData)obj;
            String pricePlanType = pricePlan.getString("PRICE_PLAN_TYPE");
            
            if(!"1".equals(pricePlanType))
            {
                continue;
            }
            
            IData tradePricePlanData = new DataMap();
            
            tradePricePlanData.put("TRADE_ID", disnctData.getString("TRADE_ID"));
            tradePricePlanData.put("ACCEPT_MONTH", disnctData.getString("TRADE_ID").substring(4, 6));
            tradePricePlanData.put("USER_ID", disnctData.getString("USER_ID"));
            tradePricePlanData.put("USER_ID_A","-1");
            tradePricePlanData.put("PRICE_PLAN_ID", pricePlan.getString("PRICE_PLAN_ID"));
            tradePricePlanData.put("INST_ID", SeqMgr.getInstId());
            tradePricePlanData.put("BILLING_CODE", pricePlan.getString("REF_BILLING_ID"));
            tradePricePlanData.put("PRICE_PLAN_TYPE", pricePlan.getString("PRICE_PLAN_TYPE"));
            tradePricePlanData.put("FEE_TYPE_CODE", pricePlan.getString("FEE_TYPE_CODE"));
            tradePricePlanData.put("FEE_TYPE", pricePlan.getString("FEE_TYPE"));
            tradePricePlanData.put("FEE", pricePlan.getString("FEE"));
            tradePricePlanData.put("PRICE_ID", pricePlan.getString("PRICE_ID"));
            tradePricePlanData.put("RELATION_TYPE_CODE", disnctData.getString("RELATION_TYPE_CODE"));
            tradePricePlanData.put("SPEC_TAG", disnctData.getString("SPEC_TAG"));
            tradePricePlanData.put("START_DATE", disnctData.getString("START_DATE"));
            tradePricePlanData.put("END_DATE", disnctData.getString("END_DATE"));
            tradePricePlanData.put("OFFER_INS_ID", disnctData.getString("INST_ID"));
            tradePricePlanData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            tradePricePlanData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            
            tradePricePlanData.put("UPDATE_TIME",disnctData.getString("UPDATE_TIME"));
            tradePricePlanData.put("UPDATE_STAFF_ID",disnctData.getString("UPDATE_STAFF_ID"));
            tradePricePlanData.put("UPDATE_DEPART_ID",disnctData.getString("UPDATE_DEPART_ID"));
            tradePricePlanData.put("REMARK","魔百和开户首免套餐，根据有没有魔百和活动进行绑定！");
            
            Dao.insert("TF_B_TRADE_PRICE_PLAN", tradePricePlanData, Route.getJourDb());
            
            isInsert = true;
        }
        
        if (isInsert)
        {
            //在tf_b_trade表里intf_id中添加TF_B_TRADE_PRICE_PLAN,完工才会插入到user
            String intfId=mainTrade.getString("INTF_ID","");
            
            if(intfId.indexOf("TF_B_TRADE_PRICE_PLAN,")==-1)
            {
                TradeInfoQry.updateTradeIntfId(mainTrade.getString("TRADE_ID"), "TF_B_TRADE_PRICE_PLAN,");
            }
        }
    }
	
}
