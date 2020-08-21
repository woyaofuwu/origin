package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetorderdestroycancel.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 删除预约信息、发送短信
 * 
 * @author songlm
 */
public class RegOrderDestoryCancelAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        //登记预约信息
        IData data = new DataMap();
        data.put("ACCEPT_MONTH", SysDateMgr.getNowCyc());
		data.put("KD_USER_ID", mainTrade.getString("USER_ID"));
		data.put("USER_ID", mainTrade.getString("USER_ID"));
		data.put("KD_SERIAL_NUMBER", serialNumber);
		data.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
		data.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
		data.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
		//Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_DESTORY_ORDER_BY_USERID", data);
		int iret = Dao.executeUpdateByCodeCode("TF_F_USER_GPON_DESTROY", "DEL_DESTORY_ORDER_BY_USERID", data);
		//短信
		if(iret>0)
		{
			//无手机宽带 chenxy3 2017-02-27
			String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
			if("1686".equals(tradeTypeCode)){
				return;
			}
			if(serialNumber.indexOf("KD_")>-1) {//宽带账号
	    		if(serialNumber.split("_")[1].length()>11)
	    			return ;//serialNumber = serialNumber;//商务宽带不发短信
	    		else
	    			serialNumber = serialNumber.split("_")[1];//个人账号
	    	}
	    	else {
	    		if(serialNumber.length()>11)
	    			return ;//serialNumber="KD_"+serialNumber;
//	    		else
//	    			serialNumber= serialNumber;
	    	}
			IData smsData = new DataMap();
			String recvObject = serialNumber.replace("KD_", "");
			String noticeContent = "尊敬的客户：您于" 
								 + SysDateMgr.getSysTime()
								 + "成功申请办理宽带预约拆机取消业务。若有疑问，详询10086或当地移动营业厅。中国移动海南公司。";
            smsData.put("RECV_OBJECT", recvObject);
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(recvObject).getString("USER_ID"));
            smsData.put("PRIORITY", "50");
            smsData.put("REMARK", "宽带预约拆机取消");
            SmsSend.insSms(smsData);
		}else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到对应的宽带预约拆机记录，userid="+mainTrade.getString("USER_ID")+"！");
		}
    }
}
