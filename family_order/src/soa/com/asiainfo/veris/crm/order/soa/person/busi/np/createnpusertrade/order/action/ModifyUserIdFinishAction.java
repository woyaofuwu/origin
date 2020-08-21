package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;

public class ModifyUserIdFinishAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		String userId = mainTrade.getString("USER_ID","");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
        String staffId = mainTrade.getString("TRADE_STAFF_ID","");
        String joinDate="";
        IDataset ids=GrpMebInfoQry.queryGrpMebBySN(serialNumber);
        
        String iv_sync_sequence = "";
		if(StringUtils.isBlank(iv_sync_sequence))
		{
			iv_sync_sequence = SeqMgr.getSyncIncreId(); 
			iv_sync_sequence = "99" + iv_sync_sequence.substring(2);
		}

        for(int i = 0; i < ids.size(); i++)
        {
        	IData data = ids.getData(i);
        	if("0".equals(data.getString("USER_ID","")))
        	{
        		synchGroupMemeber(1,iv_sync_sequence,data);
        		
        		data.put("USER_ID", userId);
        		data.put("PARTITION_ID", userId.substring(userId.length() - 4,userId.length()));
        		joinDate=data.getString("JOIN_DATE", "");
        		updateGroupMember(serialNumber,staffId,joinDate);
        		insertGroupMember(data);        		

        		synchGroupMemeber(0,iv_sync_sequence,data);
        	}
        }
        synchInfo(iv_sync_sequence,mainTrade);
        
	}
	
	public void insertGroupMember(IData inparams) throws Exception
    {
		Dao.insert("TF_F_CUST_GROUPMEMBER", inparams);
    }
	
	public void updateGroupMember(String serialNumber,String staffId,String joinDate) throws Exception
    {
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		inparams.put("STAFF_ID", staffId);
		inparams.put("JOIN_DATE", joinDate);
		Dao.executeUpdateByCodeCode("TF_F_CUST_GROUPMEMBER", "DEL_BY_SN", inparams);
    }
	
	public void synchInfo(String iv_sync_sequence, IData mainTrade) throws Exception
	{
		
		String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);		
		IData synchInfoData = new DataMap();
		synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
		
		synchInfoData.put("SYNC_DAY", syncDay);
		synchInfoData.put("SYNC_TYPE", "0");
		synchInfoData.put("TRADE_ID", mainTrade.getString("TRADE_ID", ""));
		synchInfoData.put("STATE", "0");
		synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
		synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		Dao.insert("TI_B_SYNCHINFO", synchInfoData,Route.getJourDb());
	}
	
	public void synchGroupMemeber(int ModifyTag,String iv_sync_sequence, IData data) throws Exception
	{

		String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
		data.put("SYNC_SEQUENCE", iv_sync_sequence);
		data.put("MODIFY_TAG", ModifyTag);
		data.put("SYNC_DAY", syncDay);
		Dao.insert("TI_B_CUST_GROUPMEMBER", data,Route.getJourDb());
	}
}
