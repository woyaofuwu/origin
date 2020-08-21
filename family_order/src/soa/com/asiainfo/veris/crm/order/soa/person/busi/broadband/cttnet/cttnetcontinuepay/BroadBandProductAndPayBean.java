/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

/**
 */
public class BroadBandProductAndPayBean extends CSBizBean
{

    /**
     * 计算结束时间
     * 
     * @param elementData
     * @return
     * @throws Exception
     */
    public String calEndDate(ProductModuleData elementData) throws Exception
    {
        String tomorrowDate = SysDateMgr.addDays(SysDateMgr.getSysDate(), 1) + SysDateMgr.getFirstTime00000();

        if ("0".equals(elementData.getModifyTag()) && elementData.getStartDate() != null && !"".equals(elementData.getStartDate()) && elementData.getStartDate().compareTo(tomorrowDate) < 0)
        {
            elementData.setStartDate(tomorrowDate);//
        }
        if ("1".equals(elementData.getEndEnableTag()))
        {
            if ("1".equals(elementData.getEndUnit()))
            {
                return SysDateMgr.addDays(elementData.getStartDate().substring(0, 10), Integer.parseInt(elementData.getEndOffSet())).substring(0, 10) + SysDateMgr.getEndTime235959();
            }
            if ("2".equals(elementData.getEndUnit()))
            {
                return (SysDateMgr.addDays(SysDateMgr.getAddMonthsNowday(Integer.parseInt(elementData.getEndOffSet()), elementData.getStartDate()), -1)).substring(0, 10) + SysDateMgr.getEndTime235959();
            }
            else if ("3".equals(elementData.getEndUnit()))
            {
                return SysDateMgr.addDays(SysDateMgr.getAddMonthsNowday(Integer.parseInt(elementData.getEndOffSet()), SysDateMgr.addDays(SysDateMgr.getLastDateThisMonth(), 1)), -1) + SysDateMgr.getEndTime235959();
            }
            else if ("4".equals(elementData.getEndUnit()))
            {
                return (SysDateMgr.addDays(SysDateMgr.addYears(elementData.getStartDate(), Integer.parseInt(elementData.getEndOffSet())), -1)).substring(0, 10) + SysDateMgr.getEndTime235959();
            }
            else
            {
                return SysDateMgr.END_DATE_FOREVER;
            }
        }
        else if ("0".equals(elementData.getEndEnableTag()))
        {
            if ("0".equals(elementData.getModifyTag()))
            {
                return elementData.getEndAbsoluteDate() == null ? SysDateMgr.END_TIME_FOREVER : elementData.getEndAbsoluteDate();
            }
            else
            {
                return SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();
            }
        }
        else
        {
            return elementData.getEndAbsoluteDate();
        }
    }

