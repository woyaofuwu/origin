package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TriggerSMSAction implements ITradeFinishAction
{
	private static final Logger logger = Logger.getLogger(TriggerSMSAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		IDataset productTrades = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
		if (IDataUtil.isNotEmpty(productTrades))
		{
			for (int i=0;i<productTrades.size();i++)
			{
				IData product = productTrades.getData(i);
				String productId = product.getString("PRODUCT_ID", "");
				String modifyTag = product.getString("MODIFY_TAG", "");

				if ("0".equals(modifyTag))
				{
					sendSMS(productId, serialNumber, userId, eparchyCode);
				}
			}
		}


	}


	private void sendSMS(String productId, String serialNumber, String userId, String eparchyCode) throws Exception {
		logger.debug("触点短信-办理套餐：" + productId);
		//触点短信
		IDataset proParas = CommparaInfoQry.getCommpara("CSM", "1301", productId, eparchyCode);
		logger.debug("触点短信-配置：" + proParas);

		if(IDataUtil.isEmpty(proParas)) {
			return;
		}
		String isSend = proParas.getData(0).getString("PARA_CODE6");
		String templateId = proParas.getData(0).getString("PARA_CODE7");
		logger.debug("触点短信-发送短信：" + isSend);
		if(StringUtils.equals("1", isSend)) {
			IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
			String smsContent = templateInfo.getString("TEMPLATE_CONTENT1", "");
			// 注意：短信模板的占位名需要和iData里的key保持一致
			String regex = "@\\{"+"PRODUCT_NAME"+"\\}";
			smsContent = smsContent.replaceAll(regex, UProductInfoQry.getProductNameByProductId(productId));
			if(!smsContent.equals("")){
				IData data = new DataMap();
				data.put("RECV_OBJECT", serialNumber);
				data.put("NOTICE_CONTENT", smsContent);
				data.put("BRAND_CODE", "");
				data.put("RECV_ID", userId);
				data.put("REMARK", "触点短信");
				SmsSend.insSms(data);
			}
		}
	}
}
