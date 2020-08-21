package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

/**
 * REQ201909160021_关于行业应用卡成员变更界面套餐变更优化的需求
 * @author xuzh5 
 *2019-11-27 17:11:22
 */
public class BindDiscntAttrAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<AttrTradeData> AttrTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);

        System.out.println("======BindDiscntAttrAction==AttrTradeDataList==:" + AttrTradeDataList);
        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        System.out.println("======BindDiscntAttrAction==discntTradeDatas==:" + discntTradeDatas);
        if(discntTradeDatas!=null &&discntTradeDatas.size()>0){
	        for (DiscntTradeData discntTradeData : discntTradeDatas) {
	        	 if("2".equals(discntTradeData.getModifyTag())){
	        		 if(AttrTradeDataList!=null &&AttrTradeDataList.size()>0){
	        	        	for(int i=0;i<AttrTradeDataList.size();i++){
	        	        		AttrTradeData attrTradeData=AttrTradeDataList.get(i);
	        	        		if((discntTradeData.getDiscntCode()).equals(attrTradeData.getElementId()) && "20171211".equals(attrTradeData.getAttrCode())){
	        	        			//本地折扣率修改，下个月生效
									if("0".equals(attrTradeData.getModifyTag()) )
	        	        			attrTradeData.setStartDate(SysDateMgr.getDateNextMonthFirstDay(attrTradeData.getStartDate())+" 00:00:00");

									//原本地折扣率有效期为月底
									else if("1".equals(attrTradeData.getModifyTag()))
										attrTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());

								}
	        	        	}
	        	        }
	        	 }
	        	}
	        }
        
       


    }
    
    
    
}