    /**
     * 对资费,服务 等资料的 依赖,互斥进行判断,copy from 金老板的产品变更
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset dealSelectedElementsForChg(IData data) throws Exception
    {

        String eparchyCode = data.getString("EPARCHY_CODE");
        // this.setRouteId(eparchyCode);
        String userProductId = data.getString("USER_PRODUCT_ID");
        String nextProductId = data.getString("NEXT_PRODUCT_ID");
        String sysDate = SysDateMgr.getSysDate();
        IDataset userOldProductElements = null;
        if (StringUtils.isNotBlank(nextProductId))
        {
            userOldProductElements = ProductInfoQry.getProductElements(userProductId, eparchyCode);
        }
        IDataset elements = new DatasetList(data.getString("ELEMENTS"));
        int size = elements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = elements.getData(i);
            ProductTimeEnv env = new ProductTimeEnv();
            ProductModuleData pmd = null;
            if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
            {
                pmd = new SvcData(element);

            }
            else
            {
                pmd = new DiscntData(element);
            }

            if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
            {

                if ("5".equals(pmd.getEnableTag()) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
                {
                    element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                    element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
                }
                else
                {
                    element.put("EFFECT_NOW_START_DATE", SysDateMgr.addDays(sysDate, 1));
                    element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
                }

                String startDate = data.getString("NEW_PRODUCT_STARTDATE");
                element.put("START_DATE", startDate);
                pmd.setStartDate(startDate);
                String endDate = calEndDate(pmd);
                element.put("END_DATE", endDate);
                if (data.getString("EFFECT_NOW", "").equals("1"))
                {
                    element.put("START_DATE", element.getString("EFFECT_NOW_START_DATE"));
                    element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
                }
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()))
            {
                element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
                element.put("EFFECT_NOW_END_DATE", sysDate);

                if (!data.getString("BASIC_CANCEL_DATE", "").equals(""))
                {
                    env.setBasicAbsoluteCancelDate(data.getString("BASIC_CANCEL_DATE"));
                }
                pmd.setEndDate(null);
                String cancelDate = calEndDate(pmd);
                element.put("END_DATE", cancelDate);
                if (data.getString("EFFECT_NOW", "").equals("true"))
                {
                    element.put("OLD_EFFECT_NOW_START_DATE", element.getString("START_DATE"));
                    element.put("OLD_EFFECT_NOW_END_DATE", cancelDate);
                    element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
                }
            }
        }

        // 产品元素依赖互斥校验，需要模拟台帐数据
        List<String> indexes = new ArrayList<String>();
        if (!"".equals(data.getString("TRADE_TYPE_CODE", "")))
        {
            IDataset tradeSvcs = new DatasetList();
            IDataset tradeDiscnts = new DatasetList();
            IDataset userSvcs = new DatasetList();
            IDataset userDiscnts = new DatasetList();
            for (int i = 0; i < size; i++)
            {
                IData element = elements.getData(i);
                indexes.add(element.getString("ITEM_INDEX", ""));
                IData ruleElement = new DataMap();
                ruleElement.put("USER_ID_A", "-1");
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
                    ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
                    ruleElement.putAll(element);
                    if (ruleElement.getString("INST_ID", "").equals(""))
                    {
                        ruleElement.put("INST_ID", "" + i);
                    }
                    tradeDiscnts.add(ruleElement);
                    userDiscnts.add(ruleElement);

                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
                    ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
                    ruleElement.putAll(element);
                    if (ruleElement.getString("INST_ID", "").equals(""))
                    {
                        ruleElement.put("INST_ID", "" + i);
                    }
                    tradeSvcs.add(ruleElement);
                    userSvcs.add(ruleElement);

                }
            }
            IDataset userElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
            if (userElements != null && userElements.size() > 0)
            {
                size = userElements.size();
                for (int i = 0; i < size; i++)
                {
                    IData element = userElements.getData(i);
                    String itemIndex = element.getString("ITEM_INDEX", "");
                    IData ruleElement = new DataMap();
                    ruleElement.put("USER_ID_A", "-1");// 给规则用
                    if ("0_1".equals(element.getString("MODIFY_TAG")))
                    {
                        continue;
                    }

                    if (indexes.contains(itemIndex))
                    {
                        continue;
                    }
                    if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
                        ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
                        ruleElement.putAll(element);
                        if (StringUtils.isNotBlank(element.getString("NEW_PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("NEW_PACKAGE_ID")))
                        {
                            ruleElement.put("PRODUCT_ID", element.getString("NEW_PRODUCT_ID"));
                            ruleElement.put("PACKAGE_ID", element.getString("NEW_PACKAGE_ID"));
                        }
                        userDiscnts.add(ruleElement);
                    }
                    else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        ruleElement.put("USER_ID", data.getString("USER_ID", "-1"));
                        ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
                        ruleElement.putAll(element);
                        if (StringUtils.isNotBlank(element.getString("NEW_PRODUCT_ID")) && StringUtils.isNotBlank(element.getString("NEW_PACKAGE_ID")))
                        {
                            ruleElement.put("PRODUCT_ID", element.getString("NEW_PRODUCT_ID"));
                            ruleElement.put("PACKAGE_ID", element.getString("NEW_PACKAGE_ID"));
                        }
                        userSvcs.add(ruleElement);
                    }
                }
            }

            IDataset tradeMains = new DatasetList();
            IData tradeMain = new DataMap();
            tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
            tradeMain.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
            tradeMain.put("IN_MODE_CODE", "0");
            tradeMain.put("USER_ID", data.getString("USER_ID"));
            tradeMains.add(tradeMain);
            IData ruleParam = new DataMap();
            ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
            ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
            ruleParam.put("TF_F_USER_SVC_AFTER", userSvcs);
            ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscnts);
            ruleParam.put("IS_COMPONENT", "true");
            ruleParam.put("TF_B_TRADE", tradeMains);
            IData result = BizRule.bre4ProductLimitNeedFormat(ruleParam);
            if (IDataUtil.isNotEmpty(result))
            {
                IDataset errors = result.getDataset("TIPS_TYPE_ERROR");
                if (IDataUtil.isNotEmpty(errors))
                {
                    int errorSize = errors.size();
                    StringBuilder errorInfo = new StringBuilder();
                    for (int i = 0; i < errorSize; i++)
                    {
                        IData error = errors.getData(i);
                        errorInfo.append(error.getString("TIPS_INFO"));
                    }
                    elements.getData(0).put("ERROR_INFO", errorInfo.toString());
                }
            }
        }

        return elements;
    }

    /**
     * 过滤原则 针对宽带主资费 1) 都是失效的,只留最后失效的一条 2) 一条生效,一条失效,只留生效的 一条 失效,一条未生效的,都留
     * 
     * @param discnts
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public IDataset filterBroadbandDiscnt(IDataset discnts) throws Exception
    {
        IDataset resultDatset = new DatasetList();

        IDataset unEffectDiscnt = new DatasetList();
        IDataset effectDiscnt = new DatasetList();
        IDataset oldDiscnt = new DatasetList();

        int discntsSize = discnts.size();
        String sysTime = SysDateMgr.getSysTime();

        for (int i = 0; i < discntsSize; i++)
        {

            IData discnt = discnts.getData(i);

            if ("1".equals(discnt.getString("MAIN_TAG")))
            {
                if (sysTime.compareTo(discnt.getString("START_DATE")) < 0 && sysTime.compareTo(discnt.getString("END_DATE")) < 0)
                {
                    unEffectDiscnt.add(discnt);
                }
                else if (sysTime.compareTo(discnt.getString("START_DATE")) >= 0 && sysTime.compareTo(discnt.getString("END_DATE")) <= 0)
                {
                    effectDiscnt.add(discnt);
                }
                else if (sysTime.compareTo(discnt.getString("START_DATE")) > 0 && sysTime.compareTo(discnt.getString("END_DATE")) > 0)
                {
                    oldDiscnt.add(discnt);
                }
            }
            else
            {
                if (sysTime.compareTo(discnt.getString("END_DATE")) < 0)
                {
                    resultDatset.add(discnt);
                }
            }
        }
        DataHelper.sort(oldDiscnt, "END_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        if (unEffectDiscnt.size() > 0)
        {// 有未生效的
            resultDatset.addAll(unEffectDiscnt);
            if (effectDiscnt.size() < 1)
            {// 没有正在使用的
                if (oldDiscnt.size() > 0)
                {// 有完全失效的
                    resultDatset.add(oldDiscnt.getData(0));
                }
            }
            else
            {
                resultDatset.add(effectDiscnt.getData(0));
            }
        }
        else
        {
            if (effectDiscnt.size() < 1)
            {
                if (oldDiscnt.size() > 0)
                {
                    resultDatset.add(oldDiscnt.getData(0));
                }
            }
            else
            {
                resultDatset.add(effectDiscnt.getData(0));
            }
        }
        DataHelper.sort(resultDatset, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "END_DATE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        return resultDatset;
    }

    /**
     * 获得宽带续费的信息,如果用户不更换资费,这些就够了
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-31
     */
    public IDataset getBroadbandContinuePayInfo(IData param) throws Exception
    {

        IDataset returnDataset = new DatasetList();
        IData returnData = new DataMap();

        String oldDiscntEndDate = SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();// 默认一个今天晚上
        String oldProductId = "";
        String oldPackageId = "";
        String oldDiscntId = "";
        String oldDiscntName = "";

        String userId = param.getString("USER_ID");
        if (userId == null || "".equals(userId))
        {
            return null;
        }

        IDataset allUserDiscnts = UserDiscntInfoQry.queryUserDiscntsOfBroadBand(userId);

        allUserDiscnts.addAll(UserDiscntInfoQry.queryUserTradeDiscntsOfBroadBand(userId));

        IDataset userDiscnts = filterBroadbandDiscnt(allUserDiscnts);

        int userDiscntSize = userDiscnts.size();

        if (userDiscntSize < 1)
        {
            returnData.put("RESULT_INFO", "没有查询到用户优惠，无法续费！");
            returnData.put("RESULT_TAG", "0");

            returnData.put("ELEMENTS", new DatasetList());

            returnDataset.add(returnData);
            return returnDataset;
        }

        for (int i = 0; i < userDiscntSize; i++)
        {
            IData userDiscnt = userDiscnts.getData(i);
            if (SysDateMgr.getSysTime().compareTo(userDiscnt.getString("START_DATE")) < 0)
            {
                returnData.put("RESULT_INFO", "该用户有未生效的资费，无法续费！");
                returnData.put("RESULT_TAG", "0");
                returnData.put("ELEMENTS", userDiscnts);

                returnDataset.add(returnData);
                return returnDataset;
            }
            else
            {
                if ("1".equals(userDiscnt.getString("MAIN_TAG")))
                {

                    oldProductId = userDiscnt.getString("PRODUCT_ID");
                    oldPackageId = userDiscnt.getString("PACKAGE_ID");
                    oldDiscntId = userDiscnt.getString("ELEMENT_ID");
                    oldDiscntName = userDiscnt.getString("ELEMENT_NAME");

                    if (oldDiscntEndDate.compareTo(userDiscnt.getString("END_DATE")) < 0)
                    {
                        oldDiscntEndDate = userDiscnt.getString("END_DATE");
                    }
                }
            }
        }

        returnData.put("DISCNT_CODE", oldDiscntId);
        returnData.put("PACKAGE_ID", oldPackageId);
        returnData.put("PRODUCT_ID", oldProductId);
        returnData.put("START_DATE", SysDateMgr.addDays(oldDiscntEndDate, 1) + SysDateMgr.getFirstTime00000());

        DiscntData discntData = new DiscntData(returnData);

        returnData.put("EPARCHY_CODE", this.getUserEparchyCode());
        returnData.put("TRADE_TYPE_CODE", "1116");
        returnData.put("ELEMENTS", userDiscnts);

        IDataset userDiscntsWithFee = getContinuePayFee(returnData);

        returnData.put("ELEMENTS", userDiscntsWithFee);
        returnData.put("RESULT_TAG", "1");
        returnData.put("DISCNT_CODE", oldDiscntId);
        returnData.put("DISCNT_NAME", oldDiscntName);
        returnData.put("EFFECT_DATE", discntData.getStartDate() + "~" + this.calEndDate(discntData));

        returnDataset.add(returnData);

        return returnDataset;
    }

