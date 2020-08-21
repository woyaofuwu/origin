package com.asiainfo.veris.crm.order.web.person.grpvaluecard;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 流量卡使用查询
 * 
 * @author chenzg
 * 
 */
public abstract class GRPValueCardUseQuery extends PersonBasePage {
	public abstract void setCondition(IData cond);

	public abstract void setInfo(IData info);

	public abstract void setListInfos(IDataset listInfos);

	public abstract void setPageCount(long count);

	public static Logger logger = Logger.getLogger(GRPValueCardUseQuery.class);

	/**
	 * 流量卡使用查询
	 * 
	 * @param cycle
	 * @throws Exception
	 * @Author:chenzg
	 * @Date:2017-1-5
	 */
	public void queryGrpValueCardUseInfo(IRequestCycle cycle) throws Exception {
		Pagination page = getPagination("recordNav");
		IData data = getData("cond", true);
		data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
		String startCn = data.getString("START_CARD_NUMBER", "");
		String endCn = data.getString("END_CARD_NUMBER", "");
		IDataset resDs = new DatasetList();
		if (StringUtils.isNotBlank(startCn) && StringUtils.isNotBlank(endCn) && "1".equals(data.getString("STATE_CODE", ""))) {
			// 先查资源库
			data.put("QRY_TAG", ""); // 0查询空闲表,1查询在用表,"" 两表都查
			data.put("RES_NO_S", startCn); // 起始有价卡号
			data.put("RES_NO_E", endCn); // 终止有价卡号
			data.put("RES_TYPE_CODE", "33a"); // 卡类型
			// resDs = CSAppCall.call("RM.ResPayCardIntfSvc.queryValuecardInfo",
			// data);
			//IDataOutput results = CSViewCall.callPage(this, "RM.ResPayCardIntfSvc.queryValuecardInfoPage", data, page);
			//resDs = results.getData();
			ServiceResponse response = (ServiceResponse) BizServiceFactory.call("RCF.resource.IPayCardIntfQuerySV.queryPayCardInfo", data, page);
//			ServiceResponse response = ResCall.callResPage("RCF.resource.IPayCardIntfQuerySV.queryPayCardInfo", data, page);
			resDs = response.getData();
			if (resDs != null && resDs.size() > 0) {
				// 资源接口的字段转成界面展示需要的字段
				for (int i = 0; i < resDs.size(); i++) {
					IData each = resDs.getData(i);
					each.put("CARD_NUMBER", each.getString("VALUE_CARD_NO", ""));
					each.put("STATE_CODE", each.getString("STATE_CODE", "1"));
					each.put("DEVICE_PRICE", each.getDouble("ADVISE_PRICE", 0) / 100);
					each.put("SALE_PRICE", each.getString("", ""));
					each.put("CUST_NUMBER", each.getString("", ""));
					each.put("UPDATE_TIME", each.getString("", ""));
					each.put("ACCEPT_DATE", each.getString("", ""));
					each.put("GROUP_ID", each.getString("", ""));
					each.put("GROUP_NAME", each.getString("", ""));
				}
			}

			//setPageCount(results.getDataCount());
			setPageCount(response.getDataCount());
		} else {
			// 再查CRM库
			// IDataset crmDs = CSViewCall.call(this,
			// "SS.GRPValueCardMgrSVC.queryGrpValueCardUseInfo", data);
			IDataOutput results = CSViewCall.callPage(this, "SS.GRPValueCardMgrSVC.queryGrpValueCardUseInfo", data, page);
			resDs = results.getData();
			// resDs.addAll(crmDs);
			setPageCount(results.getDataCount());
		}
		setListInfos(resDs);
		setCondition(data);

		setAjax("DATA_COUNT", "" + resDs.size());
	}
}
