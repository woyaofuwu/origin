
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plugin.orderspecdeal;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.WidenetConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.plugin.IPlugin;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class PfOrderCollectionPlugin implements IPlugin {

	public void deal(List<BusiTradeData> btds, IData input) throws Exception {
		if (btds != null && btds.size() > 0) {
			String[] tradeTypes = BizEnv.getEnvStringArray(BofConst.PF_COMPLAIN_VISUALIZATION_TYPE_ID);
			for (BusiTradeData btd : btds) {
				if (isCollectionOrder(tradeTypes, btd.getTradeTypeCode())) {
					notificationPfOrderCreate(btd, input);
				}
			}
		}
	}

	/**
	 * 调用开通中心接口
	 * 
	 * @param btd
	 * @throws Exception
	 */
	private void notificationPfOrderCreate(BusiTradeData btd, IData input) throws Exception {

		String tradeTypeCode = btd.getTradeTypeCode();
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", btd.getRD().getOrderId());
		inParam.put("TRADE_ID", btd.getTradeId());
		inParam.put("SO_CO_SERIL", btd.getTradeId());

		inParam.put("EPARCHY_CODE", btd.getRoute());
		inParam.put("SO_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工ID
		inParam.put("SO_STAFF_CODE", CSBizBean.getVisit().getStaffId());// 受理员工工号
		inParam.put("SO_STAFF_CODE", CSBizBean.getVisit().getStaffName());// 受理员工姓名
		inParam.put("SO_APPLY_ORG_ID", CSBizBean.getVisit().getDepartId());// 所属组织ID
		inParam.put("SO_OWNER_ORG_ID", CSBizBean.getVisit().getDepartId());// 所属组织ID

		String areaCode = CSBizBean.getVisit().getCityCode();
		inParam.put("DISTRICT_ID", areaCode);// 行政区
		String districtName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME",
				areaCode);
		inParam.put("DISTRICT_NAME", districtName);// 行政区名称

		String inModeCode = btd.getMainTradeData().getInModeCode();
		inParam.put("CHANNEL_CODE", inModeCode);
		String inMode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE",
				inModeCode);
		inParam.put("CHANNEL_NAME", inMode);

		inParam.put("USER_ID", btd.getRD().getUca().getUserId());
		inParam.put("TRADE_TYPE_CODE", tradeTypeCode);

		String payModeCode = input.getString("X_PAY_MONEY_CODE", "0");
		inParam.put("PAY_TYPE_CODE", input.getString("X_PAY_MONEY_CODE", "0"));
		inParam.put("PAY_TYPE_NAME", StaticUtil.getStaticValue("TD_S_PAYMODE", payModeCode));

		inParam.put("PRIORITY", btd.getMainTradeData().getPriority());
		inParam.put("REMARKS", btd.getMainTradeData().getRemark());

		inParam.put("CUST_TYPE_ID", "0");
		String value = getVipLevel(serialNumber);
		inParam.put("CUST_LEVEL_ID", value);

		inParam.put("ORG_ID", CSBizBean.getVisit().getDepartId());
		inParam.put("ORG_NAME", CSBizBean.getVisit().getDepartName());
		inParam.put("CUSTOMER_NAME", btd.getRD().getUca().getCustomer().getCustName());
		inParam.put("SO_CREATE_DATE", btd.getRD().getAcceptTime());

		String accessNum = serialNumber.startsWith("KD_") ? StringUtils.substring(serialNumber, 3) : serialNumber;
		inParam.put("ACCESS_NUM", accessNum);
		inParam.put("OLD_ACCESS_NUM", accessNum);

		if ((StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_WIDENET_OPEN)
				|| StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_NO_PHONE_WIDENET_OPEN))
				|| StringUtils.equals(btd.getMainTradeData().getRsrvStr1(), "A")) {
			inParam.put("FEE_FLAG", "2");
			List<WideNetTradeData> widenetTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
			if (widenetTradeDatas != null && widenetTradeDatas.size() > 0) {
				inParam.put("BOOKING_START_DATE", widenetTradeDatas.get(0).getSuggestDate());
			}
		} else {
			inParam.put("FEE_FLAG", "1");// 默认填1，如果需要后端收费可以为2，并在备注信息中给与描述
		}

		IData widenetInfo = getWidenetInfo(btd);
		if (IDataUtil.isNotEmpty(widenetInfo)) {
			inParam.putAll(widenetInfo);
		}

		IData product = getProductInfo(btd);
		inParam.put("IS_MAIN_PROD", BofConst.PF_COMPLAIN_VISUALIZATION_IS_MAIN_PROD);

		if (IDataUtil.isNotEmpty(product)) {
			inParam.putAll(product);
		}
		try {
			CSAppCall.callNGPf("PF_MON_ORDER_CREATE", inParam);
		} catch (Exception e) {

		}
	}

	private String getVipLevel(String serialNumber) throws Exception {
		IDataset custvipInfos = CustVipInfoQry.qryVipInfoBySn(serialNumber);
		if (IDataUtil.isNotEmpty(custvipInfos)) {
			IData vipInfo = custvipInfos.getData(0);
			return vipInfo.getString("VIP_CLASS_ID", ""); // 客户级别
		}
		return null;
	}

	private IData getWidenetInfo(BusiTradeData btd) throws Exception {
		IData widenetInfo = new DataMap();
		String tradeTypeCode = btd.getTradeTypeCode();
		if (StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_WIDENET_OPEN)
				|| StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_NO_PHONE_WIDENET_OPEN)
				|| StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_IMS_OPEN)
				|| StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_WIDENET_MOVE)
				|| StringUtils.equals(tradeTypeCode, WidenetConstants.TRADE_TYPE_CODE_NO_PHONE_WIDENET_MOVE)) {
			List<WideNetTradeData> widenetTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
			for (WideNetTradeData wideNetTradeData : widenetTradeDatas) {
				if (BofConst.MODIFY_TAG_ADD.equals(wideNetTradeData.getModifyTag())) {
					widenetInfo.put("INSTALL_ADDR", wideNetTradeData.getDetailAddress());
					widenetInfo.put("STANDARD_ADDR_ID", wideNetTradeData.getStandAddressCode());
					widenetInfo.put("STANDARD_ADDR_NAME", wideNetTradeData.getStandAddress());
					widenetInfo.put("TEL_PERSON", wideNetTradeData.getContact());
					widenetInfo.put("TEL", wideNetTradeData.getContactPhone());
				} else {
					widenetInfo.put("OLD_INSTALL_ADDR", wideNetTradeData.getDetailAddress());
					widenetInfo.put("OLD_STANDARD_ADDR_ID", wideNetTradeData.getStandAddressCode());
					widenetInfo.put("OLD_STANDARD_ADDR_NAME", wideNetTradeData.getStandAddress());
					widenetInfo.put("OLD_TEL_PERSON", wideNetTradeData.getContact());
					widenetInfo.put("OLD_TEL", wideNetTradeData.getContactPhone());
				}
			}
		} else {
			if (isWidenet(btd) || isIms(btd)) {
				IDataset widenets = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());
				if (IDataUtil.isNotEmpty(widenets)) {
					IData wideNet = widenets.getData(0);
					widenetInfo.put("INSTALL_ADDR", wideNet.getString("DETAIL_ADDRESS"));
					widenetInfo.put("STANDARD_ADDR_ID", wideNet.getString("STAND_ADDRESS_CODE"));
					widenetInfo.put("STANDARD_ADDR_NAME", wideNet.getString("STAND_ADDRESS"));
					widenetInfo.put("TEL_PERSON", wideNet.getString("CONTACT"));
					widenetInfo.put("TEL", wideNet.getString("CONTACT_PHONE"));
				}
			}
		}
		return widenetInfo;
	}

	private IData getProductInfo(BusiTradeData btd) throws Exception {
		IData productData = new DataMap();
		ProductTradeData productTd = btd.getRD().getUca().getUserMainProduct();
		String productId = null;
		String instId = null;
		String modifyTag = null;

		ProductTradeData nextProductTd = btd.getRD().getUca().getUserNextMainProduct();
		if (nextProductTd != null) {
			productId = nextProductTd.getProductId();
			instId = nextProductTd.getInstId();
			modifyTag = nextProductTd.getModifyTag();
			productData.put("OLD_PROD_CODE", productTd.getProductId());
		} else {
			productId = productTd.getProductId();
			instId = productTd.getInstId();
			modifyTag = productTd.getModifyTag();
		}
		String productName = UpcCall.qryOfferNameByOfferTypeOfferCode(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		productData.put("OFFER_ID", productId);
		productData.put("OFFER_NAME", productName);
		productData.put("PROD_CODE", productId);
		productData.put("PROD_NAME", productName);
		productData.put("PROD_INST_ID", instId);
		productData.put("MAS_PROD_INS_ID", instId);
		IDataset comchas = UpcCall.queryOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId,
				KeyConstants.NET_TYPE_CODE);
		if (IDataUtil.isNotEmpty(comchas)) {
			String value = comchas.first().getString("FIELD_VALUE");
			productData.put(KeyConstants.NET_TYPE_CODE, value);
		}

		productData.put("PROD_ACT_ID", modifyTag);
		if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {
			productData.put("PROD_ACT_NAME", "新增产品");
		} else if (BofConst.MODIFY_TAG_USER.equals(modifyTag)) {
			productData.put("PROD_ACT_NAME", "用户产品");
		} else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag)) {
			productData.put("PROD_ACT_NAME", "退订产品");
		}

		if (isWidenet(btd)) {
			String rate = WideNetUtil.getWidenetProductRate(productId);
			productData.put("BAND_WIDTH", rate);
		}

		return productData;
	}

	private boolean isIms(BusiTradeData btd) throws Exception {
		String brandCode = btd.getMainTradeData().getBrandCode();
		String sn = btd.getRD().getUca().getSerialNumber();
		if (!sn.startsWith("KD_") && StringUtils.equals(brandCode, WidenetConstants.IMS_BRAND_CODE)) {
			return true;
		}
		return false;
	}

	private boolean isWidenet(BusiTradeData btd) throws Exception {
		String brandCode = btd.getMainTradeData().getBrandCode();
		String sn = btd.getRD().getUca().getSerialNumber();
		if (sn.startsWith("KD_") && (StringUtils.equals(brandCode, WidenetConstants.BRAND_COCE)
				|| StringUtils.equals(brandCode, WidenetConstants.BRAND_CODE_GROUP_BUSINESS))) {
			return true;
		}
		return false;
	}

	private boolean isCollectionOrder(String[] tradeTypes, String tradeTypeCode) {
		for (String tradeTypec : tradeTypes) {
			if (StringUtils.equals(tradeTypec, tradeTypeCode)) {
				return true;
			}
		}
		return false;
	}
}
