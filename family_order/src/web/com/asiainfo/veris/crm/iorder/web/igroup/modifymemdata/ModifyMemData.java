package com.asiainfo.veris.crm.iorder.web.igroup.modifymemdata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class ModifyMemData extends GroupBasePage {

	/**
	 * 集团产品信息及成员信息查询
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryMemberInfo(IRequestCycle cycle) throws Exception {
		// 查询成员用户信息
		String strMebSn = getData().getString("cond_SERIAL_NUMBER");
		if (StringUtils.isEmpty(strMebSn))
			return;

		String relationCode = getData().getString("RELATION_CODE", "");

		IData resultInfo = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosBySn(
				this, strMebSn, relationCode); // 部分集团信息，成员信息，成员账户信息

		// 集团产品信息
		IDataset groupinfos = (IDataset) resultInfo.get("ORDERED_GROUPINFOS");
		setGrpProInfos(groupinfos);

		// 成员信息
		IData userinfo = (IData) resultInfo.get("MEB_USER_INFO");
		setUserinfo(userinfo);
	}

	/**
	 * 查询个人账户信息
	 * 
	 * @throws Throwable
	 */
	public void queryMemberAcct(IRequestCycle cycle) throws Throwable {
		// 查询成员信息
		String mebSn = getData().getString("cond_SERIAL_NUMBER");
		IData mebInfo = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(this,
				mebSn);
		if (IDataUtil.isEmpty(mebInfo))
			return;

		// 成员客户信息
		IData custInfo = mebInfo.getData("MEB_CUST_INFO");
		if (IDataUtil.isEmpty(custInfo))
			custInfo = new DataMap();

		// 成员用户信息
		IData userInfo = mebInfo.getData("MEB_USER_INFO");
		if (IDataUtil.isEmpty(userInfo))
			userInfo = new DataMap();
		userInfo.put("CUST_NAME", custInfo.getString("CUST_NAME", ""));
		setMebUseInfo(userInfo);

		// 用户帐期
		IData userAcctDayinfo = mebInfo.getData("MEB_ACCTDAY_INFO");
		setMebAcctDayInfo(userAcctDayinfo);
		judeMemberUserAcctDay(userAcctDayinfo);

		String user_id = userInfo.getString("USER_ID");
		String eparchy_code = userInfo.getString("EPARCHY_CODE");

		IData data = new DataMap();

		// 成员账户校验
		data.put("USER_ID", user_id);
		data.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
		IDataset memAcctInfos = CSViewCall.call(this,
				"CS.UcaInfoQrySVC.qryAcctInfoByUserId", data);
		if (IDataUtil.isEmpty(memAcctInfos)) {
			CSViewException.apperr(GrpException.CRM_GRP_375);
		}

		// 未完工的工单校验
		data.put("TRADE_TYPE_CODE", "4035");
		data.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
		IDataset results = CSViewCall.call(this,
				"CS.TradeInfoQrySVC.getTradeByUserId", data);
		if (IDataUtil.isNotEmpty(results)) {
			CSViewException.apperr(GrpException.CRM_GRP_463);
		}
		// 是否支持成员付费计划变更
		String productId = getData().getString("cond_PRODUCT_ID");
		AttrBizInfoIntfViewUtil.qryModMbProductCtrlInfoByProductId(this,
				productId);

		// 判别是否为集团成员
		data.put("EPARCHY_CODE", eparchy_code);
		data.put("RELATION_CODE", getData().getString("RELATION_CODE", ""));
		data.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
		IDataset groupInfos = CSViewCall.call(this,
				"CS.GrpInfoQrySVC.getGroupInfo", data); // CS_CustGroupInfoQrySVC_getGroupInfo
		if (IDataUtil.isEmpty(groupInfos)) {
			CSViewException.apperr(GrpException.CRM_GRP_268,
					getParameter("cond_SERIAL_NUMBER"));
		}

		if (IDataUtil.isNotEmpty(groupInfos)) {
			IDataset bbossGrps = DataHelper.filter(groupInfos,
					"BRAND_CODE=BOSG");
			if (IDataUtil.isNotEmpty(bbossGrps)) {
				// 商品
				IData param = new DataMap();
				param.put("PRODUCT_TYPE_CODE", "BBYY");
				IDataset products = CSViewCall
						.call(this,
								"CS.ProductInfoQrySVC.getProductsByTypeForGroup",
								param);
				for (int i = 0, iSize = bbossGrps.size(); i < iSize; i++) {
					boolean tag = false;
					for (int j = 0, jSize = products.size(); j < jSize; j++) {
						if (products
								.getData(j)
								.getString("PRODUCT_ID", "")
								.equals(bbossGrps.getData(i).getString(
										"PRODUCT_ID"))) {
							tag = true;
						}
					}
					if (!tag) {
						groupInfos.remove(bbossGrps.getData(i));
					}
				}
			}
		}

		// 获取集团产品与集团用户
		getGroupBySN(cycle);

		setCondition(GroupProductUtilView.getProductExplainInfo(this,
				groupInfos.getData(0).getString("PRODUCT_ID", "")));
	}

	/**
	 * 作用：根据group_sn查询集团基本信息 默认传入为cond_GROUP_ID
	 * 
	 * @author hud
	 * @param cycle
	 * @throws Throwable
	 */
	public void getGroupBySN(IRequestCycle cycle) throws Throwable {
		String grpSn = getData().getString("cond_GROUP_SERIAL_NUMBER");
		IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpSn);
		if (IDataUtil.isNotEmpty(data)) {
			IData userinfo = data.getData("GRP_USER_INFO");
			IData groupinfo = data.getData("GRP_CUST_INFO");
			String productId = userinfo.getString("PRODUCT_ID");
			getData().put("PRODUCT_ID", productId);

			setGrpProInfo(groupinfo);
			setGrpUserinfo(userinfo);
		}
	}

	private void judeMemberUserAcctDay(IData mebUserAcctDay) throws Exception {
		// 获取成员账期分布标志
		String acctDayDistribution = mebUserAcctDay
				.getString("USER_ACCTDAY_DISTRIBUTION");

		IData condition = new DataMap();
		condition.put("cond_AcctDay_Distribution", acctDayDistribution);

		// 非自然月账期情况
		if (!GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(
				acctDayDistribution)) {
			String ifNatureDayProduct = "true";// 是否必须要求成员为自然月账期的产品
			condition.put("cond_ifNatureDayProduct", ifNatureDayProduct);

			String descInfo = "";
			if ("true".equals(ifNatureDayProduct)) {
				descInfo = "提示：此产品不支持分散账期的用户";
			}
			if (acctDayDistribution
					.equals(GroupBaseConst.UserDaysDistribute.FALSE_TRUE
							.getValue())) {
				descInfo += ",成员用户下账期为[1]号账期,可办理预约业务!";
				// 可以做预约业务的情况,前台显示是否下账期生效的标志
				condition.put("cond_Booking_Flag", "true");
				condition.put("cond_Checked", "true"); // 选中复选框
				condition.put("cond_Disabled", "true"); // 复选框组件禁选
				condition.put("IF_BOOKING", "true"); // 判断是否预约工单
			} else if (acctDayDistribution
					.equals(GroupBaseConst.UserDaysDistribute.TRUE_FALSE
							.getValue())
					|| acctDayDistribution
							.equals(GroupBaseConst.UserDaysDistribute.FALSE_FALSE
									.getValue())) {
				descInfo += ",成员用户存在未生效的账期变更业务,需在["
						+ mebUserAcctDay.getString("FIRST_DAY_NEXTACCT")
						+ "]后将账期改为1号可办理业务!";
			} else if (acctDayDistribution
					.equals(GroupBaseConst.UserDaysDistribute.FALSE.getValue())) {
				descInfo += ",成员用户为[" + mebUserAcctDay.getString("ACCT_DAY")
						+ "]号账期,必须将账期改为1号才可办理业务!";
			}
			condition.put("cond_Desc", descInfo);
		}
		setCondition(condition);

	}

	public void initial(IRequestCycle cycle) throws Exception {

		this.productId = getData().getString("PRODUCT_ID");
		String userId = getData().getString("GRP_USER_ID"); // 集团user_id

		// selectAcctInfo组件参数(暂时没用到)
		IData grpInfo = new DataMap();
		grpInfo.put("GROUP_USER_ID", userId);
		grpInfo.put("GROUP_CUST_ID", getData().getString("CUST_ID"));
		setGrpInfo(grpInfo);

		// 查询集团默认付费账户
		/*
		 * acctInfo = UCAInfoIntfViewUtil.qryGrpDefAcctInfoByUserId(this,
		 * userId); setGrpAcctInfo(acctInfo);
		 * 
		 * condition.put("GRP_ACCT_ID", acctInfo.getString("ACCT_ID")); //
		 * 集团默认付费账户 condition.put("PRODUCT_ID", productId);
		 * condition.put("GROUP_USER_ID", userId); // 集团的
		 * condition.put("MEB_USER_ID", getData().getString("MEB_USER_ID")); //
		 * 成员的 condition.put("MEB_EPARCHY_CODE",
		 * getData().getString("MEB_EPARCHY_CODE")); // 成员的
		 * condition.put("GROUP_ID", getData().getString("GROUP_ID")); // 集团客户编码
		 * condition.put("IF_BOOKING", getData().getString("ifBooking")); //
		 * 是否下账期生效
		 */// 获取成员付费计划
		IDataset mebPayPlans = UserPayPlanInfoIntfViewUtil.getGrpMemPayPlanByUserId(this,getData().getString("MEB_USER_ID"), userId, getData().getString("MEB_EPARCHY_CODE"));
		IData mebPayPlan = new DataMap();

		if (IDataUtil.isNotEmpty(mebPayPlans)) {
			mebPayPlan = mebPayPlans.getData(0);
		}

		// 查询集团付费方式
		IDataset payPlans = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(this, userId);		
		IDataset patPlanset = comsisData(payPlans);		

		// 集团付费计划中的付费账目
		IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(this, productId);// 获取集团付费账目
		
		IData resultData = new DataMap();		
		resultData.put("PAYPLANSEL_PAY_TYPE_SET", patPlanset);
		resultData.put("PAYPLANSEL_PAY_ITEMS", payItems);
		resultData.put("PAYPLANSEL_MEB", mebPayPlan);
		
		this.setAjax(resultData);

		setPayTypeSet(patPlanset);
		setPayItemSet(payItems);
		


		// setCondition(condition);
	}



	/**
	 * 获取用户帐户信息
	 * 
	 * @param ctx
	 * @return
	 * @exception Exception
	 */
	public void getUserTempletInfo(IDataset memAcctInfos, IData accttemplate,
			IDataset acctitems) throws Exception {
		IData tag = new DataMap();

		if (IDataUtil.isNotEmpty(memAcctInfos)) {
			setMemacctinfo(memAcctInfos.getData(0));
		} else {
			IData memAcctInfo = new DataMap();
			memAcctInfo.put("START_CYCLE_ID",
					SysDateMgr4Web.getDateForYYYYMMDD(SysDateMgr4Web
							.getFirstDayOfThisMonth())); // 本月第一天
			memAcctInfo.put("END_CYCLE_ID",
					SysDateMgr4Web.getEndCycle20501231());
			setMemacctinfo(memAcctInfo);
		}

		IDataset dataset = new DatasetList();
		if (acctitems != null && acctitems.size() > 0) {
			for (int i = 0; i < acctitems.size(); i++) {
				IData temp = new DataMap();
				IData acctitem = (IData) acctitems.get(i);
				temp.put("NOTE_ITEM_CODE", acctitem.getString("ATTR_CODE"));
				temp.put("NOTE_ITEM", acctitem.getString("ATTR_NAME"));
				dataset.add(temp);
			}
		} else {
			IData data = new DataMap();
			data.put("NOTE_ITEM", getParameter("NOTE_ITEM"));
			dataset = CSViewCall.call(this,
					"CS.NoteItemInfoQrySVC.filterNoteItemsByGrpForHNANNew",
					data); // 综合帐目列表
		}

		IDataset payItems = new DatasetList();
		IData dataItem = new DataMap();
		if (memAcctInfos != null && memAcctInfos.size() > 0) {
			IData memacctinfo = (IData) memAcctInfos.get(0);
			tag.put("TAG", "1");
			tag.put("COMPLEMENT_TAG",
					memacctinfo.getString("COMPLEMENT_TAG", ""));
			tag.put("CHECK_ALL_TAG", memacctinfo.getString("PAYITEM_CODE", ""));

			String payItemstr = memacctinfo.getString("PAYITEM_CODE", "");
			dataItem.put("ITEMCODE", payItemstr.replace(",", "|"));
			setInfo(dataItem); // 老的明细账目项

			if (payItemstr.length() > 0) {
				String[] payitem = payItemstr.split("\\|");
				for (int i = 0; i < payitem.length; i++) {
					IData temp = new DataMap();
					temp.put("PAYITEM_CODE", payitem[i]);
					payItems.add(temp);
				}
			}
		} else {
			tag.put("TAG", "0");
			tag.put("COMPLEMENT_TAG", "0");
			tag.put("CHECK_ALL_TAG", "0");

		}
		setTag(tag);

		for (int i = 0; i < dataset.size(); i++) {
			IData payitemdata = (IData) dataset.get(i);
			IDataset results = DataHelper.filter(payItems, "PAYITEM_CODE="
					+ payitemdata.getString("NOTE_ITEM_CODE"));
			if (results.size() > 0) {
				dataset.getData(i).put("TAG", "1");
				dataset.getData(i).put(
						"DETAILITEM",
						dataItem.getString(
								payitemdata.getString("NOTE_ITEM_CODE"), ""));
			} else {
				dataset.getData(i).put("TAG", "0");
			}
		}

		setNoteItemList(dataset);
	}




	public abstract void setDetInfo(IData detInfo);

	public abstract void setInfos(IDataset infos);

	public abstract void setMebAcctDayInfo(IData info); // 用户账期信息

	public abstract void setMebUseInfo(IData info);

	public abstract void setUserinfo(IData Userinfo);

	public abstract void setGrpUserinfo(IData GrpUserinfo);

	public abstract void setGrpProInfos(IDataset GrpProInfos);

	public abstract void setGrpProInfo(IData GrpProInfo);

	public abstract void setAcctInfoDesc(IData acctInfo);

	public abstract void setCondition(IData condition);

	public abstract void setConsignDesc(IData data);

	public abstract void setGrpAcctInfo(IData grpAcctInfo);

	public abstract void setGrpInfo(IData grpInfo);

	public abstract void setInfo(IData info);

	public abstract void setMemacctinfo(IData memacctinfo);

	public abstract void setNoteItemList(IDataset dataset);

	public abstract void setOverProvinceDesc(IData data);

	public abstract void setProductCtrlInfo(IData productCtrlInfo);

	public abstract void setTag(IData tag);

	public abstract void setPayTypeSet(IDataset payTypeSet);

	public abstract void setPayItemSet(IDataset payItemSet);


	/**
	 * @return productId
	 */
	@Override
	public String getProductId() {

		return productId;
	}





	/**
	 * 提交方法
	 * 
	 * @throws Exception
	 */
	public void submitPayinfo(IRequestCycle cycle) throws Exception {
		IData inparam = new DataMap();

		String payplanString = getData().getString("PAY_INFO", "[]");
		if (StringUtils.isBlank(payplanString))
			payplanString = "[]";
		IDataset payPlan = new DatasetList(payplanString);

		inparam.put("USER_ID", getData().getString("GRP_USER_ID", "")); // 集团
		inparam.put("SERIAL_NUMBER",
				getData().getString("MEB_SERIAL_NUMBER", "")); // 成员
		inparam.put("PRODUCT_ID", getData().getString("PRODUCT_ID", ""));
		inparam.put("REMARK", getData().getString("REMARK_INFO", ""));
		inparam.put("PLAN_INFO", payPlan);

		IDataset result = CSViewCall.call(this, "SS.ModifyMemDataSVC.crtTrade",
				inparam);
		this.setAjax(result);
	}





	/**
	 * 数据转换
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private IDataset comsisData(IDataset source) throws Exception {

		IDataset payTypeSet = new DatasetList();
        if (IDataUtil.isEmpty(source))
        {
            return payTypeSet;
        }
		for (int i = 0; i < source.size(); i++) {
			IData tmp = source.getData(i);
			String payTypeCode = tmp.getString("PLAN_TYPE_CODE", "");
			String payTypeName = tmp.getString("PLAN_NAME", "");
			String planId = tmp.getString("PLAN_ID", "");

			boolean found = false;
			for (int j = 0; j < payTypeSet.size(); j++) {
				IData data = payTypeSet.getData(j);
				if (data.getString("PAY_TYPE_CODE").equals(payTypeCode)) {
					found = true;
					break;
				}
			}
			if (!found) {
				IData map = new DataMap();
				map.put("PAY_TYPE_CODE", payTypeCode);
				map.put("PLAN_TYPE_CODE", payTypeCode);
				map.put("PAY_TYPE", payTypeName);
				map.put("PLAN_ID", planId);
				map.put("PLAN_TYPE", payTypeCode);
				payTypeSet.add(map);
			}
		}
		return payTypeSet;
	}

}
