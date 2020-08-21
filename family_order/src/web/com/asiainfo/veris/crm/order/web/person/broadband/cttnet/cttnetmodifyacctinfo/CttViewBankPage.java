
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetmodifyacctinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttViewBankPage extends PersonBasePage
{
    /**
     * 根据上级银行获取银行数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBank(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData temp = getData("comminfo", true);
        data.putAll(temp);
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyAcctInfoSVC.queryBankInfoBySup", data);
        if (IDataUtil.isNotEmpty(dataset))
        {
            setInfos(dataset);
        }
        setCondition(temp);
    }

    /**
     * 根据银行名称或编码模糊查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBankInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData temp = getData("comminfo", true);
        data.putAll(temp);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyAcctInfoSVC.queryBurBankInfo", data);
        if (IDataUtil.isNotEmpty(dataset))
        {
            setInfos(dataset);
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
