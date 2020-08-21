
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.action;

import java.util.List;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
public class ChangeWidenetStopSaleactAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	IData pageData=btd.getRD().getPageRequestData();
    	String KDTS_PRODUCT =pageData.getString("KDTS_PRODUCT","");
    	
    	String serialNum =  btd.getRD().getUca().getSerialNumber();
    	
    	if(StringUtils.isNotEmpty(KDTS_PRODUCT)){
    		
    		String[] arrs=KDTS_PRODUCT.split("\\|");
    		
    		if(arrs.length>0){
    			
    			if(serialNum.contains("KD_")){
    				serialNum=serialNum.replace("KD_", "");
    			}
    			
    			String productId = arrs[0];
    			String packgeId = arrs[1];
    			String relationTradeId = arrs[2];
    			String cancelDate =  SysDateMgr.getSysTime();
    			IData cancelParam = new DataMap(); 
    			cancelParam.put("SERIAL_NUMBER", serialNum); 
    			cancelParam.put("PRODUCT_ID", productId); 
    			cancelParam.put("PACKAGE_ID", packgeId); 
    			cancelParam.put("RELATION_TRADE_ID", relationTradeId); 
    			cancelParam.put("REMARK", "宽带畅享服务特权活动需求提速终止"); 
    			cancelParam.put("NO_TRADE_LIMIT", "TRUE"); 
    			cancelParam.put("SKIP_RULE", "TRUE"); 
    			cancelParam.put("FORCE_END_DATE", cancelDate); 
    			CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam); 
    			//追加活动的受理单提示
    			btd.getMainTradeData().setRemark(btd.getMainTradeData().getRemark()+"KDTS_RATE");
    			this.SendSMS(btd);
    		}
    		
    	}
    	
    }
    
    private void SendSMS(BusiTradeData btd) throws Exception
    {
        SmsTradeData std = new SmsTradeData();
        String endDate = SysDateMgr.getSysTime();
        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        std.setBrandCode(btd.getRD().getUca().getBrandCode());
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");

        std.setRecvId(btd.getRD().getUca().getUserId());
        std.setSmsTypeCode("20");
        std.setSmsKindCode("02");
        std.setNoticeContentType("0");
        std.setReferedCount("0");
        std.setForceReferCount("1");
        std.setForceObject("");
        std.setForceStartTime("");
        std.setForceEndTime("");
        std.setSmsPriority("50");
        std.setReferTime(SysDateMgr.getSysTime());
        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        std.setDealTime(SysDateMgr.getSysTime());
        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark("宽带畅享服务特权活动需求提速终止");
        std.setRevc1("");
        std.setRevc2("");
        std.setRevc3("");
        std.setRevc4("");
        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        std.setCancelTag("0");
        // 短信截取
        String strContent = "您于"
        		+ endDate
				+ "取消中国移动20周年--家庭宽带畅享提速活动。";
        
        String serialNum =  btd.getRD().getUca().getSerialNumber();
    	if(serialNum.contains("KD_")){
			serialNum=serialNum.replace("KD_", "");
		}
			
        std.setNoticeContent(strContent);

        std.setRecvObject(serialNum);// 发送号码
        
        btd.add(serialNum, std);
    }

}
