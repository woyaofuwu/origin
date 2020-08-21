
package com.asiainfo.veris.crm.order.web.group.vpmnmanagermgr.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class VpmnDispatchEdit extends CSBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    /**
     * @Description:查询特定的导入数据明细
     * @author sungq3
     * @date 2014-05-20
     * @param cycle
     * @throws Exception
     */
    public void queryThisImportInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String importId = param.getString("IMPORT_ID");
        String dealState = param.getString("cond_DEAL_STATE");
        IData inparam = new DataMap();
        inparam.put("IMPORT_ID", importId);
        inparam.put("DEAL_STATE", dealState);
        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.queryThisVpmnManagerInfo", inparam, getPagination("pageNav"));
        IDataset dataset = dataOutput.getData();
        setInfos(dataset);
        setCondition(param);
        setPageCounts(dataOutput.getDataCount());
    }
}
