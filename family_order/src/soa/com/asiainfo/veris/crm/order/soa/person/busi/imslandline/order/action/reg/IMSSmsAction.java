package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class IMSSmsAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
        List<MainTradeData> MainTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
        String rsrvStr7 = "";
        if (null != MainTradeDatas)
        {
            rsrvStr7 = MainTradeDatas.get(0).getRsrvStr7(); 
        }
        
        if (relationTradeDatas.size() > 0)
        {
            String strContent1 = "";
            String forceObject1 = "";
            String OPEN_IMS = "";
            IData pageParam=btd.getRD().getPageRequestData();
            if(IDataUtil.isNotEmpty(pageParam))
            {
            	OPEN_IMS=pageParam.getString("OPEN_IMS","");
            }
            
            for(int i=0;i<relationTradeDatas.size();i++)
            {
            	RelationTradeData relationTradeData = relationTradeDatas.get(i);
            	if("1".equals(relationTradeData.getRoleCodeB()) && "MS".equals(relationTradeData.getRelationTypeCode()))
            	{
            		String serialNumber = relationTradeData.getSerialNumberB();
            		//如果是杭研IMS软终端办理的开户，短息修改
            		if(null != OPEN_IMS && !"".equals(OPEN_IMS.trim()))
                	{
                		if(OPEN_IMS.equals("OPEN_IMS"))
                		{
                			//strContent1 = "系统已受理您的和家固话开户申请。";
                			strContent1 = "尊敬的客户，您已开通和家固话通话功能，立即生效，月租9元，包含120分钟长市合一通话分钟数（不含港澳台地区及国际长途）和来电显示功能超出套餐通话0.1元/分钟，港澳台及国际长途资费同手机资费。由办理的手机号码代付费，每月一次性扣费。";
                		}
                	}else if(null != rsrvStr7 && !"".equals(rsrvStr7.trim())){
                		if(rsrvStr7.equals("OPEN_IMS"))
                		{
                			strContent1 = "尊敬的客户，您已开通和家固话通话功能，立即生效，月租9元，包含120分钟长市合一通话分钟数（不含港澳台地区及国际长途）和来电显示功能超出套餐通话0.1元/分钟，港澳台及国际长途资费同手机资费。由办理的手机号码代付费，每月一次性扣费。";
                		}
                	}else{
                		strContent1 = "系统已受理您的家庭IMS固话开户申请，后续工作人员将与您联系装机事宜。";
                		
                		
                		//start  《REQ201911190001 关于IMS固话开户界面受理单和成功短信的优化》 新增下发短信  by chenyw7
                		String productId = MainTradeDatas.get(0).getProductId();
                		IDataset smsTempLateList=CommparaInfoQry.getCommparaInfos("CSM", "6900", productId);//获取短信内容
        	    		if(smsTempLateList != null && smsTempLateList.size() > 0){
        	    			IData smsTempLate=smsTempLateList.getData(0);
        	    			String content = smsTempLate.getString("PARA_CODE20","");
        	    			
        	    			forceObject1 = "10086235" + serialNumber;
                            IData smsData1 = new DataMap();
                            smsData1.put("TRADE_ID", btd.getTradeId());
                            smsData1.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                            smsData1.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                            smsData1.put("SMS_PRIORITY", "5000");
                            smsData1.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
                            smsData1.put("REMARK", "业务短信通知");
                            smsData1.put("NOTICE_CONTENT_TYPE", "0");
                            smsData1.put("SMS_TYPE_CODE", "I0");
                            smsData1.put("RECV_OBJECT", serialNumber);
                            smsData1.put("RECV_ID", btd.getRD().getUca().getUserId());
                            smsData1.put("FORCE_OBJECT", forceObject1);
                            smsData1.put("NOTICE_CONTENT", content);

                            SmsSend.insSms(smsData1);
        	    		}
                		
                      //end  《REQ201911190001 关于IMS固话开户界面受理单和成功短信的优化》 新增下发短信  by chenyw7
                	}
                    
            		
            		forceObject1 = "10086235" + serialNumber;
                    IData smsData = new DataMap();
                    smsData.put("TRADE_ID", btd.getTradeId());
                    smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                    smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                    smsData.put("SMS_PRIORITY", "5000");
                    smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
                    smsData.put("REMARK", "业务短信通知");
                    smsData.put("NOTICE_CONTENT_TYPE", "0");
                    smsData.put("SMS_TYPE_CODE", "I0");

                    smsData.put("RECV_OBJECT", serialNumber);
                    smsData.put("RECV_ID", btd.getRD().getUca().getUserId());
                    smsData.put("FORCE_OBJECT", forceObject1);
                    smsData.put("NOTICE_CONTENT", strContent1);

                    SmsSend.insSms(smsData);
            	}           	
            }  
        }
    }

}
