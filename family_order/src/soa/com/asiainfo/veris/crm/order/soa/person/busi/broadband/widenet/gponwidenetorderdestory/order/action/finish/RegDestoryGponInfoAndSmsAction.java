package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.gponwidenetorderdestory.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 登记预约信息、发送短信
 * 
 * @author songlm
 */
public class RegDestoryGponInfoAndSmsAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(RegDestoryGponInfoAndSmsAction.class);
    
	public void executeAction(IData mainTrade) throws Exception
    {
    	String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeid=mainTrade.getString("TRADE_ID");
        String tradetype = mainTrade.getString("TRADE_TYPE_CODE");
        
        //获取预约时间
        String ordertime = mainTrade.getString("RSRV_STR10","");
        if("".equals(ordertime))
        	ordertime = SysDateMgr.getLastDateThisMonth();
        //获取宽带类型
        String widetype = mainTrade.getString("RSRV_STR9");
      //如果是FTTH宽带，需要记录光猫串号，和是否退订标志
        String modecode="0";
        String modefee="0";
        String modereturn="0";
        if ("3".equals(widetype) || "5".equals(widetype))
        {
        	modecode=mainTrade.getString("RSRV_STR8");
        	modefee=mainTrade.getString("RSRV_STR7");
        	modereturn=mainTrade.getString("RSRV_STR6");
        }
        //登记预约信息
        IData data = new DataMap();
        data.put("ACCEPT_MONTH", SysDateMgr.getNowCyc());
		data.put("KD_USER_ID", mainTrade.getString("USER_ID"));
		data.put("KD_SERIAL_NUMBER", serialNumber);
		data.put("DESTROY_ORDER_TIME", ordertime);
		data.put("UPDATE_TIME", SysDateMgr.getSysTime());
		data.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
		data.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
		data.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
		data.put("RSRV_STR2", modefee);//光猫押金
		data.put("RSRV_STR3", tradeid);//关联预约的台账信息
		data.put("RSRV_TAG1", widetype);//记录宽带类型
		data.put("RSRV_TAG2", modereturn);//记录是否退光猫
		data.put("RSRV_TAG3", modecode);//记录光猫租赁方式
		
        /**
         * \REQ201609280002 宽带功能优化 chenxy3 2016-11-29 
         * 记录销户原因
        * */
        String detroyReason= mainTrade.getString("RSRV_STR5","");//销号原因:  编码|其他备注
        String reasonCode=detroyReason.substring(0,detroyReason.indexOf("|"));
        String reasonElse=detroyReason.substring(detroyReason.indexOf("|")+1);
        data.put("RSRV_STR4", reasonCode);//销号原因
        data.put("RSRV_STR5", reasonElse);//销号原因-其他
		
		boolean countInsert = Dao.insert("TF_F_USER_GPON_DESTROY", data);

		//短信
		if(countInsert)
		{
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
								 + "成功申请办理宽带预约拆机业务，在"+ordertime+"系统将自动为您实施宽带拆机，期间您将不能申请宽带套餐变更或参加宽带业务相关营销活动。若有疑问，详询10086或当地移动营业厅。中国移动海南公司。";
            smsData.put("RECV_OBJECT", recvObject);
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(recvObject).getString("USER_ID"));
            smsData.put("PRIORITY", "50");
            smsData.put("REMARK", "宽带预约拆机");
            SmsSend.insSms(smsData);
		}
    }
}
