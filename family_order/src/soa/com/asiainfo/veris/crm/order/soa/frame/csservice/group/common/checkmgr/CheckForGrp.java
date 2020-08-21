
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupRuleConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public final class CheckForGrp extends CSBizBean
{
    private static Logger log = Logger.getLogger(CheckForGrp.class);

    /**
     * 集团通用受理前条件判断
     * 
     * @author 罗颖 IPageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chk(IData inData) throws Exception
    {

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "chk");

        chkGrp(inData);

        return inData;
    }

    /**
     * 闭合群业务受理规则
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkCloseGrpMain(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG", "");
        String deal_type = inData.getString("X_DEAL", "");
        String user_id_a = inData.getString("USER_ID_A");
        String user_id_b = inData.getString("USER_ID_B");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));
        if (deal_type.equals("85"))
        {
            inData.put("TRADE_TYPE_CODE", "1070"); // 闭合群新增业务
            inData.put("ID", user_id_a);

        }
        else if (deal_type.equals("82"))
        {
            inData.put("TRADE_TYPE_CODE", "1071"); // 闭合群删除业务
            inData.put("ID", user_id_b);
        }
        else if (deal_type.substring(0, 1).equals("7") && deal_type.substring(1, 2).equals("1"))
        {
            inData.put("TRADE_TYPE_CODE", "1080"); // 闭合群成员新增业务
            inData.put("ID", user_id_a);
        }
        else if (deal_type.equals("72"))
        {
            inData.put("TRADE_TYPE_CODE", "1081"); // 闭合群成员删除业务
            inData.put("ID", user_id_b);
        }
        else if (deal_type.equals("83") || deal_type.equals("84"))
        {
            inData.put("TRADE_TYPE_CODE", "1070"); // 寻呼，代答组业务
            inData.put("ID", user_id_b);
        }
        else if (deal_type.equals("86") || deal_type.equals("87"))
        {
            inData.put("TRADE_TYPE_CODE", "1071"); // 寻呼，代答组业务
            inData.put("ID", user_id_b);
        }
        else if (deal_type.equals("2"))
        {
            inData.put("TRADE_TYPE_CODE", "4015"); // 集团网外号码维护
            inData.put("ID", user_id_b);
        }
        else if (deal_type.equals("1")) // 成员网外号码维护
        {
            inData.put("TRADE_TYPE_CODE", "4000");
            inData.put("ID", user_id_b);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, deal_type);
        }

        IData resultRule = chk(inData);

        return resultRule;
    }

    /**
     * 集团融合计费新增受理前条件判断
     * 
     * @author 谢媛
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkCrtGrpUnifiedBill(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        String productId = inData.getString("PRODUCT_ID");

        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUnifiedBill);

        String tradeTypeCode = ctrlInfo.getTradeTypeCode();

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);

        // 集团客户标识
        inData.put("CUST_ID", inData.getString("CUST_ID"));

        // 集团用户标识
        inData.put("USER_ID", inData.getString("USER_ID"));

        // 成员用户标识
        inData.put("USER_ID_B", inData.getString("USER_ID_B"));

        // 成员手机号码
        inData.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));

        // 成员用户品牌
        inData.put("BRAND_CODE_B", inData.getString("BRAND_CODE_B"));

        // 成员用户产品
        inData.put("PRODUCT_ID_B", inData.getString("PRODUCT_ID_B"));

        // 成员归属地州
        inData.put("EPARCHY_CODE_B", inData.getString("EPARCHY_CODE_B"));

        // 关系类型
        inData.put("RELATION_TYPE_CODE", "UB");

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUnifiedBillOpen");
        inData.put("CTRL_TYPE", BizCtrlType.CreateUnifiedBill);

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 集团融合计费注销受理前条件判断
     * 
     * @author 谢媛
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkDstGrpUnifiedBill(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        // 扩展判断标记
        /*
         * if (IDataUtil.isEmpty(inData)) { inData.put("CHECK_TAG", "-1"); } else { inData.put("CHECK_TAG",
         * inData.getString("CHECK_TAG", "-1")); }
         */

        if (chkFlag.equals("BaseInfo"))
        {
            String productId = inData.getString("PRODUCT_ID");

            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.DestroyUnifiedBill);

            String tradeTypeCode = ctrlInfo.getTradeTypeCode();

            inData.put("TRADE_TYPE_CODE", tradeTypeCode);
            inData.put("PRODUCT_ID", productId);

            // 集团客户标识
            inData.put("CUST_ID", inData.getString("CUST_ID"));

            // 集团用户标识
            inData.put("USER_ID", inData.getString("USER_ID"));

            // 成员(融合)用户标识
            inData.put("USER_ID_B", inData.getString("USER_ID_B"));
            inData.put("ID", inData.getString("USER_ID_B"));

            // 成员用户品牌
            inData.put("BRAND_CODE_B", inData.getString("BRAND_CODE_B"));

            // 成员归属地州
            inData.put("EPARCHY_CODE_B", inData.getString("EPARCHY_CODE_B"));

        }

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUnifiedBillDestroy");
        inData.put("CTRL_TYPE", BizCtrlType.DestroyUnifiedBill);

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * ESOP预受理业务规则 天津特有
     * 
     * @description
     * @author xiaozp
     * @date Mar 26, 2010
     * @version 1.0.0
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkEsopGrpRule(IData inData) throws Exception
    {

        // 设置规则参数

        chkGrp(inData);

        return inData;
    }

    /**
     * 集团通用受理前条件判断
     * 
     * @param inData
     * @return
     * @throws Exception
     */

    public static IDataset chkEx(IData inData) throws Exception
    {
        IData idata = chk(inData);
        IDataset idataset = new DatasetList();
        idataset.add(idata);
        return idataset;

    }

    /**
     * 集团BBOSS用户开户受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkForBBOSSUserOpen(IData inData) throws Exception
    {

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "BBOSSUserOpen");

        chkGrp(inData);

        return inData;
    }

    /**
     * 集团成员业务受理前条件判断
     * 
     * @author sht PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IDataset chkForProductlimit(IData inData) throws Exception
    {

        inData.put("SPEC_TAG", "YES");
        // j2eely getVisit().setRouteEparchyCode(CSBizBean.getTradeEparchyCode());
        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        return CSAppCall.call("eckProductLeanMutexLimit", inData);
    }

    /**
     * 集团用户业务受理前条件判断
     * 
     * @author sht PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IDataset chkForProductlimitForCG(IData inData) throws Exception
    {

        inData.put("SPEC_TAG", "YES");
        // j2eely getVisit().setRouteEparchyCode(CSBizBean.getTradeEparchyCode());
        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        return CSAppCall.call("ductLimitForCG", inData);
    }

    /**
     * 集团跨省V网集团编码使用判断 云南特有
     * 
     * @author lim PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkForScareVpnNo(IData inData) throws Exception
    {

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "ScareVpnNoCheck");

        chkGrp(inData);

        return inData;
    }

    /**
     * 集团业务受理前检查
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrp(IData inData) throws Exception
    {

        if (log.isDebugEnabled())
        {
            log.debug("chkGrp >>>");
        }

        if (!inData.containsKey("TRADE_TYPE_CODE"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
        }

        // 扩展判断标记
        if (IDataUtil.isEmpty(inData))
        {
            inData.put("CHECK_TAG", "-1");
        }
        else
        {
            inData.put("CHECK_TAG", inData.getString("CHECK_TAG", "-1"));
        }

        String inModeCode = getVisit().getInModeCode();
        // 规则必传参数
        inData.put("IN_MODE_CODE", inModeCode);
        inData.put("PROVINCE_CODE", getVisit().getProvinceCode());
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inData.put("SKIP_FORCE_PACKAGE_FOR_PRODUCT", inData.getString("SKIP_FORCE_PACKAGE_FOR_PRODUCT", "0"));

        // 调用规则
        IData ruleResult = new DataMap();
        IDataset ruleInfo = new DatasetList();

        String tips_type = StringUtils.isBlank(inData.getString("TIPS_TYPE")) ? "0|1|2|4" : inData.getString("TIPS_TYPE");

        if (tips_type.indexOf("0") > -1 || tips_type.indexOf("4") > -1)
        {
            ruleResult = BizRule.Check4Error(inData);

            CSAppException.breerr(ruleResult);

            ruleInfo = ruleResult.getDataset("TIPS_TYPE_ERROR");

            tips_type.replaceAll("0", "_");
            tips_type.replaceAll("4", "_");// 用下划线替换0和4
        }

        if (IDataUtil.isEmpty(ruleInfo) && !InModeCodeUtil.isNotSale(inModeCode)) // 没有异常,前台校验提示或者询问类规则
        {
            if (tips_type.indexOf("1") > -1 || tips_type.indexOf("2") > 2) // 需要校验提示或者询问类规则
            {
                inData.put("TIPS_TYPE", tips_type);
                ruleResult = BizRule.CheckTipByTipType(inData);
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug("chkGrp <<<");
        }

        return ruleResult;
    }

    /**
     * 集团批量成员新增
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkGrpBatMebOrder(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        String userId = inData.getString("USER_ID");// 集团用户标识

        if (StringUtils.isEmpty(userId))
        {

            CSAppException.apperr(BofException.CRM_BOF_008);
        }

        IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        String productId = "";
        // 如果grpUserInfo为null,则取databus中查询,解决彩铃开户并加主付费号码(作为成员的)的问题.
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            String grpSerialNumber = inData.getString("GRP_SERIAL_NUMBER");
            UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);
            if(grpUCA!=null){
            	grpUserInfo = grpUCA.getUser().toData();
                productId = grpUCA.getProductId();
            }else{
            	CSAppException.apperr(BofException.CRM_BOF_011, userId);
            }
        }
        else
        {
            productId = grpUserInfo.getString("PRODUCT_ID");
        }

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isBlank(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateMember);
            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);

        // 集团客户标识
        inData.put("CUST_ID", grpUserInfo.getString("CUST_ID"));

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");
        inData.put("CTRL_TYPE", BizCtrlType.CreateMember);

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 集团成员变更受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpMebChg(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        // 扩展判断标记
        /*
         * if (IDataUtil.isEmpty(inData)) { inData.put("CHECK_TAG", "-1"); } else { inData.put("CHECK_TAG",
         * inData.getString("CHECK_TAG", "-1")); }
         */

        String userId = inData.getString("USER_ID");// 集团用户标识

        if (StringUtils.isEmpty(userId))
        {

            CSAppException.apperr(BofException.CRM_BOF_008);
        }

        IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_011);
        }
        String productId = grpUserInfo.getString("PRODUCT_ID");

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isEmpty(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeMemberDis);

            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebChg");
        inData.put("CTRL_TYPE", BizCtrlType.ChangeMemberDis);

        String serialNumber = inData.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, RouteInfoQry.getEparchyCodeBySn(serialNumber));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }

        // 集团客户标识
        inData.put("CUST_ID", grpUserInfo.getString("CUST_ID"));

        // 集团用户标识
        inData.put("USER_ID", inData.getString("USER_ID"));

        // 成员用户标识
        inData.put("USER_ID_B", userInfo.getString("USER_ID"));

        // 成员用户品牌
        inData.put("BRAND_CODE_B", userInfo.getString("BRAND_CODE"));

        // 成员归属地州
        inData.put("EPARCHY_CODE_B", userInfo.getString("EPARCHY_CODE"));

        inData.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        // 成员产品
        inData.put("PRODUCT_ID_B", userInfo.getString("PRODUCT_ID"));

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 集团用户成员注销受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpMebDestory(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        // 扩展判断标记
        /*
         * if (IDataUtil.isEmpty(inData)) { inData.put("CHECK_TAG", "-1"); } else { inData.put("CHECK_TAG",
         * inData.getString("CHECK_TAG", "-1")); }
         */

        String userId = inData.getString("USER_ID");// 集团用户标识

        if (StringUtils.isEmpty(userId))
        {

            CSAppException.apperr(BofException.CRM_BOF_008);
        }

        IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_011);
        }

        String productId = grpUserInfo.getString("PRODUCT_ID");

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isBlank(tradeTypeCode))
        {

            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.DestoryMember);
            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);

        String serialNumber = inData.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, RouteInfoQry.getEparchyCodeBySn(serialNumber));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }

        // 成员用户标识
        inData.put("USER_ID_B", userInfo.getString("USER_ID"));

        inData.put("ID", inData.getString("USER_ID_B"));

        // 成员用户品牌
        inData.put("BRAND_CODE_B", userInfo.getString("BRAND_CODE"));

        // 成员归属地州
        inData.put("EPARCHY_CODE_B", userInfo.getString("EPARCHY_CODE"));

        inData.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

        // 成员产品
        inData.put("PRODUCT_ID_B", userInfo.getString("PRODUCT_ID"));

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebDestory");
        inData.put("CTRL_TYPE", BizCtrlType.DestoryMember);

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 集团产品成员营销受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpMeberSale(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        // 扩展判断标记
        /*
         * if (IDataUtil.isEmpty(inData)) { inData.put("CHECK_TAG", "-1"); } else { inData.put("CHECK_TAG",
         * inData.getString("CHECK_TAG", "-1")); }
         */

        String productId = inData.getString("PRODUCT_ID");

        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.GroupMemberSale);

        String tradeTypeCode = ctrlInfo.getTradeTypeCode();

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebSale");
        inData.put("CTRL_TYPE", BizCtrlType.GroupMemberSale);

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * @Description:集团成员资料修改受理前条件判断 新疆特有
     * @author wusf
     * @date 2010-1-27
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkGrpMebMod(IData inData) throws Exception
    {

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebMod");
        chkGrp(inData);
        return inData;
    }

    /**
     * 集团用户成员定购受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpMebOrder(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        String checkTag = inData.getString("CHECK_TAG");

        // 扩展判断标记
        /*
         * if (IDataUtil.isEmpty(inData)) { inData.put("CHECK_TAG", "-1"); } else { inData.put("CHECK_TAG",
         * inData.getString("CHECK_TAG", "-1")); }
         */

        String userId = inData.getString("USER_ID");// 集团用户标识

        if (StringUtils.isEmpty(userId))
        {

            CSAppException.apperr(BofException.CRM_BOF_008);
        }
        
        //判断和校园是否激活和是否实名PRODUCT_ID=10009150, by zhuwj 
        String SERIAL_NUMBER = inData.getString("SERIAL_NUMBER");
        if(!"".equals(chkFlag) && chkFlag!=null){
        if("BaseInfo".equals(inData.getString("CHK_FLAG"))){
        	//只有第一个界面才验证
        String productId= inData.getString("PRODUCT_ID", "0");  
           if("10009150".equals(productId)){
        	//排除异网和校园用户，异网和校园用户是（H+电话号码）
        	 if(!inData.getString("SERIAL_NUMBER").trim().startsWith("H")){
        		//根据用户id去查找数据，判断是否已经实名制和已经激活
            	IData UserInfo=UcaInfoQry.getUserInfo(SERIAL_NUMBER);
            	if(IDataUtil.isNotEmpty(UserInfo)){
            		String ACCT_TAG=UserInfo.getString("ACCT_TAG");
            		String 	IS_REAL_NAME=UserInfo.getString("IS_REAL_NAME");
            		if(ACCT_TAG.equals("2")){
            			//该成员为未激活状态
            			 CSAppException.apperr(BofException.CRM_BOF_025);
            		}
            		if(IS_REAL_NAME.equals("0")){
            			//该成员未实名制状态
            			CSAppException.apperr(BofException.CRM_BOF_026);
            		}
            		
            	}
        	}
        	
          }
         }
        
        }

        IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        String productId = "";
        // 如果grpUserInfo为null,则取databus中查询,解决彩铃开户并加主付费号码(作为成员的)的问题.
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            String grpSerialNumber = inData.getString("GRP_SERIAL_NUMBER");
            UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);
            if(grpUCA!=null){
            	grpUserInfo = grpUCA.getUser().toData();
                productId = grpUCA.getProductId();
            }else{
                CSAppException.apperr(BofException.CRM_BOF_011, userId);
            }
        }
        else
        {
            productId = grpUserInfo.getString("PRODUCT_ID");
        }

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isBlank(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateMember);
            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);
        inData.put("RELATION_TYPE_CODE", UProductCompInfoQry.getRelationTypeCodeByProductId(productId));
        // 集团客户标识
        inData.put("CUST_ID", grpUserInfo.getString("CUST_ID"));

        // 集团用户标识
        // inData.put("USER_ID", inData.getString("USER_ID"));

        // 成员手机号码
        // inData.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));

        String serialNumber = inData.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, RouteInfoQry.getEparchyCodeBySn(serialNumber));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }

        // 成员用户标识
        inData.put("USER_ID_B", userInfo.getString("USER_ID"));

        // 成员用户品牌
        inData.put("BRAND_CODE_B", userInfo.getString("BRAND_CODE"));

        // 成员归属地州
        inData.put("EPARCHY_CODE_B", userInfo.getString("EPARCHY_CODE"));

        inData.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

        // 成员产品
        inData.put("PRODUCT_ID_B", userInfo.getString("PRODUCT_ID"));

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");
        inData.put("CTRL_TYPE", BizCtrlType.CreateMember);

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 成员付费关系变更规则
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkGrpMebPayMod(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG", "");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        inData.put("TRADE_TYPE_CODE", "4035");

        IData resultRule = null;

        resultRule = chk(inData);

        return resultRule;
    }

    /**
     * 集团成员营销受理前条件判断 湖南特有
     * 
     * @author 廖翊 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpMebSale(IData inData) throws Exception
    {

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpMebSale");
        chkGrp(inData);
        return inData;
    }

    /**
     * 集团营销受理前条件判断 湖南特有
     * 
     * @author 廖翊 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpSale(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");
        String productId = inData.getString("PRODUCT_ID");

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isBlank(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.GroupSalePay);
            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));
        inData.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId));

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpSale");
        inData.put("CTRL_TYPE", BizCtrlType.GroupSalePay);

        chkGrp(inData);

        return inData;
    }

    /**
     * 集团用户营销活动受理前条件判断
     * 
     * @author dingtl PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpSaleUser(IData inData) throws Exception
    {

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUserSale");
        chkGrp(inData);
        return inData;
    }

    /**
     * 集团用户变更受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpUserChg(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG", "");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));
        String productId = inData.getString("PRODUCT_ID");

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isBlank(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeUserDis);
            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);
        inData.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId));

        IData resultRule = null;

        // if (chkFlag.equals("ProductInfo"))
        // {

        inData.put("ID", inData.getString("USER_ID"));
        // resultRule = chkGrp(inData);
        //
        // IData needDelSvcChecks = ctrlInfo.getCtrlData("DelSvcCheck");
        //
        // if (IDataUtil.isEmpty(needDelSvcChecks))
        // {
        // resultRule = new DataMap();
        // }
        // String needCheckSvc = needDelSvcChecks.getString("ATTR_VALUE", "");
        //
        // String[] needCheckSvcList = needCheckSvc.split(",");
        //
        // // 预处理产品元素
        // IDataset dataset = inData.getDataset("ELEMENT_INFO");
        //
        // if (IDataUtil.isEmpty(dataset))
        // {
        // resultRule = new DataMap();
        // }
        //
        // IDataset delsvcDataset = new DatasetList(); // 服务信息
        //
        // for (int row = 0; row < dataset.size(); row++)
        // {
        // IData rowdata = dataset.getData(row);
        //
        // IDataset elementsDataset = (IDataset) rowdata.get("ELEMENTS"); // 取元素
        // for (int j = 0; j < elementsDataset.size(); j++)
        // {
        // IData packageData = (IData) elementsDataset.get(j); // 取每个元素
        // String elementType = packageData.getString("ELEMENT_TYPE_CODE", "");
        // String productmode = packageData.getString("PRODUCT_MODE", "");
        //
        // // 处理用户产品
        // if (productmode.equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()) ||
        // productmode.equals(GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue()))
        // {
        //
        // // 未修改不处理
        // if (packageData.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.DEL.getValue()) && elementType.equals("S"))
        // {
        // delsvcDataset.add(packageData);
        // }
        // }
        // }
        // }
        //
        // for (int i = 0; i < delsvcDataset.size(); i++)
        // {
        // IData delsvc = delsvcDataset.getData(i);
        //
        // String serviceId = delsvc.getString("ELEMENT_ID", "");
        // String serviceName = delsvc.getString("ELEMENT_NAME", "");
        // for (int j = 0; j < needCheckSvcList.length; j++)
        // {
        // if (serviceId.equals(needCheckSvcList[j]))
        // {
        // inData = new DataMap();
        //
        // inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        // inData.put("PRODUCT_ID", productId);
        // inData.put("SERVICE_ID", serviceId);
        // inData.put("SERVICE_NAME", serviceName);
        // inData.put("EC_USER_ID", inData.getString("USER_ID"));
        //
        // }
        // }
        // }
        // }

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUserChg");
        inData.put("CTRL_TYPE", BizCtrlType.ChangeUserDis);

        resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 集团用户销户受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpUserDestory(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG");

        String productId = inData.getString("PRODUCT_ID");

        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.DestoryUser);

        String tradeTypeCode = ctrlInfo.getTradeTypeCode();

        IData idata = ctrlInfo.getCtrlData("HaveMebDstOk");

        if (IDataUtil.isNotEmpty(idata))
        {
            inData.put("HAVEMEBDSTOK", "true");
        }
        else
        {
            inData.put("HAVEMEBDSTOK", "false");
        }
        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);
        inData.put("USER_ID", inData.getString("USER_ID"));
        inData.put("CUST_ID", inData.getString("CUST_ID"));

        inData.put("IF_BOOKING", inData.getString("IF_BOOKING"));

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUserDestory");
        inData.put("CTRL_TYPE", BizCtrlType.DestoryUser);

        // 设置业务参数
        inData.put("FEE", "0");
        inData.put("LEAVE_REAL_FEE", "0");
        inData.put("ID", inData.getString("USER_ID"));
        inData.put("ID_TYPE", "1");

        IData resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * 集团用户开户受理前条件判断
     * 
     * @author 罗颖 PageData 页面对象
     * @param inData
     *            IData 输入参数
     * @return outData IData
     * @throws Exception
     */
    public static IData chkGrpUserOpen(IData inData) throws Exception
    {

        String chkFlag = inData.getString("CHK_FLAG");
        String productId = inData.getString("PRODUCT_ID"); 

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isBlank(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);
            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        // 入参：TRADE_TYPE_CODE,PRODUCT_ID,CUST_ID,SERIAL_NUMBER,BRAND_CODE,RULE_EVNT_CODE
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));
        inData.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId));

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUserOpen");
        inData.put("CTRL_TYPE", BizCtrlType.CreateUser);

        IData resultRule = chkGrp(inData);

        return resultRule;

    }

    public static IData chkPayRelaAdvChg(IData inData) throws Exception
    {

        String chkFlag = inData.getString("CHK_FLAG");

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        String serialNumber = inData.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, RouteInfoQry.getEparchyCodeBySn(serialNumber));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }

        String userId = userInfo.getString("USER_ID");// 成员用户标识
        String acctId = inData.getString("ACCT_ID");// 成员账户标识,由bean传

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        inData.put("USER_ID", userId);
        inData.put("ACCT_ID", acctId);
        inData.put("CUST_ID", inData.getString("CUST_ID"));// 集团客户标识

        IData resultRule = chk(inData);

        return resultRule;

    }

    /**
     * 普通V网短号码验证
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkVpmnShortCode(IData inData) throws Exception
    {

        String userId = inData.getString("USER_ID");// 集团用户标识

        if (StringUtils.isEmpty(userId))
        {

            CSAppException.apperr(BofException.CRM_BOF_008);
        }

        IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_011);
        }
        String productId = grpUserInfo.getString("PRODUCT_ID");

        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");

        if (StringUtils.isEmpty(tradeTypeCode))
        {
            BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeMemberDis);

            tradeTypeCode = ctrlInfo.getTradeTypeCode();
        }

        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);
        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "VpmnShortCode");
        inData.put("CTRL_TYPE", BizCtrlType.ChangeMemberDis);

        chkGrp(inData);
        return inData;
    }

    /**
     * 普通V网升级融合V网规则
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData chkVpmnToCntrx(IData inData) throws Exception
    {
        String chkFlag = inData.getString("CHK_FLAG", "");

        inData.put("ACTION_ID", replaceRuleEvntCode(chkFlag));

        inData.put("TRADE_TYPE_CODE", "2528"); // (集团产品升级)智能网VPMN
        IData resultRule = null;

        // 设置规则参数
        inData.put("ACTION_TYPE", "chkBeforeForGrp");
        inData.put("RULE_BIZ_KIND_CODE", "GrpUserChg");

        resultRule = chkGrp(inData);

        return resultRule;
    }

    /**
     * RULE_EVNT_CODE值转成ACTION_ID 又String转成int
     * 
     * @param chkFlag
     * @return
     * @throws Exception
     */
    public static int replaceRuleEvntCode(String chkFlag) throws Exception
    {
        if (GroupRuleConst.BaseInfo.equals(chkFlag))
        {
            return GroupRuleConst.RULE_EVNT_CODE.BaseInfo.getValue();
        }
        else if (GroupRuleConst.ProductInfo.equals(chkFlag))
        {
            return GroupRuleConst.RULE_EVNT_CODE.ProductInfo.getValue();
        }
        else if (GroupRuleConst.PreView.equals(chkFlag))
        {
            return GroupRuleConst.RULE_EVNT_CODE.PreView.getValue();
        }
        else if (GroupRuleConst.BBoss.equals(chkFlag))
        {
            return GroupRuleConst.RULE_EVNT_CODE.BBoss.getValue();
        }
        else if (GroupRuleConst.PayRelaAdv.equals(chkFlag))
        {
            return GroupRuleConst.RULE_EVNT_CODE.PayRelaAdv.getValue();
        }
        else if (GroupRuleConst.VGPOPayRelaAdv.equals(chkFlag))
        {
            return GroupRuleConst.RULE_EVNT_CODE.VGPOPayRelaAdv.getValue();
        }
        else
        {
            return -1;
        }
    }
    
    /**
     * 校验成员号码的状态已经欠费情况(REQ201609180012关于优化集团统付业务限制条件的要求)
     * @param data
     * @Author:chenzg
     * @Date:2016-9-26
     */
    public static void chkPayRelaMebStateAndFeeInfo(IData data) throws Exception{
        String userId = data.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserInfoByUserIdFromDB(userId, Route.CONN_CRM_CG);
        String uState = userInfo.getString("USER_STATE_CODESET", "");
        //不存在往月欠费，不管实时结余+信用度是否>0，只判断手机状态是否正常，如果正常开通则可以办理； 
        if(!"0".equals(uState) && !"N".equals(uState)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "该用户手机状态处于非正常状态，请核实恢复后进行办理。");
        }
        //调用账务接口
        IData oweFeeInfo = AcctCall.getOweFeeByUserId(userId);
        //存在往月欠费，不管信用度多少，不管当前是否开通，都不能办理；
        double lastOweFee = oweFeeInfo.getDouble("LAST_OWE_FEE", 0);   //往月欠费
        if(lastOweFee>0){
            CSAppException.apperr(GrpException.CRM_GRP_713, "该用户存在往月欠费，请结清欠费后进行办理！");
        }
        
    }
    
    /**
     * 校验集团产品(用户)是否欠费,REQ201611170010集团代付信控需求
     * @param userId
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-8
     */
    public static void chkGrpUserIsOwnFee(String grpUserId) throws Exception{
    	IData grpUser = UcaInfoQry.qryUserMainProdInfoByUserId(grpUserId);
    	if(IDataUtil.isNotEmpty(grpUser))
    	{
    		String grpPrdId = grpUser.getString("PRODUCT_ID");
    		String rsrvStr10 = grpUser.getString("RSRV_STR10", "");	//集团代付暂停恢复标识
    		IDataset paramDs = ParamInfoQry.getCommparaByCode("CSM", "1138", grpPrdId, "0898");
            if(IDataUtil.isNotEmpty(paramDs) && "CRDIT_STOP".equals(rsrvStr10)){
            	CSAppException.apperr(GrpException.CRM_GRP_713, "集团产品（用户）已欠费，不能办理该业务！");
            }
    	}
    }

}
