package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTradeCJInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**    
 * Copyright: Copyright  2014 Asiainfo-Linkage
 * 
 * @ClassName: LotterySmsAction.java
 * @Description: 全时通抽奖短信下发处理
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 24, 2014 10:52:52 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * Sep 24, 2014    maoke       v1.0.0           修改原因	
 */
public class LotterySmsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        String lotteryStartDate = StaticUtil.getStaticValue("AUTUMN_ACTIVITY_TIME", "1");
        String lotteryEndDate = StaticUtil.getStaticValue("AUTUMN_ACTIVITY_TIME", "2");
        
        if(StringUtils.isNotBlank(lotteryStartDate) && StringUtils.isNotBlank(lotteryEndDate) 
                && SysDateMgr.getSysTime().compareTo(lotteryStartDate) >=0 && SysDateMgr.getSysTime().compareTo(lotteryEndDate) <=0)
        {
            boolean addSvc231 = false;
            
            UcaData uca = btd.getRD().getUca();
            
            List<SvcTradeData> svcTrades = uca.getUserSvcBySvcId("231");
            
            if(svcTrades != null && svcTrades.size() > 0)
            {
                for(SvcTradeData svcTrade : svcTrades)
                {
                    if(BofConst.MODIFY_TAG_ADD.equals(svcTrade.getModifyTag()))
                    {
                        addSvc231 = true;
                    }
                }
            }
            if(addSvc231)
            {
            	String serialNumber = uca.getSerialNumber();
                String userId = uca.getUserId();
            	
            	//判断用户是否办理了首免 
                boolean isAddFree=false;
                
                //判断是否这笔业务当中添加了首免
            	List<DiscntTradeData> freeDiscntData=uca.getUserDiscntByDiscntId("1234");
            	if(freeDiscntData!=null&&freeDiscntData.size()>0){
            		for(int j=0,size=freeDiscntData.size();j<size;j++){
            			if(BofConst.MODIFY_TAG_ADD.equals(freeDiscntData.get(j).getModifyTag())){
            				isAddFree=true;
            				break;
            			}
            		}
            	}
            	
            	if(isAddFree){	//办理了首免就不下发短信
            		IData cjActiveUser = new DataMap();
                    cjActiveUser.put("TRADE_ID", btd.getTradeId());
                    cjActiveUser.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                    cjActiveUser.put("USER_ID", userId);
                    cjActiveUser.put("SERIAL_NUMBER", serialNumber);
                    cjActiveUser.put("SERVICE_ID", "231");
                    cjActiveUser.put("ACCEPT_DATE", SysDateMgr.getSysTime());
                    cjActiveUser.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    cjActiveUser.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    cjActiveUser.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    cjActiveUser.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    cjActiveUser.put("CANCEL_TAG", "0");
                    cjActiveUser.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    cjActiveUser.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    cjActiveUser.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    cjActiveUser.put("DATA_TYPE", "2");
                    cjActiveUser.put("REMARK", "全时通抽奖活动");
                    Dao.insert("TF_B_TRADE_CJ_ACTIVE_USER", cjActiveUser);
            		
            	}else{	//没有办理首免
            		//获取用户所有的有效的套餐
            		IDataset userAllDiscnt=UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
            		List<String> userDiscntCotainer=new ArrayList<String>();
            		if(userAllDiscnt!=null&&userAllDiscnt.size()>0){
            			for(int j=0,size=userAllDiscnt.size();j<size;j++){
            				userDiscntCotainer.add(userAllDiscnt.getData(j).getString("DISCNT_CODE"));
            			}
            		}
            		
            		//用户有首免套餐不用下发短信
            		if(userDiscntCotainer.contains("1234")){
            			return ;
            		}
            		
            		//查询用户有相关套餐和这次是否办理资费套餐
            		IDataset commpara153 = CommparaInfoQry.getCommparaByCode1("CSM", "153", null, "231", null);          		
            		
            		if(commpara153!=null&&commpara153.size()>0){
            			List<String> discntContainer=new ArrayList<String>();
            			
            			for(int i=0,size=commpara153.size();i<size;i++){
            				discntContainer.add(commpara153.getData(i).getString("PARAM_CODE"));
            			}
            			
            			//判断用户这次是否办理了相关套餐
                		List<DiscntTradeData> discntTrades = uca.getUserDiscnts();
                        if(discntTrades != null && discntTrades.size() > 0)
                        {
                            for(DiscntTradeData discntTrade : discntTrades)
                            {
                                String discntCode = discntTrade.getDiscntCode();
                                
                                if(discntContainer.contains(discntCode)){
                                	return ;
                                }
                            }
                        }
                        
                        
                        //判断用户拥有的优惠当中是否存在相关套餐
                        for(int k=0,size=discntContainer.size();k<size;k++){
                        	if(userDiscntCotainer.contains(discntContainer.get(k))){
                        		return ;
                        	}
                        }   
            		}
            		
            		
            		//是否抽过奖，如果抽过奖，就不下发短信
                    IDataset infos = UserTradeCJInfoQry.qryCJInfos(serialNumber, userId, "231");
                    if(IDataUtil.isNotEmpty(infos))
                    {
                        return ;
                    }
                    
                    IData tradeCJParam = new DataMap();
                    tradeCJParam.put("TRADE_ID", btd.getTradeId());
                    tradeCJParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                    tradeCJParam.put("USER_ID", userId);
                    tradeCJParam.put("SERIAL_NUMBER", serialNumber);
                    tradeCJParam.put("SERVICE_ID", "231");
                    tradeCJParam.put("ACCEPT_DATE", SysDateMgr.getSysTime());
                    tradeCJParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    tradeCJParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    tradeCJParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    tradeCJParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    tradeCJParam.put("CANCEL_TAG", "0");
                    tradeCJParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    tradeCJParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    tradeCJParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    tradeCJParam.put("RSRV_STR1", "3");
                    tradeCJParam.put("REMARK", "全时通抽奖活动");
                    Dao.insert("TF_B_TRADE_CJ", tradeCJParam, Route.getJourDb());
                    
                    
                    IData cjActiveUser = new DataMap();
                    cjActiveUser.put("TRADE_ID", btd.getTradeId());
                    cjActiveUser.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                    cjActiveUser.put("USER_ID", userId);
                    cjActiveUser.put("SERIAL_NUMBER", serialNumber);
                    cjActiveUser.put("SERVICE_ID", "231");
                    cjActiveUser.put("ACCEPT_DATE", SysDateMgr.getSysTime());
                    cjActiveUser.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    cjActiveUser.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    cjActiveUser.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    cjActiveUser.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    cjActiveUser.put("CANCEL_TAG", "0");
                    cjActiveUser.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    cjActiveUser.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    cjActiveUser.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    cjActiveUser.put("DATA_TYPE", "1");
                    cjActiveUser.put("REMARK", "全时通抽奖活动");
                    Dao.insert("TF_B_TRADE_CJ_ACTIVE_USER", cjActiveUser, Route.getJourDb());
                    
                    
                    //获取短信内容
                    IData param=new DataMap();
                    param.put("SUBSYS_CODE", "CSM");
                    param.put("PARAM_ATTR", "7899");
                    param.put("PARAM_CODE", "7899001");
                    IDataset smsContentData = Dao.qryByCodeParser("TD_S_COMMPARA", "QUERY_AUTUMN_ACTIVITY_SMS_CONTENT", param, Route.CONN_CRM_CEN);
                    
                    String smsContent = "感谢您参加“和你一起疯抢好礼”活动，惊喜一：免费回复cj直接抽奖，赢取三星智能机note3、3G流量包、10元话费、和娱乐包等奖品，百分百中奖；惊喜二：次月继续使用业务，6日前还能再获得抽奖机会，赢取iphone5s、3G流量包、20元话费、3元话费等奖品。抽奖机会三天内有效（含当日），大奖月月送，赶快发送cj抽奖吧，幸运女神和你在一起！中国移动";
                    if(smsContentData!=null&&smsContentData.size()>0){
                    	smsContent=smsContentData.getData(0).getString("SMS_CONTENT");
                    }
                    
                    
                    IData smsData = new DataMap();
                    smsData.clear();
                    
                    smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    smsData.put("RECV_OBJECT", serialNumber);
                    smsData.put("RECV_ID", userId);
                    smsData.put("NOTICE_CONTENT", smsContent);
                    smsData.put("REMARK", "全时通抽奖活动短信");
                    SmsSend.insSms(smsData);
            		
            	}
            }
        }
    }
}
