
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct;

import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupDiversifyUtilCommon;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;

public class DiversifyAcctUtil
{

    /**
     * 根据指定的结账日和预约标志判断用户的账期信息
     * 
     * @param userAcctDay
     *            用户账期信息
     * @param serialNumber
     *            用户服务号码
     * @param day
     *            指定的结账日
     * @param ifBooking
     *            是否预约 true:预约;false:非预约
     * @return true:用户结账日与指定的结账日day相等; false:用户结账日与指定的结账日day不相等
     */
    public static boolean checkUserAcctDay(IData userAcctDay, String day, boolean ifBooking)
    {
        // 判断用户结账日和指定的结账日是否有值
        if (userAcctDay == null || userAcctDay.size() == 0 || day == null || "".equals(day))
        {
            return false;
        }

        // 本账期结账日
        String acctDay = userAcctDay.getString("ACCT_DAY", "");
        // 下账期结账日,如果下账期结账日不存在,则下账期结账日为本账期结账日
        String nextAcctDay = "".equals(userAcctDay.getString("NEXT_ACCT_DAY", "")) ? acctDay : userAcctDay.getString("NEXT_ACCT_DAY");

        // 预约情况下只需判断下账期结账日是否为指定结账日
        if (ifBooking)
        {
            return day.equals(nextAcctDay);
        }
        else
        {// 非预约情况判断本账期结账日和下账期结账日是否都为指定的结账日
            return day.equals(acctDay) && day.equals(nextAcctDay);
        }
    }

    /**
     * 根据指定的结账日和预约标志判断用户的账期信息 并给出相应的提示信息
     * 
     * @param userAcctDay
     *            用户账期信息
     * @param serialNumber
     *            用户服务号码
     * @param day
     *            指定结账日,如:"1"
     * @param ifBooking
     *            预约标志 true:预约;false:非预约
     * @throws Exception
     */
    public static void checkUserAcctDayWithWarn(IData userAcctDay, String serialNumber, String day, boolean ifBooking) throws Exception
    {
        // 用户服务号码判断
        if (serialNumber == null || "".equals(serialNumber))
        {

            CSAppException.apperr(GrpException.CRM_GRP_658);
        }
        // 判断用户结账日
        if (userAcctDay == null || userAcctDay.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_659, serialNumber);
        }

        // 判断用户结账日
        if (day == null || "".equals(day))
        {
            CSAppException.apperr(GrpException.CRM_GRP_660, serialNumber);
        }

        // 获取用户的账期分布
        // TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"),FALSE("4");
        String acctDayTag = GroupDiversifyUtilCommon.getUserAcctDayDistribute(userAcctDay, day);
        // 根据账期分布结合预约标志,给出相应的提示信息

