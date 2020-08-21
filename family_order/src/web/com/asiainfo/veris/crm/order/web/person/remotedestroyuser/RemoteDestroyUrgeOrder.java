package com.asiainfo.veris.crm.order.web.person.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class RemoteDestroyUrgeOrder extends PersonBasePage {
	public abstract void setCondition(IData condition);
	public abstract void setRowIndex(int rowIndex);
	public abstract void setCountTrade(long countTrade);
	public abstract void setDestroyInfoList(IDataset tradeInfoList);
	public abstract void setDestroyInfo(IData destroyInfo);
	public abstract void setCount(long count);
	public void queryRemindOrder(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		IDataset destroyInfoList = new DatasetList();
			IDataOutput out2 = CSViewCall.callPage(this, "SS.RemoteDestroyUserSVC.queryDestroyReceiOrder", pageData, getPagination("nav"));
			IDataset orderInfos = out2.getData();
			setCount(out2.getDataCount());
			if(IDataUtil.isNotEmpty(orderInfos)){
				destroyInfoList=orderInfos;
			}
		setDestroyInfoList(destroyInfoList);
		setAjax(destroyInfoList);
	}
}
