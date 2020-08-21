
package com.asiainfo.veris.crm.order.web.frame.csview.common.diversifyacct;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class DiversifyAcctViewUtil
{
    /**
     * 获取本账期的开始日期，条件存在td中
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public static String getFirstDayNextAcct(IBizCommon bc, String userId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset userAcctDays = CSViewCall.call(bc, "CS.DiversifyAcctSVC.getFirstDayNextAcct", inparam);
        IData userAcctDay = new DataMap();
        if (IDataUtil.isNotEmpty(userAcctDays))
        {
            userAcctDay = userAcctDays.getData(0);
        }
        return userAcctDay.getString("FIRST_DAY_NEXTACCT", "");

    }

    /**
     * 获取下账期的开始时间，条件存在td中
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public static String getFirstTimeNextAcct(IBizCommon bc, String userId, String routeId) throws Exception
    {

        String firsttimenextAcct = getFirstDayNextAcct(bc, userId, routeId) + SysDateMgr.getFirstTime00000();

        return firsttimenextAcct;
    }

    /**
     * 获取当前账期日的结束时间（传入用户标示）
     * 
     * @param userId
     * @return
     * @throws Exception
     */

    public static String getLastTimeThisAcctday(IBizCommon bc, String userId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset userAcctDays = CSViewCall.call(bc, "CS.DiversifyAcctSVC.getLastTimeThisAcctday", inparam);
        IData userAcctDay = new DataMap();
        if (IDataUtil.isNotEmpty(userAcctDays))
        {
            userAcctDay = userAcctDays.getData(0);
        }
        return userAcctDay.getString("LAST_TIME_NOWACCT", "");

    }

    /**
     * 获取用户的账期信息
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public static IData getUserAcctDay(IBizCommon bc, String userid, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userid);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset userAcctDays = CSViewCall.call(bc, "CS.DiversifyAcctSVC.getUserAcctDay", inparam);
        IData userAcctDay = new DataMap();
        if (IDataUtil.isNotEmpty(userAcctDays))
        {
            userAcctDay = userAcctDays.getData(0);
        }
        return userAcctDay;
    }

    /**
     * 获取用户的账期描述信息 主要为系统中的common错误时，替换原有的月提示信息
     * 
     * @param userId
     * @param desctag
     *            0 为当前账期 1 为下账期 默认当前账期
     * @return
     * @throws Exception
     */
    public static String getUserAcctDescMessage(IBizCommon bc, String userId, String desctag, String routeId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("DESC_TAG", desctag);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset userAcctDays = CSViewCall.call(bc, "CS.DiversifyAcctSVC.getUserAcctDescMessage", inparam);
        IData userAcctDay = new DataMap();
        if (IDataUtil.isNotEmpty(userAcctDays))
        {
            userAcctDay = userAcctDays.getData(0);
        }
        return userAcctDay.getString("DESC_MESS", "");

    }
}
