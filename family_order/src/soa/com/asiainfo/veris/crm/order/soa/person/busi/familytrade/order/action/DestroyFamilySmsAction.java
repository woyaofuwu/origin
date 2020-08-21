
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class DestroyFamilySmsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<RelationTradeData> uuList = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
        
        String strSms = "";
        String strSn = btd.getRD().getUca().getSerialNumber();
        String strUserid = btd.getRD().getUca().getUserId();
        //IDataset userDiscntList = UserDiscntInfoQry.getDiscntsByPMode(strUserid, "05");
        
        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);
		
		UcaData uca = UcaDataFactory.getUcaByUserId(strUserid);

		List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntArrays);
        
        
        
        if ( ArrayUtil.isNotEmpty(userDiscntList) ){
        	DiscntTradeData userDiscnt = userDiscntList.get(0);
            String discntCode = userDiscnt.getDiscntCode();
        	if( "3403".equals(discntCode) || "3404".equals(discntCode) ){
        		/*尊敬的客户，主号码XXXXXXXXXXX已解散亲亲网，亲亲网短号及优惠将于24小时内失效，
        		您可组建畅享亲亲网享受更多优惠或加入其他亲亲网继续享受优惠。咨询电话10086。*/
        		strSms = "尊敬的客户，主号码" + strSn + "已解散亲亲网，亲亲网短号及优惠将于24小时内失效，";
     		   	strSms+= "您可组建畅享亲亲网享受更多优惠或加入其他亲亲网继续享受优惠。咨询电话10086。";
        	}else if( "3410".equals(discntCode) || "3411".equals(discntCode) ){
        		/*尊敬的客户，主号码XXXXXXXXXXX已解散亲亲网，亲亲网短号及优惠将于24小时内失效，您可组建或加入其他亲亲网继续享受优惠。
            	主号码本月内有一次重新组建亲亲网机会，重新组建不重复收取月功能费。咨询电话10086。*/
        		strSms = "尊敬的客户，主号码" + strSn + "已解散亲亲网，亲亲网短号及优惠将于24小时内失效，您可组建或加入其他亲亲网继续享受优惠。";
     		   	strSms+= "主号码本月内有一次重新组建亲亲网机会，重新组建不重复收取月功能费。咨询电话10086。";
        	}
        	if( CollectionUtils.isNotEmpty(uuList) && !"".equals(strSms) ){
        		StringBuilder strContent = new StringBuilder();
                //strContent.append("尊敬的客户，您所在的亲亲网已取消。欢迎重新组建或加入亲亲网，享受亲亲网互拨短号便利及通话优惠，咨询电话10086。");
                strContent.append(strSms);

                IData smsData = new DataMap();
                smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
                smsData.put("NOTICE_CONTENT", strContent.toString());// 短信内容

                for (int i = 0, size = uuList.size(); i < size; i++)
                {
                    RelationTradeData uuInfo = uuList.get(i);
                    String serialNumberB = uuInfo.getSerialNumberB();
                    smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                    PerSmsAction.insTradeSMS(btd, smsData);
                }
            }
        }
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("OFFER_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }

}
