
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class WlwSvc20180730Action implements ITradeAction
{
	public void executeAction(BusiTradeData btd) throws Exception
    {
		if (!"PWLW".equals(btd.getMainTradeData().getBrandCode()))
        {
            return;
        }
		UcaData uca = btd.getRD().getUca();
        List<SvcTradeData> lsSvc20180730 = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        if(CollectionUtils.isEmpty(lsSvc20180730))
        {
        	return;
        }
        
        SvcTradeData dtSvc20180730 = null;
        boolean bIsSvcAdd = false;
        boolean bIsSvcDel = false;
        for (int i = 0; i < lsSvc20180730.size(); i++) 
        {
        	dtSvc20180730 = lsSvc20180730.get(i);
        	String strElementId = dtSvc20180730.getElementId();
        	String strModifyTag = dtSvc20180730.getModifyTag();
        	String strElementType = dtSvc20180730.getElementType();
        	
        	if("20180730".equals(strElementId) && "0".equals(strModifyTag) && "S".equals(strElementType))
        	{
        		bIsSvcAdd = true;
        		break;
        	}
        	else if("20180730".equals(strElementId) && "1".equals(strModifyTag) && "S".equals(strElementType))
        	{
        		bIsSvcDel = true;
        		break;
        	}
        }
        
        if((bIsSvcAdd || bIsSvcDel) && (dtSvc20180730 != null))
        {
        	// 插同步主表 begin
    		String syncId = SeqMgr.getSyncIncreId();
    		String syncDay = StrUtil.getAcceptDayById(syncId);
            String tradeId = btd.getTradeId();
            String strCustId = uca.getCustId();
            String strUserId = uca.getUser().getUserId();
            String strAcctId = uca.getAcctId();
            String strTradeTypeCode = btd.getTradeTypeCode();
            String strCancelTag =  btd.getMainTradeData().getCancelTag();
            
        	IData mainTrade = new DataMap();
            mainTrade.put("TRADE_ID", tradeId);
        	mainTrade.put("CANCEL_TAG", strCancelTag);
        	
			syncRemind(mainTrade, syncId, syncDay, dtSvc20180730);
			
			IData syncInfo = new DataMap();
			syncInfo.put("SYNC_SEQUENCE", syncId);
			syncInfo.put("SYNC_DAY", syncDay);
			syncInfo.put("SYNC_TYPE", "0");
			syncInfo.put("TRADE_ID", tradeId);
			syncInfo.put("STATE", "0");
			syncInfo.put("CUST_ID", strCustId);
			syncInfo.put("USER_ID", strUserId);
			syncInfo.put("ACCT_ID", strAcctId);
			syncInfo.put("EPARCHY_CODE", "0898");
			syncInfo.put("TRADE_TYPE_CODE", strTradeTypeCode);
			Dao.insert("TI_B_SYNCHINFO", syncInfo, Route.getJourDbDefault());
        }
    }
	
	// TODO:返销未完待续
	private static void syncRemind(IData mainTrade, String syncId, String syncDay, SvcTradeData svc) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			String strSvcId = svc.getElementId();
			String strUserId = svc.getUserId();
			IData param = new DataMap(svc.toData());
			param.put("PARTITION_ID", strUserId.substring(strUserId.length() - 4));
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("REMIND_TYPE_CODE", strSvcId);
			param.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
			param.put("UPDATE_TIME", SysDateMgr.getSysTime());
			param.put("UPDATE_STAFF_ID",  CSBizBean.getVisit().getStaffId());
			param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			param.put("REMARK", "WlwSvc20180730Action插入");
			Dao.insert("TI_B_USER_REMIND", param, Route.getJourDbDefault());
			
			/*IDataset reminds = Dao.qryByCode("BOF_SYNC", "SEL_USER_REMIND", param, Route.getJourDbDefault());
			for (Object obj : reminds)
			{
				IData remind = (IData) obj;
				String serviceId = remind.getString("SERVICE_ID");
				IDataset idataset = UpcCall.qryGroupInfoByGroupIdOfferId(BofConst.ELEMENT_TYPE_CODE_SVC, serviceId, "1000");
				if (IDataUtil.isNotEmpty(idataset))
				{
					remind.put("REMIND_TYPE_CODE", serviceId);
					Dao.insert("TI_B_USER_REMIND", remind, Route.getJourDbDefault());
				}
			}*/
		} else
		{

		}
	}
}
