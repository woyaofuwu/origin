package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;

public class TradeFinishCallMasAction implements ITradeFinishAction {

	private static final Logger logger = Logger
			.getLogger(TradeFinishCallMasAction.class);

	public void executeAction(IData mainTrade) throws Exception {
		String tradeId = mainTrade.getString("TRADE_ID");
		String userid = mainTrade.getString("USER_ID");
		String grpUserId = mainTrade.getString("USER_ID_B");
		String productId = mainTrade.getString("PRODUCT_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String custId = mainTrade.getString("CUST_ID");
		String acctId = mainTrade.getString("ACCT_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String operState = "01";
		if ("4690".equals(tradeTypeCode) || "4691".equals(tradeTypeCode)|| "4693".equals(tradeTypeCode)) 
		{
			IDataset merchDataset = ProductCompRelaInfoQry.queryProductRealByProductB(productId);
			if (IDataUtil.isNotEmpty(merchDataset)) 
			{
				String merchProductId = merchDataset.getData(0).getString("PRODUCT_ID_A");// 获取商品id
				if (("9975".equals(merchProductId) && !"997501".equals(productId))
						|| ("9976".equals(merchProductId) && !"997607".equals(productId)))// 9975长流程,9976短流程
				{
					if ("4690".equals(tradeTypeCode)) 
					{
						operState = "01";

					} 
					else if ("4691".equals(tradeTypeCode)) 
					{
						operState = "08";

					} else if ("4693".equals(tradeTypeCode)) 
					{
						operState = "02";

					}
					// 完工流程中先执行的订单归档，所以当新增和变更时可以直接查询user_attr表有效的数据，注销时查询trade_attr表的数据
					IDataset AttrTrades = null;
					if ("4690".equals(tradeTypeCode)|| "4691".equals(tradeTypeCode)) 
					{
						AttrTrades = UserAttrInfoQry.getUserAttrByUserId(userid);
					} 
					else if ("4693".equals(tradeTypeCode)) 
					{
						AttrTrades = TradeAttrInfoQry.getTradeAttrByTradeId(tradeId);
					}
					logger.error("ceshi1" + AttrTrades);
					if (IDataUtil.isEmpty(AttrTrades))
					{
						return;
					}
						
					IData productParamInfos = new DataMap();
					for (int i = 0; i < AttrTrades.size(); i++)
					{
						String attrGroup = AttrTrades.getData(i).getString(	"RSRV_STR4", "");
						String attrCode = AttrTrades.getData(i).getString("ATTR_CODE", "");
						String attrValue = AttrTrades.getData(i).getString("ATTR_VALUE", "");
						if (!"".equals(attrGroup))// 代表是属性组
						{
							productParamInfos.put(attrCode + attrGroup,	attrValue);

						} 
						else 
						{
							productParamInfos.put(attrCode, attrValue);
						}
					}
					logger.error("ceshi2" + productParamInfos);

					IDataset GrpMerchpTrades = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(tradeId);
					if (IDataUtil.isNotEmpty(GrpMerchpTrades))
					{
						String product_order_id = GrpMerchpTrades.getData(0).getString("PRODUCT_ORDER_ID");
						if (!product_order_id.startsWith("898")) 
						{
							return;
						}

					} 
					else 
					{
						return;
					}
					String group_id = GrpMerchpTrades.getData(0).getString(	"GROUP_ID");
					String product_spec_code = GrpMerchpTrades.getData(0).getString("PRODUCT_SPEC_CODE");
					IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState,product_spec_code, productParamInfos,	serialNumber);
					grpPlatSVCData.put("CUST_ID", custId);
					grpPlatSVCData.put("ACCT_ID", acctId);
					grpPlatSVCData.put("TRADE_STAFF_ID", "IBOSS000");
					grpPlatSVCData.put("TRADE_DEPART_ID", "00309");
					grpPlatSVCData.put("TRADE_CITY_CODE", "INTF");
					grpPlatSVCData.put("TRADE_EPARCHY_CODE", "INTF");
					grpPlatSVCData.put("IN_MODE_CODE", "6");
					grpPlatSVCData.put("ROUTE_EPARCH_CODE", "0898");

					CSAppCall.call("SS.YunmasynSVC.crtTrade", grpPlatSVCData);
				}
			}
		} 
		else if ("4694".equals(tradeTypeCode) || "4697".equals(tradeTypeCode)) 
		{
			if("4694".equals(tradeTypeCode)){
				operState = "01";
			}else{
				operState = "02";
			}
			IDataset mebDataset = ProductMebInfoQry.getProductMebByPidB(productId);
			if (IDataUtil.isNotEmpty(mebDataset)) 
			{
				String productMeb = mebDataset.getData(0).getString("PRODUCT_ID");
				IDataset merchDataset = ProductCompRelaInfoQry.queryProductRealByProductB(productMeb);
				if (IDataUtil.isNotEmpty(merchDataset)) 
				{
					String merchProductId = merchDataset.getData(0).getString("PRODUCT_ID_A");// 获取商品id
					if (("9975".equals(merchProductId) && !"997501".equals(productMeb))
							|| ("9976".equals(merchProductId) && !"997607".equals(productMeb)))// 9975长流程,9976短流程
					{
//						IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(userid,"81", Route.CONN_CRM_CG);
//						if (IDataUtil.isEmpty(relaBBInfoList)) 
//						{
//							CSAppException.apperr(ProductException.CRM_PRODUCT_215);
//						}
//						grpUserId = relaBBInfoList.getData(0).getString("USER_ID_A");
						IDataset GrpMerchpUsers = UserGrpMerchpInfoQry.qryMerchpInfoByUserId(grpUserId);
						if (IDataUtil.isNotEmpty(GrpMerchpUsers)) 
						{
							String product_order_id = GrpMerchpUsers.getData(0).getString("PRODUCT_ORDER_ID");
							if (!product_order_id.startsWith("898"))
							{
								return;
							}

						}

						IData blackWhiteData = BbossIAGWCloudMASDealBean.makDataForBlackWhite(userid, grpUserId,productMeb, serialNumber, operState);
						blackWhiteData.put("TRADE_STAFF_ID", "IBOSS000");
						blackWhiteData.put("TRADE_DEPART_ID", "00309");
						blackWhiteData.put("TRADE_CITY_CODE", "INTF");
						blackWhiteData.put("TRADE_EPARCHY_CODE", "INTF");
						blackWhiteData.put("IN_MODE_CODE", "6");
						blackWhiteData.put("ROUTE_EPARCH_CODE", "0898");
						
						CSBizBean.getVisit().setInModeCode("6");
						CSAppCall.call("SS.YunmasynMebSVC.crtTrade",blackWhiteData);

					}

				}

			}

		}
	}

}
