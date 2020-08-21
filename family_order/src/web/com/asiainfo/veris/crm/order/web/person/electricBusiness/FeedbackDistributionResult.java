package com.asiainfo.veris.crm.order.web.person.electricBusiness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;



public abstract class FeedbackDistributionResult extends PersonBasePage{
	/**
	 * 查询销售订单信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryDistributionOrder(IRequestCycle cycle) throws Exception {
		IData param = getData();
		param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset results = CSViewCall.call(this, "SS.ShoppingOrderSVC.queryListByTabRef", param);
		setInfos(results);
		setCond(getData());
	}
	
    /**
     * 提交反馈信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        
        IDataset results = CSViewCall.call(this, "SS.ShoppingOrderSVC.submitFeedBack", data);
          this.setAjax(results.getData(0));
        
    }
    public abstract void setInfos(IDataset infos);
     public abstract void setCond(IData cond);
	

}
