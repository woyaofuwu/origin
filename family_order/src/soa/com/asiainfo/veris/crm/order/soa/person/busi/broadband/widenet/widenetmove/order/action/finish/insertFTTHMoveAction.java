
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 1、	如果用户原来的设备属于TF_F_GARDEN_DEVICE_INFO 并且移机到FTTH且带宽小于100M的，免费给用户提速到100M，使用一年。一年后恢复到原来的带宽
 * 插入TF_F_USER_WIDENET_MOVE表，用于降速处理
 */
public class insertFTTHMoveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serial_number = mainTrade.getString("SERIAL_NUMBER");
        String user_id = mainTrade.getString("USER_ID");
        String new_product_id = mainTrade.getString("PRODUCT_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String cancelTag = mainTrade.getString("CANCEL_TAG");
		IDataset otherList = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, "FTTH");
    	IDataset widenetList = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
    	String moveTradeId = "";
		if(orderId!=null&&!"".equals(orderId)){
			IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
        	if(IDataUtil.isNotEmpty(tradeInfos)){
        		for(int j=0;j<tradeInfos.size();j++){
        			if("606".equals(tradeInfos.getData(j).getString("TRADE_TYPE_CODE")))
        				moveTradeId = tradeInfos.getData(j).getString("TRADE_ID","");
        		}
        	}
		}
		
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(moveTradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
        	CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }
    	String finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
    	//新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(new_product_id);
        if (Integer.valueOf(new_rate) < 102400){//带宽小于100M的
        	boolean isFTTH = false;
        	if(!otherList.isEmpty() && otherList.size()>0){
				for (int i = 0; i < otherList.size(); i++) {
					IData data = otherList.getData(i);
	        		String modifyTag = data.getString("MODIFY_TAG");
	        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
	        			isFTTH = true;
	        		}
				}
			}
        	
        	if(serial_number.startsWith("KD_")){
        		serial_number = serial_number.substring(3);
        	}
        	IData param=new DataMap();
        	param.put("SERIAL_NUMBER", serial_number);
        	//查询宽带账号的原设备ID
        	IDataset userDeviceIdInfos = CSAppCall.call("PB.AddressManageSvc.queryDeviceByAccount", param);
        	if(IDataUtil.isNotEmpty(userDeviceIdInfos)){
        		String deviceId = userDeviceIdInfos.getData(0).getString("DEVICE_ID");
        		IData param1=new DataMap();
        		param1.put("DEVICE_CODE", deviceId);
        		param1.put("REMOVE_TAG", "0");
        		param1.put("ACTIVITY_CODE", "60001");
        		//宽带账户的原设备ID在高价值小区的
        		IDataset infos = CSAppCall.call("SS.GardenDeviceInfoSVC.qryGardenDeviceInfo", param1);
        		if(IDataUtil.isNotEmpty(infos) && isFTTH){
        			if(!widenetList.isEmpty() && widenetList.size()>0){
        				for (int i = 0; i < widenetList.size(); i++) {
        					IData data = widenetList.getData(i);
        					String modifyTag = data.getString("MODIFY_TAG");
        	        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
        	        		    IData logInfo=new DataMap();
              				    logInfo.put("SERIAL_NUMBER", "KD_"+serial_number);//宽带账号
              				    logInfo.put("USER_ID", user_id);//宽带用户ID
              				    logInfo.put("UPDATE_TIME", finishDate);//宽带提速时间
              				    logInfo.put("PRODUCT_ID", new_product_id);//宽带提速时所办理的宽带主产品ID
              				    logInfo.put("RSRV_STR2", tradeId);
              				    Dao.insert("TF_F_USER_WIDENET_MOVE", logInfo);
              				    
              				    String dates = SysDateMgr.addYears(finishDate.replace("/", "-"),1);
	              		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	              				Date date = sdf.parse(dates);
	              				SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
	              				int yyyy = Integer.parseInt(formatter.format(date));
	              				SimpleDateFormat formatter1 = new SimpleDateFormat("MM");
	              				int MM = Integer.parseInt(formatter1.format(date));
	              				String lastDay = getLastDayOfMonth(yyyy,MM)+" 23:59:59";
	              				String forceObject1 = "10086606" + serial_number;
	              		        IData smsData = new DataMap();
	              		        smsData.put("TRADE_ID", tradeId);
	              		        smsData.put("EPARCHY_CODE", eparchyCode);
	              		        smsData.put("IN_MODE_CODE", inModeCode);
	              		        smsData.put("SMS_PRIORITY", "5000");
	              		        smsData.put("CANCEL_TAG", cancelTag);
	              		        smsData.put("REMARK", "业务短信通知");
	              		        smsData.put("NOTICE_CONTENT_TYPE", "0");
	              		        smsData.put("SMS_TYPE_CODE", "20");
	              		        smsData.put("RECV_OBJECT", serial_number);
	              		        smsData.put("RECV_ID", user_id);
	              		        smsData.put("FORCE_OBJECT", forceObject1);
	              		        smsData.put("NOTICE_CONTENT", "尊敬的客户：感谢您的支持，已为您提速至100M，有效期至"+lastDay+"。提速到期前如有：1、宽带业务或套餐变更成功生效，提速活动将终止，您所使用宽带将以您最新办理的宽带业务速率为准；2、宽带套餐变更预约，变更预约生效前，如您又取消预约，提速活动将不终止，您所使用宽带将以提速后的速率为准。中国移动。");
	              		        SmsSend.insSms(smsData);
        	        		}
        				}
        			}
        		}
        	}
        }
    }
    
    public static String getLastDayOfMonth(int year,int month)
    {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
          
        return lastDayOfMonth;
    }
}
