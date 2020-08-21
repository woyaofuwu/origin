
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TroopMemberInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;

public final class SaleActiveCheckBean extends CSBizBean
{
    /**
     * 校验一批营销包中可办理的营销包 不可办理的营销包会加一个KEY为ERROR_FLAG，VALUE为1的标记
     * 
     * @return
     */
    public static void checkPackages(IDataset saleActives, String sn) throws Exception
    {
        boolean youSeeYouCanFlag = BizEnv.getEnvBoolean("saleactive.YouSeeYouCan");

        if (youSeeYouCanFlag == false)
        {
            return;
        }

        // 所见即所得
        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
        UcaData uca = UcaDataFactory.getNormalUca(sn);
        IData ruleParam = new DataMap();
        IData saleActive;
        IData result;
        IDataset ruleInfo;

        for (int i = 0, size = saleActives.size(); i < size; i++)
        {
            saleActive = saleActives.getData(i);

            ruleParam = saleActiveCheckBean.putCommonBreParams(saleActive, uca);

            ruleParam.put("PAGE_RULE", SaleActiveBreConst.BRE_BY_PAGE_SEL_PACKAGE);
            ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE);
            ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG);

            // ruleParam.put("TIPS_TYPE", "0|4");
            result = BizRule.bre4SuperLimit(ruleParam);
            
            ruleInfo = result.getDataset("TIPS_TYPE_ERROR");

            // 增加样式的过滤
            String resultcode = "0";
      
            // proc
            if (ProductInfoQry.checkSaleActiveLimitProd(saleActive.getString("PRODUCT_ID", "")))
            {
                resultcode = saleActiveCheckBean.checkByProc(saleActive, uca);
            }