    /**
     * 宽带产品变更 费用计算
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-31
     */
    public IDataset getChangeProductFee(IData param) throws Exception
    {

        IDataset elements = new DatasetList(param.getString("ELEMENTS"));
        int size = elements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = elements.getData(i);
            if (!"1".equals(element.getString("MODIFY_TAG")) && !"0".equals(element.getString("MODIFY_TAG")))
            {
                continue;
            }
            if ("0".equals(element.getString("END_ENABLE_TAG")))
            {
                continue;
            }

            IDataset feeConfigs = ProductFeeInfoQry.getElementFee(param.getString("TRADE_TYPE_CODE"), CSBizBean.getVisit().getInModeCode(), "", element.getString("ELEMENT_TYPE_CODE"), element.getString("PRODUCT_ID"), element.getString("PACKAGE_ID"),
                    "-1", element.getString("ELEMENT_ID"), param.getString("EPARCHY_CODE"), "3");

            if (IDataUtil.isEmpty(feeConfigs))
            {
                continue;
            }

            int feeSize = feeConfigs.size();
            IDataset feeDatas = new DatasetList();
            for (int j = 0; j < feeSize; j++)
            {
                IData feeConfig = feeConfigs.getData(j);
                if (!"0".equals(feeConfig.getString("PAY_MODE")))
                {
                    continue;
                }
                IData feeData = new DataMap();
                feeData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
                feeData.put("MODE", feeConfig.getString("FEE_MODE"));
                feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));

