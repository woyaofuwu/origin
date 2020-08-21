
package com.asiainfo.veris.crm.order.web.group.apnusermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ApnUserImpDetails extends CSBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    /**
     * 查询特定的导入数据明细
     * @param cycle
     * @throws Exception
     */
    public void queryThisImportInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String importId = param.getString("IMPORT_ID");
        String dealState = param.getString("cond_DEAL_STATE");
        IData inParam = new DataMap();
        inParam.put("IMPORT_ID", importId);
        inParam.put("DEAL_STATE", dealState);
        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.queryThisVpmnManagerInfo",
        		inParam, getPagination("pageNav"));
        IDataset dataSet = dataOutput.getData();
        setInfos(dataSet);
        setCondition(param);
        setPageCounts(dataOutput.getDataCount());
    }
}
