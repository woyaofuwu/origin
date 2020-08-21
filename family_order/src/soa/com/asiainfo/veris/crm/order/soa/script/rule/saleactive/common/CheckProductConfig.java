
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import java.util.StringTokenizer;


import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TroopMemberInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;

/**
 * 营销活动通用规则校验 按PRODUCT表的TAG_SET配置来做校验，前台选择包、业务受理后两种业务场景都需要校验！ ----2.4.1.[1]用户是一卡付多号副卡用户不可以办理本业务！
 * ----2.4.2.[2]该用户是一卡双号副卡，不能办理本业务！ ----2.4.3.[3]该用户有JTZ套餐(移动公司员工套餐)，不能办理本业务！ ----2.4.4.[4][预开未返单]用户不可以办理购机业务！
 * ----2.4.5.[5]未激活用户不能办理该业务 ----2.4.6.[6]只有实名制客户才能办理的该业务 ----2.4.7.[7]不是当月激活客户，不能办理该业务
 * ----2.4.8.[8]我公司未发送短信通知该客户参加活动，不能办理2008真情回馈业务！ ----2.4.9.[9]移动公司员工不能办理该购机业务 ----2.4.10.[11]用户没有加入VPN集团，不能办理本礼包业务！
 * ----2.4.11.[12]用户没有加入898集团，不能办理本礼包业务！ ----2.4.12.[13]不是老客户，不能办理本礼包业务！ ----2.4.13.[14]ParamAttr=1259的品牌不能办理
 * ----2.4.14.[15]大客户客户群限制 ----2.4.15.[16]用户是代理商套餐(VPMN JPA)，不能办理该业务！ ----2.4.16.[17]办理活动是判断宽带开户条件
 * ----2.4.17.[18]=1用户是统一付费副卡用户不可以办理本业务 ----2.4.17.[18]=2用户是统一付费副卡用户才可以办理本业务
 * 
 * @author Mr.Z
 */
public class CheckProductConfig extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1757416881615427525L;

    private static Logger logger = Logger.getLogger(CheckProductConfig.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckProductConfig() >>>>>>>>>>>>>>>>>>");
        }
        String saleProductId = databus.getString("PRODUCT_ID");
        String openDate = databus.getString("OPEN_DATE");
        String sysDate = SysDateMgr.getSysTime();
        String userId = databus.getString("USER_ID");
        String brandCode = databus.getString("BRAND_CODE");
        String serialNumber = databus.getString("SERIAL_NUMBER");

        IDataset userDiscnts = new DatasetList();
        userDiscnts = databus.getDataset("TF_F_USER_DISCNT");

        IDataset userSvcs = new DatasetList();
        userSvcs = databus.getDataset("TF_F_USER_SVC");

        IDataset userSaleActives = new DatasetList();
        userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");

        IData customers = new DataMap();
        customers = databus.getDataset("TF_F_CUSTOMER").getData(0);

