
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * Copyright: Copyright 2015 Asiainfo-Linkage
 * 
 * @ClassName: DealForAgentPresentFeeAction.java
 * @Description: 代理商开户和批量预处理完工后插处理到期提醒
 * @version: v1.0.0
 * @author: songlm
 */
public class DealForAgentPresentFeeAction implements ITradeFinishAction
{

	@Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String execTime = mainTrade.getString("ACCEPT_DATE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String rsrvStr10 = mainTrade.getString("RSRV_STR10", "");

        //如果rsrvStr10有值则继续
        if (StringUtils.isNotBlank(rsrvStr10))
        {
        	//由于开户后同步给账务需要时间，所以到期处理的执行时间向后延
        	String paramValue = "";
        	int addTime = 60;
        	paramValue = StaticUtil.getStaticValue("DEAL_CREATEUSER_EXPIRE", "EXPIRE_TIME");
            if (StringUtils.isNotBlank(paramValue))
            {
            	addTime = Integer.parseInt(paramValue);
            }
            
            IData param = new DataMap();
            param.put("DEAL_ID", SeqMgr.getTradeId());
            param.put("USER_ID", userId);
            param.put("PARTITION_ID", userId.substring(userId.length() - 4));
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("EPARCHY_CODE", eparchyCode);
            param.put("IN_TIME", SysDateMgr.getSysTime());
            param.put("DEAL_STATE", "0");
            param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_CREATE_USER);
            param.put("EXEC_TIME", SysDateMgr.addSecond(execTime,addTime));
            param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(SysDateMgr.addSecond(execTime,addTime)));
            param.put("TRADE_ID", tradeId);

            Dao.insert("TF_F_EXPIRE_DEAL", param);
        }


            
    }
}
