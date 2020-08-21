package com.asiainfo.veris.crm.order.web.person.evaluecard;

import org.apache.tapestry.IRequestCycle;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReissueEValueCard extends PersonBasePage{
	public abstract void setInfos(IDataset info);
	public abstract void setInfo(IData info);
	/**
	 *根据手机号码查询一个月中购卡记录
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryECardForCancel(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IDataset result=CSViewCall.call(this, "SS.TelValueCardSVC.QuerySellValueCard", data);
		IDataset idataset=result.getData(0).getDataset("CARD_INFO");
		setAjax(idataset);
		setInfos(idataset);
	}
	/**
	 * 电子有价卡补发(按卡号或者交易流水号)
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void onSubmitInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		String ids = data.getString("ids");
		String IDVALUE = data.getString("IDVALUE");
		IData input = new DataMap();
		String[] params = ids.split(";");
		input.put("ACTION_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		input.put("IDVALUE",IDVALUE);
		input.put("IN_MODE_CODE","0");		
		input.put("REISSUE_TYPE", data.getString("REISSUE_TYPE"));
		if("1".equals(data.getString("REISSUE_TYPE"))){
			input.put("ORI_TRANSACTIONID",params[1]);
		}else{
			input.put("CARD_NO",params[0]);
		}
		CSViewCall.call(this,"SS.TelValueCardSVC.ReissueValueCard", input);
		//显示办理有价卡补发成功
		
	}


}
