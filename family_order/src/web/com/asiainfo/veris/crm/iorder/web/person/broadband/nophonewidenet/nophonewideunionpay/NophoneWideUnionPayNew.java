package com.asiainfo.veris.crm.iorder.web.person.broadband.nophonewidenet.nophonewideunionpay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class NophoneWideUnionPayNew extends PersonBasePage{
	
	public void checkPaySerialNumber(IRequestCycle cycle) throws Exception{
		IData data = getData();
		data.put("SERIAL_NUMBER", data.getString("PAY_SERIAL_NUMBER"));
		IData result = CSViewCall.callone(this, "SS.NophoneWideUnionPaySVC.checkPaySerialNumber", data);
        setAjax(result);
	}
	
	public void checkNophone(IRequestCycle cycle) throws Exception{
		IData data = getData();
		IData result = CSViewCall.callone(this, "SS.NophoneWideUnionPaySVC.checkNophone", data);
        setAjax(result);
	}
	
	public void loadNumInfo(IRequestCycle cycle) throws Exception{
		IData data = getData();

        IDataset results = CSViewCall.call(this, "SS.NophoneWideUnionPaySVC.getNumUcaInfo", data);
        IData userInfo = results.getData(0).getData("USER_INFO");
        userInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this,"0", "S",userInfo.getString("USER_STATE_CODESET", "")));
        this.setCustInfoView2(results.getData(0).getData("CUST_INFO"));
        this.setUserInfoView2(userInfo);
        this.setAjax(data);
	}
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception{
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("USER_EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.NophoneWideUnionPayRegSVC.tradeReg", data);
        setAjax(dataset);
	}

	/**
	 * 一机多宽“统付主号码”的身份证号和界面上输入的身份证号必须一致检验
	 * @param cycle
	 * @throws Exception
	 */
	public void getPsptBySn(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IDataset result = CSViewCall.call(this, "CS.CustomerInfoQrySVC.getCustInfoBySn", data);
		setAjax(result.first());
	}

	public abstract void setCustInfoView2(IData cond);

    public abstract void setUserInfoView2(IData cond);
}
