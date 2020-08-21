
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophoneschoolcreatewideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class NoPhoneSchoolWidenetSmsAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<WideNetTradeData> widenetTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);

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
            
            if (!"650".equals(btd.getTradeTypeCode()))
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
            
            strContent1 = "尊敬的客户，您好！您于" + strTime + "申请办理中国移动" + productName + "宽带业务，" + "系统已经受理。我们的工作人员将在48小时内会为您提供上门安装、调测宽带服务。如您需要延后安装时间，请回复“**年**月**日”，我们将尽量按您要求的日期提供上门安装服务。如您在宽带业务使用过程中需要帮忙请拨打24小时服务热线10086！";
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

            
            IData smsData1 = new DataMap(smsData);
            smsData1.put("RECV_OBJECT", contactPhone);
            smsData1.put("RECV_ID", "0");
            smsData1.put("FORCE_OBJECT", forceObject1);
            smsData1.put("NOTICE_CONTENT", strContent1);
            SmsSend.insSms(smsData1);
        }
    }

}
