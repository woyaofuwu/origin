
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUserForPre;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeDeviceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/*
 * @description 预受理用户转正式受理用户
 * @author xunyl
 * @date 2013-10-17
 */
public class PreUserToBBossUserBean
{

    /**
     * 典型场景：用户在发送正式受理报文前多次操作资费变更，第一次订购资费，第二次删除资费，第三次再订购资费 解决方案：订购时将资费置成发服开标记，删除资费时又将标记置为不发服开，再次订购时再次将标记改回 chenyi
     * 2014-6-19
     * 
     * @param distinctInfo
     * @param tradeId
     * @param productUserId
     * @param merchSpecCode
     * @param productOrderId
     * @param productOfferId
     * @param productSpecCode
     * @throws Exception
     */
    private static void addDiscntInfo(IData distinctInfo, String tradeId, String productUserId, String merchSpecCode, String productOrderId, String productOfferId, String productSpecCode) throws Exception
    {

        String productId = distinctInfo.getString("PRODUCT_ID");
        String packageId = distinctInfo.getString("PACKAGE_ID");
        String elementId = distinctInfo.getString("ELEMENT_ID");
        String endDate = distinctInfo.getString("END_DATE");

        IDataset discntTradeInfoList = TradeDiscntInfoQry.getTradeDiscntInfosByDiscntCode(tradeId, elementId);
        // 直接新增资费
        if (IDataUtil.isEmpty(discntTradeInfoList))
        {
            // 新增资费信息
            IData discntTradeInfo = GrpCommonBean.getDiscntTradeInfo(tradeId, productUserId, productId, packageId, elementId, endDate);
            TradeDiscntInfoQry.insertDiscntInfo(discntTradeInfo);

            // 新增BBOSS侧产品资费信息(TF_B_TRADE_GRP_MERCHP_DISCNT表，供服务开通用)
            IDataset discntCodeInfoList = AttrBizInfoQry.getBizAttr("1", "B", "DIS", elementId, null);
            if (IDataUtil.isNotEmpty(discntCodeInfoList))
            {
                IData discntCodeInfo = discntCodeInfoList.getData(0);
                String productDiscntCode = discntCodeInfo.getString("ATTR_VALUE");
                String instId = discntTradeInfo.getString("INST_ID");
                IData merchpDiscntTradeInfo = GrpCommonBean.getMerchpDiscntTradeInfo(tradeId, merchSpecCode, productOrderId, productOfferId, productSpecCode, productDiscntCode, productUserId, instId, endDate, "1");
                TradeGrpMerchpDiscntInfoQry.insertMerchpDiscntInfo(merchpDiscntTradeInfo);
            }

            // 新增资费参数，发送服务
            IDataset icbParamInfoList = distinctInfo.getDataset("ATTR_PARAM");
            if (IDataUtil.isNotEmpty(icbParamInfoList))
            {
                // 获取ICB参数名字
                GrpCommonBean.getDistinctAttrName(icbParamInfoList);

                for (int j = 0; j < icbParamInfoList.size(); j++)
                {
                    IData icbParamInfo = icbParamInfoList.getData(j);
                    String relaInstId = discntTradeInfo.getString("INST_ID");
                    String attrCode = icbParamInfo.getString("ATTR_CODE");
                    String attrName = icbParamInfo.getString("ATTR_NAME");
                    String attrValue = icbParamInfo.getString("ATTR_VALUE");
                    String isneedpf = icbParamInfo.getString("IS_NEED_PF", "1");
                    IData icbTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, null, attrName, attrValue, isneedpf, endDate, "D");
                    TradeAttrInfoQry.insertAttrInfo(icbTradeInfo);
                }
            }

        }
        else
        {
            // 将已有的资费状态修改成新增
            IData discntTradeInfo = discntTradeInfoList.getData(0);
            discntTradeInfo.put("MODIFY_TAG_NEW", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
            discntTradeInfo.put("MODIFY_TAG_OLD", discntTradeInfo.getString("MODIFY_TAG"));

            TradeDiscntInfoQry.updateModefytag(discntTradeInfo);

            // 将BBOSS侧产品资费信息状态改为新增，是否发送服开的标志设置为与之前相反
            String isNeedPf = "";
            IDataset discntCodeInfoList = AttrBizInfoQry.getBizAttr("1", "B", "DIS", elementId, null);
            if (IDataUtil.isNotEmpty(discntCodeInfoList))
            {
                IData discntCodeInfo = discntCodeInfoList.getData(0);
                String productDiscntCode = discntCodeInfo.getString("ATTR_VALUE");
                IData inparam = new DataMap();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("PRODUCT_DISCNT_CODE", productDiscntCode);
                IDataset merchpDiscntInfoList = TradeGrpMerchpDiscntInfoQry.getMerchpDisInfoByDiscntCode(inparam);
                if (IDataUtil.isNotEmpty(merchpDiscntInfoList))
                {
                    IData merchpDiscntInfo = merchpDiscntInfoList.getData(0);
                    merchpDiscntInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                    merchpDiscntInfo.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());
                    isNeedPf = merchpDiscntInfo.getString("IS_NEED_PF");
                    merchpDiscntInfo.put("IS_NEED_PF", ("1".equals(isNeedPf) || StringUtils.isEmpty(isNeedPf)) ? "0" : "1");
                    TradeGrpMerchpDiscntInfoQry.updateMerchpDistinctState(merchpDiscntInfo);
                }
            }

            // 如果产品资费发服开，删除ICB参数然后新增一条发送服开的ICB参数台账,否则删除ICB参数参数后新增一条不发服开的ICB参数台账
            IDataset icbParamInfoList = distinctInfo.getDataset("ATTR_PARAM");
            if (IDataUtil.isNotEmpty(icbParamInfoList))
            {
                // 获取ICB参数名字
                GrpCommonBean.getDistinctAttrName(icbParamInfoList);

                for (int j = 0; j < icbParamInfoList.size(); j++)
                {
                    IData icbParamInfo = icbParamInfoList.getData(j);
                    // 删除老ICB参数
                    IData inparam = new DataMap();
                    inparam.put("TRADE_ID", tradeId);
                    inparam.put("INST_TYPE", "D");
                    inparam.put("ATTR_CODE", icbParamInfo.getString("ATTR_CODE"));
                    inparam.put("USER_ID", productUserId);
                    TradeAttrInfoQry.delTradeAttrInfo(inparam);
                    // 新增ICB参数
                    String relaInstId = discntTradeInfo.getString("INST_ID");
                    String attrCode = icbParamInfo.getString("ATTR_CODE");
                    String attrName = icbParamInfo.getString("ATTR_NAME");
                    String attrValue = icbParamInfo.getString("ATTR_VALUE");
                    isNeedPf = ("1".equals(isNeedPf) || StringUtils.isEmpty(isNeedPf)) ? "0" : "1";
                    IData icbTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, null, attrName, attrValue, isNeedPf, endDate, "D");
                    TradeAttrInfoQry.insertAttrInfo(icbTradeInfo);
                }
            }

        }

    }

    /**
     * 处理管理流程中新增的属性
     * 
     * @param baseData
     * @param onlineData
     */
    protected static void dealAddParamAttr(IData baseData, IData addParamData) throws Exception
    {
        String tradeId = baseData.getString("TRADE_ID");
        String productUserId = baseData.getString("BBOSS_USER_ID");
        String relaInstId = baseData.getString("RELA_INST_ID");

        String attrCode = addParamData.getString("ATTR_CODE", "");
        String attrGroup = addParamData.getString("ATTR_GROUP", "");
        String attrValue = addParamData.getString("ATTR_VALUE", "");
        String attrName = addParamData.getString("ATTR_NAME", "");

        String modifyTag = "";
        String endDate = SysDateMgr.getTheLastTime();

        String oldValue = addParamData.getString("PARAM_OLD_VALUE");

        // 1-处理变更场景
        if (StringUtils.isNotBlank(oldValue))// 说明是修改更新
        {
            // 场景1 台帐表有old值，修改为new值 则old需要删除且发完服开回收

            // 场景2 台帐表有old值，修改为空，即删除原来的值 则old值需要删除发完服开回收， 新增空值 发完服开回收

            modifyTag = CSBaseConst.TRADE_MODIFY_TAG.Add.getValue();
            IDataset delAttrTradeInfoList = TradeAttrInfoQry.getTradeAttrByAttrCode(attrCode, modifyTag, attrGroup, tradeId);

            // 1.1- 处理老值
            if (IDataUtil.isNotEmpty(delAttrTradeInfoList))
            {
                // 老台账信息置位删除状态
                GrpCommonBean.delAttrTradeInfo(tradeId, attrCode, oldValue, "P", "M", "1");
            }

            // 1.2.1-处理新值
            if (StringUtils.isNotBlank(attrValue))
            {
                // 新增新台账信息 场景1 新增new值
                IData attrTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, attrGroup, attrName, attrValue, "1", endDate, "P");
                TradeAttrInfoQry.insertAttrInfo(attrTradeInfo);
            }
            else
            {
                // 1.2.2-新增新台账信息 场景2 新增空值且回收
                IData attrTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, attrGroup, attrName, attrValue, "1", endDate, "P");
                attrTradeInfo.put("RSRV_STR1", "M");
                TradeAttrInfoQry.insertAttrInfo(attrTradeInfo);
            }

        }
        else
        {// 2-处理新增场景
            IData attrTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, attrGroup, attrName, attrValue, "1", endDate, "P");
            TradeAttrInfoQry.insertAttrInfo(attrTradeInfo);
        }

        // 3- 处理一次性费用 发起方记录一次性费用表
        if (attrCode.startsWith("ONCEFEE_") && addParamData.getInt("ATTR_VALUE", 0) > 0)
        {
            InsOneFeeTrade(tradeId, productUserId, attrValue, attrName);
        }

    }

    /**
     * 2014-6-19 chenyi 预受理转正式受理删除资费 场景 在转正式受理前已经订购过该资费，在转正式受理时删除此资费
     * 
     * @param distinctInfo
     * @param tradeId
     * @param productUserId
     * @param merchSpecCode
     * @param productOrderId
     * @param productOfferId
     * @param productSpecCode
     * @throws Exception
     */
    private static void delDiscntInfo(IData distinctInfo, String tradeId, String productUserId, String merchSpecCode, String productOrderId, String productOfferId, String productSpecCode) throws Exception
    {

        // 1- 查询台帐信息
        String elementId = distinctInfo.getString("ELEMENT_ID");
        IDataset discntTradeInfoList = TradeDiscntInfoQry.getTradeDiscntInfosByDiscntCode(tradeId, elementId);

        if (IDataUtil.isNotEmpty(discntTradeInfoList))
        {
            // 更细本地资费台帐信息
            IData discntTradeInfo = discntTradeInfoList.getData(0);
            discntTradeInfo.put("MODIFY_TAG_NEW", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
            discntTradeInfo.put("MODIFY_TAG_OLD", discntTradeInfo.getString("MODIFY_TAG"));
            TradeDiscntInfoQry.updateModefytag(discntTradeInfo);

            // 更新集团资费信息 将BBOSS侧产品资费信息状态改为删除，是否发送服开的标志设置为与之前相反
            String isNeedPf = "";
            IDataset discntCodeInfoList = AttrBizInfoQry.getBizAttr("1", "B", "DIS", elementId, null);
            if (IDataUtil.isNotEmpty(discntCodeInfoList))
            {
                IData discntCodeInfo = discntCodeInfoList.getData(0);
                String productDiscntCode = discntCodeInfo.getString("ATTR_VALUE");
                IData inparam = new DataMap();
                inparam.put("TRADE_ID", tradeId);
                inparam.put("PRODUCT_DISCNT_CODE", productDiscntCode);
                IDataset merchpDiscntInfoList = TradeGrpMerchpDiscntInfoQry.getMerchpDisInfoByDiscntCode(inparam);
                if (IDataUtil.isNotEmpty(merchpDiscntInfoList))
                {
                    IData merchpDiscntInfo = merchpDiscntInfoList.getData(0);
                    merchpDiscntInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                    merchpDiscntInfo.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
                    isNeedPf = merchpDiscntInfo.getString("IS_NEED_PF");
                    merchpDiscntInfo.put("IS_NEED_PF", ("1".equals(isNeedPf) || StringUtils.isEmpty(isNeedPf)) ? "0" : "1");
                    TradeGrpMerchpDiscntInfoQry.updateMerchpDistinctState(merchpDiscntInfo);
                }
            }

            // 删除ICB参数，是否发送服开的标志设置为与之前相反
            IDataset icbParamInfoList = distinctInfo.getDataset("ATTR_PARAM");

            if (IDataUtil.isNotEmpty(icbParamInfoList))
            {
                // 获取ICB参数名字
                GrpCommonBean.getDistinctAttrName(icbParamInfoList);

                // 更新资费参数
                for (int j = 0; j < icbParamInfoList.size(); j++)
                {
                    IData icbParamInfo = icbParamInfoList.getData(j);
                    IData inparam = new DataMap();
                    inparam.put("TRADE_ID", tradeId);
                    inparam.put("INST_TYPE", "D");
                    inparam.put("ATTR_CODE", icbParamInfo.getString("ATTR_CODE"));
                    inparam.put("ATTR_VALUE", icbParamInfo.getString("ATTR_VALUE"));
                    inparam.put("USER_ID", productUserId);
                    inparam.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                    inparam.put("RSRV_STR1", null);
                    inparam.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
                    inparam.put("IS_NEED_PF", ("1".equals(isNeedPf) || StringUtils.isEmpty(isNeedPf)) ? "0" : "1");
                    TradeAttrInfoQry.updateBbossAttrState(inparam);
                }

            }

        }

    }

    /**
     * @descirption 新增合同附件、审批人信息、联系人信息
     * @author xunyl
     * @date 2013-10-18
     */
    protected static void insertAttInfos(String merchTradeId, IData map) throws Exception
    {
        // 1- 初始化入参
        IData inparam = new DataMap();

        // 2- 添加商品台账编号
        inparam.put("TRADE_ID", merchTradeId);

        // 3- 添加商品编号
        IDataset productTradeInfoList = TradeProductInfoQry.getTradeProduct(merchTradeId, null);
        if (IDataUtil.isEmpty(productTradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData productTradeInfo = productTradeInfoList.getData(0);
        inparam.put("PRODUCT_ID", productTradeInfo.getString("PRODUCT_ID"));

        // 4- 添加合同附件信息
        if (StringUtils.isNotEmpty(map.getString("ATT_INFOS", "")))
        {
            inparam.put("ATT_INFOS", new DatasetList(map.getString("ATT_INFOS")));
        }

        // 5- 添加审批人信息
        if (StringUtils.isNotEmpty(map.getString("AUDITOR_INFOS", "")))
        {
            inparam.put("AUDITOR_INFOS", new DatasetList(map.getString("AUDITOR_INFOS")));
        }

        // 6- 添加联系人信息
        if (StringUtils.isNotEmpty(map.getString("CONTACTOR_INFOS", "")))
        {
            inparam.put("CONTACTOR_INFOS", new DatasetList(map.getString("CONTACTOR_INFOS")));
        }

        // 7- 新增合同附件、审批人信息、联系人信息
        serverAttInfos(inparam);
    }

    /**
     * @description 一次性费用入表
     * @author xunyl
     * @date 2013-10-25
     */
    protected static void InsOneFeeTrade(String tradeId, String productUserId, String attrValue, String attrName) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        inparam.put("INST_TYPE", "P");
        inparam.put("USER_ID", productUserId);
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        inparam.put("FEE_MODE", "0");
        inparam.put("FEE_TYPE_CODE", "0");
        inparam.put("DEFER_CYCLE_ID", "-1");
        inparam.put("FEE_TYPE_CODE", "999");
        inparam.put("MONEY", attrValue);
        inparam.put("ACT_TAG", "1");
        inparam.put("RSRV_STR1", "ONCEFEE"); // 标识为BBOSS一次性费用
        inparam.put("RSRV_STR2", attrName); // 一次性费用名称
        inparam.put("REMARK", "BBOSS一次性费用");
        TradeFeeDeviceQry.insertFeeInfo(inparam);

    }

    /**
     * 处理合同，审批，联系人信息，插入TF_B_TRADE_OTHER
     * 
     * @author weixb3
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public static void serverAttInfos(IData inparam) throws Exception
    {
        // 1- 获取BBOSS侧的商品信息(TF_B_TRADE_GRP_MERCH)
        String merchTradeId = inparam.getString("TRADE_ID");
        IDataset grpMerchInfoList = TradeGrpMerchInfoQry.qryAllMerchInfoByTradeId(merchTradeId, null);
        if (IDataUtil.isEmpty(grpMerchInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }

        // 2- 获取商品用户编号
        IData grpMerchInfo = grpMerchInfoList.getData(0);
        String merchUserId = grpMerchInfo.getString("USER_ID");

        // 3- 添加合同附件
        IDataset attInfoList = inparam.getDataset("ATT_INFOS");
        if (IDataUtil.isNotEmpty(attInfoList))
        {
            // 删除原有的合同附件
            IData inparams = new DataMap();
            inparams.put("TRADE_ID", merchTradeId);
            inparams.put("RSRV_VALUE_CODE", "ATT_INFOS");
            TradeOtherInfoQry.delRsrvValueCode(inparams);
            for (int i = 0; i < attInfoList.size(); i++)
            {
                // 新增合同附件
                IData attInfo = attInfoList.getData(i);
                attInfo.put("TRADE_ID", merchTradeId);
                attInfo.put("USER_ID", merchUserId);
                attInfo.put("RSRV_VALUE_CODE", "ATT_INFOS");
                attInfo.put("RSRV_STR1", inparam.getString("PRODUCT_ID"));// 商品编号
                attInfo.put("RSRV_STR2", attInfo.getString("ATT_TYPE_CODE"));
                IData cliAttInfo = getAttInfo(merchTradeId,inparam.getString("PRODUCT_ID"),attInfo.getString("ATT_TYPE_CODE"));
                attInfo.put("RSRV_STR3", cliAttInfo.getString("CONTRACT_BBOSS_CODE"));
                attInfo.put("RSRV_STR4", cliAttInfo.getString("CONTRACT_NAME"));                
                String attName = GrpCommonBean.checkFileState(attInfo.getString("ATT_NAME"));
                attInfo.put("RSRV_STR5", attName);
                attInfo.put("RSRV_STR6", cliAttInfo.getString("CONTRACT_START_DATE"));
                attInfo.put("RSRV_STR7", cliAttInfo.getString("CONTRACT_END_DATE"));
                attInfo.put("RSRV_STR8", cliAttInfo.getString("CONTRACT_IS_AUTO_RENEW"));
                attInfo.put("RSRV_STR9", cliAttInfo.getString("RENEW_END_DATE"));
                attInfo.put("RSRV_STR10", cliAttInfo.getString("CONT_FEE"));
                attInfo.put("RSRV_STR11", cliAttInfo.getString("PERFER_PALN"));
                attInfo.put("RSRV_STR12", cliAttInfo.getString("CONTRACT_AUTO_RENEW_CYCLE"));
                attInfo.put("RSRV_STR13", cliAttInfo.getString("CONTRACT_IS_RENEW"));
                attInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                attInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd"));
                attInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                attInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchTradeId));
                attInfo.put("UPDATE_STAFF_ID", grpMerchInfo.getString("UPDATE_STAFF_ID"));
                attInfo.put("UPDATE_DEPART_ID", grpMerchInfo.getString("UPDATE_DEPART_ID"));
                attInfo.put("INST_ID", SeqMgr.getInstId());
                attInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd"));
                attInfo.put("RSRV_TAG10", "1");// 标记受理时填写的
                TradeOtherInfoQry.insTradeOther(attInfo);
            }
        }

        // 4- 添加审批人信息
        IDataset auditorInfoList = inparam.getDataset("AUDITOR_INFOS");
        if (IDataUtil.isNotEmpty(auditorInfoList))
        {
            // 删除原有的审批人信息
            IData inparams = new DataMap();
            inparams.put("TRADE_ID", merchTradeId);
            inparams.put("RSRV_VALUE_CODE", "AUDITOR_INFOS");
            TradeOtherInfoQry.delRsrvValueCode(inparams);
            for (int i = 0; i < auditorInfoList.size(); i++)
            {
                IData auditorInfo = attInfoList.getData(i);
                auditorInfo.put("TRADE_ID", merchTradeId);
                auditorInfo.put("USER_ID", merchUserId);
                auditorInfo.put("RSRV_VALUE_CODE", "AUDITOR_INFOS");
                auditorInfo.put("RSRV_STR1", inparam.getString("PRODUCT_ID"));
                auditorInfo.put("RSRV_STR2", auditorInfo.getString("AUDITOR"));
                auditorInfo.put("RSRV_STR3", auditorInfo.getString("AUDITOR_TIME"));
                auditorInfo.put("RSRV_STR4", auditorInfo.getString("AUDITOR_DESC"));
                auditorInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                auditorInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd"));
                auditorInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                auditorInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchTradeId));
                auditorInfo.put("UPDATE_STAFF_ID", grpMerchInfo.getString("UPDATE_STAFF_ID"));
                auditorInfo.put("UPDATE_DEPART_ID", grpMerchInfo.getString("UPDATE_DEPART_ID"));
                auditorInfo.put("INST_ID", SeqMgr.getInstId());
                auditorInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd"));
                auditorInfo.put("RSRV_TAG10", "1");// 标记受理时填写的
                TradeOtherInfoQry.insTradeOther(auditorInfo);
            }
        }

        // 5- 添加联系人信息
        IDataset contactorInfoList = inparam.getDataset("CONTACTOR_INFOS");
        if (IDataUtil.isNotEmpty(contactorInfoList))
        {
            // 删除原有的联系人信息
            IData inparams = new DataMap();
            inparams.put("TRADE_ID", merchTradeId);
            inparams.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
            TradeOtherInfoQry.delRsrvValueCode(inparams);
            for (int i = 0; i < contactorInfoList.size(); i++)
            {
                IData contactorInfos = contactorInfoList.getData(i);
                contactorInfos.put("TRADE_ID", merchTradeId);
                contactorInfos.put("USER_ID", merchUserId);
                contactorInfos.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
                contactorInfos.put("RSRV_STR1", inparam.getString("PRODUCT_ID"));
                contactorInfos.put("RSRV_STR2", contactorInfos.getString("CONTACTOR_TYPE_CODE"));
                contactorInfos.put("RSRV_STR3", contactorInfos.getString("CONTACTOR_NAME"));
                contactorInfos.put("RSRV_STR4", contactorInfos.getString("CONTACTOR_PHONE"));
                contactorInfos.put("RSRV_STR5", contactorInfos.getString("STAFF_NUMBER"));
                contactorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                contactorInfos.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd"));
                contactorInfos.put("END_DATE", SysDateMgr.getTheLastTime());
                contactorInfos.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchTradeId));
                contactorInfos.put("UPDATE_STAFF_ID", grpMerchInfo.getString("UPDATE_STAFF_ID"));
                contactorInfos.put("UPDATE_DEPART_ID", grpMerchInfo.getString("UPDATE_DEPART_ID"));
                contactorInfos.put("INST_ID", SeqMgr.getInstId());
                contactorInfos.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd"));
                contactorInfos.put("RSRV_TAG10", "1");// 标记受理时填写的
                TradeOtherInfoQry.insTradeOther(contactorInfos);
            }
        }
    }

    /**
     * 对产品参数的数据库更改 如果修改了产品产数，update数据库属性 2014-6-19
     * 
     * @author chenyi
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public static void updateBbossAttrSl(IData param, IDataset productParamInfoList) throws Exception
    {

        for (int i = 0; i < productParamInfoList.size(); i++)
        {
            IData paramInfo = productParamInfoList.getData(i);

            // 已存在的属性，或者新老属性值为空的属性不做任何操作
            if ("EXIST".equals(paramInfo.getString("MODIFY_TAG")) || (StringUtils.isEmpty(paramInfo.getString("ATTR_VALUE")) && StringUtils.isEmpty(paramInfo.getString("PARAM_OLD_VALUE"))))
            {
                continue;

            }

            if ("ADD".equals(paramInfo.getString("MODIFY_TAG")))
            {

                dealAddParamAttr(param, paramInfo);
            }

        }
    }

    /**
     * 对产品资费的数据更改 如果修改了产品资费，update数据库属性 2014-6-19
     * 
     * @author chenyi
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public static void updateBbssDistinctSl(String tradeId, String productUserId, String merchSpecCode, String productOrderId, String productOfferId, String productSpecCode, IDataset distinctInfoList) throws Exception
    {
        if (IDataUtil.isEmpty(distinctInfoList))
        {
            return;
        }

        for (int i = 0; i < distinctInfoList.size(); i++)
        {
            IData distinctInfo = distinctInfoList.getData(i);

            // 典型场景：用户在发送正式受理报文前多次操作资费变更，第一次订购资费，第二次删除资费，第三次再订购资费
            // 解决方案：订购时将资费置成发服开标记，删除资费时又将标记置为不发服开，再次订购时再次将标记改回
            if (StringUtils.equals("D", distinctInfo.getString("ELEMENT_TYPE_CODE")) && StringUtils.equals("0", distinctInfo.getString("MODIFY_TAG")))
            {
                addDiscntInfo(distinctInfo, tradeId, productUserId, merchSpecCode, productOrderId, productOfferId, productSpecCode);
            }
            // 台帐表有数据 在转正式受理的时候需删除该资费
            else if (StringUtils.equals("D", distinctInfo.getString("ELEMENT_TYPE_CODE")) && StringUtils.equals("1", distinctInfo.getString("MODIFY_TAG")))
            {
                delDiscntInfo(distinctInfo, tradeId, productUserId, merchSpecCode, productOrderId, productOfferId, productSpecCode);
            }
        }

        // 将TF_B_TRADE_GRP_MERCHP_DISCNT,TF_B_TRADE_DISCNT加入TF_B_TRADE的INTF_ID中
        GrpCommonBean.dealTradeIntfId(tradeId);
    }

    /**
     * @description 更新预受理台账信息
     * @author xunyl
     * @date 2013-10-17
     */
    public static void updatePreTradeInfo(IData map) throws Exception
    {
        // 1- 获取产品台账编号
        String productTradeId = map.getString("TRADE_ID");

        // 2- 根据产品台账编号获取商品台账编号
        IData merchTradeInfo = GrpCommonBean.getMerchTradeInfo(productTradeId);
        String merchTradeId = merchTradeInfo.getString("TRADE_ID");

        // 3- 更新产品参数信息
        String productId = map.getString("PRODUCT_ID");
        String userId = map.getString("BBOSS_USER_ID");
        IData manageInfo = new DataMap(map.getString("MANAGE_INFO_HIDDEN"));
        IDataset productParamInfoList = manageInfo.getData("PRODUCT_PARAM").getDataset(productId);
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", productTradeId);
        inparam.put("PRODUCT_NUMBER", productId);
        inparam.put("BBOSS_USER_ID", userId);
        IDataset productTradeInfoList = TradeProductInfoQry.getTradeProduct(productTradeId, null);
        if (IDataUtil.isEmpty(productTradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        String relaInstId = productTradeInfoList.getData(0).getString("INST_ID");
        inparam.put("RELA_INST_ID", relaInstId);

        // 如果没修改产品参数则不执行任何操作
        if (IDataUtil.isNotEmpty(productParamInfoList))
        {
            updateBbossAttrSl(inparam, productParamInfoList);
        }

        // 4- 更新资费信息
        IData merchPDiscntsData = manageInfo.getData("PRODUCTS_ELEMENT");
        IDataset merchPDiscnts = merchPDiscntsData.getDataset(productId);
        String merchSpecCode = map.getString("MERCH_SPEC_CODE");
        String productOrderId = map.getString("PRODUCT_ORDER_ID");
        String productOfferId = map.getString("PRODUCT_OFFER_ID");
        String productSpecCode = map.getString("PRODUCT_SPEC_CODE");
        updateBbssDistinctSl(productTradeId, userId, merchSpecCode, productOrderId, productOfferId, productSpecCode, merchPDiscnts);

        // 5- 新增合同附件、审批人信息、联系人信息
        insertAttInfos(merchTradeId, map);
    }
    
    /**
     * @descripiton 根据用户编号和产品编号获取客户合同信息
     * @author xunyl
     * @date 2016-03-10
     */
    private static IData getAttInfo(String merchTradeId,String productId,String attTypeCode)throws Exception{
        //1- 根据商品用户编号获取客户编号
        IDataset tradeMerchInfoList = TradeInfoQry.getTradeInfobyTradeId(merchTradeId);
        if(IDataUtil.isEmpty(tradeMerchInfoList)){
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        String custId = tradeMerchInfoList.getData(0).getString("CUST_ID");
        
        //2- 根据客户编码，产品编码和合同类型查询客管侧的合同信息
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("ATT_TYPE_CODE", attTypeCode);
        IDataset attInfoList = CSAppCall.callCCHT("ITF_CRM_ContractList", inparam, false);
        return attInfoList.getData(0);
    }
}
