package com.asiainfo.veris.crm.order.web.person.speservice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upc.UpcViewCallIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NewSvcRecomdInfo extends PersonBasePage {
	/**
	 * 获取推荐服务用语
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void createDept(IRequestCycle cycle) throws Exception {

		IData pageData = getData();
		String recomdSvcInfo = "";

		if (StringUtils.isBlank(pageData.getString("SERIAL_NUMBER", ""))) {
			pageData.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		}
		IDataset recomdSvcList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.createDept", pageData);

		if (IDataUtil.isEmpty(recomdSvcList))
			recomdSvcInfo = "暂无推荐服务用语";
		else
			recomdSvcInfo = recomdSvcList.getData(0).getString("MESSAGE", "");
		setAjax("MESSAGE", recomdSvcInfo);
	}

	/**
	 * 获取推荐活动用语
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getRecomdActInfo(IRequestCycle cycle) throws Exception {

		IData pageData = getData();
		String recomdActInfo = "";
		pageData.put("TYPE", "4");
		IDataset recomdActList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdString", pageData);
		if (recomdActList == null || recomdActList.size() <= 0)
			recomdActInfo = "暂无推荐活动用语";
		else
			recomdActInfo = recomdActList.getData(0).getString("RECOMM_CONTENT", "");

		setAjax("RECOMDACTINFO", recomdActInfo);
	}

	/**
	 * 获取推荐优惠用语
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getRecomdDisInfo(IRequestCycle cycle) throws Exception {

		IData pageData = getData();
		String recomdDisInfo = "";
		pageData.put("TYPE", "1");
		IDataset recomdDisList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdString", pageData);
		if (recomdDisList == null || recomdDisList.size() <= 0)
			recomdDisInfo = "暂无推荐优惠用语";
		else
			recomdDisInfo = recomdDisList.getData(0).getString("RECOMM_CONTENT", "");

		setAjax("RECOMDDISINFO", recomdDisInfo);
	}

	/**
	 * 获取推荐平台业务用语
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getRecomdPlatInfo(IRequestCycle cycle) throws Exception {

		IData pageData = getData();
		String recomdPlatInfo = "";
		pageData.put("TYPE", "3");
		IDataset recomdPlatList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdString", pageData);
		if (recomdPlatList == null || recomdPlatList.size() <= 0)
			recomdPlatInfo = "暂无推荐平台业务用语";
		else
			recomdPlatInfo = recomdPlatList.getData(0).getString("RECOMM_CONTENT", "");

		setAjax("RECOMDPLATINFO", recomdPlatInfo);
	}

	/**
	 * 获取推荐产品用语
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getRecomdProInfo(IRequestCycle cycle) throws Exception {

		IData pageData = getData();
		String recomdProInfo = "";
		pageData.put("TYPE", "0");
		IDataset recomdProList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdString", pageData);
		if (recomdProList == null || recomdProList.size() <= 0)
			recomdProInfo = "暂无推荐产品用语";
		else
			recomdProInfo = recomdProList.getData(0).getString("RECOMM_CONTENT", "");

		setAjax("RECOMDPROINFO", recomdProInfo);
	}

	/**
	 * 获取推荐服务用语
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getRecomdSvcInfo(IRequestCycle cycle) throws Exception {

		IData pageData = getData();
		pageData.put("TYPE", "2");
		String recomdSvcInfo = "";
		IDataset recomdSvcList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdString", pageData);

		if (recomdSvcList == null || recomdSvcList.size() <= 0)
			recomdSvcInfo = "暂无推荐服务用语";
		else
			recomdSvcInfo = recomdSvcList.getData(0).getString("RECOMM_CONTENT", "");
		setAjax("RECOMDSVCINFO", recomdSvcInfo);
	}

	/**
	 * 主要用来获取页面初始化时的用户推荐服务、推荐优惠、反馈结果、历史消费信息、历史推荐信息
	 * 
	 * @data 2013-9-25
	 * @param cycle
	 * @throws Exception
	 */
	public void loadChildInfo(IRequestCycle cycle) throws Exception {

		IData pagedata = getData();
		IData editInfo = new DataMap();
		IData custInfo = new DataMap();
		IData userInfo = new DataMap();

		if (!"1".equals(pagedata.getString("PUSH_FLAG", ""))) {
			custInfo = new DataMap(pagedata.getString("CUST_INFO"));
			userInfo = new DataMap(pagedata.getString("USER_INFO"));
		} else {
			String serialNumber = pagedata.getString("AUTH_SERIAL_NUMBER");
			pagedata.put("SERIAL_NUMBER", serialNumber);

			// 获取三户资料
			IDataset datasetUca = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getAllInfobySn", pagedata);
			userInfo = datasetUca.getData(0).getData("USER_INFO");
			custInfo = datasetUca.getData(0).getData("CUST_INFO");
		}

		// 加载三户资料
		String open_date = userInfo.getString("OPEN_DATE", "").substring(0, 10);
		String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);

		if (current_date.equals(open_date)) {
			userInfo.put("TRADE_TYPE_CODE_A", "A");
		}

		String userState = UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, userInfo.getString("PRODUCT_ID", ""), "P", userInfo.getString("USER_STATE_CODESET", ""));
		userInfo.put("STATE_NAME", userState);

		editInfo.putAll(custInfo);
		editInfo.putAll(userInfo);
		setEditInfo(editInfo);

		// 该用户注销标志不是正常状态,不能进行新业务推荐！
		String remove_tag = userInfo.getString("REMOVE_TAG", "");
		if (!remove_tag.equals("0")) {
			CSViewException.apperr(CrmUserException.CRM_USER_538);
		}

		IDataset RecomdList = new DatasetList();
		IDataset acceptList = new DatasetList();
		IDataset RefuseList = new DatasetList();
		IDataset UseOwnList = new DatasetList();
		IDataset VipList = new DatasetList();
		IDataset HistoryConsumeList = new DatasetList();
		IDataset ZhongDuanList = new DatasetList();
		IDataset historyConsumeList = new DatasetList();
		IData tempdata = new DataMap();
		IData info = new DataMap();
		String historyConsumeInfo = "";

		tempdata.putAll(userInfo);
		RecomdList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdInfo", tempdata);
		setRecomdLists(RecomdList);// 获取用户推荐集

		RefuseList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRefuseInfo", tempdata);
		setForeachs(RefuseList);

		UseOwnList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getUseOwnInfo", tempdata);

		IData info1 = new DataMap();
		if (IDataUtil.isNotEmpty(UseOwnList)) {
			for (int i = 0; i < UseOwnList.size(); i++) {
				info1.putAll(UseOwnList.getData(i));
			}

		}

		VipList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.Vipquery", tempdata);

		if (IDataUtil.isNotEmpty(VipList)) {
			for (int i = 0; i < VipList.size(); i++) {
				if (StringUtils.isBlank(VipList.getData(i).getString("RET"))) {
					info1.put("vipinfo", VipList.getData(i).getString("RET"));
				}

			}
		}

		HistoryConsumeList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getHistoryConsumeInfo", tempdata);

		if (IDataUtil.isNotEmpty(HistoryConsumeList)) {
			for (int i = 0; i < HistoryConsumeList.size(); i++) {
				if (StringUtils.isNotBlank(HistoryConsumeList.getData(i).getString("RET"))) {
					historyConsumeInfo = HistoryConsumeList.getData(i).getString("RET");
				}
			}
		}

		ZhongDuanList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getZhongDuanInfo", tempdata);

		if (IDataUtil.isNotEmpty(ZhongDuanList)) {
			IData zhongDuanInfo = ZhongDuanList.getData(0);

			if (IDataUtil.isNotEmpty(zhongDuanInfo)) {
				if (!zhongDuanInfo.getString("tag").equals("0")) {
					info.put("history_CONSUME_ACTION", historyConsumeInfo);
					info.put("PHONE_QUALITY_NAME", zhongDuanInfo.getString("PHONE_QUALITY_NAME"));
					info.put("PHONE_MODEL_NAME", zhongDuanInfo.getString("PHONE_MODEL_NAME"));
					setInfo(info);
				}
			}
		}

		historyConsumeList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getHistoryRecomdInfo", tempdata);

		setHistoryConsumeList(historyConsumeList);

		setInfo1(info1);// 《新业务推荐受理》界面优化

	}

	/**
	 * 业务提交
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData data = getData();

		if (StringUtils.isBlank(data.getString("SERIAL_NUMBER", ""))) {
			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		}

		IDataset dataset = CSViewCall.call(this, "SS.NewSvcRecomdInfoRegSVC.tradeReg", data);
		setAjax(dataset);
	}

	public abstract void setEditInfo(IData editInfo);

	public abstract void setForeachs(IDataset ForeachList);

	public abstract void setHistoryConsumeList(IDataset historyConsumeList);

	public abstract void setInfo(IData info);

	public abstract void setInfo1(IData info1);

	public abstract void setRecomdLists(IDataset productList);

}
