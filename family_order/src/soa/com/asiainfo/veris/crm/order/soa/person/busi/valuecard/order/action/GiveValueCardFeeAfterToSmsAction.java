
package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeDeviceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 
 * 
 * @ClassName: GiveValueCardFeeAfterToSmsAction
 * @Description: 
 *  REQ201712150007_关于2018年有价卡赠送成功给赠送客户号码发送提醒短信的功能需求
 *  <br/>
 *  有价卡赠送界面优化：有价卡赠送录入客户手机号码赠送成功后，针对每一张卡赠送录入的客户手机号码，系统自动给客户下发10086或10086XXX端口的短信
 * @version: v1.0.0
 * @author: zhuoyingzhi
 * @date: 20180122
 */
public class GiveValueCardFeeAfterToSmsAction implements ITradeFinishAction
{
	private static final Logger log = Logger.getLogger(GiveValueCardFeeAfterToSmsAction.class);
	
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        
    	String tradeTypeCode= mainTrade.getString("TRADE_TYPE_CODE","");
    	String serialNumber= mainTrade.getString("SERIAL_NUMBER","");
    	String tradeId= mainTrade.getString("TRADE_ID","");
    	System.out.println("---GiveValueCardFeeAfterToSmsAction----tradeTypeCode:"+tradeTypeCode+",serialNumber:"+serialNumber);
    	log.debug("---GiveValueCardFeeAfterToSmsAction----tradeTypeCode:"+tradeTypeCode+",serialNumber:"+serialNumber);
		if ("418".equals(tradeTypeCode)) {
			//有价卡赠送
			  IDataset tradeFeeList=TradeFeeDeviceQry.queryTradeFeeDeviceByTradeId(tradeId);
			  System.out.println("---GiveValueCardFeeAfterToSmsAction----tradeFeeList:"+tradeFeeList);
		      log.debug("---GiveValueCardFeeAfterToSmsAction----tradeFeeList:"+tradeFeeList);
			  if(IDataUtil.isNotEmpty(tradeFeeList)){
				  for(int i=0;i< tradeFeeList.size();i++){
					  IData  tradeFee=tradeFeeList.getData(i);
					  //短信提示内容
					  StringBuffer msg=new StringBuffer();
					  //赠送手机号码
					  String giveSerialNumber=tradeFee.getString("RSRV_STR7","");
					  //有价卡开始号段
					  String deviceS=tradeFee.getString("DEVICE_NO_S","");
					  //有价卡结束号段
					  String deviceE=tradeFee.getString("DEVICE_NO_E","");
					  //有价卡面额(分)
					  String devicePrice=tradeFee.getString("DEVICE_PRICE","");
					  //有价卡销售数据
					  String deviceNum=tradeFee.getString("DEVICE_NUM","");
	
					  if(deviceS.equals(deviceE)){
						  //说明增送一条
						  //尊敬的客户，您好！您于XX年XX月XX日获赠一张面额为XX元的话费卡，卡序列号为：XXXX，请您领取后及时使用充值。 
						  msg.append("尊敬的客户,您好!");
						  msg.append("您于"+SysDateMgr.getSysDate(SysDateMgr.PATTERN_CHINA_DATE)+"获赠一张面额为");
						  double price=Double.valueOf(devicePrice)/100;
						  msg.append(price +"元的话费卡,");
						  msg.append("卡序列号为:"+deviceS+",请您领取后及时使用充值.");
					  }else{
						//说明增送二条以上
						  //尊敬的客户，您好！您于XX年XX月XX日获赠一百张面额为XX元的话费卡，卡序列号为：XXXX-XXXX，请您领取后及时使用充值。
						  msg.append("尊敬的客户,您好!");
						  msg.append("您于"+SysDateMgr.getSysDate(SysDateMgr.PATTERN_CHINA_DATE)+"获赠");
						  msg.append(deviceNum+"张");
						  double price=Double.valueOf(devicePrice)/100;
						  msg.append("面额为"+price +"元的话费卡，");
						  msg.append("卡序列号为："+deviceS+"-"+deviceE);  
						  msg.append(",请您领取后及时使用充值.");
					  }
					  sendSms(giveSerialNumber, msg.toString());
				  }
			  }
		}
   }
    /**
     * 给赠送号码发送短信
     * @param mainSn
     * @throws Exception
     */
 	public void sendSms(String  giveSn,String msg) throws Exception{
 		//发送短信通知
 		IData inparam = new DataMap();
         inparam.put("NOTICE_CONTENT", msg);
         inparam.put("RECV_OBJECT", giveSn);
         inparam.put("RECV_ID", giveSn);
         inparam.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
         inparam.put("REFER_DEPART_ID",CSBizBean.getVisit().getDepartId());
         inparam.put("REMARK", "有价卡赠送发送短信");
         SmsSend.insSms(inparam);
 	}
}
