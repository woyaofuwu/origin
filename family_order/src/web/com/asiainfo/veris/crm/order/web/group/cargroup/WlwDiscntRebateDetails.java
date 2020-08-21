
package com.asiainfo.veris.crm.order.web.group.cargroup;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class WlwDiscntRebateDetails extends CSBasePage
{

    /**
     * 查询特定的导入数据明细
     * @param cycle
     * @throws Exception
     */
    public void queryRebateDetailsByOprSeq(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String oprSeqId = param.getString("OPR_SEQ_ID");
        IData inParam = new DataMap();
        inParam.put("OPR_SEQ", oprSeqId);
        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GrpWlwDiscntRebateApproveSVC.queryRebateDetailsByOprSeq",
        		inParam, getPagination("pageNav"));
        IDataset dataSet = dataOutput.getData();
        setInfos(dataSet);
        setCondition(param);
        setPageCounts(dataOutput.getDataCount());
    }
    
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);
}
