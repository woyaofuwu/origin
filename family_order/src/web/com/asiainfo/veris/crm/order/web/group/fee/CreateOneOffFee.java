
package com.asiainfo.veris.crm.order.web.group.fee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class CreateOneOffFee extends GroupBasePage
{
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("CUST_ID", condData.getString("CUST_ID"));
        svcData.put("FEE_SUB_LIST", new DatasetList(condData.getString("FEE_SUB_LIST", "[]")));

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.CreateOneOffFeeSVC.crtTrade", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * 查询费用信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryOneFeeList(IRequestCycle cycle) throws Exception
    {
        IData svcData = new DataMap();
        svcData.put("GROUP_ID", getData().getString("GROUP_ID"));

        IDataset feeList = CSViewCall.call(this, "CS.FeeInfoQrySVC.qryOneFeeList", svcData);

        // 设置费用信息
        setOneFeeList(feeList);
    }

    public abstract void setOneFeeList(IDataset oneFeeList);

}
