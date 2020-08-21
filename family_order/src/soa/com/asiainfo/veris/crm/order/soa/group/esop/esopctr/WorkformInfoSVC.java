package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class WorkformInfoSVC extends GroupOrderService
{
	private static final long serialVersionUID = 1L;

	public IDataset queryComInfo(IData inparam) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		String ibsysid = inparam.getString("IBSYSID", "");
		IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(subscribeInfos))
		{
			resultInfos.add(subscribeInfos.first());
		}
		return resultInfos;
	}
}
