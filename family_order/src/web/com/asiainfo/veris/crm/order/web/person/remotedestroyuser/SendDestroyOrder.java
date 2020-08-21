package com.asiainfo.veris.crm.order.web.person.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class SendDestroyOrder extends PersonBasePage {
	public abstract void setCondition(IData condition);
	public abstract void setRowIndex(int rowIndex);
	public abstract void setCountTrade(long countTrade);
	public abstract void setDestroyInfoList(IDataset tradeInfoList);
	public abstract void setDestroyInfo(IData tradeInfo);
	
	public void queryDestroyOrder(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		//调后台服务
		IDataOutput out = CSViewCall.callPage(this, "SS.RemoteDestroyUserSVC.queryDestroyOrder", pageData, getPagination("queryTradeNav"));
//		setDestroyInfoList(out.getData());
//        setCountTrade(out.getDataCount());
//        setAjax(out.getData());
		IDataset result = out.getData();
		setDestroyInfoList(result);
		setCountTrade(result.size());
		setAjax(result);
	}

	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
	    IDataset resultInfo = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.applyCancelAccount", data);
	    if(DataSetUtils.isNotBlank(resultInfo))
		{
			IData resultData = resultInfo.getData(0);
			String isSuccess = resultData.getString("IS_SUCCESS", "");
			String resultDesc = resultData.getString("RESULT_DESC", "未返回任何信息！");
			if("0".equals(isSuccess))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "派单异常：" + resultDesc);
			}
			setAjax(resultData);
		}
	}
}
