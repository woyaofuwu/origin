
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnettradeinfoquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttTradeInfoQuery extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void getTradeInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData param = new DataMap();
        IDataOutput result = null;
        String startDate = data.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER;
        param.put("START_DATE", startDate);
        if (!data.getString("END_DATE").equals(""))
        {
            String endDate = data.getString("END_DATE") + SysDateMgr.END_DATE;
            param.put("END_DATE", endDate);
        }
        String alertInfo = null;
        // 没有配置权限的，只能查自己办理的工单
        if (data != null)
        {
            if (!data.getString("STAFF_ID", "").equals("") && !data.getString("STAFF_ID", "").equals(getVisit().getStaffId()))
            {
                // 您没有查询其他员工工单的权限！
                CSViewException.apperr(CrmUserException.CRM_USER_1082);
            }
            param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        }
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        result = CSViewCall.callPage(this, "SS.CttTradeInfoQuerySVC.getTradeInfo", param, getPagination("pagin"));

        if (IDataUtil.isEmpty(result.getData()))
        {
            alertInfo = "未获取到相关资料！请修改查询条件！";
            this.setAjax("ALERT_INFO", alertInfo);
        }

        setInfos(result.getData());
        setCount(result.getDataCount());

    }

    public void initTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData condition = getData();
        if (IDataUtil.isEmpty(condition))
        {
            String nowDate = SysDateMgr.getSysDate();
            condition.put("START_DATE", nowDate);
            condition.put("END_DATE", nowDate);
        }
        setCondition(condition);
    }

    public abstract void setCondition(IData data);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset dataset);

}
