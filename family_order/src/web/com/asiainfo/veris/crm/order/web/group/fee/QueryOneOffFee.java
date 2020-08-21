
package com.asiainfo.veris.crm.order.web.group.fee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryOneOffFee extends GroupBasePage
{
    /**
     * 查询费用信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryOneFeeList(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        IData svcData = new DataMap();
        svcData.put("GROUP_ID", condData.getString("POP_cond_GROUP_ID"));
        svcData.put("CUST_MANAGER_ID", condData.getString("cond_CUST_MANAGER_ID"));
        svcData.put("START_TIME", condData.getString("cond_START_DATE"));
        svcData.put("END_TIME", condData.getString("cond_END_DATE"));

        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.FeeInfoQrySVC.qryOneFeeList", svcData, getPagination("pageNavTrade"));

        IDataset feeList = dataOutput.getData();
        setFeeList(feeList);

        setFeeCount(dataOutput.getDataCount());
    }

    public abstract void setCondition(IData condition);

    public abstract void setFeeCount(long feeCount);

    public abstract void setFeeList(IDataset feeList);
}
