
package com.asiainfo.veris.crm.order.web.person.agent;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AgentPayManage extends PersonBasePage
{

    /**
     * 删除代理商银行账户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void deleteAgentInfo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.AgentPayManageSVC.deleteAgentInfo", data);
        this.setAjax(results.getData(0));
    }

    /**
     * 导出代理商信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void importAgentInfos(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.AgentPayManageSVC.importAgentPayInfos", data);
        this.setAjax(results.getData(0));
    }

    /**
     * 新增或修改代理商银行账户信息初始化载入
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadAgentPayForm(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData agentPay = new DataMap();
        if (!StringUtils.equals(data.getString("AGENT_ID"), ""))
        {
            data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            IDataset agentPays = CSViewCall.call(this, "SS.AgentPayManageSVC.getAgentPayInfo", data);
            if (IDataUtil.isNotEmpty(agentPays))
            {
                agentPay = agentPays.getData(0);
            }
        }
        IDataset results = CSViewCall.call(this, "SS.AgentPayManageSVC.qryAgentCityCode", data);
        setCityInfo(results);
        setCond(agentPay);
    }

    /**
     * 代理商管理主页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset results = CSViewCall.call(this, "SS.AgentPayManageSVC.qryAgentCityCode", data);
        setCityInfo(results);
        data.put("CITY_CODE", getVisit().getCityCode());
        setCond(data);
    }

    /**
     * 保存代理商银行账户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.AgentPayManageSVC.saveAgentPayInfo", data);
        this.setAjax(results.getData(0));
    }

    /**
     * 代理商银行账户信息管理查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAgentPayInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        Pagination page = getPagination("recordNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataOutput result = CSViewCall.callPage(this, "SS.AgentPayManageSVC.queryAgentPayInfos", data, page);
        setRecordCount(result.getDataCount());
        setInfos(result.getData());
    }

    public abstract void setCityInfo(IDataset cityInfo);

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount);
}
