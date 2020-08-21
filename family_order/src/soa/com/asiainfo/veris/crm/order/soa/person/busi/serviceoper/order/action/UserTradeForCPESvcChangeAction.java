package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.action;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;


public class UserTradeForCPESvcChangeAction implements ITradeAction
{
	/**
	 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
	 * 
	 * @ClassName: UserTradeForCPESvcChangeAction.java
	 * @Description:CPE号码，在流量封顶、月初自动恢复、不在指定小区，会使用130业务来改变主服务状态，需要把tf_f_user的user_state_codeset值也修改了
	 * @version: v1.0.0
	 * @author: songlm
	 * @date: 2016-3-18
	 */

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String userBrand = btd.getRD().getUca().getBrandCode();//获取品牌
    	String userId = btd.getRD().getUca().getUserId();
    	
    	//只对CPE号码的业务进行处理
    	if ("CPE1".equals(userBrand))
		{
    		List<SvcStateTradeData> stateList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);//获取服务状态子台帐
    		
    		if (stateList != null && stateList.size() > 0)
    		{
    			for (SvcStateTradeData stateData : stateList)
    			{
    				String modifyTag = stateData.getModifyTag();//变更类型
    				String mainTag = stateData.getMainTag();//是否主服务
            	
    				//取出服务状态变更台帐中，变更类型为1新增且是主服务的数据
    				if("0".equals(modifyTag) && "1".equals(mainTag))
    				{
    					String stateCode = stateData.getStateCode();
    					
    					//获取userTradeData,一个userId同一次y业务应该只有一个userTrade
    		            UserTradeData utd = this.getUserTradeData(btd, userId);
    		            if (StringUtils.isEmpty(utd.getModifyTag()) || StringUtils.equals(utd.getModifyTag(), BofConst.MODIFY_TAG_USER))
    		            {
    		                utd.setModifyTag(BofConst.MODIFY_TAG_UPD);
    		            }
    		            utd.setUserStateCodeset(stateCode);
    		            break;
    				}
    			}
    		}
		}
    }
    
    // 查询是否已经存在了用户订单数据
    private UserTradeData getUserTradeData(BusiTradeData btd, String userId) throws Exception
    {
        List<UserTradeData> tradeUserDataList = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        if (tradeUserDataList.size() > 0)
        {
            for (int i = 0, size = tradeUserDataList.size(); i < size; i++)
            {
                if (StringUtils.equals(userId, tradeUserDataList.get(i).getUserId()))
                {
                    return tradeUserDataList.get(i);
                }
            }
        }
        UserTradeData userData = btd.getRD().getUca().getUser().clone();
        btd.add(btd.getRD().getUca().getSerialNumber(), userData);// 加入busiTradeData中
        return userData;
    }
}
