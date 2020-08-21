package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 关于向校园营销活动期间开户校园卡客户发送防诈提醒短信
 * @author: wuwangfeng
 * @date: 2019-11-4
 */
public class SimCardRegSMSAction implements ITradeAction{
	
	private static Logger log = Logger.getLogger(SimCardRegSMSAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		try {
			String productId = ""; //用户开户主产品
			String userId = ""; //用户id
			String serialNumber = btd.getMainTradeData().getSerialNumber(); //手机号
			List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
			if (productTrade != null && productTrade.size()>0) {
				for (ProductTradeData product : productTrade) {
					if (BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag())) {
						productId = product.getProductId();
						userId = product.getUserId();
					}
				}
			}											
			log.debug("用户开户主产品-------------------->productId= "+productId);
			
			if (StringUtils.isBlank(productId)) {
				return;
			}
			
			IDataset commparaInfos = CommparaInfoQry.getCommparaByCode23("CSM", "1910", "0", productId);			
			if (IDataUtil.isEmpty(commparaInfos)) {
				return;
	        }
			log.debug("commparaInfos.size()-------------------->"+commparaInfos.size());
			
			String paraCode23 = "";
	        for (int i = 0; i < commparaInfos.size(); i++) {
	        	//获取短信内容
	        	paraCode23 = commparaInfos.getData(i).getString("PARA_CODE23");
	        	log.debug("paraCode23---------短信内容---------->"+paraCode23);
	        	
	        	//发送短信
	        	IData smsData = new DataMap();
	        	smsData.clear();
        	
                smsData.put("TRADE_ID", btd.getTradeId());
                smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                smsData.put("SMS_PRIORITY", "5000");
                smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
                smsData.put("NOTICE_CONTENT_TYPE", "0");
                smsData.put("SMS_TYPE_CODE", "I0");
	        	smsData.put("RECV_ID", userId);	        	
	        	smsData.put("REMARK", "关于向校园营销活动期间开户校园卡客户发送防诈提醒短信");
	        	
	        	smsData.put("RECV_OBJECT", serialNumber);
	        	smsData.put("NOTICE_CONTENT", paraCode23);	        	
	        	SmsSend.insSms(smsData);
			}
       
		} catch (Exception e) {
			log.error("关于向校园营销活动期间开户校园卡客户发送防诈提醒短信--异常: "+e.getMessage());
		}
	}

}
