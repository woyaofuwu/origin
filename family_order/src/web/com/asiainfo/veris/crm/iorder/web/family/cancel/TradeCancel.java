package com.asiainfo.veris.crm.iorder.web.family.cancel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TradeCancel extends PersonBasePage {

	public abstract void setMainCauseList(IDataset mainCauseList);
	
	public abstract void setCancelTradeList(IDataset cancelTradeList);
	
	public abstract void setPageInfo(IData pageInfo);
	
	public abstract void setInitInfo(IData initInfo);
	
	public abstract void setUnfinishedTradeList(IDataset unfinishedTradeList);
	
	public abstract void setIsOverMonth(String isOverMonth);
	
	public void onInitTrade(IRequestCycle cycle) throws Exception {
		IData initInfo = new DataMap();
		String sysDate = SysDateMgr.getSysDate("yyyy-MM-dd");
		initInfo.put("START_DATE", SysDateMgr.addMonths(sysDate, -3));
		initInfo.put("END_DATE", SysDateMgr.addDays(sysDate, 1));
		this.setInitInfo(initInfo);
	}
	
	
	public void getUserUnfinishedTradeList(IRequestCycle cycle) throws Exception {
		IData tmpEC = this.getData();
		IDataset unfinishedTradeList = new DatasetList();
		//判断是否EC宽带账号进行撤单
		tmpEC.put(Route.ROUTE_EPARCHY_CODE,getVisit().getStaffEparchyCode());
		IData isEc = CSViewCall.callone(this, "SS.TradeCancelSVC.getIsEC",tmpEC);
		if(IDataUtil.isNotEmpty(isEc)){
			unfinishedTradeList = CSViewCall.call(this, "SS.TradeCancelSVC.getECUserUnfinishedTradeList",tmpEC);
		}else{
			unfinishedTradeList = CSViewCall.call(this, "SS.TradeCancelSVC.getUserUnfinishedTradeList",this.getData());
		}
		this.setUnfinishedTradeList(unfinishedTradeList);
		this.setAjax(unfinishedTradeList);
	}
	
	
	public void getMainCauseList(IRequestCycle cycle) throws Exception {
		String causeType = this.getData().getString("CANCEL_CAUSE_TYPE");
		IDataset mainCauseList = null;
		if (StringUtils.isNotEmpty(causeType)) {
			mainCauseList = pageutil.getStaticListByParent("TRADE_CANCEL_REASON", causeType);
		}
		setMainCauseList(mainCauseList);
	}
	
	
	public void getCancelTradeDetails(IRequestCycle cycle) throws Exception {
	    IDataset results = CSViewCall.call(this, "SS.TradeCancelSVC.getCancelTradeDetails", this.getData());
		IData result = results.first();
	    IDataset cancelTradeList = result.getDataset("CANCEL_TRADE_LIST", new DatasetList());
		IData pageInfo = result.getData("PAGE_DATA");
		IDataset feeList = result.getDataset("FEE_LIST", new DatasetList());
		
		this.setCancelTradeList(cancelTradeList);
		this.setPageInfo(pageInfo);
		this.setIsOverMonth(result.getString("IS_OVERMONTH","0"));
		
		IData ajaxData = new DataMap();
		ajaxData.put("IS_OVERMONTH", result.getString("IS_OVERMONTH","0"));
		ajaxData.put("CANCEL_TRADE_LIST", cancelTradeList);
		ajaxData.put("FEE_LIST", feeList);
		this.setAjax(ajaxData);
	}
	
	/**
	 * @desc 业务提交
	 * @param cycle
	 * @throws Exception
	 * @author danglt
	 */
	public void cancelTradeSubmit(IRequestCycle cycle) throws Exception {
		IData params = this.getData();
		params.put("CANCEL_TRADE_LIST", trimSpecPrefixForList(new DatasetList(params.getString("CANCEL_TRADE_LIST"))));
		String cancelReason = params.getString("CANCEL_MAIN_CAUSE");
		if (StringUtils.isEmpty(cancelReason))
		{
		    cancelReason = params.getString("CANCEL_OTHER_CAUSE");
		}
		else 
		{
		    cancelReason = pageutil.getStaticValue("TRADE_CANCEL_REASON", cancelReason);
        }
		params.put("CANCEL_REASON", cancelReason);
		IDataset results = CSViewCall.call(this, "SS.TradeCancelSVC.cancelTradeSubmit", params);
		setAjax(results);
	}
	
	private IDataset trimSpecPrefixForList(IDataset list) throws Exception {
		IDataset newList = new DatasetList();
		for (int i = 0, s = list.size(); i < s; i++) {
			IData newData = list.getData(i).subData("COL", true);
			newList.add(newData);
		}
		return newList;
	}
}

