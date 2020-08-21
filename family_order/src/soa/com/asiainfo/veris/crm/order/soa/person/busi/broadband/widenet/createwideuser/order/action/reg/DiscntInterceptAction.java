package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class DiscntInterceptAction implements ITradeAction
{
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String strSerialNumber = btd.getMainTradeData().getSerialNumber();
		String strSn = strSerialNumber;
		IData params = new DataMap();
		

		List<DiscntTradeData> discnTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if(CollectionUtils.isNotEmpty(discnTrades))
		{
			for (int i = 0; i < discnTrades.size(); i++) 
			{
				DiscntTradeData discnTrade = discnTrades.get(i);
	            String strDiscntCode = discnTrade.getDiscntCode();

	            if("84020042".equals(strDiscntCode) && "0".equals(discnTrade.getModifyTag()))
	            {
	            	if (strSn.indexOf("KD_") > -1) {//宽带账号
	        			if (strSn.split("_")[1].length() > 11)
	        				CSAppException.apperr(CrmCommException.CRM_COMM_888, "商务宽带不能办理！");
	        			else
	        				params.put("SERIAL_NUMBER", strSn);//个人账号
	        		} else {
	        			if (strSn.length() > 11)
	        				CSAppException.apperr(CrmCommException.CRM_COMM_888, "商务宽带不能办理！");
	        			else
	        				params.put("SERIAL_NUMBER", "KD_" + strSn);
	        		}
	            	
	            	StringBuilder sql = new StringBuilder(1000);

        	        sql.append("SELECT D.DISCNT_CODE ");
        	        sql.append("  FROM TF_F_USER T, TF_F_USER_DISCNT D ");
        	        sql.append(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        	        sql.append(" AND T.USER_ID = D.USER_ID ");
        	        sql.append(" AND T.PARTITION_ID = D.PARTITION_ID ");
        	        sql.append(" AND D.DISCNT_CODE = 84020042 ");

        	        IDataset interceppt = Dao.qryBySql(sql, params);
        	        
        	        if(IDataUtil.isNotEmpty(interceppt))
        	        {
        	        	CSAppException.apperr(CrmCommException.CRM_COMM_888, "一个用户只能办理一次此套餐！");
        	        }else
        	        {
        	        	String serialNumber = btd.getRD().getUca().getSerialNumber().substring(3);
        	        	IData smsData = new DataMap();
        	            smsData.put("TRADE_ID", btd.getTradeId());
        	            smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        	            smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        	            smsData.put("SMS_PRIORITY", "5000");
        	            smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
        	            smsData.put("REMARK", "业务短信通知");
        	            smsData.put("NOTICE_CONTENT_TYPE", "0");
        	            smsData.put("SMS_TYPE_CODE", "I0");
        	            smsData.put("RECV_OBJECT", serialNumber);
        	            smsData.put("RECV_ID", btd.getRD().getUca().getUserId());
        	            smsData.put("FORCE_OBJECT", "10086235" + serialNumber);
        	            
        	            String acceptTime = btd.getRD().getAcceptTime();
        	            
        	            String strTime = acceptTime.substring(0, 4) + "年" + acceptTime.substring(5, 7) + "月" + acceptTime.substring(8, 10) + "日 " + acceptTime.substring(11, 13) + ":" + acceptTime.substring(14, 16);

        	            String strContent1 = "尊敬的客户，您好！您于" + strTime + "申请办理中国移动琼中户户通包年50M宽带， 系统已经受理。活动为233元含12个月50M宽带功能费，活动办理次月或装机开通后次月生效。套餐到期后，若客户未办理产品变更，将按对应宽带速率的包月资费收取费用。";

        	            smsData.put("NOTICE_CONTENT", strContent1);

        	            SmsSend.insSms(smsData);
        	        }
	            }
			}
		}
	}
}
