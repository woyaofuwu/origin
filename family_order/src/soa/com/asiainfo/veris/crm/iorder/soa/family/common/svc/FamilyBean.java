package com.asiainfo.veris.crm.iorder.soa.family.common.svc;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class FamilyBean extends CSBizBean {

	/**
	 * 设置元素的时间等字段
	 * 
	 * @author zhangby2
	 * @param elements
	 * @throws Exception
	 */
	public void dealOfferTime(IDataset elements, String type, String tradeTypeCode) throws Exception {
		int size = elements.size();
		ProductTimeEnv env = new ProductTimeEnv();
		if (tradeTypeCode.equals(FamilyConstants.FamilyTradeType.CHANGE)) {
			env.setBasicAbsoluteStartDate(SysDateMgr.getFirstDayOfNextMonth4WEB());
		}

		if (StringUtils.isBlank(DataBusManager.getDataBus().getAcceptTime())) {
			DataBusManager.getDataBus().setAcceptTime(SysDateMgr.getSysTime());
		}

		for (int i = 0; i < size; i++) {
			IData element = elements.getData(i);
			if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG"))) {
				continue;
			}
			if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(element.getString("ELEMENT_TYPE_CODE"))) {
				String startDate = null;
				if (FamilyConstants.TYPE_OLD.equals(type)) {
					startDate = SysDateMgr.getFirstDayOfNextMonth4WEB();
				} else {
					startDate = SysDateMgr.getSysDate();
				}
				element.put("START_DATE", startDate);
				element.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
				continue;
			}
			ProductModuleData pmd = null;
			if (StringUtils.equals(element.getString("ELEMENT_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_SVC)) {
				pmd = new SvcData(element);
			} else if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_PLATSVC, element.getString("ELEMENT_TYPE_CODE"))) {
				element.put("OPER_CODE", PlatConstants.OPER_ORDER);
				pmd = new PlatSvcData(element);
				pmd.setPkgElementConfig(element.getString("PACKAGE_ID"));
			} else {
				pmd = new DiscntData(element);
			}

			pmd.setStartDate(null);
			String startDate = ProductModuleCalDate.calStartDate(pmd, env);
			element.put("START_DATE", startDate);

			pmd.setEndDate(null);
			String endDate = ProductModuleCalDate.calEndDate(pmd, startDate);
			element.put("END_DATE", endDate);
			pmd = null;
		}
	}

	/**
	 * 处理Attr
	 * 
	 * @param elements
	 * @throws Exception
	 */
	public void dealOfferAttrs(IDataset elements) throws Exception {
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			IData element = elements.getData(i);
			if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG"))
					|| BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(element.getString("ELEMENT_TYPE_CODE"))) {
				continue;
			}
			IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"),
					element.getString("ELEMENT_ID"), CSBizBean.getUserEparchyCode());
			if (attrItemAList != null && attrItemAList.size() > 0) {
				int attrSize = attrItemAList.size();
				IDataset attrs = new DatasetList();
				for (int j = 0; j < attrSize; j++) {
					IData attr = new DataMap();
					IData itemA = attrItemAList.getData(j);
					attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
					attr.put("ATTR_VALUE", itemA.getString("ATTR_INIT_VALUE", ""));
					attrs.add(attr);
				}
				element.put("ATTR_PARAM", attrs);
			}
		}
	}

	/**
	 * 规则
	 * 
	 * @param input
	 * @param user
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	public IDataset checkRule(IData input, IDataset elements) throws Exception {
		IDataset result = new DatasetList();
		String eparchyCode = CSBizBean.getUserEparchyCode();
		String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "110");
		String roleType = IDataUtil.chkParam(input, KeyConstants.ROLE_TYPE);

		String sysTime = SysDateMgr.getSysTime();
		IData user = null;
		String userId = "-1";
		if (FamilyConstants.TYPE_OLD.equals(roleType)) {
			String sn = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			user = UcaInfoQry.qryUserInfoBySn(sn);
			if (IDataUtil.isEmpty(user)) {
				CSAppException.apperr(CrmUserException.CRM_USER_1);
			}
			userId = user.getString("USER_ID");
			IDataset productInfos = UserProductInfoQry.queryUserMainProduct(userId);
			if (IDataUtil.isNotEmpty(productInfos)) {
				int size = productInfos.size();
				for (int i = 0; i < size; i++) {
					IData userProduct = productInfos.getData(i);
					if (SysDateMgr.compareTo(userProduct.getString("START_DATE"), sysTime) < 0) {
						user.put("USER_PRODUCT_ID", userProduct.getString("PRODUCT_ID"));
					} else {
						user.put("NEXT_PRODUCT_ID", userProduct.getString("PRODUCT_ID"));
					}
				}
			} else {
				CSAppException.apperr(CrmUserException.CRM_USER_631);
			}
		}

		IDataset tradeMains = new DatasetList();
		IData tradeMain = new DataMap();
		tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
		tradeMain.put("TRADE_TYPE_CODE", tradeTypeCode);
		tradeMain.put("IN_MODE_CODE", "0");
		tradeMain.put("USER_ID", userId);
		tradeMains.add(tradeMain);
		IData ruleParam = new DataMap();
		ruleParam.put("IS_COMPONENT", "true");
		ruleParam.put("TF_B_TRADE", tradeMains);

		IDataset svcs = new DatasetList();
		IDataset discnts = new DatasetList();
		ruleParam.put("TF_B_TRADE_DISCNT", discnts);
		ruleParam.put("TF_B_TRADE_SVC", svcs);
		for (int i = 0; i < elements.size(); i++) {
			IData element = elements.getData(i);
			IData ruleElement = new DataMap();
			ruleElement.put("USER_ID_A", "-1");
			ruleElement.put("USER_ID", element.getString("USER_ID", userId));
			ruleElement.putAll(element);
			if (ruleElement.getString("INST_ID", "").equals("")) {
				ruleElement.put("INST_ID", "" + i);
			}
			if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE"))) {
				ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
				discnts.add(ruleElement);
			} else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))) {
				ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
				svcs.add(ruleElement);
			}
		}
		IDataset discntAfters = new DatasetList();
		if (IDataUtil.isNotEmpty(discnts)) {
			discntAfters.addAll(discnts);
		}

		IDataset svcAfters = new DatasetList();
		if (IDataUtil.isNotEmpty(svcs)) {
			svcAfters.addAll(svcs);
		}
		if ("OLD".equals(roleType)) {
			IData newProduct = getNewProductByElements(elements);
			String userProductId = "";
			if (IDataUtil.isNotEmpty(newProduct)) {
				checkChangeProduct(user, newProduct);
				userProductId = user.getString("USER_PRODUCT_ID");
			}

			IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, eparchyCode);
			IDataset userDiscnts = getRuleAfterUserDiscnts(userId, userOfferRels);
			IDataset userSvcs = getRuleAfterUserSvcs(userId, userOfferRels);

			if (IDataUtil.isNotEmpty(newProduct) && StringUtils.isNotEmpty(userProductId)) {
				String endDate = SysDateMgr.getLastDateThisMonth4WEB();
				endUserProductElements(userDiscnts, userProductId, endDate);
				endUserProductElements(userSvcs, userProductId, endDate);
			}

			if (IDataUtil.isNotEmpty(userDiscnts)) {
				filterUserElements(userDiscnts, discntAfters, userId);
				discntAfters.addAll(userDiscnts);
			}
			if (IDataUtil.isNotEmpty(userSvcs)) {
				filterUserElements(userSvcs, svcAfters, userId);
				svcAfters.addAll(userSvcs);
			}
			IDataset productAfters = getRuleAfterUserProducts(user);
			if (IDataUtil.isNotEmpty(productAfters)) {
				ruleParam.put("TF_F_USER_PRODUCT_AFTER", productAfters);
			}
		}
		if (IDataUtil.isNotEmpty(discntAfters)) {
			ruleParam.put("TF_F_USER_DISCNT_AFTER", discntAfters);
		}
		if (IDataUtil.isNotEmpty(svcAfters)) {
			ruleParam.put("TF_F_USER_SVC_AFTER", svcAfters);
		}

		IData ruleInfo = BizRule.bre4ProductLimitNeedFormat(ruleParam);
		if (IDataUtil.isNotEmpty(ruleInfo)) {
			IDataset errors = ruleInfo.getDataset("TIPS_TYPE_ERROR");
			if (IDataUtil.isNotEmpty(errors)) {
				result = errors;
				return result;
			}
		}
		return result;
	}

	public static void filterUserElements(IDataset userElements, IDataset elements, String userId) throws Exception {
		if (IDataUtil.isNotEmpty(elements) && IDataUtil.isNotEmpty(userElements)) {
			for (int i = 0; i < elements.size(); i++) {
				IData element = elements.getData(i);
				if (element.containsKey("ELEMENT_ID") && StringUtils.isNotBlank(element.getString("ELEMENT_ID"))
						&& element.containsKey("ELEMENT_TYPE_CODE")
						&& StringUtils.isNotBlank(element.getString("ELEMENT_TYPE_CODE"))) {
					String elementId = element.getString("ELEMENT_ID", "");
					String elementTypeCode = element.getString("ELEMENT_TYPE_CODE", "");
					for (int k = 0; k < userElements.size(); k++) {
						IData userElement = userElements.getData(k);
						String userElementId = userElement.getString("ELEMENT_ID", "");
						String userEelementTypeCode = userElement.getString("ELEMENT_TYPE_CODE", "");
						if (StringUtils.equals(elementId, userElementId)
								&& StringUtils.equals(elementTypeCode, userEelementTypeCode)) {
							userElement.remove(k--);
							break;
						}
					}
				}
			}
		}
	}

	private void endUserProductElements(IDataset elements, String userProductId, String disableEndDate)
			throws Exception {
		if (IDataUtil.isNotEmpty(elements)) {
			for (int i = 0; i < elements.size(); i++) {
				IData element = elements.getData(i);
				String productId = element.getString("PRODUCT_ID");
				String endDate = element.getString("END_DATE");
				if (StringUtils.equals(productId, userProductId)
						&& SysDateMgr.getDayIntervalNoAbs(disableEndDate, endDate) > 0) {
					element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
					element.put("END_DATE", disableEndDate);
				}
			}
		}
	}

	private IData getNewProductByElements(IDataset elements) {
		for (int i = 0; i < elements.size(); i++) {
			IData element = elements.getData(i);
			if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(element.getString("ELEMENT_TYPE_CODE"))) {
				String newProductId = element.getString("ELEMENT_ID");
				return element;
			}
		}
		return null;
	}

	public void checkChangeProduct(IData user, IData newProductElement) throws Exception {
		String nextProductId = user.getString("NEXT_PRODUCT_ID");
		String userProductId = user.getString("USER_PRODUCT_ID");
		String newProductId = newProductElement.getString("ELEMENT_ID");
		if (StringUtils.isNotBlank(nextProductId)) {
			String newProductName = UpcCall.qryOfferNameByOfferTypeOfferCode(nextProductId,
					BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			if (!nextProductId.equals(newProductId))
				CSAppException.appError("-1", "用户已经办理过预约产品【" + newProductName + "】,不能变更产品！");
		} else {
			if (StringUtils.isNotBlank(userProductId) && !userProductId.equals(newProductId)) {
				IData productTran = UpcCall.queryOfferTransOffer(userProductId, "P", newProductId, "P");
				if (IDataUtil.isEmpty(productTran)) {
					CSAppException.apperr(ProductException.CRM_PRODUCT_119, userProductId, newProductId);
				}
			}
		}
	}

	private IDataset getRuleAfterUserProducts(IData user) throws Exception {
		String userId = user.getString("USER_ID");
		String userProductId = user.getString("USER_PRODUCT_ID");
		String nextProductId = user.getString("NEXT_PRODUCT_ID");

		IDataset tradeProducts = new DatasetList();
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		if (uca != null) {
			List<ProductTradeData> productList = uca.getUserProduct(userProductId);

			if (StringUtils.isEmpty(nextProductId)) {
				ProductTradeData oldTradeProduct = null;
				if (productList != null && productList.size() > 0) {
					oldTradeProduct = productList.get(0);
				} else {
					CSAppException.apperr(ProductException.CRM_PRODUCT_19, userProductId);
				}
				tradeProducts.add(oldTradeProduct.toData());
			} else {
				ProductTradeData oldTradeProduct = new ProductTradeData();
				if (productList != null && productList.size() > 0) {
					oldTradeProduct = productList.get(0);
				}
				ProductTradeData newTradeProduct = oldTradeProduct.clone();

				String productChangeDate = ProductUtils.getProductChangeDate(userProductId, nextProductId);
				oldTradeProduct.setEndDate(SysDateMgr.addSecond(productChangeDate, -1));

				newTradeProduct.setStartDate(productChangeDate);
				newTradeProduct.setEndDate(SysDateMgr.END_DATE_FOREVER);
				newTradeProduct.setProductId(nextProductId);
				newTradeProduct.setOldBrandCode(oldTradeProduct.getBrandCode());
				newTradeProduct.setOldProductId(oldTradeProduct.getProductId());
				newTradeProduct.setInstId(SeqMgr.getInstId());

				tradeProducts.add(oldTradeProduct.toData());
				tradeProducts.add(newTradeProduct.toData());
			}
		}
		return tradeProducts;
	}

	private IDataset getRuleAfterUserSvcs(String userId, IDataset userOfferRels) throws Exception {
		IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
		userSvcElements = OfferUtil.fillStructAndFilter(userSvcElements, userOfferRels);

		if (IDataUtil.isNotEmpty(userSvcElements)) {
			for (int i = 0; i < userSvcElements.size(); i++) {
				IData ruleElement = userSvcElements.getData(i);
				ruleElement.put("USER_ID_A", "-1");// 给规则用
				ruleElement.put("USER_ID", userId);
				ruleElement.put("SERVICE_ID", ruleElement.getString("ELEMENT_ID"));
			}
		}
		return userSvcElements;
	}

	private IDataset getRuleAfterUserDiscnts(String userId, IDataset userOfferRels) throws Exception {
		IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
		userDiscntElements = OfferUtil.fillStructAndFilter(userDiscntElements, userOfferRels);

		if (IDataUtil.isNotEmpty(userDiscntElements)) {
			for (int i = 0; i < userDiscntElements.size(); i++) {
				IData ruleElement = userDiscntElements.getData(i);
				ruleElement.put("USER_ID_A", "-1");// 给规则用
				ruleElement.put("USER_ID", userId);
				ruleElement.put("DISCNT_CODE", ruleElement.getString("ELEMENT_ID"));
			}
		}
		return userDiscntElements;
	}

	public void dealFees(IDataset offers) throws Exception {
		if (IDataUtil.isNotEmpty(offers)) {
			for (int i = 0; i < offers.size(); i++) {
				IData offer = offers.getData(i);
				if ("K".equals(offer.getString("ELEMENT_TYPE_CODE"))) {
					IDataset fees = getSaleActiveFee(offer.getString("ELEMENT_ID"));
					if (IDataUtil.isNotEmpty(fees)) {
						offer.put("FEE_DATA", fees);
					}
				}
			}
		}
	}

	private IDataset getSaleActiveFee(String packageId) throws Exception {
		IDataset result = new DatasetList();
//		IDataset fees = UProductTradeFeeQry.getProductTradeFee(null, packageId, "K", packageId, null,getTradeEparchyCode());
//		if (IDataUtil.isNotEmpty(fees)) {
//			for (int j = 0; j < fees.size(); j++) {
//				IData fee = fees.getData(j);
//				String payMode = fee.getString("PAY_MODE", "0");
//				if ("1".equals(payMode)) {
//					continue;
//				}
//				IData temp = new DataMap();
//				temp.put("FEE", fee.getString("FEE"));
//				temp.put("MODE", fee.getString("FEE_MODE"));
//				temp.put("CODE", fee.getString("FEE_TYPE_CODE"));
//				temp.put("PAY_MODE", fee.getString("PAY_MODE", "0"));
//				temp.put("ELEMENT_ID", packageId);
//				result.add(temp);
//			}
//		}
		return result;
	}
}
