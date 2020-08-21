
package com.asiainfo.veris.crm.order.web.person.handgatheringfee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class HandGatheringFee extends PersonBasePage
{
    public abstract IData getFee();

    /**
     * 提交收款补录
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        data.put("FEE_AMOUNT", data.getInt("FEE_TOTAL"));
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.HandGatheringFeeSVC.handGatheringFee", data);
        if (IDataUtil.isNotEmpty(dataset))
        {
            returnData = dataset.getData(0);
        }
        setAjax(returnData);
    }

    /**
     * 打印手工收款补录
     * 
     * @param cycle
     * @throws Exception
     */
    public void printHandGathering(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.HandGatheringFeeSVC.printHandGathering", data);
        setAjax(dataset);
    }
    
    public void createPaySubmit(IRequestCycle cycle)throws Exception
	{
		IData pageData = this.getData();
		double feeAmount = pageData.getDouble("FEE_AMOUNT", 0)*100;
		String feeName = pageData.getString("FEE_NAME");
		IDataset goodLists = new DatasetList();

		IData temp1 =new DataMap();
		String orderId =  System.currentTimeMillis() + "";
		temp1.put("PEER_TRADE_ID", orderId);
		temp1.put("GOODS_NAME",  feeName);
		temp1.put("GOODS_PRICE", (int)feeAmount+"");
		temp1.put("GOODS_NUM", "1");
		temp1.put("TOTAL_MONEY", (int)feeAmount+"");
		goodLists.add(temp1);
		
		IData input = new DataMap();
		input.put("PEER_ORDER_ID", orderId);
		input.put("ORDER_FEE", (int)feeAmount+"");
		String sn = pageData.getString("SERIAL_NUMBER");
		if("undefined".equals(sn) || StringUtils.isBlank(sn))
		{
			sn = "收款补录";
		}
		input.put("SERIAL_NUMBER", sn);
		input.put("ORDER_DESC","订单总费用");
		input.put("MERCHANT_ID", "1000");
		input.put("GOODS_LIST", goodLists.toString());

		IDataset dataset = CSViewCall.call(this, "payment.order.IPayAccessSV.createPayDetail", input);
		this.setAjax(dataset);
	}

    public abstract void setFee(IData fee);
}
