package com.asiainfo.veris.crm.iorder.web.person.replaceCardOrder;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReplaceCardOrder extends PersonBasePage {
	
	public abstract void setInfos(IDataset infos);
	public abstract void setCode(IDataset code);
    public abstract void setInfo(IData info);
	public abstract void setCount(long count);
	
	public void qryOrder(IRequestCycle cycle)throws Exception {
		String alertInfo = "";
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("MOBILE", param.getString("MOBILE"));
		inParam.put("START_DATE", param.getString("START_DATE"));
		inParam.put("END_DATE", param.getString("END_DATE"));
		inParam.put("STATE", param.getString("STATE"));
		IDataOutput output = CSViewCall.callPage(this, "SS.ReplaceCardOrderSVC.qryOrder", inParam,getPagination("recordNav"));
		IDataset info = output.getData();
		if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		this.setInfos(info);
		setCount(output.getDataCount());
		
	}
	//确认
	public void conFirm(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		inParam.put("STATE", "PK");
		inParam.put("RSRV_TAG1", null);
		inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		IData result = CSViewCall.callone(this,"SS.ReplaceCardOrderSVC.conFirm", inParam);
		setAjax(result);
	}
	
	//备货查询
	public void qryReadyGood(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		IDataset qryResult = CSViewCall.call(this,"SS.ReplaceCardOrderSVC.qryReadyGood", inParam);
		IData result = qryResult.getData(0);
		setAjax(result);
	}
	//备货修改状态readyGoodUpdate
	public void readyGoodUpdate(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		inParam.put("STATE", "PD");
		inParam.put("RSRV_TAG1", null);
		inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		IData result = CSViewCall.callone(this,"SS.ReplaceCardOrderSVC.readyGoodUpdate", inParam);
		setAjax(result);
	}
	//发货查询qrysendGood
	public void qrysendGood(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		IDataset qryResult = CSViewCall.call(this,"SS.ReplaceCardOrderSVC.qrysendGood", inParam);
		IData result = qryResult.getData(0);
		setAjax(result);
	}
	//发货修改状态sendGoodUpdate
	public void sendGoodUpdate(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		inParam.put("LOGISTICS_NUMBER", param.getString("LOG_NUMBER"));
		inParam.put("LOGISTICS_COMPANY", param.getString("COMPANY"));
		inParam.put("STATE", "PS");
		inParam.put("RSRV_TAG1", null);
		inParam.put("RSRV_STR1", "3");
		inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		IData result = CSViewCall.callone(this,"SS.ReplaceCardOrderSVC.sendGoodUpdate", inParam);
		setAjax(result);
	}
	//完成修改状态complete
	public void complete(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		inParam.put("STATE", "SS");
		inParam.put("RSRV_TAG1", null);
		inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		IData result = CSViewCall.callone(this,"SS.ReplaceCardOrderSVC.completeUpdate", inParam);
		setAjax(result);
	}
	//cancel取消订单，修改状态
	public void cancel(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("ORDER_ID", param.getString("RADIO_ORDERID"));
		inParam.put("STATE", "CL");
		inParam.put("RSRV_TAG1", null);
		inParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		IData result = CSViewCall.callone(this,"SS.ReplaceCardOrderSVC.completeUpdate", inParam);
		setAjax(result);
	}
	//writeCardUpdate
	public void writeCardUpdate(IRequestCycle cycle)throws Exception {
		IData param = getData();
		IData inParam = new DataMap();
		inParam.put("SIM_CARD_NO", getParameter("SIM_CARD_NO"));
		inParam.put("IMSI", getParameter("IMSI"));
		inParam.put("EMPTY_CARD_ID", getParameter("EMPTY_CARD_ID"));
		inParam.put("OPERA_STAFF", getVisit().getStaffId());
		inParam.put("OPERA_DEPART", getVisit().getDepartId());
		inParam.put("OPERA_CITY_CODE", getVisit().getCityCode());
		inParam.put("RSRV_STR1", "2");
		inParam.put("ORDER_ID", getParameter("ORDER_ID"));
		inParam.put("SERIAL_NUMBER", getParameter("SERIAL_NUMBER"));
		
		
		IData result = CSViewCall.callone(this,"SS.ReplaceCardOrderSVC.writeCardUpdate", inParam);
		
		setAjax(result);
	}
	
	
	
	
	
	
	
	
}
