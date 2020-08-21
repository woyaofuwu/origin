
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OcsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOcsQry;

/**
 * sunxin 为4g卡用户，则处理ocs
 * 
 * @author sunxin
 */
public class DealForOcsAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

       List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        
       String userId =btd.getRD().getUca().getUserId();
       String sn =btd.getRD().getUca().getSerialNumber();
        String lteTag = "";
        // 防止没有台账 故返回，防止有错 sunxin
        if (resTradeDatas.isEmpty())
        {
            return;
        }
        for (ResTradeData resTradeData : resTradeDatas)
        {
        	//判断是不是4g卡用户
            if ("1".equals(resTradeData.getResTypeCode()))
            	lteTag = resTradeData.getRsrvTag3();
        }
        
        if ("1".equals(lteTag))
        {
        	//4g卡用户，判断是不是存在user_ocs用户 不存在则插入台账
        	IDataset exitOcs =UserOcsQry.getUserOcsByUserid(userId,"3");
        	if(IDataUtil.isEmpty(exitOcs)){
	        	OcsTradeData OcsTradeData = new OcsTradeData();
	        	
	        	OcsTradeData.setInstId(SeqMgr.getInstId());
	        	OcsTradeData.setUserId(userId);
	        	OcsTradeData.setUserTypeCode("1");
	        	OcsTradeData.setMonitorType("1");
	        	OcsTradeData.setSerialNumber(sn);
	        	OcsTradeData.setEparchyCode(btd.getRD().getUca().getUserEparchyCode());
	        	OcsTradeData.setStatus("1");
	        	OcsTradeData.setBizType("3");
	        	OcsTradeData.setMonitorFlag("1");
	        	OcsTradeData.setMonitorRuleCode("0000");
	        	OcsTradeData.setStartDate(SysDateMgr.getSysTime());
	        	OcsTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
	        	OcsTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        	OcsTradeData.setRsrvStr1(btd.getRD().getUca().getUser().getCityCode());
	        	
	            btd.add(sn, OcsTradeData);
        	}
        }

    }

}
