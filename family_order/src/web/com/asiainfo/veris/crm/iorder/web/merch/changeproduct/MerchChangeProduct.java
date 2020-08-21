package com.asiainfo.veris.crm.iorder.web.merch.changeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MerchChangeProduct extends PersonBasePage
{
	public abstract void setInfo(IData info);

	public abstract void setNewProductId(String newProductId);
	
	public abstract void setVpmnDiscnt(IDataset vpmnDiscnt);

	public void init(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		String productId = data.getString("NEW_PRODUCT_ID");
		if (StringUtils.isEmpty(productId))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "参数错误");
		}
		data.put("", data.getString(""));
		//打开详情页时校验购物车有无主产品变更
		CSViewCall.call(this, "SS.MerchChangeProductSVC.checkShoppingCartForProduct", data);
		setInfo(data);
	}

    public void changeProductTipsInfo(IRequestCycle cycle) throws Exception
    {
        IData infos = new DataMap();

        IData data = getData();

        IDataset choiceInfos = CSViewCall.call(this, "SS.ChangeProductSVC.changeProductTipsInfo", data);
        
        if (IDataUtil.isNotEmpty(choiceInfos))
        {
            infos.put("TIPS_TYPE_CHOICE", choiceInfos);
        }

        setAjax(infos);
    }

	/**
	 * 号码查询
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void loadChildInfo(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		IData result = CSViewCall.callone(this, "SS.MerchChangeProductSVC.loadChildInfo", data);
		this.setAjax(result);
	}

	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        CSViewCall.call(this, "SS.ChangeProductIntfSVC.checkPwlwApnName", data);
        
        //从前台渠道进入的标识
        data.put("IS_FROM_FOREGROUND", "1");
		IDataset dataset = CSViewCall.call(this, "SS.ChangeProductRegSVC.tradeReg", data);
		String tradeId = dataset.getData(0).getString("TRADE_ID");
		setAjax(dataset);
	}

	public void changeProductForStrom(IRequestCycle cycle) throws Exception {
		IData data = this.getData();

		IData result = CSViewCall.callone(this, "SS.ChangeProductSVC.changeProductElementForStrom", data);

		this.setAjax(result);

	}
	
    /**
     * 获取新VPMN优惠、以及预约产品时间
     * 
     * @param cycle
     * @throws Exception
     */
    public void getNewVpmnDiscntBookProductDate(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.clear();

        cond.put("NEW_PRODUCT_ID", this.getParameter("NEW_PRODUCT_ID", ""));
        cond.put("EPARCHY_CODE", this.getParameter("EPARCHY_CODE"));
        cond.put("OLD_VPMN_DISCNT", this.getParameter("OLD_VPMN_DISCNT", ""));
        cond.put("ACCT_DAY", this.getParameter("ACCT_DAY", ""));
        cond.put("FIRST_DATE", this.getParameter("FIRST_DATE", ""));
        cond.put("VPMN_USER_ID_A", this.getParameter("VPMN_USER_ID_A", ""));
        cond.put("VPMN_PRODUCT_ID", this.getParameter("VPMN_PRODUCT_ID", ""));

        IDataset returnResult = CSViewCall.call(this, "SS.ChangeProductSVC.getNewVpmnDiscntBookProductDate", cond);

        if (IDataUtil.isNotEmpty(returnResult) && IDataUtil.isNotEmpty(returnResult.getData(0).getDataset("NEW_VPMN_DISCNT")))
        {
            setVpmnDiscnt(returnResult.getData(0).getDataset("NEW_VPMN_DISCNT"));
        }

        setAjax(returnResult);
    }
	
    public void getDisctTipsInfo(IRequestCycle cycle) throws Exception
    {
        String serialNumber = this.getParameter("SERIAL_NUMBER");
        IData data = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        data.put("SERIAL_NUMBER", serialNumber);
        IDataset result = CSViewCall.call(this, "SS.ChangeProductSVC.getDisctTipsInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
             this.setAjax(result);
        }
    }
    
    public void getCancelActiveInfos(IRequestCycle cycle) throws Exception
    {
        String serialNumber = this.getParameter("SERIAL_NUMBER");
        String offerCode = this.getParameter("NEW_PRODUCT_ID");
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("NEW_PRODUCT_ID", offerCode);
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.ChangeProductSVC.getCancelActiveInfos", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax(result);
        }
    }
    public void checkShareMealPhoneNum(IRequestCycle cycle) throws Exception
    {
        IData userData = this.getData();
        IDataset results = CSViewCall.call(this, "SS.ChangeProductSVC.checkShareMealPhoneNum", userData);
        setAjax(results.first());
    }
}
