
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.newfastauthconfig;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.NewFastAuthConfigQry;

public class NewFastAuthConfigBean extends CSBizBean
{

    public int[] applyAuthFunc(IDataset opMenus) throws Exception
    {
        return NewFastAuthConfigQry.applyAuthFunc(opMenus);
    }

    public void delFastAuth(IData data) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        NewFastAuthConfigQry.delFastAuth(data);
    }

    public IDataset judgeMenuExist(IData data) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String menuId = data.getString("MENU_ID", "");
        String subsysCode = data.getString("SUBSYS_CODE", "");
        return NewFastAuthConfigQry.judgeMenuExist(menuId, subsysCode);
    }

    public IDataset judgeMenuIsNot(IData data) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String menuId = data.getString("MENU_ID", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        return NewFastAuthConfigQry.judgeMenuIsNot(menuId, startDate, endDate);
    }

    public IDataset queryAuthTradeType(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String menuId = data.getString("MENU_ID", "");
        String nowDate = data.getString("NOW_DATE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        return NewFastAuthConfigQry.queryAuthTradeType(menuId, nowDate, startDate, endDate, page);
    }

    public IDataset queryAuthTradeType2(int dateFlag, String menuId, String endDate1, Pagination page) throws Exception
    {
        return NewFastAuthConfigQry.queryAuthTradeType2(dateFlag, menuId, endDate1, page);
    }

    public IDataset queryChildSysRange(IData data) throws Exception
    {
        return NewFastAuthConfigQry.queryChildSysRange();
    }

    public IDataset queryMenus(IData data) throws Exception
    {
        String parentMenuId = data.getString("PARENT_MENU_ID", "");
        String subsysCode = data.getString("SUBSYS_CODE", "");
        return NewFastAuthConfigQry.queryMenus(parentMenuId, subsysCode);
    }

    public IDataset queryStaffcode(IData data, Pagination page) throws Exception
    {
        String staffId = data.getString("STAFF_ID", "");
        String staffName = data.getString("STAFF_NAME", "");
        return NewFastAuthConfigQry.queryStaffcode(staffId, staffName, page);
    }

    public IDataset queryTradeChildRange(IData data) throws Exception
    {
        return NewFastAuthConfigQry.queryTradeChildRange();
    }

    public IDataset queryTradeTypeRange(IData data) throws Exception
    {
        return NewFastAuthConfigQry.queryTradeTypeRange();
    }

    public void updateFastAuth(IData data) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String menuId = data.getString("MENU_ID", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String updateStaffId = this.getVisit().getStaffId();
        String updateDepartId = this.getVisit().getDepartId();
        String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        NewFastAuthConfigQry.updateFastAuth(menuId, startDate, endDate, updateStaffId, updateDepartId, updateTime);
    }

}
