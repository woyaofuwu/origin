
package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.action.finish;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.SynJingxinUtils;


/**
 * REQ201805160025_2018年海洋通业务办理开发需求
 * <br/>
 * 
 * @author zhuoyingzhi
 * @date 20180621
 *
 */
public class OceanFrontFinishAction implements ITradeFinishAction
{
	public static final Logger logger=Logger.getLogger(OceanFrontFinishAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber=mainTrade.getString("SERIAL_NUMBER");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE","");
        
        String stopOrOpenName="";
        Integer itfType = null;
        if("9830".equals(tradeTypeCode)){
        	//海洋通报停：9830
        	stopOrOpenName="海洋通报停业务";
        	itfType=0;
        }else if("9831".equals(tradeTypeCode)){
        	stopOrOpenName="海洋通报开业务";
        	itfType=1;
        }
        /**
         * 调第三方接口
         */
    	IData insparam = new DataMap();
    	
    	insparam.put("appId", SynJingxinUtils.getJingxinAppId());
    	insparam.put("timeStamp", String.valueOf(System.currentTimeMillis()));
    	//手机号码
    	insparam.put("account", serialNumber);
        //接口类型，0表示报停，1表示复通
        insparam.put("itfType", itfType);
        
        IData result=SynJingxinUtils.post("stopOrRepeat", insparam);
        System.out.println("---OceanFrontFinishAction---result:"+result);
        if(IDataUtil.isNotEmpty(result)){
        	String code=result.getString("code", "");
        	if("0".equals(code)){
        		//发送短信
        		sendSms(serialNumber, stopOrOpenName);
        	}else{
        		//接口返回失败
        		String message=result.getString("message", "");
        		String bizCode=result.getString("bizCode", "");
        		logger.debug("---OceanFrontFinishAction---海洋通报停或报开，调第三方接口反馈报错.message:"+message+",bizCode:"+bizCode);
        		System.out.println("---OceanFrontFinishAction---海洋通报停或报开，调第三方接口反馈报错.message:"+message+",bizCode:"+bizCode);
        	}
        }else{
        	logger.debug("---OceanFrontFinishAction---海洋通报停或报开，调第三方接口报错.");
        	System.out.println("---OceanFrontFinishAction---海洋通报停或报开，调第三方接口报错.");
        }
    }
    
	public void sendSms(String  serialNumber,String stopOrOpenName) throws Exception{
		
		//尊敬的客户，办理的：海洋通报停业务。 中国移动
		String msg = "尊敬的客户，办理的： "+stopOrOpenName+".中国移动";
		//发送短信通知
		IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", msg);
        inparam.put("RECV_OBJECT", serialNumber);
        inparam.put("RECV_ID", serialNumber);
        inparam.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("REFER_DEPART_ID",CSBizBean.getVisit().getDepartId());
        inparam.put("REMARK", "办理"+stopOrOpenName+",发送短信");
        SmsSend.insSms(inparam);
	}	
    
}
