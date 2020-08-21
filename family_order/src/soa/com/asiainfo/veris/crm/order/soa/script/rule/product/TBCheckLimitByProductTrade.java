package com.asiainfo.veris.crm.order.soa.script.rule.product;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断用户的产品，服务，资费，是否不能办理某项业务 【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBCheckLimitByProductTrade extends BreBase implements IBREScript {
	private static Logger logger = Logger.getLogger(TBCheckLimitByProductTrade.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitByProductTrade() >>>>>>>>>>>>>>>>>>");

		boolean bResult = false;

		// 判断用户资料是否存在
		if (!RuleUtils.existsUserById(databus)) {
			return bResult;
		}

		String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
		if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag)) {
			IData param = new DataMap();
			IDataset listTmp = new DatasetList();
			StringBuilder strbError = new StringBuilder();

			String strIdType = databus.getString("ID_TYPE");

			if ("1".equals(strIdType)) {
				String strId = databus.getString("ID");
				String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
				String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
				String productId = "";
				if (strIdType.equals("1")) {
					param.clear();
					param.put("USER_ID", strId);
					param.put("EPARCHY_CODE", strEparchyCode);
					param.put("TRADE_TYPE_CODE", strTradeTypeCode);
					IDataset userInfoChange = UserInfoQry.getUserInfoChgByUserId(strId);
					if (IDataUtil.isNotEmpty(userInfoChange)) {
						for (int k = 0, iSize = userInfoChange.size(); k < iSize; k++) {
							IData userInfo = userInfoChange.getData(k);
							productId = userInfo.getString("PRODUCT_ID");
							// Dao.qryByCode("TD_B_PROD_TRADE_LIMIT","SEL_PRODLIMIT_BY_USERID", param);
							listTmp = UpcCallIntf.queryProdTradeLimit(productId,"0",strEparchyCode,strTradeTypeCode);// 调用产商品接口   add by  fangwz
							if (IDataUtil.isNotEmpty(listTmp)) {
								String strProductName = BreQueryHelp.getNameByCode("ProductName", listTmp.getData(0).getString("PID"));
								strbError.delete(0, strbError.length());
								strbError.append("业务受理前条件判断：用户当前产品为【").append(strProductName).append("】，不能办理该业务！");
								BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 751022,strbError.toString());
							}
						}
					}
					param.clear();
					IDataset listUserSvc = databus.getDataset("TF_F_USER_SVC");
					param.put("TRADE_TYPE_CODE", strTradeTypeCode);
					param.put("ID_TYPE", "1");
					param.put("EPARCHY_CODE", strEparchyCode);
					//listTmp = Dao.qryByCode("TD_B_PROD_TRADE_LIMIT","SEL_TRADELIMIT", param);
					listTmp = UpcCallIntf.queryProdTradeLimit(null,"1",strEparchyCode,strTradeTypeCode);// 调用产商品接口   add by  fangwz
					if (IDataUtil.isEmpty(listTmp))
						return false;
					if (IDataUtil.isEmpty(listUserSvc))
						return false;
					for (int i = 0, iSize = listTmp.size(); i < iSize; i++) {
						IData dataTmp = listTmp.getData(i);
						for (int ii = 0, ilistUserSvc = listUserSvc.size(); ii < ilistUserSvc; ii++) {
							IData dataSvc = listUserSvc.getData(ilistUserSvc);
							if (dataTmp.getString("PID").equals(dataSvc.getString("SERVICE_ID"))) {
								String strServiceName = BreQueryHelp.getNameByCode("ServiceName", listTmp.getData(0).getString("PID"));
								strbError.delete(0, strbError.length());
								strbError.append("业务受理前条件判断：用户当前服务为【").append(strServiceName).append("】，不能办理该业务！");
								BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 751023,strbError.toString());
							}
						}
					}
					IDataset listUserDiscnt = databus
							.getDataset("TF_F_USER_DISCNT");
					param.clear();
					param.put("TRADE_TYPE_CODE", strTradeTypeCode);
					param.put("ID_TYPE", "2");
					param.put("EPARCHY_CODE", strEparchyCode);
					//listTmp = Dao.qryByCode("TD_B_PROD_TRADE_LIMIT","SEL_TRADELIMIT", param);
					listTmp = UpcCallIntf.queryProdTradeLimit(null,"2",strEparchyCode,strTradeTypeCode);// 调用产商品接口   add by  fangwz
					if (IDataUtil.isEmpty(listTmp))
						return false;
					if (IDataUtil.isEmpty(listUserDiscnt))
						return false;
					for (int i = 0, iSize = listTmp.size(); i < iSize; i++) {
						IData dataTmp = listTmp.getData(i);
						for (int ii = 0, ilistUserDiscnt = listUserDiscnt.size(); ii < ilistUserDiscnt; ii++) {
							IData dataUserDiscnt = listUserDiscnt.getData(ilistUserDiscnt);
							if (dataTmp.getString("PID").equals(dataUserDiscnt.getString("DISCNT_CODE"))) {
								String strDiscntName = BreQueryHelp.getNameByCode("DiscntName", listTmp.getData(0).getString("PID"));
								strbError.delete(0, strbError.length());
								strbError.append("业务受理前条件判断：用户当前资费为【").append(strDiscntName).append("】，不能办理该业务！");
								BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 751024,strbError.toString());
							}
						}
					}
				}
			}
		}

		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitByProductTrade() "
					+ bResult + "<<<<<<<<<<<<<<<<<<<");

		return bResult;
	}

}
