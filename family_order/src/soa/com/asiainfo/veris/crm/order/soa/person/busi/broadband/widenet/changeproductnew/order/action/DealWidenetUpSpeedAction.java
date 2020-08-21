package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.action;

import java.util.List;

import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * REQ201806050001:100兆及以下宽带用户提速200-500兆增加施工单需求
 * @author lilu
 *
 */
public class DealWidenetUpSpeedAction implements ITradeAction {

	@SuppressWarnings("rawtypes")
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String serialNum = btd.getRD().getUca().getSerialNumber();
		String newProductId = btd.getMainTradeData().getProductId();
		String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
		
		//老产品速率
        String old_rate = WideNetUtil.getWidenetUserRate(serialNum);
        //新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(newProductId);
        
        //如果用户原来的光猫型号是：P0005272、HG8M8310MG01、P0005273、ZXHN F601  这4种之一，那么需要更换光猫
        //宽带速率为100M以下并且提速到200M以上，需要变更光猫
        //或者速率为200-500提速到1000
        if((Integer.valueOf(old_rate) <= (100 * 1024) && Integer.valueOf(new_rate) >= (200 * 1024))
        		|| ((Integer.valueOf(old_rate) == (200 * 1024) 
        				|| Integer.valueOf(old_rate) == (300 * 1024) 
        				|| Integer.valueOf(old_rate) == (500 * 1024)) && Integer.valueOf(new_rate) == (1000 * 1024)))
        {
    		IDataset results = null;
    		if("686".equals(tradeTypeCode) || "681".equals(tradeTypeCode))
    		{
    			results = UserOtherInfoQry.getUserOther(btd.getRD().getUca().getUserId(), "FTTH");
    		}
    		if("606".equals(tradeTypeCode) || "601".equals(tradeTypeCode))
    		{
    			//传入的是宽带用户ID
    			IDataset relaUU = RelaUUInfoQry.getUserUU(btd.getRD().getUca().getUserId(),"1", "47");	//有手机宽带查询宽带用户标识
    			if(DataSetUtils.isBlank(relaUU))
    			{
    				CSAppException.apperr(CrmCommException.CRM_COMM_103 , "获取用户宽带信息为空，请确认");
    			}
    			results = UserOtherInfoQry.getUserOther(relaUU.getData(0).getString("USER_ID_B"), "FTTH");
    		}
    		String modermType ="";//默认光猫型号没有，存在光猫信息则重新赋值，否则默认不更换光猫 modify by xuzh5 2019-7-31 18:30:45
    		if(DataSetUtils.isNotBlank(results))
    		{
    		 modermType = results.first().getString("RSRV_STR6");	//光猫型号
    		}
    		
    		
    		if("606".equals(tradeTypeCode) || "686".equals(tradeTypeCode))	//宽带移机服开数据构建再TRADE_WIDENET表
    		{
    			List tradeWidenetDatas = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
				for (int i = 0; i < tradeWidenetDatas.size(); i++) {
					WideNetTradeData widenetTradeData = (WideNetTradeData) tradeWidenetDatas.get(i);
					widenetTradeData.setRsrvTag3("U");	////速率提升标识
					if("P0005272".equals(modermType) 
							|| "HG8M8310MG01".equals(modermType) 
							|| "P0005273".equals(modermType) 
							|| "ZXHNF601".equals(modermType))
					{
						widenetTradeData.setRsrvStr5("N");	//标识需要光猫变更
					}
				}
    		}else if("601".equals(tradeTypeCode) || "681".equals(tradeTypeCode))	//产品变更构建服务台账
    		{
    			List svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
    			for (int i = 0; i < svcTradeDatas.size(); i++) {
    				SvcTradeData svcTradeData = (SvcTradeData) svcTradeDatas.get(i);
					svcTradeData.setIsNeedPf(BofConst.IS_NEED_PF_YES);	//发服开标记
					svcTradeData.setRsrvTag2("U");	//速率提升标识
					if("P0005272".equals(modermType) 
							|| "HG8M8310MG01".equals(modermType) 
							|| "P0005273".equals(modermType) 
							|| "ZXHNF601".equals(modermType))
					{
						svcTradeData.setRsrvTag3("N");	//标识需要光猫变更
					}
    			}
    		}
        }
	}
}
