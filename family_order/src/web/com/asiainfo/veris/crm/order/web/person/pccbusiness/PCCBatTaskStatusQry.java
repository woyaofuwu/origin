package com.asiainfo.veris.crm.order.web.person.pccbusiness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

/**
 * 关于配合做好全网五项PCC策略
 */
public abstract class PCCBatTaskStatusQry extends PersonQueryPage {

	public abstract void setCondition(IData condition);

	public abstract void setInfo(IData infos);

	public abstract void setInfos(IDataset a);

	public void qryPCCBatTaskStatus(IRequestCycle cycle) throws Exception {
		IData param = getData("cond", true);

        IDataset infos = CSViewCall.call(this, "SS.PCCBusinessSVC.qryPCCBatTaskStatus", param);
        IData params = new DataMap();
        IData reData = new DataMap();
        IData res = new DataMap();
        for (int i = 0; IDataUtil.isNotEmpty(infos) && i < infos.size(); i++)
        {
            params.clear();
            reData.clear();
            reData = infos.getData(i);

            params.put("EP_SN", reData.getString("EP_SN"));
            params.put("TASK_ID", reData.getString("TASK_ID"));
            params.put("SYSTEM_TYPE", param.getString("SYSTEM_TYPE"));
            res = CSViewCall.call(this, "SS.PCCBusinessSVC.qryPCCBatTaskStatusIboss", params).getData(0);

            if ("1".equals(res.getString("RESULT")))
            {
                break;
            }

        }
        infos = CSViewCall.call(this, "SS.PCCBusinessSVC.qryPCCBatTaskStatus", param);
        if (infos.size() == 0)
        {
            res.put("RESULT", "2");
        }
        setInfos(infos);
        setAjax(res);
	}
}