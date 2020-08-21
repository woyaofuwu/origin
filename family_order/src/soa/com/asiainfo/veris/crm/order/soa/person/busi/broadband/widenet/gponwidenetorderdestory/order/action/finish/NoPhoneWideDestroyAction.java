package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.gponwidenetorderdestory.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 无手机宽带-- 登记预约信息 
 * 
 * @author chenxy3
 */
public class NoPhoneWideDestroyAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(NoPhoneWideDestroyAction.class);
    
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
		data.put("RSRV_STR1", "NOPHONEWIDE");//区别于有手机宽带
		data.put("RSRV_STR2", modefee);//光猫押金
		data.put("RSRV_STR3", tradeid);//关联预约的台账信息
		data.put("RSRV_TAG1", widetype);//记录宽带类型
		data.put("RSRV_TAG2", modereturn);//记录是否退光猫
		data.put("RSRV_TAG3", modecode);//记录光猫租赁方式
		boolean countInsert = Dao.insert("TF_F_USER_GPON_DESTROY", data);

		//无手机还发什么短信 
    }
}