//        IData productInfo = ProductInfoQry.queryAllProductInfo(saleProductId);
        
        IData productInfo = databus.getData("PM_CATALOG");//UProductInfoQry.qrySaleActiveProductByPK(saleProductId);
        if (IDataUtil.isEmpty(productInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024024, SaleActiveBreConst.PRODUCT_INFO_EMPTY);
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RSRV_STR3活动目标客户群判断
        String rsrvStr3 = productInfo.getString("RSRV_STR3", "");
        if (StringUtils.isNotBlank(rsrvStr3))
        {
            boolean isTroopMember = databus.getBoolean("IS_TROOP_MEMBER");
            if (isTroopMember)
                return true;
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024025, "该用户不归属目标客户群，不能办理该业务！");
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RSRV_DATE1必须在此时间前开户的用户才能办理该活动
        String rsrvDate1 = productInfo.getString("RSRV_DATE1");
        if (StringUtils.isNotBlank(rsrvDate1))
        {
            if (openDate.compareTo(rsrvDate1) > 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024026, "必须是在" + rsrvDate1 + "前开户用户才能办理!");
            }
        }

        String tagSet = productInfo.getString("TAG_SET", "");

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（1）用户是一卡付多号副卡用户不可以办理本业务！

        if (tagSet.length() > 0 && tagSet.substring(0, 1).equals("1"))
        {
            IDataset relationUuUserIdBs = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "97", "2");

            if (IDataUtil.isNotEmpty(relationUuUserIdBs))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024027, SaleActiveBreConst.ERROR_1);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（2）用户是一卡双号副卡，不能办理本业务！

        if (tagSet.length() > 1 && tagSet.substring(1, 2).equals("1"))
        {
            IDataset relationUuUserIdB30 = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "30", "2");
            IDataset relationUuUserIdB34 = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "34", "2");

            if (IDataUtil.isNotEmpty(relationUuUserIdB30) || IDataUtil.isNotEmpty(relationUuUserIdB34))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024028, SaleActiveBreConst.ERROR_2);

            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（3）该用户有JTZ套餐(移动公司员工套餐)，不能办理本业务！

        if (tagSet.length() > 2 && tagSet.substring(2, 3).equals("1") && IDataUtil.isNotEmpty(userDiscnts))
        {
            boolean isJTZuser = false;
            for (int index = 0, size = userDiscnts.size(); index < size; index++)
            {
                IData userDiscnt = userDiscnts.getData(index);
                String discntCode = userDiscnt.getString("DISCNT_CODE");
                if ("270".equals(discntCode))
                {
                    isJTZuser = true;
                    break;
                }
            }

            if (isJTZuser)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024029, SaleActiveBreConst.ERROR_3);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（4）[预开未返单]用户不可以办理购机业务！

        if (tagSet.length() > 3 && tagSet.substring(3, 4).equals("1"))
        {
            if ("1".equals(databus.getString("OPEN_MODE")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024030, SaleActiveBreConst.ERROR_4);

            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（5）未激活用户不能办理该业务！

        if (tagSet.length() > 4 && tagSet.substring(4, 5).equals("1"))
        {
            if (!"0".equals(databus.getString("ACCT_TAG")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024031, SaleActiveBreConst.ERROR_5);
            }
        }
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（5.1）未激活用户才能办理该业务！
        if (tagSet.length() > 4 && tagSet.substring(4, 5).equals("2"))
        {
            if (!"2".equals(databus.getString("ACCT_TAG")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024051, SaleActiveBreConst.ERROR_51);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（6）只有实名制客户才能办理该业务

        if (tagSet.length() > 5 && tagSet.substring(5, 6).equals("1") && IDataUtil.isNotEmpty(customers))
        {
            if (!"1".equals(customers.getString("IS_REAL_NAME")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024032, SaleActiveBreConst.ERROR_6);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（7）不是当月激活客户，不能办理该业务

        if (tagSet.length() > 6 && tagSet.substring(6, 7).equals("1"))
        {
            boolean isSamMoth = SysDateMgr.sameMonthCompare(sysDate, openDate);
            if (!("0".equals(databus.getString("ACCT_TAG")) && isSamMoth))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024033, SaleActiveBreConst.ERROR_7);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（8）该用户不归属目标客户群，不能办理该业务！
        String packageId = databus.getString("PACKAGE_ID");
        String condFactor1="";
//        IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE"));
//        IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        IData pkgExtData = databus.getData("PM_OFFER_EXT");
        if (IDataUtil.isNotEmpty(pkgExtData))
        {
            condFactor1 = pkgExtData.getString("COND_FACTOR1");
        }
        if(StringUtils.isNotBlank(condFactor1) && !databus.getBoolean("IS_TROOP_MEMBER")
                && tagSet.length()>8 && "1".equals(tagSet.substring(7, 8)))
        {
            boolean isSmsTroopMember = TroopMemberInfoQry.isSmsTroopMember(userId, saleProductId);
            if (!isSmsTroopMember)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024034, SaleActiveBreConst.ERROR_8);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（9）移动公司员工不能办理该购机业务

        if (tagSet.length() > 8 && tagSet.substring(8, 9).equals("1"))
        {
            boolean isCmccStaffUser = RelaUUInfoQry.isCMCCstaffUser(userId);
            if (isCmccStaffUser)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024035, SaleActiveBreConst.ERROR_9);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（10）购机需要判断自身三个月

        if (tagSet.length() > 9 && tagSet.substring(9, 10).equals("1") && IDataUtil.isNotEmpty(userSaleActives))
        {
            String maxStartDate = "";
            for (int index = 0, size = userSaleActives.size(); index < size; index++)
            {
                IData saleActive = userSaleActives.getData(index);
                String existsProductId = saleActive.getString("PRODUCT_ID");
                if (saleActive.getString("START_DATE").compareTo(maxStartDate) > 0 && saleProductId.equals(existsProductId))
                {
                    maxStartDate = saleActive.getString("START_DATE");
                }
            }

            String lastMonthOfMaxStartDate = SysDateMgr.getDateLastMonthSec(maxStartDate);
            String lastMohtOfCurrent = SysDateMgr.getLastDateThisMonth();

            boolean isBetween3moths = SysDateMgr.monthInterval(lastMohtOfCurrent, lastMonthOfMaxStartDate) < 3 ? true : false;

            if (isBetween3moths)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024036, SaleActiveBreConst.ERROR_10);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（11）用户没有加入VPN集团，不能办理本礼包业务！
        
        if (tagSet.length() > 10 && tagSet.substring(10, 11).equals("1") && IDataUtil.isNotEmpty(userSvcs))
        {
            boolean isVpnUser = false;

            for (int index = 0, size = userSvcs.size(); index < size; index++)
            {
                IData userSvc = userSvcs.getData(index);
                String serviceId = userSvc.getString("SERVICE_ID");
                if ("860".equals(serviceId))
                {
                    isVpnUser = true;
                    break;
                }
            }

            if (!isVpnUser)
            { 
            	this.checkGroupProduct(databus, saleProductId, serialNumber, "14024037", SaleActiveBreConst.ERROR_11);
                //check_tag_set="ERROR_11";//BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024037, SaleActiveBreConst.ERROR_11); 
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（12）用户没有加入898集团，不能办理本礼包业务！

        if (tagSet.length() > 11 && tagSet.substring(11, 12).equals("1"))
        {
            boolean is898groupMem = CParamQry.is898GroupMember(userId);

            if (!is898groupMem)
            {
            	this.checkGroupProduct(databus, saleProductId, serialNumber, "14024038", SaleActiveBreConst.ERROR_12);
            	//BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024038, SaleActiveBreConst.ERROR_12);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（13）不是老客户，不能办理本礼包业务！

        if (tagSet.length() > 12 && tagSet.substring(12, 13).equals("1"))
        {
            boolean isSamMoth = SysDateMgr.sameMonthCompare(sysDate, openDate);

            if (isSamMoth)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024039, SaleActiveBreConst.ERROR_13);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（14）该用户的品牌不能办理本业务！

        if (tagSet.length() > 13 && tagSet.substring(13, 14).equals("1"))
        {
            IDataset targetBrandUser = CommparaInfoQry.getCommPkInfo("CSM", "1259", brandCode, databus.getString("EPARCHY_CODE"));

            if (IDataUtil.isNotEmpty(targetBrandUser))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024040, SaleActiveBreConst.ERROR_14);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（15）大客户客户群限制>>>>>>>>TODO检查逻辑,对比过程

        if (tagSet.length() > 14 && !tagSet.substring(14, 15).equals("0"))
        {
            boolean isTargetGroupMem = CParamQry.isTargetGroupMember(userId, saleProductId);
            boolean isTargetVpmnGroupMem = CParamQry.isTargetVpmnGroupMember(userId, saleProductId);

            if (!isTargetGroupMem || !isTargetVpmnGroupMem)
            {
                boolean isCommpara142GroupMember = CParamQry.isCommpara142GroupMember(userId, saleProductId);
                if (isCommpara142GroupMember)
                {
                    IDataset relationUuUserIdB20 = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(userId, "20");
                    if (IDataUtil.isNotEmpty(relationUuUserIdB20))
                    {
                        isTargetGroupMem = true;
                        isTargetVpmnGroupMem = true;
                    }
                }
            }

            String tagset15 = tagSet.substring(14, 15);

            if (("1".equals(tagset15) || "3".equals(tagset15)) && isTargetGroupMem == false)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024041, SaleActiveBreConst.ERROR_15_1);
            }

            if (("2".equals(tagset15) || "3".equals(tagset15)) && isTargetVpmnGroupMem == false)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024042, SaleActiveBreConst.ERROR_15_2);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（16）用户是代理商套餐(VPMN JPA)，不能办理该业务！

        if (tagSet.length() > 15 && tagSet.substring(15, 16).equals("1"))
        {
            boolean is655Discnt = false;
            if (IDataUtil.isNotEmpty(userDiscnts))
            {
                for (int index = 0, size = userDiscnts.size(); index < size; index++)
                {
                    IData userDiscnt = userDiscnts.getData(index);
                    String discntCode = userDiscnt.getString("DISCNT_CODE");
                    if ("655".equals(discntCode))
                    {
                        is655Discnt = true;
                        break;
                    }
                }
            }

            boolean isSpecUserIdA = false;
            IDataset relationUuUserIdB20 = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(userId, "20");
            if (IDataUtil.isNotEmpty(relationUuUserIdB20))
            {
                for (int index = 0, size = relationUuUserIdB20.size(); index < size; index++)
                {
                    IData relationUuUserIdB = relationUuUserIdB20.getData(index);
                    String serialNumberA = relationUuUserIdB.getString("SERIAL_NUMBER_A");
                    if ("V0HN001010".equals(serialNumberA) || "V0SJ004536".equals(serialNumberA))
                    {
                        isSpecUserIdA = true;
                        break;
                    }
                }
            }

            if (isSpecUserIdA || is655Discnt)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024043, SaleActiveBreConst.ERROR_16);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（17）判断宽带开户条件

        if (tagSet.length() > 16 && tagSet.substring(16, 17).equals("1"))
        {
            IDataset maxLimitDataset = CommparaInfoQry.getCommPkInfo("CSM", "73", saleProductId, databus.getString("EPARCHY_CODE"));
            if (IDataUtil.isNotEmpty(maxLimitDataset))
            {
                String maxDay = maxLimitDataset.getData(0).getString("PARA_CODE1", "0");
                String maxFee = maxLimitDataset.getData(0).getString("PARA_CODE2", "0");
                String maxTradeId = "";
                if (!"0".equals(maxDay))
                {
                    maxTradeId = TradeInfoQry.getMaxOpenTradeInfoBySn("KD_" + serialNumber, maxDay);
                    if (StringUtils.isBlank(maxTradeId))
                    {
                        maxTradeId = TradeBhQry.getMaxOpenTradeInfoBySn("KD_" + serialNumber, maxDay);
                    }

                    if (StringUtils.isBlank(maxTradeId))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024044, "宽带未开户或宽带开户时间不满足" + maxDay + "内天要求！");
                    }

                    if (!"0".equals(maxFee) && StringUtils.isNotBlank(maxTradeId))
                    {
                        boolean isMaxFee = TradeInfoQry.getMaxAdvancePayTradeInfoByTid(maxTradeId, maxFee);
                        if (!isMaxFee)
                        {
                            isMaxFee = TradeBhQry.getMaxAdvancePayTradeInfoByTid(maxTradeId, maxFee);
                        }

                        if (!isMaxFee)
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024045, "宽带开户预存款不满足大于等于[" + maxFee + "]元要求！");
                        }
                    }
                }

                if ("0".equals(maxDay) && !"0".equals(maxFee))
                {
                    // TODO 无该模式配置，暂不实现！
                }
                //REQ202003180001 “共同战疫宽带助力”
                String limitDateTag = maxLimitDataset.getData(0).getString("PARA_CODE5", "0");//判断是否在活动时间内开关
                String limitDateStart = maxLimitDataset.getData(0).getString("PARA_CODE3", "");
                String limitDateEnd = maxLimitDataset.getData(0).getString("PARA_CODE4", "");
                String limitHistoryTag = maxLimitDataset.getData(0).getString("PARA_CODE6", "");

                if("1".equals(limitDateTag)&&StringUtils.isNotBlank(limitDateStart) ){
                    //上面的判断宽带的sql语句是否有问题,撤单返销的都不考虑?待考察?
                    //是否宽带用户查询
                    String kdSerialNumber = serialNumber;
                    if (!serialNumber.startsWith("KD")) {
                        kdSerialNumber = "KD_" + serialNumber;
                    }
                    if(!"1".equals(databus.getString("WIDE_USER_CREATE_SALE_ACTIVE",""))){
                        ////如果是开户营销活动不需要进行此校验

                        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", kdSerialNumber);
                        if(IDataUtil.isEmpty(userInfos)){
                            //CheckPackageExtConfig配置校验了宽带未开户及开户装机未完工的校验
                            //BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2020032302, "宽带未开户不能参加该活动！");
                        }else{
                            String startDate = userInfos.first().getString("OPEN_DATE");
                            if(SysDateMgr.compareTo(limitDateStart,startDate)>0){
                                //判断宽带开户是否在活动起始时间
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2020032303, "宽带开户时间不在活动期内！");
                            }else{
                                if(StringUtils.isNotBlank(limitDateEnd)&&SysDateMgr.compareTo(startDate,limitDateEnd)>0){
                                    //若配置活动结束时间判断宽带开户是否在活动结束时间后
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2020032304, "宽带开户时间不在活动期内！");

                                }
                            }
                        }
                    }
                    if("1".equals(limitHistoryTag)){
                        IDataset allDestroyUserInfoBySns = UserInfoQry.getAllDestroyUserInfoBySn(kdSerialNumber);
                        if(IDataUtil.isNotEmpty(allDestroyUserInfoBySns)){
                            for (int i = 0; i < allDestroyUserInfoBySns.size(); i++) {
                                IData destroyUserInfoBySnsData = allDestroyUserInfoBySns.getData(i);
                                String startDate = destroyUserInfoBySnsData.getString("OPEN_DATE");
                                if(SysDateMgr.compareTo(limitDateStart,startDate)>0){
                                    //判断宽带历史开户是否在活动起始时间
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2020032303, "您在"+limitDateStart+"前已开过户,不能参加该活动!");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（18）[1] 用户是统一付费副卡用户不可以办理本业务！

        if (tagSet.length() > 17 && tagSet.substring(17, 18).equals("1"))
        {
            IDataset relationUuUserIdB56 = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "56", "2");

            if (IDataUtil.isNotEmpty(relationUuUserIdB56))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024046, SaleActiveBreConst.ERROR_18_1);
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>（18）[2] 用户不是统一付费副卡用户不可以办理本业务！

        if (tagSet.length() > 17 && tagSet.substring(17, 18).equals("2"))
        {
            IDataset relationUuUserIdB56 = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "56", "2");
            if (IDataUtil.isEmpty(relationUuUserIdB56))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024047, SaleActiveBreConst.ERROR_18_2);
            }

            // 该统一付费业务的主卡号码（老号码）须办理约定消费优惠使用宽带业务中的158元档或188元档。TODO 移到单独的规则中
            if ("69900363".equals(saleProductId))
            {
                boolean isSpecPayMainUser = CParamQry.isSpecDiscntMainUser(userId);
                if (!isSpecPayMainUser)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024048, SaleActiveBreConst.ERROR_18_3);
                }
            }

            // 该统一付费业务的主卡号码必须在网满一年 TODO 移到单独的规则中
            if ("69900736".equals(saleProductId) || "69900764".equals(saleProductId) || "69900737".equals(saleProductId) || "69900768".equals(saleProductId))
            {
                boolean isSpecOpenMainUser = CParamQry.isSpecOpenMainUser(userId);
                if (!isSpecOpenMainUser)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024049, SaleActiveBreConst.ERROR_18_4);
                }
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>用户没有集团彩铃服务，不能办理本礼包业务！
        if (tagSet.length() > 18 && tagSet.substring(18, 19).equals("1") && IDataUtil.isNotEmpty(userSvcs))
        {
            boolean isGrpCailingUser = false;

            for (int index = 0, size = userSvcs.size(); index < size; index++)
            {
                IData userSvc = userSvcs.getData(index);
                String serviceId = userSvc.getString("SERVICE_ID");
                if ("910".equals(serviceId))
                {
                    isGrpCailingUser = true;
                    break;
                }
            }

            if (!isGrpCailingUser)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024050, SaleActiveBreConst.ERROR_20);
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckProductConfig() >>>>>>>>>>>>>>>>>>");
        }
        
        
        return true;
    }
    
    private void checkGroupProduct(IData databus,String saleProductId,String serialNumber,String errNo,String errMsg)throws Exception{
    	/**
    	 * REQ201604290016 关于新增集团购机判断条件的需求
    	 * chenxy3 20160607
    	 * */
    	IDataset ruleSet= CommparaInfoQry.getCommPkInfo("CSM", "1144", saleProductId, "0898"); 
    	if(ruleSet!=null && ruleSet.size()>0){
    		//1、先取出para_code1，表示与tag_set的第？位有“或”的关系
    		String tagSetNO=ruleSet.getData(0).getString("PARA_CODE1","");
    		String tipInfo=ruleSet.getData(0).getString("PARA_CODE23","");//错误提示
    		
    		StringTokenizer st=new StringTokenizer(ruleSet.getData(0).getString("PARA_CODE24",""),"|");
    	    boolean isExist=false;
    	    for(int i=1;st.hasMoreTokens();i++){ 
    	        String prodId =st.nextToken();
    	        IDataset groupMem= ProductInfoQry.queryProdInGroupMem(serialNumber,prodId); 
    	        if(groupMem!=null && groupMem.size()>0){ 
    	        	isExist=true;
    	        	break;
    	        }
    	    } 
    	    if(isExist==false){
    	        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024052, tipInfo);
    	    }
    	}else{
    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errNo, errMsg);
    	} 
    }

}
