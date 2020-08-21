
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupDiversifyUtilCommon;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;

public class GroupDiversifyUtilBean
{
    public static final String NATURE_DAY = "1";

    /**
     * 分散账期接口校验处理(处理成员)
     * 
     * @param map
     * @throws Exception
     */
    public static void checkMebDiversifyIntf(IData map) throws Exception
    {
        // 1. 集团级业务不用处理
        if (BizRoute.getRouteId().equals(Route.CONN_CRM_CG))
            return;

        String serialNumber = map.getString("SERIAL_NUMBER");

        IData mebUserData = UcaInfoQry.qryUserInfoBySn(serialNumber);

        // 2. 用户信息不存在不用处理
        if (IDataUtil.isEmpty(mebUserData))
            return;

        if (StringUtils.isBlank(map.getString("PRODUCT_ID")))
        {
            return;
        }

        IDataset userAcctDayList = UserAcctDayInfoQry.getUserAcctDays(mebUserData.getString("USER_ID", ""), mebUserData.getString("EPARCHY_CODE", ""));

        if (IDataUtil.isEmpty(userAcctDayList))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_16, serialNumber);
        }

        // 将IDataset格式的账期数据转化为IData格式
        IData userAcctDayData = rebuildUserAcctDay(userAcctDayList);

        // 获取账期分布
        String acctDayDistribute = GroupDiversifyUtilCommon.getUserAcctDayDistribute(userAcctDayData, NATURE_DAY);

        // 3. 用户账期为自然月账期不用处理
        if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(acctDayDistribute))
            return;

        // 付费方式,默认个人付费
        String payType = map.getString("PAY_TYPE_CODE", "P");

        String firstDayNextAcct = userAcctDayData.getString("FIRST_DAY_NEXTACCT");

        IData exceptionData = new DataMap();
        exceptionData.put("SERIAL_NUMBER", serialNumber);
        exceptionData.put("FIRST_DAY_NEXTACCT", firstDayNextAcct);
        exceptionData.put("ACCTDAY_DISTRIBUTE", acctDayDistribute);

        // 是否必须要求为自然月账期产品
        boolean ifNatureDayProduct = getNatureProductTag(map.getString("PRODUCT_ID"));

        // 是否可以立即生效的产品
        boolean ifImmediProduct = getSpecialImmeProductTag(map.getString("PRODUCT_ID"));

        // 子交易编码
        String xSubtransCode = map.getString(GroupBaseConst.X_SUBTRANS_CODE);

        // 4. 批量接口、集团成员处理接口处理
        if (("GrpBat").equals(xSubtransCode) || ("ProcessGrpMem").equals(xSubtransCode))
        {
            // 根据成员的付费方式判断是否支持预约

            if ("P".equals(payType))// 个人付费
            {
                if (ifNatureDayProduct)
                {
                    // 排除不能做业务的情况
                    checkUserAcctDayWithException(exceptionData, true);

                    if (!ifImmediProduct && GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(acctDayDistribute))
                    {
                        map.put("PRODUCT_PRE_DATE", firstDayNextAcct);
                        map.put("IF_BOOKING", "true");
                    }
                }
            }
            else
            // 集团付费
            {
                // 排除不能做业务的情况
                checkUserAcctDayWithException(exceptionData, true);

                if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(acctDayDistribute))
                {
                    map.put("PRODUCT_PRE_DATE", firstDayNextAcct);
                    map.put("IF_BOOKING", "true");
                }
            }
        }
        else
        { // 5. 处理反向过来的接口(反向不支持预约)
            if (!"P".equals(payType) || ifNatureDayProduct)
            {
                CSAppException.apperr(AcctDayException.CRM_ACCTDAY_28, serialNumber);
            }
        }
    }

    /**
     * 判断用户账期信息, 抛出异常
     * 
     * @param acctDayDistribute
     * @param isBooking
     * @throws Exception
     */
    public static void checkUserAcctDayWithException(IData inParam, boolean isBooking) throws Exception
    {
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String firstDayNextAcct = inParam.getString("FIRST_DAY_NEXTACCT");
        String acctDayDistribute = inParam.getString("ACCTDAY_DISTRIBUTE");

        if (GroupBaseConst.UserDaysDistribute.FALSE.getValue().equals(acctDayDistribute))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_28, serialNumber);
        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(acctDayDistribute) && !isBooking)
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_26, serialNumber, firstDayNextAcct);
        }
        else if (GroupBaseConst.UserDaysDistribute.TRUE_FALSE.getValue().equals(acctDayDistribute) || GroupBaseConst.UserDaysDistribute.FALSE_FALSE.getValue().equals(acctDayDistribute))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_27, serialNumber, firstDayNextAcct);
        }
    }

    /**
     * 根据产品编码判断产品是否必须要求成员为自然月账期产品
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean getNatureProductTag(String productId) throws Exception
    {
        boolean ifNatureProduct = true;

        IDataset sepcialProductList = CommparaInfoQry.getCommparaAllCol("CSM", "7013", productId, "ZZZZ");

        if (IDataUtil.isNotEmpty(sepcialProductList))
            ifNatureProduct = false;

        return ifNatureProduct;
    }

    /**
     * 判断产品是否为特殊产品（分散账期下允许业务可立即生效的产品）
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean getSpecialImmeProductTag(String productId) throws Exception
    {
        boolean ifspecialProduct = false;

        IDataset sepcialProductList = CommparaInfoQry.getCommparaAllCol("CSM", "7014", productId, "ZZZZ");

        if (IDataUtil.isNotEmpty(sepcialProductList))
            ifspecialProduct = true;

        return ifspecialProduct;
    }

    /**
     * 根据用户结账日信息构建新的账期格式数据
     * 
     * @param userAcctDayList
     * @throws Exception
     */
    public static IData rebuildUserAcctDay(IDataset userAcctDayList) throws Exception
    {
        IData builerAcctDay = new DataMap();

        String nowDate = SysDateMgr.getSysTime();
        String space = " ";// 空格

        String acctDay = "";
        String firstDate = "";
        String startDate = "";
        String endDate = "";
        String nextAcctDay = "";
        String nextFirstDate = "";
        String nextStartDate = "";
        String nextEndDate = "";

        for (int i = 0, row = userAcctDayList.size(); i < row; i++)
        {
            IData userAcctDayData = userAcctDayList.getData(i);

            if (userAcctDayData.getString("START_DATE", "").compareTo(nowDate) > 0)
            {
                nextAcctDay = userAcctDayData.getString("ACCT_DAY");
                nextFirstDate = userAcctDayData.getString("FIRST_DATE", "").split(space)[0];
                nextStartDate = userAcctDayData.getString("START_DATE", "").split(space)[0];
                nextEndDate = userAcctDayData.getString("END_DATE", "").split(space)[0];
            }
            else
            {
                acctDay = userAcctDayData.getString("ACCT_DAY");
                firstDate = userAcctDayData.getString("FIRST_DATE", "").split(" ")[0];
                startDate = userAcctDayData.getString("START_DATE", "").split(" ")[0];
                endDate = userAcctDayData.getString("END_DATE", "").split(" ")[0];
            }
        }

        AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate, startDate, nextStartDate);
        AcctTimeEnvManager.setAcctTimeEnv(env);

        String firstDayThisAcct = SysDateMgr.getFirstDayOfThisMonth();// 本账期第一天
        String lastDayThisAcct = SysDateMgr.getLastDateThisMonth();// 本账期最后一天
        String firstDayNextAcct = SysDateMgr.getFirstDayOfNextMonth();// 下账期第一天

        builerAcctDay.put("ACCT_DAY", acctDay);
        builerAcctDay.put("FIRST_DATE", firstDate);
        builerAcctDay.put("START_DATE", startDate);
        builerAcctDay.put("END_DATE", endDate);

        builerAcctDay.put("NEXT_ACCT_DAY", nextAcctDay);
        builerAcctDay.put("NEXT_FIRST_DATE", nextFirstDate);
        builerAcctDay.put("NEXT_START_DATE", nextStartDate);
        builerAcctDay.put("NEXT_END_DATE", nextEndDate);

        builerAcctDay.put("FIRST_DAY_THISACCT", firstDayThisAcct);
        builerAcctDay.put("LAST_DAY_THISACCT", lastDayThisAcct);
        builerAcctDay.put("FIRST_DAY_NEXTACCT", firstDayNextAcct);

        return builerAcctDay;
    }
}
