package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 
 * @author think
 *
 */
public class ApnUserBindingForOlcomAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(ApnUserBindingForOlcomAction.class);
    
	public void executeAction(IData mainTrade) throws Exception
    {
    	//String serialNumber = mainTrade.getString("SERIAL_NUMBER");
    	String userId = mainTrade.getString("USER_ID");
    	    	
    	String apnName = mainTrade.getString("RSRV_STR1","");
    	String apnDesc = mainTrade.getString("RSRV_STR2","");
    	String apnCntxId = mainTrade.getString("RSRV_STR3","");
    	String apnTplId = mainTrade.getString("RSRV_STR4","");
    	String apnType = mainTrade.getString("RSRV_STR5","");
    	String apnIP4Add = mainTrade.getString("RSRV_STR6","");
    	String updateStaffId = mainTrade.getString("TRADE_STAFF_ID", "");
    	
        IData data = new DataMap();
        data.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
        data.put("USER_ID",userId);
        data.put("APN_NAME",apnName);
        data.put("APN_DESC",apnDesc);
        data.put("APN_CNTXID",apnCntxId);
        data.put("APN_TPLID",apnTplId);
        data.put("APN_IPV4ADD",apnIP4Add);
        data.put("APN_TYPE",apnType);
        data.put("REMOVE_TAG","0");
        data.put("INST_ID",SeqMgr.getInstId());
        data.put("ADD_STAFF_ID",updateStaffId);
        data.put("ADD_TIME", SysDateMgr.getSysTime());
		boolean insertTag = Dao.insert("TF_F_USER_APN_INFO", data);

		if(logger.isDebugEnabled())
		{
			logger.debug("insertTag==" + insertTag);
		}
		
		if(!insertTag)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "专用APN绑定IP新增(发指令),添加表TF_F_USER_APN_INFO数据失败!");
		}
    }
}
