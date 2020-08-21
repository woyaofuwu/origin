
package com.asiainfo.veris.crm.order.web.person.sundryquery.agentBackBillId;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class AgentBillIdListQry extends PersonQueryPage
{
    /**
     * 初始化页面数据
     * 代理商买断套卡库存清单查询
     * @param cycle
     * @throws Exception
     */

    public void qryAgentBillIdListByCond(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataOutput results = CSViewCall.callPage(this, "SS.QueryInfoSVC.qryAgentUserListByCond", data, getPagination("qryInfoNav"));
        setInfos(results.getData());
        setCount(results.getDataCount());
        setCondition(data);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);
}
