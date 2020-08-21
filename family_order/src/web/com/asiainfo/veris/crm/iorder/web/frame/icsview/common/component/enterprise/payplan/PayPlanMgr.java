package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.payplan;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplanedit.PayPlanEditView;
import com.veris.crm.staticparam.StaticParamUtil;

/**
 * 合同查询组件view
 * 
 * @author admin
 *
 */
public abstract class PayPlanMgr extends BizTempComponent
{
    public abstract String getOfferId();

    public abstract String getSubscriberInsId();
    
    public abstract String getObj();

    public abstract void setPayPlans(IDataset params);

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String offerId = cycle.getRequestContext().getParameter("offerId");
        String subscriberInsId = cycle.getRequestContext().getParameter("subscriberInsId");
        String attrObj = cycle.getRequestContext().getParameter("obj");
        if(attrObj==null||attrObj==""){
        	attrObj="0";
        }
        if (offerId != null)
        {
            queryPayPlansByOfferID(offerId, subscriberInsId,attrObj);
        }
        // 添加js
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/enterprise/payplan/PayPlanMgr.js", false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/payplan/PayPlanMgr.js", false, false);

        }

    }

    /**
     * 使用custId查询合同信息
     * 
     * @param custId
     * @throws Exception
     */
    public void queryPayPlansByOfferID(String offerId, String subscriberInsId,String obj) throws Exception
    {
    	
    	IData resultData = PayPlanEditView.renderPayPlanEditInfo(this, offerId, subscriberInsId);
    	
    	if(IDataUtil.isEmpty(resultData))
    		return;
    	
    	IDataset payPanSrc = resultData.getDataset("PAYPLAN_SRC");
    	
    	this.setPayPlans(parserPayPlans(payPanSrc));

        // IData ajaxData = new DataMap();
        // ajaxData.put("HAS_CONTRACT", hasContract);
        // this.getPage().setAjax(ajaxData);

    }

    public IDataset StringToList(String payModes) throws Exception
    {
        IDataset params = new DatasetList();
        String[] paramStrs = payModes.split(",");
        for (int i = 0; i < paramStrs.length; i++)
        {
            IData param = new DataMap();
            param.put("PLAN_TYPE_CODE", paramStrs[i]);
            param.put("PLAN_TYPE", StaticParamUtil.getStaticValue("PLAN_TYPE_CODE", paramStrs[i]));
            param.put("PLAN_TYPE_SELECTED", "false");
            params.add(param);
        }
        return params;
    }

    
    public IDataset parserPayPlans(IDataset payPlanParams) throws Exception
    {
    	IDataset payPlans = new DatasetList();
    	if(IDataUtil.isEmpty(payPlanParams))
    		return payPlans;
    	
    	for(int i = 0, payPlanSize = payPlanParams.size(); i < payPlanSize; i++)
    	{
    		IData payPlan = new DataMap();
    		payPlan.put("PLAN_TYPE_CODE", payPlanParams.getData(i).getString("PLAN_TYPE"));
    		payPlan.put("PLAN_TYPE", payPlanParams.getData(i).getString("PLAN_NAME"));
    		payPlan.put("PLAN_TYPE_SELECTED", payPlanParams.getData(i).getString("CHECKED", "false"));
    		payPlans.add(payPlan);
    	}
        
        return payPlans;
    }
}
