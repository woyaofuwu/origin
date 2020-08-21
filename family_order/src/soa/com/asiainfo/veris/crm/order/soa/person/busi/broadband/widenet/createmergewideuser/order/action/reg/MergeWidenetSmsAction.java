
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class MergeWidenetSmsAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<WideNetTradeData> widenetTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
        List<OtherTradeData> otherTrades = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);

        if (widenetTradeDatas.size() > 0)
        {
            String productName = "";
            String strContent1 = "";
            String strContent2 = "";
            String forceObject1 = "";
            String forceObject2 = "";
            String acceptTime = btd.getRD().getAcceptTime();
            String payMode = btd.getMainTradeData().getRsrvStr1();
            productName = UProductInfoQry.getProductNameByProductId(btd.getRD().getUca().getProductId());
            
            if (StringUtils.isNotBlank(productName))
            {
               String[] productNames = productName.split("\\(");
               
               productName = productNames[0];
            }
            
            String wideType = "";
            
            if (!"630".equals(btd.getTradeTypeCode()))
            {
                wideType = ((MergeWideUserCreateRequestData)btd.getRD()).getWideType();
            }
            
            //1:'GPON宽带',2:'铁通ADSL',3:'移动FTTH',5:'铁通FTTH',6:'铁通FTTB'
            if("3".equals(wideType) || "5".equals(wideType))
            {
                productName.replaceAll("FTTH", "光纤");
            }

            WideNetTradeData widenetTradeData = widenetTradeDatas.get(0);
            String contactPhone = widenetTradeData.getContactPhone();
            String serialNumber = btd.getRD().getUca().getSerialNumber().substring(3);
            String strTime = acceptTime.substring(0, 4) + "年" + acceptTime.substring(5, 7) + "月" + acceptTime.substring(8, 10) + "日 " + acceptTime.substring(11, 13) + ":" + acceptTime.substring(14, 16);

            //REQ202005050005 开发智能家居有一套营销活动—BOSS侧
            String otherContents=getOtherContents(otherTrades);
            if (contactPhone.equals(serialNumber))
            {
                strContent1 = "尊敬的客户，您好！您于" + strTime + "申请办理中国移动" + productName + "宽带业务，"+otherContents+ "系统已经受理。我们的工作人员将在48小时内会为您提供上门安装、调测宽带服务。如您需要延后安装时间，请回复“**年**月**日”，我们将尽量按您要求的日期提供上门安装服务。如您在宽带业务使用过程中需要帮忙请拨打24小时服务热线10086！";
                forceObject1 = "10086235" + serialNumber;
            }
            else
            {
                IData msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumberAsp(contactPhone, "1");
                
                if (IDataUtil.isNotEmpty(msisdnInfo))
                {
                    strContent1 = "尊敬的客户，您好！您于" + strTime + "申请办理中国移动" + productName + "宽带业务，"+otherContents+ "系统已经受理。我们的工作人员将与" + contactPhone + "联系提供上门安装、调测宽带服务。如您在宽带业务使用过程中需要帮忙请拨打24小时服务热线10086！";
                    strContent2 = "尊敬的客户，您好！" + serialNumber + "于" + strTime + "申请办理中国移动" + productName + "宽带业务，"+otherContents+"委托您为上门安装工作联系人。我们的工作人员将在48小时内与您联系提供上门安装、调测宽带服务。如您需要延后安装时间，请回复“**年**月**日”，我们将尽量按您要求的日期提供上门安装服务。";
                    forceObject2 = "10086235" + serialNumber;
                }
                else
                {
                    strContent1 = "尊敬的客户，您好！您于" + strTime + "申请办理中国移动" + productName + "宽带业务，" +otherContents+ "系统已经受理。我们的工作人员将在48小时内会为您提供上门安装、调测宽带服务。如您需要延后安装时间，请回复“**年**月**日”，我们将尽量按您要求的日期提供上门安装服务。如您在宽带业务使用过程中需要帮忙请拨打24小时服务热线10086！";
                    forceObject1 = "10086235" + serialNumber;
                }
            }
            
            IData smsData = new DataMap();
            smsData.put("TRADE_ID", btd.getTradeId());
            smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            smsData.put("SMS_PRIORITY", "5000");
            smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
            smsData.put("REMARK", "业务短信通知");
            smsData.put("NOTICE_CONTENT_TYPE", "0");
            smsData.put("SMS_TYPE_CODE", "I0");

            if (StringUtils.isNotBlank(strContent1))
            {

                smsData.put("RECV_OBJECT", serialNumber);
                smsData.put("RECV_ID", btd.getRD().getUca().getUserId());
                smsData.put("FORCE_OBJECT", forceObject1);
                smsData.put("NOTICE_CONTENT", strContent1);

                SmsSend.insSms(smsData);
            }
            
            if (StringUtils.isNotBlank(strContent2))
            {
                IData smsData1 = new DataMap(smsData);
                smsData1.put("RECV_OBJECT", contactPhone);
                smsData1.put("RECV_ID", "0");
                smsData1.put("FORCE_OBJECT", forceObject2);
                smsData1.put("NOTICE_CONTENT", strContent2);
                SmsSend.insSms(smsData1);
            }
            if (StringUtils.isNotBlank(payMode) && "A".equals(payMode))
            {
            	IData smsData2 = new DataMap(smsData);
            	smsData2.put("RECV_OBJECT", serialNumber);
                smsData2.put("RECV_ID", btd.getRD().getUca().getUserId());
                smsData2.put("FORCE_OBJECT", "10086235" + serialNumber);
                smsData2.put("NOTICE_CONTENT", "尊敬的客户，您于"+strTime+"办理中国移动光宽带“先装后付”模式，将在完成装机后通过话费扣取费用，请确保您的手机话费余额充足。【中国移动】");

                SmsSend.insSms(smsData2);
            }
            
          //start  《REQ201911190001 关于IMS固话开户界面受理单和成功短信的优化》 新增下发短信  by chenyw7
            if (otherTrades.size() > 0)
            {
            	for (int i = 0; i < otherTrades.size(); i++) {
    				OtherTradeData otherData = otherTrades.get(i);
    				
                    if ("IMSTRADE".equals(otherData.getRsrvValueCode())){
                    	
                    	String productId = otherData.getRsrvStr4();
        	    		IDataset smsTempLateList=CommparaInfoQry.getCommparaInfos("CSM", "6900", productId);//获取短信内容
        	    		if(smsTempLateList != null && smsTempLateList.size() > 0){
        	    			IData smsTempLate=smsTempLateList.getData(0);
        	    			String content = smsTempLate.getString("PARA_CODE20","");
        	    			
        	    			forceObject1 = "10086235" + serialNumber;
            	            IData smsData3 = new DataMap();
            	            smsData3.put("TRADE_ID", btd.getTradeId());
            	            smsData3.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            	            smsData3.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            	            smsData3.put("SMS_PRIORITY", "5000");
            	            smsData3.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
            	            smsData3.put("REMARK", "业务短信通知");
            	            smsData3.put("NOTICE_CONTENT_TYPE", "0");
            	            smsData3.put("SMS_TYPE_CODE", "I0");
            	            smsData3.put("RECV_OBJECT", serialNumber);
            	            smsData3.put("RECV_ID", btd.getRD().getUca().getUserId());
            	            smsData3.put("FORCE_OBJECT", forceObject1);
            	            smsData3.put("NOTICE_CONTENT", content);
            	            SmsSend.insSms(smsData3);
        	    		}
                    }
    				
            	}
            }
            //end  《REQ201911190001 关于IMS固话开户界面受理单和成功短信的优化》 新增下发短信  by chenyw7
        }
    }

    /**
     * REQ202005050005 开发智能家居有一套营销活动—BOSS侧
     * @param otherTrades
     * @return
     * @throws Exception
     */
    private String getOtherContents(List<OtherTradeData> otherTrades) throws Exception {
        String contents="";
        String bindPlatsvcContent="";
        String topSetPlatsvc="";
        if(otherTrades!=null&&otherTrades.size()>0){
            for (int i = 0; i < otherTrades.size(); i++) {
                OtherTradeData otherData = otherTrades.get(i);
                String modifyTag=otherData.getModifyTag();
                String rsrvValueCode = otherData.getRsrvValueCode();
                String serviceId = otherData.getRsrvStr3();
                String rsrvStr4 = otherData.getRsrvStr4();
                if (StringUtils.isNotBlank(serviceId)&&"0".equals(modifyTag)
                        &&"BIND_PLATSVC".equals(rsrvValueCode)&&"Y".equals(rsrvStr4)){
                    IDataset commparaInfos2509 = CommparaInfoQry.getCommparaByCode4to6("CSM","2509","WIDENET",serviceId,null,null,"0898");
                    if(IDataUtil.isNotEmpty(commparaInfos2509)){
                        bindPlatsvcContent = commparaInfos2509.first().getString("PARA_CODE20","");
                    }
                }else if(StringUtils.isNotBlank(serviceId)&&"0".equals(modifyTag)
                        &&"TOPSET_PLATSVC".equals(rsrvValueCode)){
                    IDataset commparaInfos2509 = CommparaInfoQry.getCommparaByCode4to6("CSM","2509","TOPSETBOX",serviceId,null,null,"0898");
                    if(IDataUtil.isNotEmpty(commparaInfos2509)){
                        topSetPlatsvc=  commparaInfos2509.first().getString("PARA_CODE20","");
                    }
                }

            }
        }
        if(StringUtils.isNotBlank(bindPlatsvcContent)){
            contents+=bindPlatsvcContent+",";
        }
        if(StringUtils.isNotBlank(topSetPlatsvc)){
            contents+=topSetPlatsvc+",";
        }
        return contents;
    }
}