            if (IDataUtil.isNotEmpty(ruleInfo) || !"0".equals(resultcode))
            {
                saleActive.put("ERROR_FLAG", "1");
            }
        }
    }

    private void buildSaleActiveBreDataBus(IData ruleParam, UcaData uca) throws Exception
    {
        IDataset userDiscntDataset = new DatasetList();
        List<DiscntTradeData> discntTradeDataList = uca.getUserDiscnts();
        if (CollectionUtils.isNotEmpty(discntTradeDataList))
        {
            for (int index = 0, size = discntTradeDataList.size(); index < size; index++)
            {
                userDiscntDataset.add(discntTradeDataList.get(index).toData());
            }
        }
        ruleParam.put("TF_F_USER_DISCNT", userDiscntDataset);

        IDataset userSvcDataset = new DatasetList();
        List<SvcTradeData> svcTradeDataList = uca.getUserSvcs();
        if (CollectionUtils.isNotEmpty(svcTradeDataList))
        {
            for (int index = 0, size = svcTradeDataList.size(); index < size; index++)
            {
                userSvcDataset.add(svcTradeDataList.get(index).toData());
            }
        }
        ruleParam.put("TF_F_USER_SVC", userSvcDataset);

        IDataset userPlatSvcDataset = new DatasetList();
        List<PlatSvcTradeData> platsvcTradeDataList = uca.getUserPlatSvcs();
        if (CollectionUtils.isNotEmpty(platsvcTradeDataList))
        {
            for (int index = 0, size = platsvcTradeDataList.size(); index < size; index++)
            {
                userPlatSvcDataset.add(platsvcTradeDataList.get(index).toData());
            }
        }
        ruleParam.put("TF_F_USER_PLATSVC", userPlatSvcDataset);

        IDataset userSaleActiveDataset = new DatasetList();
        List<SaleActiveTradeData> saleActiveTradeDataList = uca.getUserSaleActives();

        saleActiveTradeDataList = SaleActiveUtil.filterUserSaleActivesByProcessTag(saleActiveTradeDataList);

        if (CollectionUtils.isNotEmpty(saleActiveTradeDataList))
        {
            for (int index = 0, size = saleActiveTradeDataList.size(); index < size; index++)
            {
                userSaleActiveDataset.add(saleActiveTradeDataList.get(index).toData());
            }
        }
        ruleParam.put("TF_F_USER_SALE_ACTIVE", userSaleActiveDataset);

        CustomerTradeData customerTradeData = uca.getCustomer();
        IDataset customerList = new DatasetList();
        customerList.add(customerTradeData.toData());
        ruleParam.put("TF_F_CUSTOMER", customerList);

        VipTradeData vipTradeData = uca.getVip();
        IDataset vipDataset = new DatasetList();
        if (vipTradeData != null)
        {
            vipDataset.add(vipTradeData.toData());
            ruleParam.put("VIP_CLASS_ID", vipTradeData.getVipClassId());
        }
        else
        {
            ruleParam.put("VIP_CLASS_ID", "XXX");
        }

        ruleParam.put("TF_F_CUST_VIP", vipDataset);

        String productId = ruleParam.getString("PRODUCT_ID");
        IDataset productInfo = UpcCall.queryTempChaByCond(productId, "TD_B_PRODUCT", "RSRV_STR3");// ProductInfoQry.queryAllProductInfo(productId);
        String rsrvStr3 = "";
        if (IDataUtil.isNotEmpty(productInfo))
        {
            rsrvStr3 = productInfo.getData(0).getString("FIELD_VALUE");
        }

        if (StringUtils.isNotBlank(rsrvStr3))
        {
            boolean isTroopMember = TroopMemberInfoQry.isTroopMember(ruleParam.getString("USER_ID"), rsrvStr3);
            if (isTroopMember)
            {
                ruleParam.put("IS_TROOP_MEMBER", true);
            }
        }

        IDataset userResDataset = new DatasetList();
        List<ResTradeData> resTradeData = uca.getUserAllRes();
        if (CollectionUtils.isNotEmpty(resTradeData))
        {
            for (int index = 0, size = resTradeData.size(); index < size; index++)
            {
                userResDataset.add(resTradeData.get(index).toData());
            }
        }
        ruleParam.put("TF_F_USER_RES", userResDataset);
    }

    private IData buildSpecProductTipsInfo(UcaData uca, String productId) throws Exception
    {
        List<SaleActiveTradeData> userActives = uca.getUserSaleActives();
        boolean isExists1593Actives = SaleActiveBreUtil.isExists1593Actives(userActives, productId, uca.getUserEparchyCode());
        IData tipsInfosMap = new DataMap();
        if (isExists1593Actives)
        {
            IDataset tipsInfosSet = new DatasetList();
            IData tempMap = new DataMap();
            tempMap.put("TIPS_INFO", "如果办理此营销活动，用户已经办理的礼包将于本月底终止，本活动下月生效，是否继续办理?");
            tempMap.put("TIPS_TYPE", "2");
            tipsInfosSet.add(tempMap);
            tipsInfosMap.put("TIPS_TYPE_CHOICE", tipsInfosSet);
            return tipsInfosMap;
        }
        return tipsInfosMap;
    }

    public IData checkBeforeTrade(IData input) throws Exception
    {
        IData param = new DataMap();

        param.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
        param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
        param.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
        param.put("PAY_CHARGE_ID", input.getString("PAY_CHARGE_ID"));

        UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
        IData ruleParam = putCommonBreParams(param, uca);
        String preType = input.getString("PRE_TYPE");

        checkisNoCheckActives(param.getString("PRODUCT_ID"), uca, true);

        ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE);
        ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_TRADE_BEFORE);

        // 宽带开户营销活动增加传入参数
        ruleParam.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE", ""));
        ruleParam.put("SALE_ACTIVE_ID", input.getString("SALE_ACTIVE_ID", "")); // add 20170511
        ruleParam.put("SPECIAL_SALE_FLAG", input.getString("SPECIAL_SALE_FLAG", ""));
        ruleParam.put("WIDENET_MOVE_SALEACTIVE_SIGN", input.getString("WIDENET_MOVE_SALEACTIVE_SIGN", "0"));
        ruleParam.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS", ""));
        ruleParam.put("CHANGE_UP_DOWN_TAG", input.getString("CHANGE_UP_DOWN_TAG", ""));
        ruleParam.put("BOOKING_DATE", input.getString("BOOKING_DATE", ""));
        //REQ201810290024宽带开户界面增加手机号码套餐的判断
        ruleParam.put("NO_SALE_TRADE_LIMIT_642", input.getString("NO_SALE_TRADE_LIMIT_642", ""));

        if (BofConst.PRE_TYPE_CHECK.equals(preType))
        {
            ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_INTF_CHEK);
            ruleParam.put("DEVICE_MODEL_CODE", input.getString("DEVICE_MODEL_CODE"));
            ruleParam.put("RES_TYPE_ID", input.getString("RES_TYPE_ID"));
            ruleParam.put("TERMINAL_ID", input.getString("TERMINAL_ID"));
        }

        ruleParam.put("TIPS_TYPE", "0|4");

        // REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
        if (StringUtils.isNotBlank(input.getString("IMEI_CODE")))
        {
            ruleParam.put("IMEI_CODE", input.getString("IMEI_CODE"));
        }
        if (StringUtils.isNotBlank(input.getString("GIFT_CODE")))
        {
            ruleParam.put("GIFT_CODE", input.getString("GIFT_CODE"));
        }
        // END

        return BizRule.bre4SuperLimit(ruleParam);
    }

    public IData checkBindSerialNumber(String bindSerialNumber, String productId, String packageId) throws Exception
    {
        IDataset bindSnActives = SaleActiveInfoQry.queryBindUserActives(bindSerialNumber, productId, packageId);

        if (IDataUtil.isEmpty(bindSnActives))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "绑定号码[" + bindSerialNumber + "]未办理可以绑定的营销活动!");
        }

        IData returnMap = new DataMap();

        for (int index = 0, size = bindSnActives.size(); index < size; index++)
        {
            IData bindSnActive = bindSnActives.getData(index);
            String rsrvTag2 = bindSnActive.getString("RSRV_TAG2", "0");
            if ("0".equals(rsrvTag2))
            {
                returnMap = bindSnActive;
                break;
            }
        }

        if (IDataUtil.isEmpty(returnMap))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "绑定号码[" + bindSerialNumber + "]办理的营销活动已被绑定!");
        }

        return returnMap;
    }

    public IData checkIsBindSerialNumberB(IData param) throws Exception
    {
        String packageId = param.getString("PACKAGE_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");

        // PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);
        // packageExtData.getString("TAG_SET1");

        String pkgExtTagSet1 = SaleActiveUtil.getPackageExtTagSet1(packageId, null);

        if (StringUtils.isNotBlank(pkgExtTagSet1) && pkgExtTagSet1.length() > 13 && pkgExtTagSet1.charAt(1) == '1')
        {
            IData returnData = new DataMap();
            returnData.put("BIND_SERIAL_NUMBER_B", "true");
            return returnData;
        }

        return null;
    }

    public IData checkIsSmsVeriCodeTrade(IData param) throws Exception
    {
        boolean hasPrivate = StaffPrivUtil.isPriv(CSBizBean.getVisit().getStaffId(), "SYS_ACTIVE_SEND_RANDOMCODE", StaffPrivUtil.PRIV_TYPE_FIELD);

        if (hasPrivate)
            return null;

        String packageId = param.getString("PACKAGE_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");

        // PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);
        IData packageExtData = UPackageExtInfoQry.queryPkgExtInfoByPackageId(packageId);

        String checkTaginfo = packageExtData.getString("RSRV_STR9");

        if (StringUtils.isBlank(checkTaginfo) || checkTaginfo.length() < 3)
            return null;

        String checktag = checkTaginfo.substring(0, 1);

        IData returnData = new DataMap();

        if ("2".equals(checktag))
        {
            returnData.put("SMS_VERI_CODE_TYPE", "2");
            returnData.put("LIMIT_COUNT", checkTaginfo.substring(2, 3));
            returnData.put("NOTICE_CONTENT", checkTaginfo.substring(4));
        }

        if ("1".equals(checktag))
        {
            returnData.put("SMS_VERI_CODE_TYPE", "1");
            returnData.put("LIMIT_COUNT", checkTaginfo.substring(2, 3));
            returnData.put("NOTICE_CONTENT", checkTaginfo.substring(4));
        }

        return returnData;
    }

    public IData checkPackage(IData param) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER"));

        IData _1593TipsData = new DataMap();
        if (checkisNoCheckActives(param.getString("PRODUCT_ID"), uca, true))
        {
            _1593TipsData = buildSpecProductTipsInfo(uca, param.getString("PRODUCT_ID"));
        }

        IData ruleParam = putCommonBreParams(param, uca);

        ruleParam.put("PAGE_RULE", SaleActiveBreConst.BRE_BY_PAGE_SEL_PACKAGE);
        ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE);
        ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG);

        IData breReturnData = BizRule.bre4SuperLimit(ruleParam);

        if (IDataUtil.isNotEmpty(_1593TipsData))
        {
            breReturnData.putAll(_1593TipsData);
        }

        return breReturnData;
    }

    public boolean checkisNoCheckActives(String productId, UcaData uca, boolean flag) throws Exception
    {
        IDataset commInfoSet = CommparaInfoQry.getCommPkInfo("CSM", "1593", productId, uca.getUserEparchyCode());
        if (IDataUtil.isEmpty(commInfoSet))
        {
            return false;
        }

        List<String> productIds = new ArrayList<String>();
        SaleActiveUtil.getCommparaProductIds(productIds, commInfoSet);

        List<SaleActiveTradeData> exists1593SaleActives = uca.getUserSaleActiveByProductIds(productIds);

        if (CollectionUtils.isNotEmpty(exists1593SaleActives))
        {
            List<SaleActiveTradeData> notExists1593SaleActives = uca.getUserSaleActiveExceptProductIds(productIds);

            notExists1593SaleActives = SaleActiveBreUtil.getQyyxActives(notExists1593SaleActives);

            if (CollectionUtils.isNotEmpty(notExists1593SaleActives))
            {
                IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", uca.getUserEparchyCode());
                boolean hasOtherBackActives = false;
                for (SaleActiveTradeData saleActiveTradeData : notExists1593SaleActives)
                {
                    String _productId = saleActiveTradeData.getProductId();
                    String _pacakgeId = saleActiveTradeData.getPackageId();
                    boolean noBack = SaleActiveUtil.isInCommparaConfigs(_productId, _pacakgeId, noBackConfigs);

                    if (!noBack)
                    {
                        hasOtherBackActives = true;
                        break;
                    }
                }

                if (hasOtherBackActives && flag)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户除了存在指定活动外，还存在其他约定消费或话费返还类的营销活动，不能办理本活动！");
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    public IData putCommonBreParams(IData param, UcaData uca) throws Exception
    {
        IData ruleParam = new DataMap();

        ruleParam.put("RULE_BIZ_TWIG_CODE", "0");
        ruleParam.put("TRADE_TYPE_CODE", "240");

        ruleParam.put("SERIAL_NUMBER", uca.getSerialNumber());
        ruleParam.put("USER_ID", uca.getUserId());
        ruleParam.put("USER_PRODUCT_ID", uca.getProductId());
        ruleParam.put("BRAND_CODE", uca.getUserNewBrandCode());
        ruleParam.put("OPEN_MODE", uca.getUser().getOpenMode());
        ruleParam.put("OPEN_DATE", uca.getUser().getOpenDate());
        ruleParam.put("ACCT_TAG", uca.getUser().getAcctTag());
        ruleParam.put("IS_TROOP_MEMBER", false);
        ruleParam.put("ACCT_ID", uca.getAcctId());
        ruleParam.put("ACCT_DAY", uca.getAcctDay());
        ruleParam.put("FIRST_DATE", uca.getFirstDate());
        ruleParam.put("NEXT_ACCT_DAY", uca.getNextAcctDay());
        ruleParam.put("NEXT_FIRST_DATE", uca.getNextFirstDate());
        ruleParam.put("ACCT_START_DATE", uca.getAcctDayStartDate());
        ruleParam.put("NEXT_START_DATE", uca.getNextAcctDayStartDate());
        ruleParam.put("EPARCHY_CODE", uca.getUserEparchyCode());

        ruleParam.put("CAMPN_TYPE", param.getString("CAMPN_TYPE"));
        ruleParam.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        ruleParam.put("PACKAGE_ID", param.getString("PACKAGE_ID"));

        // 20160111 by songlm 冼乃捷 紧急需求，暂无需求名，作用：将终端类型传进规则内
        if (StringUtils.isNotBlank(param.getString("DEVICE_MODEL_CODE", "")))
        {
            ruleParam.put("DEVICE_MODEL_CODE", param.getString("DEVICE_MODEL_CODE"));
        }
        // end

        ruleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        ruleParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        ruleParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        ruleParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        ruleParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());

        ruleParam.put("QRY_RESULT_INFO", "");

        ruleParam.put("WIDE_USER_CREATE_SALE_ACTIVE", param.getString("WIDE_USER_CREATE_SALE_ACTIVE", "0"));
        ruleParam.put("WIDE_USER_SELECTED_SERVICEIDS", param.getString("WIDE_USER_SELECTED_SERVICEIDS", ""));
        ruleParam.put("WIDENET_MOVE_SALEACTIVE_SIGN", param.getString("WIDENET_MOVE_SALEACTIVE_SIGN", "0"));
        ruleParam.put("CHANGE_UP_DOWN_TAG", param.getString("CHANGE_UP_DOWN_TAG", ""));
        ruleParam.put("BOOKING_DATE", param.getString("BOOKING_DATE", ""));
        ruleParam.put("SPECIAL_SALE_FLAG", param.getString("SPECIAL_SALE_FLAG", "")); // add 20170511

        buildSaleActiveBreDataBus(ruleParam, uca);

        buildSaleActiveOfferDataBus(ruleParam, uca);

        return ruleParam;
    }

    private String checkByProc(IData input, UcaData uca) throws Exception
    {

        IData paramValue = new DataMap();

        paramValue.put("V_EVENT_TYPE", "ATTR");
        paramValue.put("V_EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
        paramValue.put("V_CITY_CODE", this.getVisit().getCityCode());
        paramValue.put("V_DEPART_ID", this.getVisit().getDepartId());
        paramValue.put("V_STAFF_ID", this.getVisit().getStaffId());

        paramValue.put("V_USER_ID", uca.getUserId());
        paramValue.put("V_DEPOSIT_GIFT_ID", uca.getCustId());
        paramValue.put("V_PURCHASE_MODE", input.getString("PRODUCT_ID"));
        paramValue.put("V_PURCHASE_ATTR", input.getString("PACKAGE_ID"));
        paramValue.put("V_TRADE_ID", "-1");

        paramValue.put("V_CHECKINFO", input.getString("CHECKINFO", ""));
        paramValue.put("V_RESULTCODE", input.getString("RESULTCODE", ""));
        paramValue.put("V_RESULTINFO", input.getString("RESULTINFO", ""));
        paramValue.put("V_SALE_TYPE", input.getString("CAMPN_TYPE"));
        paramValue.put("V_VIP_TYPE_ID", input.getString("VIP_TYPE_ID", "-1"));

        paramValue.put("V_VIP_CLASS_ID", input.getString("VIP_CLASS_ID", "-1"));
        
        ProductInfoQry.checkSaleActiveProdByProced(paramValue);
        
        return paramValue.getString("V_RESULTCODE");
    }

    private void buildSaleActiveOfferDataBus(IData ruleParam, UcaData uca) throws Exception
    {
        String saleCampnType = ruleParam.getString("CAMPN_TYPE");
        String saleProductId = ruleParam.getString("PRODUCT_ID");
        String salePackageId = ruleParam.getString("PACKAGE_ID");

        boolean isQyyx = SaleActiveUtil.isQyyx(saleCampnType);
        ruleParam.put("IS_QYYX", isQyyx);

        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(saleProductId);
        ruleParam.put("PM_CATALOG", productInfo);

        IData packageExtInfo = UPackageExtInfoQry.qryPkgExtEnableByPackageId(salePackageId);
        ruleParam.put("PM_OFFER_EXT", packageExtInfo);

        IData pkgInfo = UpcCall.qryOfferComChaTempChaByCond(salePackageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        ruleParam.put("PM_OFFER", pkgInfo);
    }
}
