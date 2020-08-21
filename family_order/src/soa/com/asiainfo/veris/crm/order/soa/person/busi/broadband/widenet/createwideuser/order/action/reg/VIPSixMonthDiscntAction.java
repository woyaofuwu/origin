package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class VIPSixMonthDiscntAction implements ITradeAction
{
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String strSerialNumber = btd.getMainTradeData().getSerialNumber();
		String strSn = strSerialNumber;
		String strUser = "";
		if (!strSerialNumber.substring(0, 3).equals("KD_"))
        {
			strSerialNumber = "KD_" + strSerialNumber;
        }
		else
		{
			strSn = strSerialNumber.replaceAll("KD_", "");
		}
		List<DiscntTradeData> lsDTVIP6 = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if(CollectionUtils.isNotEmpty(lsDTVIP6))
		{
			for (int i = 0; i < lsDTVIP6.size(); i++) 
			{
				DiscntTradeData dtVIP6 = lsDTVIP6.get(i);
	            String strDiscntCode = dtVIP6.getDiscntCode();
	            String strUserID = dtVIP6.getUserId();
	            String strInstId = dtVIP6.getInstId();
	            //String strProductId = dtVIP6.getProductId();
	            //String strPackageId = dtVIP6.getPackageId();
        		IDataset idsDiscnt3969 = CommparaInfoQry.getCommparaAllColByParser("CSM", "3969", strDiscntCode, "0898");
        		if (IDataUtil.isNotEmpty(idsDiscnt3969))
                {
        			IDataset idsUserInfo_KD = UcaInfoQry.qryAllUserInfoBySn(strSerialNumber);
        	        if (IDataUtil.isNotEmpty(idsUserInfo_KD))
        	        {
        	        	for (int j = 0; j < idsUserInfo_KD.size(); j++)
        	        	{
        	        		IData idUserInfo_KD = idsUserInfo_KD.getData(j);
        	        		String strOpenDate = idUserInfo_KD.getString("OPEN_DATE", "");
        	        		String strRemoveTag = idUserInfo_KD.getString("REMOVE_TAG", "");
        	        		if("0".equals(strRemoveTag))
        	        		{
        	        			CSAppException.apperr(CrmCommException.CRM_COMM_888, "前三个月（90天）未办理过宽带业务的客户，可申请办理6个月免费体验套餐。");
        	        		}
        	        		else
        	        		{
        	        			strOpenDate = SysDateMgr.endDateOffset(strOpenDate, "90", "1");
        	        			String strSysTime = SysDateMgr.getSysTime();
        	        			if(strSysTime.compareTo(strOpenDate) < 0)
        	        			{
        	        				CSAppException.apperr(CrmCommException.CRM_COMM_888, "前三个月（90天）未办理过宽带业务的客户，可申请办理6个月免费体验套餐！");
        	        			}
							}
						}
        	        }
        	        
        	        IData idUserInfo = UcaInfoQry.qryUserInfoBySn(strSn);
        	        if(IDataUtil.isNotEmpty(idUserInfo))
        	        {
        	        	strUser = idUserInfo.getString("USER_ID", "");
        	        }
        	        
        	        String strPartitionID = strUserID.substring(strUserID.length() - 4);
        	        IData params = new DataMap();
        	        params.put("PARTITION_ID", strPartitionID);
        	        params.put("USER_ID", strUserID);
        	        StringBuilder sql = new StringBuilder(1000);

        	        sql.append("SELECT T.PARTITION_ID, T.USER_ID, T.SERIAL_NUMBER, T.STATE_TAG, T.OPEN_DATE, ");
        	        sql.append("T.UPDATE_TIME, T.UPDATE_STAFF_ID, T.UPDATE_DEPART_ID, T.REMARK, ");
        	        sql.append("T.RSRV_STR1, T.RSRV_STR2, T.RSRV_STR3, T.RSRV_STR4, T.RSRV_STR5, ");
        	        sql.append("T.RSRV_STR6, T.RSRV_STR7, T.RSRV_STR8, T.RSRV_STR9, T.RSRV_STR10, ");
        	        sql.append("T.RSRV_DATE1, T.RSRV_DATE2, T.RSRV_DATE3, ");
        	        sql.append("T.RSRV_TAG1, T.RSRV_TAG2, T.RSRV_TAG3 ");
        	        sql.append("FROM TF_F_USER_VIP6 T ");
        	        sql.append("WHERE T.PARTITION_ID = :PARTITION_ID AND T.USER_ID = :USER_ID AND T.STATE_TAG IN ('0', '1', '2') ");

        	        IDataset idsUserVip6 = Dao.qryBySql(sql, params);
        	        if(IDataUtil.isEmpty(idsUserVip6))
        	        {
        	        	IData idUserVip6 = new DataMap();
            	        idUserVip6.put("PARTITION_ID", strUserID.substring(strUserID.length() - 4));
            	        idUserVip6.put("USER_ID", strUserID);
            	        idUserVip6.put("SERIAL_NUMBER", strSerialNumber);
            	        idUserVip6.put("STATE_TAG", "0");
            	        idUserVip6.put("OPEN_DATE", SysDateMgr.getSysTime());
            	        idUserVip6.put("RSRV_STR1", strDiscntCode);
            	        idUserVip6.put("RSRV_STR2", strSn);
            	        idUserVip6.put("RSRV_STR3", strUser);
            	        idUserVip6.put("RSRV_STR4", strInstId);
            	        idUserVip6.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	        idUserVip6.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                        idUserVip6.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
                        idUserVip6.put("REMARK","开户6个月免费体验套餐");
                        Dao.insert("TF_F_USER_VIP6", idUserVip6);
        	        }
                }
			}
		}
	}
}