                if ("1".equals(element.getString("MODIFY_TAG")))
                {
                    feeData.put("FEE", getElementFee(feeConfig.getString("FEE"), element));
                }
                else if ("0".equals(element.getString("MODIFY_TAG")))
                {
                    feeData.put("FEE", feeConfig.getString("FEE"));
                }

                // feeData.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                feeDatas.add(feeData);
            }
            element.put("FEE_DATA", feeDatas);
        }

        return elements;
    }

    /**
     * 获取续费金额等,对END_ENABLE_TAG=0 的资费,不进行续费
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getContinuePayFee(IData param) throws Exception
    {
        IDataset elements = new DatasetList(param.getString("ELEMENTS"));
        int size = elements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = elements.getData(i);

            if ("0".equals(element.getString("END_ENABLE_TAG")))
            {
                continue;
            }

            IDataset feeConfigs = ProductFeeInfoQry.getElementFee(param.getString("TRADE_TYPE_CODE"), CSBizBean.getVisit().getInModeCode(), "", element.getString("ELEMENT_TYPE_CODE"), element.getString("PRODUCT_ID"), element.getString("PACKAGE_ID"),
                    "-1", element.getString("ELEMENT_ID"), param.getString("EPARCHY_CODE"), "3");

            if (IDataUtil.isEmpty(feeConfigs))
            {
                continue;
            }

            int feeSize = feeConfigs.size();
            IDataset feeDatas = new DatasetList();
            for (int j = 0; j < feeSize; j++)
            {
                IData feeConfig = feeConfigs.getData(j);
                if (!"0".equals(feeConfig.getString("PAY_MODE")))
                {
                    continue;
                }
                IData feeData = new DataMap();
                feeData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
                feeData.put("MODE", feeConfig.getString("FEE_MODE"));
                feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
                feeData.put("FEE", feeConfig.getString("FEE"));
                // feeData.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                feeDatas.add(feeData);
            }
            element.put("FEE_DATA", feeDatas);
        }

        return elements;
    }

    /**
     * 得到一个资费的结束时间,以及下一个产品的生效开始时间
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getElementEndDate(IData data) throws Exception
    {

        IDataset elements = new DatasetList(data.getString("ELEMENTS"));
        boolean effectNow = "1".equals(data.getString("EFFECT_NOW", ""));

        for (int i = 0; i < elements.size(); i++)
        {
            IData element = elements.getData(i);
            ProductModuleData pmd = null;
            if (IDataUtil.isNotEmpty(element))
            {

                if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
                {
                    pmd = new SvcData(element);

                }
                else
                {
                    pmd = new DiscntData(element);
                }
                if (effectNow)
                {
                    element.put("END_DATE", SysDateMgr.getSysDate().substring(0, 10) + SysDateMgr.getEndTime235959());
                    element.put("NEW_PRODUCT_STARTDATE", SysDateMgr.addDays(SysDateMgr.getSysDate().substring(0, 10), 1) + SysDateMgr.getFirstTime00000());
                }
                else
                {
                    String enddate = calEndDate(pmd);
                    element.put("END_DATE", enddate);
                    if (enddate.compareTo(SysDateMgr.getSysTime()) < 0)
                    {
                        enddate = SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();
                    }
                    element.put("NEW_PRODUCT_STARTDATE", SysDateMgr.addDays(enddate.substring(0, 10), 1) + SysDateMgr.getFirstTime00000());
                }
            }
        }

        return elements;

    }

    /**
     * 计算单个资费的费用,特别是正在使用的
     * 
     * @param fee
     * @param element
     * @return
     * @throws Exception
     */
    public String getElementFee(String fee, IData element) throws Exception
    {

        ProductModuleData pmd = new DiscntData(element);

        int discntCycleDay = 1;
        int discntUsedDay = 1;

        if ("0".equals(pmd.getEndEnableTag()))
        {
            discntCycleDay = 31;
            discntUsedDay = 31;
        }
        else if ("1".equals(pmd.getEndEnableTag()))
        {
            if ("2".equals(pmd.getEndUnit()))
            {
                String realEndDate = pmd.getEndDate();
                String discntEndDate = (SysDateMgr.addDays(SysDateMgr.getAddMonthsNowday(Integer.parseInt(pmd.getEndOffSet()), pmd.getStartDate()), -1)).substring(0, 10) + SysDateMgr.getEndTime235959();

                discntCycleDay = SysDateMgr.dayInterval(pmd.getStartDate().substring(0, 10), discntEndDate.substring(0, 10)) + 1;

                discntUsedDay = SysDateMgr.dayInterval(pmd.getStartDate().substring(0, 10), realEndDate.substring(0, 10)) + 1;

            }
            else if ("4".equals(pmd.getEndUnit()))
            {
                String realEndDate = pmd.getEndDate();
                String discntEndDate = (SysDateMgr.addDays(SysDateMgr.addYears(pmd.getStartDate(), Integer.parseInt(pmd.getEndOffSet())), -1)).substring(0, 10) + SysDateMgr.getEndTime235959();

                discntCycleDay = SysDateMgr.dayInterval(pmd.getStartDate().substring(0, 10), discntEndDate.substring(0, 10)) + 1;

                discntUsedDay = SysDateMgr.dayInterval(pmd.getStartDate().substring(0, 10), realEndDate.substring(0, 10)) + 1;
            }
            else
            {

            }
        }
        else
        {

        }
        double cycleFee = Double.parseDouble(fee);

        double backFee = cycleFee - ((cycleFee / discntCycleDay) * discntUsedDay);

        String leftFee = String.valueOf(-Math.floor(backFee / 100) * 100);

        return leftFee.substring(0, leftFee.indexOf(".") == -1 ? leftFee.length() : leftFee.indexOf("."));

    }

    /**
     * * 获取查询时的路由 如果当前服务默认路由是CG，则继续连CG，否则为用户的归属地州
     * 
     * @return
     */
    private String getQueryRoute() throws Exception
    {
        String route = BizRoute.getRouteId().equals(Route.CONN_CRM_CG) ? BizRoute.getRouteId() : this.getUserEparchyCode();
        return route;
    }

    /**
     * 获取用户结余等信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserAcctBalanceInfo(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        if (userId == null || "".equals(userId))
        {
            return null;
        }

        return AcctCall.queryUserEveryBalanceFee(userId, "USER");

    }

    /**
     * 根据instId 获得用户资费 针对宽带资费服务在 失效的情况下依然要查出来,所以要重新写
     * 
     * @param instId
     * @param ucaData
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public DiscntTradeData getUserDiscntByInstId(String instId, UcaData ucaData) throws Exception
    {
        List<DiscntTradeData> discntTradeDatas = this.getUserDiscnts(ucaData);
        int size = discntTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData dtd = discntTradeDatas.get(i);
            if (dtd.getInstId().equals(instId))
            {
                return dtd;
            }
        }
        return null;
    }

    /**
     * 取得用户的资费,包括未完工工单
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-31
     */
    public IDataset getUserDiscnts(IData param) throws Exception
    {

        String userId = param.getString("USER_ID");
        if (userId == null || "".equals(userId))
        {
            return null;
        }

        IDataset userDiscnts = UserDiscntInfoQry.queryUserDiscntsOfBroadBand(userId);

        userDiscnts.addAll(UserDiscntInfoQry.queryUserTradeDiscntsOfBroadBand(userId));

        return filterBroadbandDiscnt(userDiscnts);
    }

    /**
     * 针对宽带资费服务在 失效的情况下依然要查出来,所以要重新写
     * 
     * @param ucaData
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public List<DiscntTradeData> getUserDiscnts(UcaData ucaData) throws Exception
    {

        List<DiscntTradeData> discntDatas = new ArrayList<DiscntTradeData>();

        IData param = new DataMap();
        param.put("USER_ID", ucaData.getUser().getUserId());

        IDataset dataset = this.getUserDiscnts(param);

        int size = dataset.size();
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData discntTradeData = new DiscntTradeData(dataset.getData(i));
            discntDatas.add(discntTradeData);
        }

        return discntDatas;
    }

    /**
     * 根据instId 获得用户服务 针对宽带资费服务在 失效的情况下依然要查出来,所以要重新写
     * 
     * @param instId
     * @param ucaData
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public SvcTradeData getUserSvcByInstId(String instId, UcaData ucaData) throws Exception
    {
        List<SvcTradeData> svcTradeDatas = this.getUserSvcs(ucaData);
        int size = svcTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
            SvcTradeData std = svcTradeDatas.get(i);
            if (std.getInstId().equals(instId))
            {
                return std;
            }
        }
        return null;
    }

    /**
     * 取得用户的服务
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserSvcs(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        if (userId == null || "".equals(userId))
        {
            return null;
        }

        return UserSvcInfoQry.queryUserSvcsBroadband(userId);
    }

    /**
     * 针对宽带资费服务在 失效的情况下依然要查出来,所以要重新写
     * 
     * @param ucaData
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */

    public List<SvcTradeData> getUserSvcs(UcaData ucaData) throws Exception
    {

        List<SvcTradeData> svcDatas = new ArrayList<SvcTradeData>();

        IData param = new DataMap();
        param.put("USER_ID", ucaData.getUser().getUserId());

        IDataset dataset = this.getUserSvcs(param);

        int size = dataset.size();
        for (int i = 0; i < size; i++)
        {
            SvcTradeData svcTradeData = new SvcTradeData(dataset.getData(i));
            svcDatas.add(svcTradeData);
        }

        return svcDatas;

    }

}
