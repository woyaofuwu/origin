
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.managetask;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ManageTaskQry;

public class ManageTaskBean extends CSBizBean
{

    public int delConfiguredTask(IData tabData, Pagination page) throws Exception
    {
        String tradeTypeCodeList = tabData.getString("multi_TRADE_TYPE_CODE");
        IDataset ids = new DatasetList(tradeTypeCodeList);
        String tradeTypeCodes = "";
        // 将业务编码进行分割并保存在参数容器中
        for (int i = 0; i < ids.size(); i++)
        {
            tradeTypeCodes += "'" + ids.getData(i).getString("PARAM_CODE") + "'";
            if (ids.size() > 1 && i < ids.size() - 1)
                tradeTypeCodes += ",";
        }
        String paramAttr = "9984";
        String paramCode = tradeTypeCodes;
        return ManageTaskQry.delConfiguredTask(paramAttr, paramCode, page);
    }

    public boolean insertTaskConfiguration(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String subSysCode = data.getString("SUBSYS_CODE", "");
        String paramAttr = data.getString("PARAM_ATTR", "");
        String paramCode = data.getString("PARAM_CODE", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        String paramName = data.getString("PARAM_NAME", "");
        String paraCode1 = data.getString("PARA_CODE1", "");
        String startTime = data.getString("START_DATE", "");
        String endTime = data.getString("END_DATE", "");
        return ManageTaskQry.insertTaskConfiguration(subSysCode, paramAttr, paramCode, eparchyCode, paramName, paraCode1, startTime, endTime, page);
    }

    public IDataset isTaskConfigured(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String subSysCode = data.getString("SUBSYS_CODE", "");
        String paramAttr = data.getString("PARAM_ATTR", "");
        String paramCode = data.getString("PARAM_CODE", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        return ManageTaskQry.isTaskConfigured(subSysCode, paramAttr, paramCode, eparchyCode, page);
    }

    public IDataset queryConfiguredTask(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String subSysCode = data.getString("SUBSYS_CODE", "");
        String paramAttr = data.getString("PARAM_ATTR", "");
        String paramCode = data.getString("PARAM_CODE", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        String paramName = data.getString("PARAM_NAME", "");
        return ManageTaskQry.queryConfiguredTask(subSysCode, paramAttr, paramCode, eparchyCode, paramName, page);
    }

    public IDataset queryTaskByCon(IData data, Pagination page) throws Exception
    {
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "");
        String tradeType = data.getString("TRADE_TYPE", "");
        return ManageTaskQry.queryTaskByCon(tradeType, tradeTypeCode, page);
    }

    public int updateConfiguredTask(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String paraCode1 = data.getString("PARA_CODE1", "");
        String paramAttr = data.getString("PARAM_ATTR", "");
        String paramCode = data.getString("PARAM_CODE", "");
        return ManageTaskQry.updateConfiguredTask(paraCode1, paramAttr, paramCode, page);
    }

}
