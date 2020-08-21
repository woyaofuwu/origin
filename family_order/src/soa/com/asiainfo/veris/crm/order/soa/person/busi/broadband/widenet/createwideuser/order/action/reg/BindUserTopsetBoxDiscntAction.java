package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class BindUserTopsetBoxDiscntAction 
implements ITradeFinishAction
{

	public void executeAction(IData mainTrade) throws Exception
    {
		
		String wdSerialNumber=mainTrade.getString("SERIAL_NUMBER");
		String serialNumber="";
		if(wdSerialNumber.indexOf("KD_")!=-1){
			serialNumber=wdSerialNumber.replaceAll("KD_", "");
		}
		IData userData=UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isNotEmpty(userData)){
			String userId=userData.getString("USER_ID");
			
				IDataset configDiscnts1=CommparaInfoQry.getCommNetInfo("CSM", "3012", "free_discnt");
				String freeDiscnt=null;
				if(IDataUtil.isNotEmpty(configDiscnts1)){
					freeDiscnt=configDiscnts1.getData(0).getString("PARA_CODE1","3000002");
				}else{
					freeDiscnt="3000002";
				}
				
				//如果用户存在首免套餐，说明魔百和先完工
				IDataset orderFreeDiscntData=UserDiscntInfoQry.getAllDiscntByUser(userId, freeDiscnt);
				if(IDataUtil.isNotEmpty(orderFreeDiscntData)){
					IData param = new DataMap();
					param.put("USER_ID", userId);
					boolean Flag = saleActvieHave(param);
					
					//获取当前时间，并验证当前是25号之前，还是之后
					//REQ201803260008开户首月免费优化:取消25号的判断
					/*boolean isBefore25=true;
					String curDay=SysDateMgr.getCurDay();
				
					if(curDay.compareTo("25")>=0){
						isBefore25=false;
					}*/
					
					//解除用户的首免套餐
					IData freeDiscntData=orderFreeDiscntData.getData(0);
					freeDiscntData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
					freeDiscntData.put("MODIFY_TAG",BofConst.MODIFY_TAG_DEL);
					if(Flag){ //根据有没有活动来修改结束时间。
						//REQ201803260008开户首月免费优化:取消25号的判断
						freeDiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						/*if(isBefore25){	//如果是25号前，就是本月底失效
							freeDiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						}else{	//如果是25号后，就是下月底失效
							freeDiscntData.put("END_DATE", SysDateMgr.getNextMonthLastDate());
						}*/
					}else{
						freeDiscntData.put("END_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
					}
					freeDiscntData.put("SPEC_TAG","0");
					freeDiscntData.put("UPDATE_TIME",mainTrade.getString("UPDATE_TIME"));
					freeDiscntData.put("UPDATE_STAFF_ID",mainTrade.getString("UPDATE_STAFF_ID"));
					freeDiscntData.put("UPDATE_DEPART_ID",mainTrade.getString("UPDATE_DEPART_ID"));
					freeDiscntData.put("REMARK","终止魔百和首免套餐");
					
					TradeDiscntInfoQry.saveDiscntTradeForTopSetOpen(freeDiscntData);
					
					//在tf_b_trade表里intf_id中添加TF_B_TRADE_DISCNT,才能执行优惠信息
					String intfId=mainTrade.getString("INTF_ID","");
					
					String addIntfId = "";
					
					if(intfId.indexOf("TF_B_TRADE_DISCNT,")==-1)
					{
					    addIntfId = "TF_B_TRADE_DISCNT,";
					}
					
					IDataset userPricePlanInfos = BofQuery.queryUserPricePlanByOfferInsId(freeDiscntData.getString("INST_ID"), BizRoute.getTradeEparchyCode());
                    
                    if (IDataUtil.isNotEmpty(userPricePlanInfos))
                    {
                        IData userPricePlanData = userPricePlanInfos.getData(0);
                        
                        userPricePlanData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
                        userPricePlanData.put("ACCEPT_MONTH", mainTrade.getString("TRADE_ID").substring(4, 6));
                        userPricePlanData.put("MODIFY_TAG",BofConst.MODIFY_TAG_DEL);
                        userPricePlanData.put("END_DATE", freeDiscntData.getString("END_DATE"));
                        userPricePlanData.put("UPDATE_TIME",mainTrade.getString("UPDATE_TIME"));
                        userPricePlanData.put("UPDATE_STAFF_ID",mainTrade.getString("UPDATE_STAFF_ID"));
                        userPricePlanData.put("UPDATE_DEPART_ID",mainTrade.getString("UPDATE_DEPART_ID"));
                        userPricePlanData.put("REMARK","终止魔百和首免套餐");
                        
                        Dao.insert("TF_B_TRADE_PRICE_PLAN", userPricePlanData, Route.getJourDb());
                        
                        if(intfId.indexOf("TF_B_TRADE_PRICE_PLAN,")==-1)
                        {
                            addIntfId = addIntfId + "TF_B_TRADE_PRICE_PLAN,";
                        }
                    }
                    
                    if (StringUtils.isNotBlank(addIntfId))
                    {
                        TradeInfoQry.updateTradeIntfId(mainTrade.getString("TRADE_ID"), addIntfId);
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
	
}
