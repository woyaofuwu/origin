package com.asiainfo.veris.crm.iorder.web.person.popularity;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PopularityManageNew extends PersonBasePage {

	private static final Logger log = Logger.getLogger(PopularityManageNew.class);
	/**
	 * @param cycle
	 */
	public void init(IRequestCycle cycle) {
		
	}
	
	/**
	 * 查询热门推荐业务信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryPopularity(IRequestCycle cycle) throws Exception {
		IData param = this.getData();
		IDataOutput result = CSViewCall.callPage(this, "SS.PopularityManageSVC.queryPopularityInfo", param, this.getPagination("pagin"));
	
		IDataset datas = result.getData();
		
		if (IDataUtil.isNotEmpty(datas)) {
			for (int i = 0; i < datas.size(); i++) {
				IData data = datas.getData(i);
				IData qryParam = new DataMap();
				qryParam.put("OFFER_CODE", data.getString("OFFER_CODE"));
				qryParam.put("POPULARITY_TRADE_TYPE", data.getString("POPULARITY_TRADE_TYPE"));

				IData saleActiveInfo = CSViewCall.callone(this, "SS.PopularityManageSVC.queryCode", qryParam);

				if (IDataUtil.isNotEmpty(saleActiveInfo)) {
					data.put("PRODUCT_NAME", saleActiveInfo.getString("PRODUCT_NAME"));
					data.put("CAMPN_NAME", saleActiveInfo.getString("CAMPN_NAME"));
				}

			}
		}
		setPaginCount(result.getDataCount());
		setInfos(datas);
	}
	
	/**
	 * 获取推荐业务新增或修改默认数据
	 * @param cycle
	 * @throws Exception
	 */
	public void getDefaultParam(IRequestCycle cycle) throws Exception {
		IData defaultParam = new DataMap();
		defaultParam.put("STAFF_ID", this.getVisit().getStaffId());
		defaultParam.put("DEPART_ID", this.getVisit().getDepartId());

		setAjax(defaultParam);
	}
	
	/**
	 * 根据商品编码及商品类型查询商品信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryCode(IRequestCycle cycle) throws Exception{
		IData param = this.getData();
		IData qryParam = new DataMap();
		IData qryParam1 = new DataMap();
		IData result = new DataMap();

		result = CSViewCall.callone(this, "SS.PopularityManageSVC.queryCode", param);
		
		result.put("START_DATE", TimeUtil.getSysTime());
		result.put("END_DATE", TimeUtil.END_TIME_FOREVER);
		
		qryParam.put("QRY_OFFER_CODE", param.getString("OFFER_CODE"));
		qryParam.put("QRY_POPULARITY_TYPE", "2");
		IDataOutput qryResult = CSViewCall.callPage(this, "SS.PopularityManageSVC.queryPopularityInfo", qryParam, this.getPagination("pagin"));

		IDataset datas = qryResult.getData();

		if (DataUtils.isNotEmpty(datas)) {
			result.put("ISEXIST", "1");
			result.putAll(datas.first());
		} else {
			qryParam1.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
			qryParam1.put("POPULARITY_TYPE", "2");
			qryParam1.put("POPULARITY_TRADE_TYPE", param.getString("POPULARITY_TRADE_TYPE"));
			log.debug("SS.PopularityManageSVC.queryMaxPriority->qryParam1:" + qryParam1);
			IData defaultPriority = CSViewCall.callone(this, "SS.PopularityManageSVC.queryMaxPriority", qryParam1);

			result.put("PRIORITY", Integer.parseInt(defaultPriority.getString("MAX_PRIORITY")) + 1);
			result.put("POPULARITY_TYPE", "2");
		}
		
		setCodeInfo(result);
		setAjax(result);
	}
	
	/**
	 * 提交
	 * @param cycle
	 * @throws Exception
	 */
	public void submitPopularity(IRequestCycle cycle) throws Exception {
		IData data = getData();

		IData inputData = new DataMap();

		IDataset popularityList = new DatasetList(data.getString("POPULARITY_LIST"));
		if (IDataUtil.isNotEmpty(popularityList)) {
			inputData.put("POPULARITY_LIST", popularityList);
		}

		CSViewCall.call(this, "SS.PopularityManageSVC.managePopularityInfo", inputData);
	}

	public abstract void setCond(IData cond);
	
	public abstract void setCodeInfo(IData codeInfo);

	public abstract void setInfos(IDataset infos);

	public abstract void setPaginCount(long count);
}