        String firstDayNextAcct = userAcctDay.getString("FIRST_DAY_NEXTACCT");// 下账期第一天
        if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(acctDayTag) && !ifBooking)
        {

            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_30, serialNumber, firstDayNextAcct);

        }
        else if (GroupBaseConst.UserDaysDistribute.TRUE_FALSE.getValue().equals(acctDayTag))
        {

            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_31, serialNumber, firstDayNextAcct);

        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE_FALSE.getValue().equals(acctDayTag))
        {

            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_31, serialNumber, firstDayNextAcct);

        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE.getValue().equals(acctDayTag))
        {

            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_32, serialNumber);
        }

    }

    /**
     * 修改原有的生效账期段。
     * 
     * @param inparam
     * @param acctdays
     * @return
     * @throws Exception
     */
    public static IDataset dealAcctByModi(IData inparam, IDataset acctdays) throws Exception
    {
        IDataset acctdaysdataset = new DatasetList();
        if (inparam == null)
            return null;

        String userId = inparam.getString("USER_ID", "");
        if (userId.equals(""))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户标示失败！");

        // 生效方式 默认为下账期生效
        String active_tag = inparam.getString("CHG_MODE", "");
        if (active_tag.equals(""))
            active_tag = "1";

        // 变更类型：默认为主动变更类
        String chg_type = inparam.getString("CHG_TYPE", "");
        if (chg_type.equals(""))
            chg_type = "0";

        String nowdate = inparam.getString("NOW_DATE", "");
        if (nowdate.equals(""))
            nowdate = SysDateMgr.getSysDate();

        String newacctday = inparam.getString("ACCT_DAY", "");
        if (newacctday.equals(""))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户新的账期日信息失败！");

        String nextacctdate = "";

        for (int i = 0; i < acctdays.size(); i++)
        {
            IData acctday = acctdays.getData(i);
            String startdate = acctday.getString("START_DATE", "");
            String firstday = acctday.getString("FIRST_DATE", "");
            String acctdaynum = acctday.getString("ACCT_DAY", "");
            String end_date = acctday.getString("END_DATE", "");
            // 已经过了首次出账日
            if (firstday.compareTo(nowdate) < 0)
            {

                if (active_tag.equals(0))
                {
                    // 立即生效,则推算前一个账期的结束时间点
                    nextacctdate = getLastTimeBeforeAcctday(nowdate, Integer.parseInt(acctdaynum));
                }
                else
                {
                    // 下账期生效，则推算当前账期的结束时间点
                    nextacctdate = getLastTimeThisAcctday(nowdate, Integer.parseInt(acctdaynum));
                }

            }
            else
            {
                // 首次结账日还未出账

                if (active_tag.equals(0))
                {
                    // 立即生效,则推算前一个账期的结束时间点
                    nextacctdate = SysDateMgr.getLastSecond(startdate);
                }
                else
                {
                    // 下账期生效，则推算当前账期的结束时间点
                    nextacctdate = SysDateMgr.getLastSecond(firstday);
                    // 主动变更需要判断账期天数(15到45天之间)
                    /*
                     * int daynums = SysDateMgr.dayInterval(startdate, firstday); if (15 > daynums)
                     * CSAppException.apperr(TradeException.CRM_TRADE_95, "账期变更，产品的账期小于15天,不允许变更!"); if (daynums > 45)
                     * CSAppException.apperr(TradeException.CRM_TRADE_95, "账期变更，产品的账期大于45天,不允许变更!");
                     */
                }
            }

            // 如果结束时间没有发生改动，不做修改。
            if (end_date.equals(nextacctdate))
                continue;
            acctday.put("END_DATE", nextacctdate);
            acctday.put("CHG_DATE", nowdate);
            acctday.put("CHG_MODE", active_tag);
            acctday.put("CHG_TYPE", chg_type);
            acctday.put("RSRV_TAG1", acctday.getString("RSRV_TAG1", "0").equals("1") ? "1" : "0");
            acctday.put("STATE", "MODI");
            acctday.put("MODIFY_TAG", "2");
            acctdaysdataset.add(acctday);
        }
        return acctdaysdataset;
    }

    /**
     * 处理分散用户的元素时间
     * 
     * @param inParam
     * @param elementData
     * @throws Exception
     */
    public static void dealDiversifyElementStartAndEndDate(IData inParam, IData elementData) throws Exception
    {

        boolean diversifyBooking = inParam.getBoolean(GroupBaseConst.DIVERSIFY_BOOKING, false);
        boolean effect_now = inParam.getBoolean(GroupBaseConst.EFFECT_NOW, false);
        String acceptTime = inParam.getString("ACCEPT_TIME", SysDateMgr.getSysTime());// 受理时间
        String productPreDate = inParam.getString("PRODUCT_PRE_DATE", "");// 预约时间

        String eparchyCode = inParam.getString("EPARCHY_CODE", "");

        String productId = elementData.getString("PRODUCT_ID", "");
        String packageId = elementData.getString("PACKAGE_ID", "");
        String elementId = elementData.getString("ELEMENT_ID", "");

        String elementTypeCode = elementData.getString("ELEMENT_TYPE_CODE", "");

        String startDate = elementData.getString("START_DATE", "");
        String endDate = elementData.getString("END_DATE", "");

        String dealStartDate = startDate.substring(0, 10);
        String dealEndDate = endDate.substring(0, 10);

        String modifyTag = elementData.getString("MODIFY_TAG", "");

        IDataset pkgElementList = ProductPkgInfoQry.qryPackageElement(productId, packageId, elementId, eparchyCode);

        if (IDataUtil.isEmpty(pkgElementList))
            CSAppException.apperr(ElementException.CRM_ELEMENT_116);

        IData pkgElementData = pkgElementList.getData(0);

        String startAbsoluteDate = pkgElementData.getString("START_ABSOLUTE_DATE");
        String enableTag = pkgElementData.getString("ENABLE_TAG");
        String startOffset = pkgElementData.getString("START_OFFSET");
        String startUnit = pkgElementData.getString("START_UNIT");

        String endAbsoluteDate = pkgElementData.getString("END_ABSOLUTE_DATE");
        String endEnableTag = pkgElementData.getString("END_ENABLE_TAG");
        String endOffset = pkgElementData.getString("END_OFFSET");
        String endUnit = pkgElementData.getString("END_UNIT");

        String cancelAbsoluteDate = pkgElementData.getString("CANCEL_ABSOLUTE_DATE", "");
        String cancelTag = pkgElementData.getString("CANCEL_TAG", "");
        String cancelOffset = pkgElementData.getString("CANCEL_OFFSET", "");
        String cancelUnit = pkgElementData.getString("CANCEL_UNIT", "");

        String rsrvStr1 = pkgElementData.getString("RSRV_STR1", "");

        String firstTimeNextAcct = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();

        // 设置预约时间
        if (diversifyBooking && StringUtils.isBlank(productPreDate))
        {
            productPreDate = firstTimeNextAcct;
        }

        // 处理服务时间
        if ("S".equals(elementTypeCode) || "Z".equals(elementTypeCode))
        {
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                if (diversifyBooking)
                {
                    if (effect_now)
                    {
                        dealStartDate = firstTimeNextAcct;
                    }
                    else
                    {
                        ElementUtil.dealSelectedElementStartDateAndEndDate(elementData, firstTimeNextAcct, effect_now, eparchyCode);

                        dealStartDate = elementData.getString("START_DATE", firstTimeNextAcct);
                    }
                }
                else
                {
                    if (effect_now)
                    {
                        dealStartDate = acceptTime;
                    }
                    else
                    {
                        dealStartDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);
                    }
                }

                dealEndDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                if (diversifyBooking)
                {
                    if (effect_now)
                    {
                        dealEndDate = SysDateMgr.getLastSecond(firstTimeNextAcct);
                    }
                    else
                    {
                        dealEndDate = SysDateMgr.cancelDate(cancelTag, firstTimeNextAcct, cancelOffset, cancelUnit);
                    }
                }
                else
                {
                    if (effect_now)
                    {
                        dealEndDate = acceptTime;
                    }
                    else
                    {
                        dealEndDate = SysDateMgr.cancelDate(cancelTag, cancelAbsoluteDate, cancelOffset, cancelUnit);
                    }
                }
            }
        }
        else if ("D".equals(elementTypeCode))
        {
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                boolean isPkgExist = false;// 是否订购了此包中的元素

                if ("1".equals(rsrvStr1) || "2".equals(rsrvStr1))
                {
                    String userId = elementData.getString("USER_ID", "");
                    String userIdA = elementData.getString("USER_ID_A", "-1");

                    IDataset userDiscntList = UserDiscntInfoQry.getExistUserSingleProductDis(userId, userIdA, productId, packageId, elementId);

                    if (IDataUtil.isNotEmpty(userDiscntList))
                    {

                        if (diversifyBooking)
                        {
                            for (int i = 0; i < userDiscntList.size(); i++)
                            {
                                if (userDiscntList.getData(i).getString("END_DATE").compareTo(productPreDate) > 0)
                                {
                                    isPkgExist = true;
                                    break;
                                }
                            }
                        }
                    }
                    isPkgExist = IDataUtil.isNotEmpty(userDiscntList);
                }

                if (diversifyBooking)
                {
                    if (isPkgExist && rsrvStr1.equals("1"))
                    {
                        // 如果在TD_B_PRODUCT_PACKAGE表中RSRV_STR1 = "1", 表示在该包中, 如果用户已经订购包中资费,
                        // 则新订购的资费下账期生效(不在依赖enable_tag字段)
                        dealStartDate = SysDateMgr.startDate("1", startAbsoluteDate, startOffset, startUnit);
                    }
                    else if (!isPkgExist && rsrvStr1.equals("2"))
                    {
                        // 如果在TD_B_PRODUCT_PACKAGE表中RSRV_STR1 = "2", 表示在该包中, 如果用户没有订购过包中的资费,
                        // 则新订购的资费立即生效(不在依赖enable_tag字段)
                        dealStartDate = SysDateMgr.startDate("0", startAbsoluteDate, startOffset, startUnit);
                    }
                    else if (effect_now)
                    {
                        dealStartDate = firstTimeNextAcct;
                    }
                    else
                    {
                        dealStartDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);
                    }
                }
                else
                {

                    if (isPkgExist && rsrvStr1.equals("1"))
                    {
                        // 如果在TD_B_PRODUCT_PACKAGE表中RSRV_STR1 = "1", 表示在该包中, 如果用户已经订购包中资费,
                        // 则新订购的资费下账期生效(不在依赖enable_tag字段)
                        dealStartDate = SysDateMgr.startDate("1", startAbsoluteDate, startOffset, startUnit);
                    }
                    else if (!isPkgExist && rsrvStr1.equals("2"))
                    {
                        // 如果在TD_B_PRODUCT_PACKAGE表中RSRV_STR1 = "2", 表示在该包中, 如果用户没有订购过包中的资费,
                        // 则新订购的资费立即生效(不在依赖enable_tag字段)
                        dealStartDate = SysDateMgr.startDate("0", startAbsoluteDate, startOffset, startUnit);
                    }
                    else if (effect_now)
                    {
                        dealStartDate = acceptTime;
                    }
                    else
                    {
                        dealStartDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);
                    }
                }

                dealEndDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);

            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                if (diversifyBooking)
                {
                    if (effect_now)
                    {
                        dealEndDate = SysDateMgr.getLastSecond(firstTimeNextAcct);
                    }
                    else
                    {
                        dealEndDate = SysDateMgr.cancelDate(cancelTag, firstTimeNextAcct, cancelOffset, cancelUnit);
                    }
                }
                else
                {
                    if (effect_now)
                    {
                        dealEndDate = acceptTime;
                    }
                    else
                    {
                        dealEndDate = SysDateMgr.cancelDate(cancelTag, cancelAbsoluteDate, cancelOffset, cancelUnit);
                    }
                }
            }
        }

        String space = " ";

        if (startDate.split(space).length == 2)
        {
            if (startDate.split(space)[0].equals(dealStartDate.split(space)[0]))
            {
                dealStartDate = startDate;
            }
        }

        if (endDate.split(space).length == 2)
        {
            if (endDate.split(space)[0].equals(dealEndDate.split(space)[0]))
            {
                dealEndDate = endDate;
            }
        }

        elementData.put("START_DATE", dealStartDate);
        elementData.put("END_DATE", dealEndDate);

    }

    /**
     * 判断号码是否存在集团付费 (陕西特殊处理,判断号码是否已经订购集团产品)
     * 
     * @throws Exception
     */
    public static IData existGrpPayRelation(String userId, String routeId) throws Exception
    {

        IData result = new DataMap();
        String resultcode = "false";
        String resultinfo = "该号码允许变更账期";

        IData userAcctDayInfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);
        String firstTimeNextAcct = userAcctDayInfo.getString("FIRST_DAY_NEXTACCT", "") + SysDateMgr.START_DATE_FOREVER;

        IDataset idsDataset = RelaUUInfoQry.qryRelaUUByUserIdBAndEndDate(userId, firstTimeNextAcct);
        if (IDataUtil.isNotEmpty(idsDataset))
        {
            for (int i = 0, sz = idsDataset.size(); i < sz; i++)
            {
                IData uuData = idsDataset.getData(i);

                // 查询集团用户
                String userIdA = uuData.getString("USER_ID_A");
                IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
                if (IDataUtil.isEmpty(grpUserInfo))
                {
                    continue;
                }
                String productId = grpUserInfo.getString("PRODUCT_ID", "");

                // 查询集团客户资料
                String grpCustId = grpUserInfo.getString("CUST_ID", "");
                if (StringUtils.isEmpty(grpCustId))
                {
                    continue;
                }
                IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
                if (IDataUtil.isEmpty(grpCustInfos))
                {
                    continue;
                }

                // 判断该用户的产品是否支持分散的产品，不支持分散的产品不允许办理账期变更
                IDataset divProductListData = ParamInfoQry.getCommparaByCode("CSM", "7013", null, "ZZZZ");
                IDataset productDataset = DataHelper.filter(divProductListData, "PARAM_CODE=" + productId);
                if (IDataUtil.isEmpty(productDataset))
                {
                    resultcode = "true";
                    resultinfo = "该客户已开通[" + grpUserInfo.getString("PRODUCT_NAME") + "]业务，为保证业务的正常使用，建议维持当前的自然月账期！";
                    result.put("RESULT", resultcode);
                    result.put("RESULT_DESC", resultinfo);
                    return result;
                }

            }
        }

        IDataset idsBBDataset = RelaBBInfoQry.qryRelaBBByUserIdBAndEndDate(userId, firstTimeNextAcct);
        if (IDataUtil.isNotEmpty(idsBBDataset))
        {
            for (int i = 0, sz = idsBBDataset.size(); i < sz; i++)
            {
                IData uuData = idsBBDataset.getData(i);

                // 查询集团用户
                String userIdA = uuData.getString("USER_ID_A");
                IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
                if (IDataUtil.isEmpty(grpUserInfo))
                {
                    continue;
                }
                String productId = grpUserInfo.getString("PRODUCT_ID", "");

                // 查询集团客户资料
                String grpCustId = grpUserInfo.getString("CUST_ID", "");
                if (StringUtils.isEmpty(grpCustId))
                {
                    continue;
                }
                IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
                if (IDataUtil.isEmpty(grpCustInfos))
                {
                    continue;
                }

                // 判断该用户的产品是否支持分散的产品，不支持分散的产品不允许办理账期变更
                IDataset divProductListData = ParamInfoQry.getCommparaByCode("CSM", "7013", null, "ZZZZ");
                IDataset productDataset = DataHelper.filter(divProductListData, "PARAM_CODE=" + productId);
                if (IDataUtil.isEmpty(productDataset))
                {
                    resultcode = "true";
                    resultinfo = "该客户已开通[" + grpUserInfo.getString("PRODUCT_NAME") + "]业务，为保证业务的正常使用，建议维持当前的自然月账期！";
                    result.put("RESULT", resultcode);
                    result.put("RESULT_DESC", resultinfo);
                    return result;
                }

            }
        }

        IDataset acctInfos = PayRelaInfoQry.qryGrpAcctPayrelatinByMebUserId(userId, routeId);
        if (IDataUtil.isNotEmpty(acctInfos))
        {
            resultcode = "true";
            resultinfo = "该号码办理了集团付费，为保证业务的正常使用，建议维持当前的自然月账期。";
            result.put("RESULT", resultcode);
            result.put("RESULT_DESC", resultinfo);
            return result;
        }

        IDataset aaInfos = AcctInfoQry.qryAllRelAAByUserA(userId, routeId);
        if (IDataUtil.isNotEmpty(aaInfos))
        {
            resultcode = "true";
            resultinfo = "该号码办理了集团账户统付，为保证业务的正常使用，建议维持当前的自然月账期。";
            result.put("RESULT", resultcode);
            result.put("RESULT_DESC", resultinfo);
            return result;
        }

        result.put("RESULT", resultcode);
        result.put("RESULT_DESC", resultinfo);
        return result;

    }

    /**
     * 获取本账期的开始日期，条件存在td中
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public static String getFirstDayNextAcct(String userId) throws Exception
    {

        String firstdaynextAcct = "";

        IData useracctday = getUserAcctDay(userId);

        if (IDataUtil.isEmpty(useracctday))
        {

            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_17, userId);
        }

        String firstdaynextacct = useracctday.getString("FIRST_DAY_NEXTACCT", "");

        if (StringUtils.isEmpty(firstdaynextacct))
        {
            firstdaynextAcct = SysDateMgr.getFirstDayOfNextMonth();
        }
        else
        {
            firstdaynextAcct = firstdaynextacct;
        }
        return firstdaynextAcct;
    }

    /**
     * 获取本账期开始日期，条件存在td中
     * 
     * @return YYYY-MM-DD
     * @throws Exception
     */
    public static String getFirstDayThisAcct(String userId) throws Exception
    {

        String firstDayThisAcct = "";
        IData useracctday = getUserAcctDay(userId);
        if (IDataUtil.isEmpty(useracctday))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_17, userId);
        }

        String daythisaccttemp = useracctday.getString("FIRST_DAY_THISACCT", "");

        if (StringUtils.isBlank(daythisaccttemp))
        {
            firstDayThisAcct = SysDateMgr.getFirstDayOfThisMonth();
        }
        else
        {
            firstDayThisAcct = daythisaccttemp;
        }
        return firstDayThisAcct;

    }

    /**
     * 获取下账期的开始时间，条件存在td中
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public static String getFirstTimeNextAcct(String userId) throws Exception
    {

        String firsttimenextAcct = getFirstDayNextAcct(userId) + SysDateMgr.getFirstTime00000();

        return firsttimenextAcct;
    }

    /**
     * 获取下账期开始时间（传入结账日）
     * 
     * @param nowdate
     * @param acctnum
     *            结账日信息
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getFirstTimeNextAcctday(String nowdate, int acctnum) throws Exception
    {
        String nextacctdate = getNextDateByDay(nowdate, acctnum, "0");
        String first_time = SysDateMgr.addDays(nextacctdate, -1) + SysDateMgr.getFirstTime00000();
        return first_time;
    }

    /**
     * 获取本账期开始时间 yyyy-MM-dd HH:mm:ss
     * 
     * @param userId
     * @return
     * @throws Exception
     */

    public static String getFirstTimeThisAcct(String userId) throws Exception
    {
        String firstTimeThisAcct = getFirstDayThisAcct(userId) + SysDateMgr.START_DATE_FOREVER;

        return firstTimeThisAcct;
    }

    /**
     * 判断系统是否支持多账期
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static String getJudeAcctDayTag() throws Exception
    {
        String ifuseraccttag = "true";
        IDataset commparas = CommparaInfoQry.getCommpara("CSM", "2012", "GRP_USER_ACCT_TAG", "ZZZZ");

        if (commparas.size() == 0)
        {
            ifuseraccttag = "true";
        }
        else
        {
            IData commpara = commparas.getData(0);
            String useraccttag = commpara.getString("PARA_CODE1", "false");
            if ("true".equals(useraccttag))
                ifuseraccttag = "true";
        }
        return ifuseraccttag;

    }

    /**
     * 判断系统是否支持多账期
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static String getJudeAcctDayTag(IData param) throws Exception
    {

        String ifuseraccttag = param.getString("IF_USERACCT", "");
        if ("".equals(ifuseraccttag))
        {

            ifuseraccttag = getJudeAcctDayTag();

        }
        return ifuseraccttag;

    }

    /**
     * 获取前一个账期日的结束时间（传入结账日）
     * 
     * @param nowdate
     * @param acctnum
     *            结账日信息
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getLastDayBeforeAcctday(String nowdate, int acctnum) throws Exception
    {
        String nextacctdate = getNextDateByDay(nowdate, acctnum, "1");
        String end_date = SysDateMgr.addDays(nextacctdate, -1);
        return end_date;
    }

    /**
     * 获取下账期的结束日期，条件存在td中
     * 
     * @param userId
     * @return
     * @throws Exception
     */

    public static String getLastDayNextAcct(String userId) throws Exception
    {

        String lastdaynextAcct = "";

        IData useracctday = getUserAcctDay(userId);
        if (IDataUtil.isEmpty(useracctday))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_17, userId);
        }
        String daythisaccttemp = useracctday.getString("LAST_DAY_NEXTACCT", "");

        if (StringUtils.isEmpty(daythisaccttemp))
        {
            lastdaynextAcct = SysDateMgr.getNextMonthLastDate();
        }
        else
        {
            lastdaynextAcct = daythisaccttemp;
        }
        return lastdaynextAcct;
    }

    /**
     * 获取本账期结束日期，条件存在td中
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getLastDayThisAcct(String userId) throws Exception
    {

        String lastdaythisacct = "";
        IData useracctday = getUserAcctDay(userId);
        if (IDataUtil.isEmpty(useracctday))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_17, userId);
        }

        String daythisaccttemp = useracctday.getString("LAST_DAY_THISACCT", "");

        if (StringUtils.isEmpty(daythisaccttemp))
        {
            lastdaythisacct = SysDateMgr.getLastDateThisMonth();
        }
        else
        {
            lastdaythisacct = daythisaccttemp;
        }
        return lastdaythisacct;
    }

    /**
     * 获取当前账期日的结束时间（传入结账日）
     * 
     * @param nowdate
     * @param acctnum
     *            结账日信息
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getLastDayThisAcctday(String nowdate, int acctnum) throws Exception
    {
        String nextacctdate = getNextDateByDay(nowdate, acctnum, "0");
        String end_date = SysDateMgr.addDays(nextacctdate, -1);
        return end_date;
    }

    //
    /**
     * 获取前一个账期日的结束时间（传入结账日）
     * 
     * @param nowdate
     * @param acctnum
     *            结账日信息
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getLastTimeBeforeAcctday(String nowdate, int acctnum) throws Exception
    {
        String nextacctdate = getNextDateByDay(nowdate, acctnum, "1");
        String end_date = SysDateMgr.addDays(nextacctdate, -1) + SysDateMgr.getEndTime235959();
        return end_date;
    }

    /**
     * 获取当前账期日的结束时间（传入用户有效的账期时间段和当前系统的时间）
     * 
     * @param nowdate
     * @param acctnum
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getLastTimeThisAcctday(IData acctDay, String nowdate) throws Exception
    {

        String firstdate = acctDay.getString("FIRST_DATE");
        String acctdaynum = acctDay.getString("ACCT_DAY");
        String lasttimeacctday = "";
        if (firstdate.compareTo(nowdate) < 0)
        {
            lasttimeacctday = getLastTimeThisAcctday(nowdate, Integer.parseInt(acctdaynum));
        }
        else
        {
            lasttimeacctday = SysDateMgr.getLastSecond(firstdate);
        }
        return lasttimeacctday;
    }

    /**
     * 获取当前账期日的结束时间（传入结账日）
     * 
     * @param nowdate
     * @param acctnum
     *            结账日信息
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getLastTimeThisAcctday(String nowdate, int acctnum) throws Exception
    {
        String nextacctdate = getNextDateByDay(nowdate, acctnum, "0");
        String end_date = SysDateMgr.addDays(nextacctdate, -1) + SysDateMgr.getEndTime235959();
        return end_date;
    }

    // /**
    // * 根据user_id, query_month 查询指定月份用户的账期信息
    // * @param pd
    // * @param userId 用户标识
    // * @param queryMonth 指定查询月份 yyyy-mm
    // * @return
    // * @throws Exception
    // */
    // public static IDataset getUserAcctDayByQryMonth( String userId, String queryMonth)throws Exception
    // {
    // CSAppEntity dao = new CSAppEntity(pd);
    //
    // IData temp = new DataMap();
    // temp.put("USER_ID", userId);
    // temp.put("MONTH", queryMonth); //yyyy-mm
    //
    // }
    //
    // /**
    // * 根据user_id, query_month 查询指定月份用户的账期信息
    // * @param pd
    // * @param userId 用户标识
    // * @param queryMonth 指定查询月份 yyyy-mm
    // * @return
    // * @throws Exception
    // */
    // public static IDataset getUserAcctDayByQryMonth2( String userId, String queryMonth)throws Exception
    // {
    // CSAppEntity dao = new CSAppEntity(pd);
    //
    // IData temp = new DataMap();
    // temp.put("USER_ID", userId);
    // temp.put("MONTH", queryMonth); //yyyy-mm-dd
    //
    // }
    //
    /**
     * 获取当前账期日的结束时间（传入用户标示）
     * 
     * @param nowdate
     * @param acctnum
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getLastTimeThisAcctday(String userId, String nowDate) throws Exception
    {
        // String userId = inparam.getString("USER_ID", "");
        if (StringUtils.isEmpty(userId))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户ID失败！");

        // String nowdate = inparam.getString("NOW_DATE", "");
        if (StringUtils.isEmpty(nowDate))
            nowDate = SysDateMgr.getSysDate();

        IDataset acctDays = UserAcctDayInfoQry.getValidUserAcctDays(userId, nowDate);
        IData acctDay = acctDays.getData(0);
        return getLastTimeThisAcctday(acctDay, nowDate);
    }

    /**
     * 开户时新增账户结账日台账信息 需要传入结账日信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getNewAccountAcctDayByOpenUser(IData inparam) throws Exception
    {
        IDataset acctdaysdataset = new DatasetList();
        if (inparam == null)
            return null;

        String AcctId = inparam.getString("ACCT_ID", "");

        String nowdate = inparam.getString("NOW_DATE", "");
        if (nowdate.equals(""))
            nowdate = SysDateMgr.getSysDate();
        String newacctday = inparam.getString("ACCT_DAY", "");
        if (newacctday.equals(""))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取账户结账日信息失败！");

        // 新增账期段
        IData newAcctDay = new DataMap();
        String nextacctdate = getLastTimeThisAcctday(nowdate, Integer.parseInt(newacctday));
        String insid = SeqMgr.getInstId();
        newAcctDay.put("ACCT_ID", AcctId);
        newAcctDay.put("INST_ID", insid);
        newAcctDay.put("ACCT_DAY", newacctday);
        // newAcctDay.put("START_DATE", nowdate);
        // newAcctDay.put("CHG_DATE", nowdate);
        newAcctDay.put("START_DATE", SysDateMgr.getSysDate() + SysDateMgr.getFirstTime00000());// 湖南特殊要求， 取当前零点时间
        newAcctDay.put("CHG_DATE", SysDateMgr.getSysDate() + SysDateMgr.getFirstTime00000());
        newAcctDay.put("FIRST_DATE", SysDateMgr.getNextSecond(nextacctdate));
        newAcctDay.put("END_DATE", SysDateMgr.getTheLastTime());
        newAcctDay.put("CHG_MODE", "0");
        newAcctDay.put("CHG_TYPE", "1");
        newAcctDay.put("MODIFY_TAG", "0");
        newAcctDay.put("STATE", "ADD");
        newAcctDay.put("RSRV_TAG1", "1");
        acctdaysdataset.add(newAcctDay);

        // 返回结果
        return acctdaysdataset;
    }

    /**
     * 开户时新增账期数据 需要传入结账日信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getNewAcctDayByOpenUser(IData inparam) throws Exception
    {
        IDataset acctdaysdataset = new DatasetList();
        if (inparam == null)
            return null;

        String userId = inparam.getString("USER_ID", "");

        String nowdate = inparam.getString("NOW_DATE", "");
        if (nowdate.equals(""))
            nowdate = SysDateMgr.getSysDate();
        String newacctday = inparam.getString("ACCT_DAY", "");
        if (newacctday.equals(""))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取账期日信息失败！");

        // 新增账期段
        IData newAcctDay = new DataMap();
        String nextacctdate = getLastTimeThisAcctday(nowdate, Integer.parseInt(newacctday));
        newAcctDay.put("USER_ID", userId);
        newAcctDay.put("ACCT_DAY", newacctday);
        newAcctDay.put("START_DATE", nowdate);
        newAcctDay.put("CHG_DATE", nowdate);
        newAcctDay.put("FIRST_DATE", SysDateMgr.getNextSecond(nextacctdate));
        newAcctDay.put("END_DATE", SysDateMgr.getTheLastTime());
        newAcctDay.put("CHG_MODE", "0");
        newAcctDay.put("CHG_TYPE", "1");
        newAcctDay.put("MODIFY_TAG", "0");
        newAcctDay.put("STATE", "ADD");
        newAcctDay.put("RSRV_TAG1", "1");
        acctdaysdataset.add(newAcctDay);

        // 返回结果
        return acctdaysdataset;
    }

    /**
     * 获取用户的账期数据
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getNewAcctDaysByModi(IData inparam) throws Exception
    {
        IDataset acctdaysdataset = new DatasetList();
        if (inparam == null)
            return null;

        String userId = inparam.getString("USER_ID", "");
        if (userId.equals(""))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户标示失败！");

        // 帐期变更方式：0-立即生效，1-下账期生效 默认为下账期生效
        String active_tag = inparam.getString("CHG_MODE", "");
        if (active_tag.equals(""))
            active_tag = "1";

        // 帐期变更类型：0-主动变更，1-被动变更：默认为主动变更类
        String chg_type = inparam.getString("CHG_TYPE", "");
        if (chg_type.equals(""))
            chg_type = "0";

        String nowdate = inparam.getString("NOW_DATE", "");
        if (nowdate.equals(""))
            nowdate = SysDateMgr.getSysTime();
        inparam.put("NOW_DATE", nowdate);

        String newacctday = inparam.getString("ACCT_DAY", "");
        if (newacctday.equals(""))
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户新的账期日信息失败！");

        IDataset acctdayset = UcaInfoQry.qryUserAcctDaysByUserId(userId);

        if (acctdayset == null || acctdayset.size() == 0)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "当前用户无有效的账期,请联系系统管理员！");
        }

        int len = acctdayset.size();
        // 已生效的账期段记录集
        IDataset aciveAcctDay = new DatasetList();
        // 未生效的账期段记录集
        IDataset noAciveAcctDay = new DatasetList();

        for (int i = 0; i < len; i++)
        {
            IData temp = acctdayset.getData(i);
            String startdate = temp.getString("START_DATE", "");
            if (startdate.compareTo(nowdate) <= 0)
            {
                aciveAcctDay.add(temp);
            }
            else
            {
                noAciveAcctDay.add(temp);
            }
        }

        // 修改有效账期段数据
        if (aciveAcctDay.size() == 0)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "当前用户无有效的账期！");
        }
        if (aciveAcctDay.size() > 1)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "当前用户多条有效的账期！");
        }
        // 修改用户现有生效账期段
        IDataset tempset = dealAcctByModi(inparam, aciveAcctDay);
        acctdaysdataset.addAll(tempset);

        // 终止未生效的账期段数据

        for (int j = 0; j < noAciveAcctDay.size(); j++)
        {
            IData acctday = noAciveAcctDay.getData(j);
            acctday.put("END_DATE", nowdate);
            acctday.put("STATE", "DEL");
            acctday.put("MODIFY_TAG", "1");
            acctday.put("CHG_DATE", nowdate);
            acctday.put("CHG_MODE", active_tag);
            acctday.put("CHG_TYPE", chg_type);
            acctday.put("RSRV_TAG1", acctday.getString("RSRV_TAG1", "0").equals("1") ? "1" : "0");
            acctdaysdataset.add(acctday);
        }

        // 新增账期段
        String nextacctdate = aciveAcctDay.getData(0).getString("END_DATE");
        if (tempset != null && tempset.size() != 0)
            nextacctdate = tempset.getData(0).getString("END_DATE");

        IData newAcctDay = new DataMap();
        String startdate = SysDateMgr.getNextSecond(nextacctdate);// 新账期开始时间

        if (startdate.compareTo(nowdate) > 0)
            nextacctdate = getLastTimeThisAcctday(startdate, Integer.parseInt(newacctday));
        else
            nextacctdate = getLastTimeThisAcctday(nowdate, Integer.parseInt(newacctday));

        String insid = SeqMgr.getInstId();
        /*
         * int daynums = SysDateMgr.dayInterval(startdate, SysDateMgr.getNextSecond(nextacctdate)); if (daynums > 45)
         * CSAppException.apperr(TradeException.CRM_TRADE_95, "主动账期变更，产品的账期大于45天,不允许变更!"); if (daynums < 15) {
         * nextacctdate = SysDateMgr.getLastSecond(SysDateMgr.getAddMonthsNowday(1,
         * SysDateMgr.getNextSecond(nextacctdate))); }
         */
        newAcctDay.put("USER_ID", userId);
        newAcctDay.put("INST_ID", insid);
        newAcctDay.put("ACCT_DAY", newacctday);
        newAcctDay.put("START_DATE", startdate);
        newAcctDay.put("CHG_DATE", nowdate);
        newAcctDay.put("CHG_MODE", active_tag);
        newAcctDay.put("CHG_TYPE", chg_type);
        newAcctDay.put("FIRST_DATE", SysDateMgr.getNextSecond(nextacctdate));
        newAcctDay.put("END_DATE", SysDateMgr.getTheLastTime());
        newAcctDay.put("STATE", "ADD");
        newAcctDay.put("RSRV_TAG1", "0");
        newAcctDay.put("MODIFY_TAG", "0");

        acctdaysdataset.add(newAcctDay);

        // 返回结果
        return acctdaysdataset;
    }

    /**
     * 获取下账期日的开始日期（传入结账日）
     * 
     * @param nowdate
     * @param acctnum
     *            结账日信息
     * @param accttag
     * @return
     * @throws Exception
     */
    public static String getNextAcctday(String nowdate, int acctnum) throws Exception
    {
        String nextacctdate = getNextDateByDay(nowdate, acctnum, "0");
        return nextacctdate;
    }

    /**
     * 获取指定时间相邻的日号等于acctnum的日期
     * 
     * @param nowdate
     * @param acctnum
     * @param accttag
     *            1 返回小于当前时间的最近指定日的日期 0 返回大于当前时间的最近指定日的日期
     * @return
     * @throws Exception
     */
    public static String getNextDateByDay(String nowdate, int acctnum, String accttag) throws Exception
    {

        String nextacctdate = "";
        if (accttag == null || accttag.equals(""))
            accttag = "0";

        int nowday = Integer.parseInt(nowdate.substring(8, 10));

        if (accttag.equals("0"))
        {
            if (acctnum > nowday)
            {
                nextacctdate = getOffsetMonAndDay(nowdate, 0, acctnum);
            }
            else
            {
                nextacctdate = getOffsetMonAndDay(nowdate, 1, acctnum);
            }

        }
        else if (accttag.equals("1"))
        {
            if (acctnum <= nowday)
            {
                nextacctdate = getOffsetMonAndDay(nowdate, 0, acctnum);
            }
            else
            {
                nextacctdate = getOffsetMonAndDay(nowdate, -1, acctnum);
            }

        }

        return nextacctdate;
    }

    /**
     * 返回当前时间偏移某个指定月份后，指定day的日期
     * 
     * @param nowdate
     *            当前日期
     * @param month_offset
     *            偏移月份
     * @param acctnum
     *            指定日
     * @return
     * @throws Exception
     */
    public static String getOffsetMonAndDay(String nowdate, int month_offset, int day) throws Exception
    {
        String format = Utility.getTimestampFormat(nowdate);
        Date dtNew = DateUtils.addMonths(SysDateMgr.string2Date(nowdate, format), month_offset);
        return SysDateMgr.date2String(DateUtils.setDays(dtNew, day), format);
    }

    /**
     * 获取用户的账期信息
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public static IData getUserAcctDay(String userid) throws Exception
    {

        IData userAcctDay = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userid);

        // td.put("USER_ACCT_DAY", acctDay);

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
    public static String getUserAcctDescMessage(String userId, String desctag) throws Exception
    {

        String descmess = "";
        if (desctag == null)
        {
            desctag = "0";
        }

        if ("0".equals(desctag))
        {
            descmess = "当前账期";
            String firstdaythisacct = getFirstDayThisAcct(userId);
            String lastdaythisacct = getLastDayThisAcct(userId);
            if (firstdaythisacct != null && !firstdaythisacct.equals("") && lastdaythisacct != null && !lastdaythisacct.equals(""))
            {
                descmess = descmess + firstdaythisacct + "到" + lastdaythisacct;
            }
        }
        else if ("1".equals(desctag))
        {
            descmess = "下账期";
            String firstdaynextacct = getFirstDayNextAcct(userId);
            String lastdaynextacct = getLastDayNextAcct(userId);
            if (firstdaynextacct != null && !firstdaynextacct.equals("") && lastdaynextacct != null && !lastdaynextacct.equals(""))
            {
                descmess = descmess + firstdaynextacct + "到" + lastdaynextacct;
            }
        }
        else
        {
            descmess = "当前账期";
            String firstdaythisacct = getFirstDayThisAcct(userId);
            String lastdaythisacct = getLastDayThisAcct(userId);
            if (firstdaythisacct != null && !firstdaythisacct.equals("") && lastdaythisacct != null && !lastdaythisacct.equals(""))
            {
                descmess = descmess + firstdaythisacct + "到" + lastdaythisacct;
            }
        }

        return descmess;
    }

    /**
     * 判断用户是否为分散的用户
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static boolean ifNaturUser(String userId) throws Exception
    {
        IData useracctday = getUserAcctDay(userId);
        if (useracctday == null)
        {
            return false;
        }
        return true;
    }

    /**
     * 计算就近结账日
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryAcctInfoDay() throws Exception
    {

        String route_eparchy_code = BizRoute.getRouteId();
        String trade_eparchy_code = CSBizBean.getTradeEparchyCode();
        String eparchy_code = CSBizBean.getTradeEparchyCode();

        if (trade_eparchy_code == null || "".equals(trade_eparchy_code))
        {
            trade_eparchy_code = CSBizBean.getTradeEparchyCode();
        }

        if (route_eparchy_code == null || "".equals(route_eparchy_code))
        {
            route_eparchy_code = CSBizBean.getTradeEparchyCode();
        }

        if (trade_eparchy_code.equals(route_eparchy_code))
        {
            eparchy_code = CSBizBean.getTradeEparchyCode();
        }
        else
        {

            eparchy_code = route_eparchy_code;
        }

        IDataset acctInfoDay = CommparaInfoQry.getCommparaAllCol("CSM", "880", "ACCT_DAY", eparchy_code);

        IDataset acctInfoDaynew = new DatasetList();

        IDataset acctInfoDaynew1 = new DatasetList();

        String para_code1_new = SysDateMgr.getSysTime().substring(8, 10);

        int compar_para_code1 = 1;

        int para_code2 = 0;

        int para_code3 = 0;

        String monthFirstDay = "0";

        if (acctInfoDay != null && acctInfoDay.size() > 0)
        {

            if ("0".equals(para_code1_new.substring(0, 1)))
            {

                compar_para_code1 = Integer.parseInt(para_code1_new.substring(1, 2));
            }
            else
            {
                compar_para_code1 = Integer.parseInt(para_code1_new);
            }

            for (int i = 0; i < acctInfoDay.size(); i++)
            {

                IData acctInfo = acctInfoDay.getData(i);

                para_code2 = Integer.parseInt(acctInfo.getString("PARA_CODE2"));
                para_code3 = Integer.parseInt(acctInfo.getString("PARA_CODE3"));

                if ((compar_para_code1 < para_code3) && (para_code2 <= compar_para_code1))
                {

                    // acctInfoDay.set(0, acctInfo);
                    if ("1".equals(acctInfo.getString("PARA_CODE1")) && "1".equals(acctInfo.getString("PARA_CODE5")))
                    {

                        acctInfoDaynew.add(acctInfoDay.getData(acctInfoDay.size() - 1));

                        monthFirstDay = "1";
                    }
                    else
                    {
                        acctInfoDaynew.add(acctInfo);
                    }
                }
                else
                {

                    if ("1".equals(monthFirstDay) && "1".equals(acctInfo.getString("PARA_CODE5")) && (i == acctInfoDay.size() - 1))
                    {
                        acctInfoDaynew1.add(acctInfoDay.getData(0));
                    }
                    else
                    {

                        acctInfoDaynew1.add(acctInfo);
                    }
                }
            }

            for (int j = 0; j < acctInfoDaynew1.size(); j++)
            {

                acctInfoDaynew.add(acctInfoDaynew1.getData(j));
            }
        }

        return acctInfoDaynew;
    }

    /**
     * 判断用户的账期分布
     * 
     * @param userAcctDay
     *            用户账期信息
     * @param day
     *            指定的结账日
     * @return string: true 出账日为目标出账日 false-true 下账期出账日为目标出账日 true-false 当前账期的出账日为目标出账日
     *         false-false用户的出账日非目标出账日，并且存在未生效的账期变更 false 用户的出账日非目标出账日
     */
    public static String userAcctDayDistribution(IData userAcctDay, String day)
    {
        // 判断用户结账日和指定的结账日是否有值
        if (userAcctDay == null || userAcctDay.size() == 0 || day == null || "".equals(day))
        {
            return "";
        }

        // 本账期结账日
        String acctDay = userAcctDay.getString("ACCT_DAY", "");
        // 下账期结账日,如果下账期结账日不存在,则下账期结账日为本账期结账日
        String nextAcctDay = "".equals(userAcctDay.getString("NEXT_ACCT_DAY", "")) ? acctDay : userAcctDay.getString("NEXT_ACCT_DAY");

        if (day.equals(acctDay) && day.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.TRUE.getValue();
        }

        if (!day.equals(acctDay) && day.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue();
        }

        if (day.equals(acctDay) && !day.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.TRUE_FALSE.getValue();
        }

        if (!day.equals(acctDay) && !day.equals(nextAcctDay) && !acctDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.FALSE_FALSE.getValue();
        }

        if (!day.equals(acctDay) && !day.equals(nextAcctDay) && acctDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.FALSE.getValue();
        }

        return "";
    }
}
