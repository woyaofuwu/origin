
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.alarmdeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.AlarmDealQry;

public class AlarmDealBean extends CSBizBean
{

    public IDataset queryAlarmByCond(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String handleState = data.getString("HANDLE_STATE", "");
        String startTime = data.getString("START_TIME", "");
        String endTime = data.getString("END_TIME", "");
        return AlarmDealQry.queryAlarmByCond(handleState, startTime, endTime, page);
    }

    public IDataset queryAlarmByMonth(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String handleState = data.getString("HANDLE_STATE", "");
        String startTime = data.getString("START_TIME", "");
        String endTime = data.getString("END_TIME", "");
        return AlarmDealQry.queryAlarmByMonth(handleState, startTime, endTime, page);
    }

    public IDataset queryChart(IData data) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        return AlarmDealQry.queryChart(routeEparchyCode);
    }

    public int updAlarmClose(IData data, Pagination page) throws Exception
    {
        String closedState = "1";
        String alarmId = data.getString("ALARM_ID", "");
        return AlarmDealQry.updAlarmClose(closedState, alarmId, page);
    }

    public int updAlarmState(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String handleState = "2";
        String resultInfo = "已处理";
        String alarmId = data.getString("ALARM_ID", "");
        return AlarmDealQry.updAlarmState(handleState, resultInfo, alarmId, page);
    }

    public int updAlarmStateBatch(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String handleState = "2";
        String resultInfo = "已处理";
        String alarmIds = data.getString("ALARM_ID");
        return AlarmDealQry.updAlarmStateBatch(handleState, resultInfo, alarmIds, page);

    }

}
