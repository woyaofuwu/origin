
package com.asiainfo.veris.crm.order.web.person.sundryquery.agentBackBillId;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class AgentBackBillIdListQry extends PersonQueryPage
{
    /**
     * 初始化页面数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void initPageInfo(IRequestCycle cycle) throws Exception
    {
        this.qryDepartKinds(cycle);
    }

    public void qryAgentBackBillIdListByCond(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataOutput results = CSViewCall.callPage(this, "SS.QueryInfoSVC.qryAgentUserBackListByCond", data, getPagination("qryInfoNav"));
        setInfos(results.getData());
        setCount(results.getDataCount());
        setCondition(data);
    }

    /**
     * 查询部门类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryDepartKinds(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset departKinds = CSViewCall.call(this, "SS.QueryInfoSVC.queryDepartKinds", input);
        if (IDataUtil.isNotEmpty(departKinds))
        {
            for (int i = 0; i < departKinds.size(); i++)
            {
                String departKind = departKinds.getData(i).getString("DEPART_KIND");
                String departKindCode = departKinds.getData(i).getString("DEPART_KIND_CODE");
                departKinds.getData(i).put("DEPART_KIND", "[" + departKindCode + "]" + departKind);
            }
            setDepartKinds(departKinds);
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    public abstract void setDepartKinds(IDataset departKinds);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);
}
