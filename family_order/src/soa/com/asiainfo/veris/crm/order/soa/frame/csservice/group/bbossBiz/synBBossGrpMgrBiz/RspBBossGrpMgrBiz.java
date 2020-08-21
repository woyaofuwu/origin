
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.synBBossGrpMgrBiz;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeMag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class RspBBossGrpMgrBiz
{

    /**
     * @description 新增台帐表资费(包括资费新增和资费删除)
     * @author xunyl
     * @date 2014-08-27
     */
    private static void addTradeDiscntInfo(IData rspInfo, IData discntInfo, String modifyTag, boolean isSendServOp, boolean isDel) throws Exception
    {
        // 1- 新增资费台帐信息
        IData tradeDiscntInfo = new DataMap();
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        tradeDiscntInfo.put("TRADE_ID", merchpTradeId);
        tradeDiscntInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchpTradeId));
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        tradeDiscntInfo.put("USER_ID", merchpUserId);
        tradeDiscntInfo.put("USER_ID_A", "-1");
        tradeDiscntInfo.put("PACKAGE_ID", discntInfo.getString("PACKAGE_ID"));
        String merchpId = discntInfo.getString("PRODUCT_ID");
        tradeDiscntInfo.put("PRODUCT_ID", merchpId);
        String discntCode = discntInfo.getString("ELEMENT_ID", discntInfo.getString("DISCNT_CODE"));
        tradeDiscntInfo.put("DISCNT_CODE", discntCode);
        tradeDiscntInfo.put("SPEC_TAG", "0");// 0代表正常产品优惠
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(merchpId);
        tradeDiscntInfo.put("RELATION_TYPE_CODE", relationTypeCode);
        tradeDiscntInfo.put("INST_ID", SeqMgr.getInstId());
        tradeDiscntInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
        {
            tradeDiscntInfo.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        }
        else
        {           
            tradeDiscntInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
        tradeDiscntInfo.put("MODIFY_TAG", modifyTag);
        tradeDiscntInfo.put("IS_NEED_PF", isSendServOp ? "1" : "0");
        tradeDiscntInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        tradeDiscntInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeDiscntInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tradeDiscntInfo.put("RSRV_STR3", isDel ? "M" : null);// 删除标记，标记为M时会在管理节点回单接口中将该记录删除
        TradeDiscntInfoQry.insertDiscntInfo(tradeDiscntInfo);

        // 2- 新增资费参数台帐信息
        IDataset icbParamInfoList = discntInfo.getDataset("ATTR_PARAM");
        if (IDataUtil.isEmpty(icbParamInfoList))
        {
            icbParamInfoList = new DatasetList();
        }
        for (int i = 0; i < icbParamInfoList.size(); i++)
        {
            IData icbParamInfo = icbParamInfoList.getData(i);
            IData tradeIcbParamInfo = new DataMap();
            tradeIcbParamInfo.put("TRADE_ID", merchpTradeId);
            tradeIcbParamInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchpTradeId));
            tradeIcbParamInfo.put("USER_ID", merchpUserId);
            tradeIcbParamInfo.put("INST_TYPE", "D");
            tradeIcbParamInfo.put("INST_ID", SeqMgr.getInstId());
            String attrCode = icbParamInfo.getString("ATTR_CODE", "");
            tradeIcbParamInfo.put("ATTR_CODE", attrCode);
            tradeIcbParamInfo.put("ATTR_VALUE", icbParamInfo.getString("ATTR_VALUE"));
            tradeIcbParamInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                tradeIcbParamInfo.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
            }
            else
            {
                tradeIcbParamInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            }
            tradeIcbParamInfo.put("MODIFY_TAG", modifyTag);
            tradeIcbParamInfo.put("IS_NEED_PF", isSendServOp ? "1" : "0");
            tradeIcbParamInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            tradeIcbParamInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            tradeIcbParamInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            tradeIcbParamInfo.put("RELA_INST_ID", tradeDiscntInfo.getString("INST_ID"));
            String attrName = "";
            IDataset poRatePlanIcbInfoList = PoRatePlanIcbQry.getIcbsByParameterNumber(attrCode);
            if (IDataUtil.isNotEmpty(poRatePlanIcbInfoList))
            {
                attrName = poRatePlanIcbInfoList.getData(0).getString("PARAMETERNAME", "");
            }
            tradeIcbParamInfo.put("RSRV_STR1", isDel ? "M" : null);// 删除标记，标记为M时会在管理节点回单接口中将该记录删除
            tradeIcbParamInfo.put("RSRV_STR3", attrName);// 参数名称
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                tradeIcbParamInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧状态
            }
            else
            {
                tradeIcbParamInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧状态
            }
            TradeAttrInfoQry.insertAttrInfo(tradeIcbParamInfo);
        }

        // 3- 新增BBOSS侧资费台帐信息
        String productDiscntCode = GrpCommonBean.productToMerch(discntCode, 1);
        if (StringUtils.isBlank(productDiscntCode))
        {
            return;
        }
        IDataset tradeGrpMerchpInfoList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(merchpTradeId);
        if (IDataUtil.isEmpty(tradeGrpMerchpInfoList))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "根据台帐编号[" + merchpTradeId + "]找不到对应的MERCHP_DISCNT表信息");
        }
        IData tradeGrpMerchpInfo = tradeGrpMerchpInfoList.getData(0);
        IData tradeGrpMerchpDiscntInfo = new DataMap();
        tradeGrpMerchpDiscntInfo.put("TRADE_ID", merchpTradeId);
        tradeGrpMerchpDiscntInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchpTradeId));
        tradeGrpMerchpDiscntInfo.put("USER_ID", merchpUserId);
        tradeGrpMerchpDiscntInfo.put("MERCH_SPEC_CODE", tradeGrpMerchpInfo.getString("MERCH_SPEC_CODE"));
        tradeGrpMerchpDiscntInfo.put("PRODUCT_ORDER_ID", tradeGrpMerchpInfo.getString("PRODUCT_ORDER_ID"));
        tradeGrpMerchpDiscntInfo.put("PRODUCT_OFFER_ID", tradeGrpMerchpInfo.getString("PRODUCT_OFFER_ID"));
        tradeGrpMerchpDiscntInfo.put("PRODUCT_SPEC_CODE", tradeGrpMerchpInfo.getString("PRODUCT_SPEC_CODE"));
        tradeGrpMerchpDiscntInfo.put("PRODUCT_DISCNT_CODE", productDiscntCode);
        tradeGrpMerchpDiscntInfo.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
        {
            tradeGrpMerchpDiscntInfo.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        }
        else
        {
            tradeGrpMerchpDiscntInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
        tradeGrpMerchpDiscntInfo.put("MODIFY_TAG", modifyTag);
        tradeGrpMerchpDiscntInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        tradeGrpMerchpDiscntInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeGrpMerchpDiscntInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tradeGrpMerchpDiscntInfo.put("INST_ID", SeqMgr.getInstId());
        tradeGrpMerchpDiscntInfo.put("RELA_INST_ID", tradeDiscntInfo.getString("INST_ID"));
        tradeGrpMerchpDiscntInfo.put("IS_NEED_PF", isSendServOp ? "1" : "0");

        if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
        {
            tradeGrpMerchpDiscntInfo.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧参数状态，服开拼报文用
        }
        else
        {
            tradeGrpMerchpDiscntInfo.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态，服开拼报文用
        }
        tradeGrpMerchpDiscntInfo.put("RSRV_STR2", isDel ? "M" : null);// 删除标记，标记为M时会在管理节点回单接口中将该记录删除
        Dao.insert("TF_B_TRADE_GRP_MERCHP_DISCNT", tradeGrpMerchpDiscntInfo, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @description 处理新增的产品参数
     * @author xunyl
     * @date 2014-08-21
     */
    private static void dealAddAttrInfo(IData rspInfo, IData attrInfo) throws Exception
    {
        // 1- 获取产品参数的新、老值
        String oldAttrValue = attrInfo.getString("PARAM_OLD_VALUE", "");
        String newAttrValue = attrInfo.getString("ATTR_VALUE", "");

        // 2- 新值为空，老值为空，属于前台比较失误，不做处理
        if (StringUtils.isEmpty(newAttrValue) && StringUtils.isEmpty(oldAttrValue))
        {
            return;
        }

        // 3- 新增台帐记录
        if (StringUtils.isEmpty(newAttrValue) && StringUtils.isNotEmpty(oldAttrValue))
        {
            inserAttr(rspInfo, attrInfo, "M");// "M"代表该记录不转资料
        }
        else
        {
            inserAttr(rspInfo, attrInfo, "");// ""代表该记录转资料
        }
    }

    /**
     * @description 新增产品资费
     * @author xunyl
     * @date 2014-08-26
     */
    private static void dealAddDiscntInfo(IData rspInfo, IData discntInfo) throws Exception
    {
        // 1- 将当前所有该资费的台帐信息删除
        delAllDiscntInfo(rspInfo, discntInfo);

        // 2- 根据产品用户编号和资费编号查询资料表数据
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        String discntCode = discntInfo.getString("ELEMENT_ID");
        IDataset userDiscntInfoList = UserDiscntInfoQry.getAllDiscntByUser(merchpUserId, discntCode);

        // 3- 资料表数据存在，生成删除的资费台帐信息，不发服务开通，回单时台帐不删除
        if (IDataUtil.isNotEmpty(userDiscntInfoList))
        {
            delUserDiscntInfo(rspInfo, userDiscntInfoList, false, false);
        }

        // 4- 生成新增的资费台帐信息，发送服务开通，回单时台帐不删除
        addTradeDiscntInfo(rspInfo, discntInfo, TRADE_MODIFY_TAG.Add.getValue(), true, false);
    }

    /**
     * @description 处理删除的产品参数
     * @author xunyl
     * @date 2014-08-21
     */
    private static void dealDelAttrInfo(IData rspInfo, IData attrInfo, String modifyTag) throws Exception
    {
        // 1- 查询当前删除参数的台帐信息
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        String attrCode = attrInfo.getString("ATTR_CODE");
        String attrGroup = attrInfo.getString("ATTR_GROUP");
        String attrValue = attrInfo.getString("ATTR_VALUE");
        IDataset tradeAttrInfoList = TradeAttrInfoQry.getTradeAttrByAttrCodeForGrp(attrCode, "0", attrGroup, merchpTradeId);

        // 2- 台帐信息存在并且值相等，则将台帐信息设置为删除状态，不转资料
        if (IDataUtil.isNotEmpty(tradeAttrInfoList))
        {
            IData tradeAttrInfo = tradeAttrInfoList.getData(0);
            String tradeAttrValue = tradeAttrInfo.getString("ATTR_VALUE");
            if (StringUtils.equals(attrValue, tradeAttrValue))
            {
                tradeAttrInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态
                tradeAttrInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                tradeAttrInfo.put("IS_NEED_PF", "1");// 1或者是空： 发指令
                tradeAttrInfo.put("RSRV_STR1", "M");// "M"代表该记录不转资料
                tradeAttrInfo.put("ATTR_NAME", tradeAttrInfo.getString("RSRV_STR3"));
                TradeAttrInfoQry.updateBbossAttrState(tradeAttrInfo);
                return;
            }
        }

        // 3- 台帐信息不存在，则新增台帐信息，转资料
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        IDataset userAttrInfoDataset = UserAttrInfoQry.getUserAttrInfoByUserId(merchpUserId, attrCode, attrGroup);
        if (IDataUtil.isNotEmpty(userAttrInfoDataset))
        {
            IData userAttrInfo = userAttrInfoDataset.getData(0);
            userAttrInfo.put("TRADE_ID", merchpTradeId);
            userAttrInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchpTradeId));
            userAttrInfo.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态
            userAttrInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
            userAttrInfo.put("IS_NEED_PF", "1");// 1或者是空： 发指令
            if (TRADE_MODIFY_TAG.EXIST.getValue().equals(modifyTag))
            {
                userAttrInfo.put("RSRV_STR1", "M");//"M"代表不转资料
            }
            else 
            {
                userAttrInfo.put("RSRV_STR1", "");// ""代表该记录转资料
            }
            
            userAttrInfo.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            userAttrInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            TradeAttrInfoQry.insertAttrInfo(userAttrInfo);
        }
    }

    /**
     * @description 删除产品资费
     * @author xunyl
     * @date 2014-08-26
     */
    private static void dealDelDiscntInfo(IData rspInfo, IData discntInfo) throws Exception
    {
        // 1- 将当前所有该资费的台帐信息删除
        delAllDiscntInfo(rspInfo, discntInfo);

        // 2- 根据用户编号和资费编号查询资料表数据
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        String discntCode = discntInfo.getString("ELEMENT_ID");
        IDataset userDiscntInfoList = UserDiscntInfoQry.getAllDiscntByUser(merchpUserId, discntCode);

        // 3- 资料表数据存在，将资料表的资费信息删除，不发送服务开通，回单时台帐不删除
        if (IDataUtil.isNotEmpty(userDiscntInfoList))
        {
            delUserDiscntInfo(rspInfo, userDiscntInfoList, false, false);
        }

        // 4- 生成删除的资费台帐信息，发送服务开通，回单时台帐删除
        addTradeDiscntInfo(rspInfo, discntInfo, TRADE_MODIFY_TAG.DEL.getValue(), true, true);
    }

    /**
     * @description 修改产品资费
     * @author xunyl
     * @date 2014-08-26
     */
    private static void dealModiDiscntInfo(IData rspInfo, IData discntInfo) throws Exception
    {
        // 1- 将当前所有该资费的台帐信息删除
        delAllDiscntInfo(rspInfo, discntInfo);

        // 2- 根据台帐编号和资费编号查询台帐表数据
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        String discntCode = discntInfo.getString("ELEMENT_ID");
        IDataset tradeDiscntInfoList = TradeDiscntInfoQry.getTradeDiscntInfoListForAdd(merchpTradeId, discntCode,"0");

        // 3- 根据用户编号和资费编号查询资料表数据
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        IDataset userDiscntInfoList = UserDiscntInfoQry.getAllDiscntByUser(merchpUserId, discntCode);

        // 4- 资料表有数据而台帐表没有数据
        if (IDataUtil.isNotEmpty(userDiscntInfoList) && IDataUtil.isEmpty(tradeDiscntInfoList))
        {
            // 4-1 根据资料表数据生成删除的台帐,发送服务开通，回单后不删除
            IData userDiscntInfo = userDiscntInfoList.getData(0);
            IDataset userDiscntAttrInfoList = UserAttrInfoQry.getUserAttrByUserIdInstidForGrp(merchpUserId, "D", userDiscntInfo.getString("INST_ID"));
            userDiscntInfo.put("ATTR_PARAM", userDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, userDiscntInfo, TRADE_MODIFY_TAG.DEL.getValue(), true, false);

            // 4-2 根据资料表数据生成新增的台帐(台帐中需要放入变更后的最新值),发送服务开通，回单后不删除
            IDataset discntAttrInfoList = discntInfo.getDataset("ATTR_PARAM");
            for (int i = 0; i < userDiscntAttrInfoList.size(); i++)
            {
                IData userDiscntAttrInfo = userDiscntAttrInfoList.getData(i);
                String userDiscntAttrCode = userDiscntAttrInfo.getString("ATTR_CODE", "");
                for (int j = 0; j < discntAttrInfoList.size(); j++)
                {
                    IData discntAttrInfo = discntAttrInfoList.getData(j);
                    String discntAttrCode = discntAttrInfo.getString("ATTR_CODE", "");
                    if (userDiscntAttrCode.equals(discntAttrCode))
                    {
                        userDiscntAttrInfo.put("ATTR_VALUE", discntAttrInfo.getString("ATTR_VALUE"));
                    }
                }
            }
            userDiscntInfo.put("ATTR_PARAM", userDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, userDiscntInfo, TRADE_MODIFY_TAG.Add.getValue(), true, false);
        }

        // 5- 资料表没有数据而台帐表有数据
        if (IDataUtil.isEmpty(userDiscntInfoList) && IDataUtil.isNotEmpty(tradeDiscntInfoList))
        {
            // 5-1 将台帐表中新增的数据修改成删除的数据,发送服务开通，回单后不删除
            IData tradeDiscntInfo = tradeDiscntInfoList.getData(0);
            IDataset tradeDiscntAttrInfoList = TradeAttrInfoQry.getUserAttrByUserIdInstid(merchpTradeId, "D", tradeDiscntInfo.getString("INST_ID"));
            tradeDiscntInfo.put("ATTR_PARAM", tradeDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, tradeDiscntInfo, TRADE_MODIFY_TAG.DEL.getValue(), true, false);

            // 5-2 根据台帐表中新增的数据生成新增的台帐(台帐中需要放入变更后的最新值)，发送服务开通，回单后不删除
            IDataset discntAttrInfoList = discntInfo.getDataset("ATTR_PARAM");
            for (int i = 0; i < tradeDiscntAttrInfoList.size(); i++)
            {
                IData tradeDiscntAttrInfo = tradeDiscntAttrInfoList.getData(i);
                String tradeDiscntAttrCode = tradeDiscntAttrInfo.getString("ATTR_CODE", "");
                for (int j = 0; j < discntAttrInfoList.size(); j++)
                {
                    IData discntAttrInfo = discntAttrInfoList.getData(j);
                    String discntAttrCode = discntAttrInfo.getString("ATTR_CODE", "");
                    if (tradeDiscntAttrCode.equals(discntAttrCode))
                    {
                        tradeDiscntAttrInfo.put("ATTR_VALUE", discntAttrInfo.getString("ATTR_VALUE"));
                    }
                }
            }
            tradeDiscntInfo.put("ATTR_PARAM", tradeDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, tradeDiscntInfo, TRADE_MODIFY_TAG.Add.getValue(), true, false);
        }

        // 6- 资料表有数据而且台帐表也有数据
        if (IDataUtil.isNotEmpty(userDiscntInfoList) && IDataUtil.isNotEmpty(tradeDiscntInfoList))
        {
            // 6-2 将台帐表中新增的数据修改成删除的数据,发送服务开通，回单后删除
            IData tradeDiscntInfo = tradeDiscntInfoList.getData(0);
            IDataset tradeDiscntAttrInfoList = TradeAttrInfoQry.getUserAttrByUserIdInstid(merchpTradeId, "D", tradeDiscntInfo.getString("INST_ID"));
            tradeDiscntInfo.put("ATTR_PARAM", tradeDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, tradeDiscntInfo, TRADE_MODIFY_TAG.DEL.getValue(), true, true);

            // 6-2 根据台帐表中新增的数据生成新增的台帐(台帐中需要放入变更后的最新值)，发送服务开通，回单后不删除
            IDataset discntAttrInfoList = discntInfo.getDataset("ATTR_PARAM");
            for (int i = 0; i < tradeDiscntAttrInfoList.size(); i++)
            {
                IData tradeDiscntAttrInfo = tradeDiscntAttrInfoList.getData(i);
                String tradeDiscntAttrCode = tradeDiscntAttrInfo.getString("ATTR_CODE", "");
                for (int j = 0; j < discntAttrInfoList.size(); j++)
                {
                    IData discntAttrInfo = discntAttrInfoList.getData(j);
                    String discntAttrCode = discntAttrInfo.getString("ATTR_CODE", "");
                    if (tradeDiscntAttrCode.equals(discntAttrCode))
                    {
                        tradeDiscntAttrInfo.put("ATTR_VALUE", discntAttrInfo.getString("ATTR_VALUE"));
                    }
                }
            }
            tradeDiscntInfo.put("ATTR_PARAM", tradeDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, tradeDiscntInfo, TRADE_MODIFY_TAG.Add.getValue(), true, false);

            // 6-3 根据资料表数据生成删除的台帐,不发送服务开通，回单后不删除
            IData userDiscntInfo = userDiscntInfoList.getData(0);
            IDataset userDiscntAttrInfoList = UserAttrInfoQry.getUserAttrByUserIdInstidForGrp(merchpUserId, "D", userDiscntInfo.getString("INST_ID"));
            userDiscntInfo.put("ATTR_PARAM", userDiscntAttrInfoList);
            addTradeDiscntInfo(rspInfo, userDiscntInfo, TRADE_MODIFY_TAG.DEL.getValue(), false, false);
        }
    }

    /**
     * @description 删除该资费对应的所有台帐信息(包括TRADE_DISCNT、MERCHP_DISCNT、TRADE_ATTR)
     * @author xunyl
     * @date 2014-08-27
     */
    private static void delAllDiscntInfo(IData rspInfo, IData discntInfo) throws Exception
    {
        // 1- 删除TF_B_TRADE_DISCNT表信息(这里的删除是指回单后删除)
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        String discntCode = discntInfo.getString("ELEMENT_ID");
        IDataset tradeDiscntInfoList = TradeDiscntInfoQry.getTradeDiscntInfosByDiscntCode(merchpTradeId, discntCode);
        if (IDataUtil.isNotEmpty(tradeDiscntInfoList))
        {
            TradeDiscntInfoQry.uptradeDistinctState1(merchpTradeId, discntCode, "M", "0");// M表示回单后删除,0表示不发送服开
        }

        // 2- 删除TF_B_TRADE_GRP_MERCHP_DISCNT表信息(这里的删除是指回单后删除)
        String merchpDiscntCode = GrpCommonBean.productToMerch(discntCode, 1);
        if (StringUtils.isNotBlank(merchpDiscntCode))
        {
            IData inparams = new DataMap();
            inparams.put("TRADE_ID", merchpTradeId);
            inparams.put("PRODUCT_DISCNT_CODE", merchpDiscntCode);
            IDataset tradeMerchpDiscntInfoList = TradeGrpMerchpDiscntInfoQry.getMerchpDisInfoByDiscntCode(inparams);
            if (IDataUtil.isNotEmpty(tradeMerchpDiscntInfoList))
            {
                TradeGrpMerchpDiscntInfoQry.updateDelFlag(merchpTradeId, merchpDiscntCode, "M", "0");// M表示回单后删除,0表示不发送服开
            }
        }

        // 3- 删除TF_B_TRADE_ATTR表信息(这里的删除是指回单后删除)
        if (IDataUtil.isEmpty(tradeDiscntInfoList))
        {
            return;
        }
        for (int i = 0; i < tradeDiscntInfoList.size(); i++)
        {
            IData tradeDiscntInfo = tradeDiscntInfoList.getData(i);
            String instId = tradeDiscntInfo.getString("INST_ID");
            TradeAttrInfoQry.updateDelFlag(merchpTradeId, instId, "D", "M", "0");// D表示资费参数，M表示回单后删除,0表示不发送服开
        }
    }

    /**
     * @description 删除资料表资费
     * @author xunyl
     * @date 2014-08-27
     */
    private static void delUserDiscntInfo(IData rspInfo, IDataset userDiscntInfoList, boolean isSendServOp, boolean isDel) throws Exception
    {
        // 1- 删除资费信息
        IData userDiscntInfo = userDiscntInfoList.getData(0);
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        userDiscntInfo.put("TRADE_ID", merchpTradeId);
        userDiscntInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        userDiscntInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        userDiscntInfo.put("IS_NEED_PF", isSendServOp ? "1" : "0");
        userDiscntInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        userDiscntInfo.put("RSRV_STR3", isDel ? "M" : null);// 删除标记，标记为M时会在管理节点回单接口中将该记录删除
        TradeDiscntInfoQry.insertDiscntInfo(userDiscntInfo);

        // 2- 删除资费下的ICB参数
        String discntInstId = userDiscntInfo.getString("INST_ID");
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        IDataset icbParamInfoList = UserAttrInfoQry.getUserAttrByUserIdInstid(merchpUserId, "D", discntInstId);
        if (IDataUtil.isEmpty(icbParamInfoList))
        {
            return;
        }
        for (int i = 0; i < icbParamInfoList.size(); i++)
        {
            IData icbParamInfo = icbParamInfoList.getData(i);
            icbParamInfo.put("TRADE_ID", merchpTradeId);
            icbParamInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            icbParamInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            icbParamInfo.put("IS_NEED_PF", isSendServOp ? "1" : "0");
            icbParamInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            icbParamInfo.put("RSRV_STR1", isDel ? "M" : null);// 删除标记，标记为M时会在管理节点回单接口中将该记录删除
            TradeAttrInfoQry.insertAttrInfo(icbParamInfo);
        }
    }

    /**
     * @description 添加新增的产品参数
     * @author xunyl
     * @date 2014-08-22
     */
    protected static void inserAttr(IData rspInfo, IData attrInfo, String transUserAttrFlag) throws Exception
    {
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        IDataset tradeGrpMerchInfoList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(merchpTradeId);
        if (IDataUtil.isEmpty(tradeGrpMerchInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_853);
        }

        String productUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        String rela_inst_id = tradeGrpMerchInfoList.getData(0).getString("INST_ID");
        String relaInstId = rspInfo.getString("RELA_INST_ID", rela_inst_id);
        String attrCode = attrInfo.getString("ATTR_CODE");
        String attrGroup = attrInfo.getString("ATTR_GROUP");
        String attrName = attrInfo.getString("ATTR_NAME");
        String attrValue = attrInfo.getString("ATTR_VALUE");
        String endDate = SysDateMgr.getTheLastTime();
        String instType = attrInfo.getString("INST_TYPE", "P");
        String is_need_pf = attrInfo.getString("IS_NEED_PF", "1");
        IData newAttrTradeInfo = GrpCommonBean.getAttrTradeInfo(merchpTradeId, productUserId, relaInstId, attrCode, attrGroup, attrName, attrValue, is_need_pf, endDate, instType);

        newAttrTradeInfo.put("RSRV_TAG1", "1");// 表示是管理流程插入
        newAttrTradeInfo.put("RSRV_STR1", transUserAttrFlag);// 回收标记,值为M时不转资料
        newAttrTradeInfo.put("ELEMENT_ID", attrInfo.getString("ELEMENT_ID"));// 资费与服务参数有值，产品参数没值

        // 将数据插入到数据库
        TradeAttrInfoQry.insertAttrInfo(newAttrTradeInfo);
    }

    /**
     * @description 回复管理节点
     * @author xunyl
     * @date 2014-08-20
     */
    public static void RspBBossManage(IData rspInfo) throws Exception
    {
        // 1- 更新管理节点信息
        IData manageInfo = rspInfo.getData("MANAGE_INFO");
        if (IDataUtil.isNotEmpty(manageInfo))
        {
            updateManageNodeInfo(manageInfo);
        }

        // 2- 更新产品参数
        IData productInfo = rspInfo.getData("ATTR_DINS_INFO");
        if (IDataUtil.isNotEmpty(productInfo))
        {
            IDataset attrInfoList = productInfo.getDataset("PRODUCT_PARMS");
            dealAttrGroupParams(attrInfoList);
            updateTradeAttr(rspInfo, attrInfoList, manageInfo.getString("FLOWPOINT").substring(6));
        }

        // 3- 更新资费及资费参数
        if (IDataUtil.isNotEmpty(productInfo))
        {
            IDataset discntInfoList = productInfo.getDataset("MERCHPDISCNTS");
            updateTradeDiscnt(rspInfo, discntInfoList);
        }

        // 5- 管理流程报文发送特殊更新
        updateSpecialInfo(rspInfo);

		// 5.1 -更新商品附件级信息
        updateContractInfo(rspInfo);
		
        // 6- 发送ESOP
        boolean sendpf = BizEnv.getEnvBoolean("isesop", false);
        IData esop = rspInfo.getData("ESOP");
        if (sendpf && IDataUtil.isNotEmpty(esop))
        {
            ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", esop);
        }
    }

    /**
     * @description 更新管理节点的节点信息
     * @author xunyl
     * @date 2014-08-20
     */
    private static void updateManageNodeInfo(IData manageInfo) throws Exception
    {
        // 1- 获取集团下发管理节点信息(获取节点编码信息模型)
        String manageNode = manageInfo.getString("FLOWPOINT").substring(6);
        String merchpTradeId = manageInfo.getString("TRADE_ID");
        String merchpUserId = manageInfo.getString("BBOSS_USER_ID");
        IDataset manageNodeInfoList = TradeOtherInfoQry.getTradeOtherByTradeId(merchpTradeId, "BBOSS_" + manageNode, merchpUserId);
        if (IDataUtil.isEmpty(manageNodeInfoList))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_332);
        }
        IData rspManageNodeInfoModel = manageNodeInfoList.getData(0);

        // 2- 将节点编码信息插入OHTER表
        IDataset rspManageNodeInfoList = manageInfo.getDataset("MANAGE_INFO");
        if (IDataUtil.isEmpty(rspManageNodeInfoList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_370);
        }
        for (int i = 0; i < rspManageNodeInfoList.size(); i++)
        {
            IData rspManageNodeInfo = rspManageNodeInfoList.getData(i);
            rspManageNodeInfoModel.put("RSRV_STR12", rspManageNodeInfo.getString("PARAM_CODE", ""));
            rspManageNodeInfoModel.put("RSRV_STR13", rspManageNodeInfo.getString("PARAM_NAME", ""));
            rspManageNodeInfoModel.put("RSRV_STR14", rspManageNodeInfo.getString("PARAM_VALUE", ""));
            rspManageNodeInfoModel.put("RSRV_VALUE_CODE", "BBOSS_MANAGE_" + manageNode);// 标志是返回的审核信息
            rspManageNodeInfoModel.put("RSRV_VALUE", "管理流程审核信息");// RSRV_VALUE必须有值，否则同步计费账务报错
            rspManageNodeInfoModel.put("INST_ID", SeqMgr.getInstId());
            rspManageNodeInfoModel.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchpTradeId));
            rspManageNodeInfoModel.put("RSRV_NUM10", manageNode);// 节点操作码
            rspManageNodeInfoModel.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
            rspManageNodeInfoModel.put("START_DATE", SysDateMgr.getSysTime());
            rspManageNodeInfoModel.put("END_DATE", SysDateMgr.getTheLastTime());
            rspManageNodeInfoModel.put("IS_NEED_PF", "1");// 是否需要发服开 0为不发，1或"" 是要发服开

            TradeOtherInfoQry.insTradeOther(rspManageNodeInfoModel);
        }

        // 3- 更新下发的管理节点数据(RSRV_TAG1设置为1，表示该管理节点已经回复，页面不再展示)
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", merchpTradeId);
        inparams.put("USER_ID", merchpUserId);
        inparams.put("RSRV_TAG1", "1");
        TradeOtherInfoQry.udpateBbossOtherFlag(inparams);
    }

    /**
     * @description 管理节点回复，更改订单表(TF_B_ORDER)和主台帐表(TF_B_TRADE)状态
     * @author xunyl
     * @date 2014-08-20
     */
    private static void updateStatus(IData rspInfo) throws Exception
    {
        // 1- 更新商品主台账表的状态(SUBSCRIBE_STATE)为0
        IData tradeInfo = rspInfo.getData("TRADE_INFO");
        if (IDataUtil.isEmpty(tradeInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_370);
        }
        String merchpTradeId = tradeInfo.getString("TRADE_ID");
        TradeInfoQry.updateSubstate(merchpTradeId, "0");

        // 2- 更新产品主台帐表的状态(SUBSCRIBE_STATE)为0
        String merchTradeId = tradeInfo.getString("MERCH_TRADE_ID");
        TradeInfoQry.updateSubstate(merchTradeId, "0");

        // 3- 更新订单表的状态(HQ_TAG为r代表重新发送服务开通、APP_TYPE为M代表管理节点报文、ORDER_STATE为0表示让AEE重新发服开)
        String orderId = tradeInfo.getString("ORDER_ID");
        TradeMag.updateStateHq(orderId, "r", "M", "0");
    }
    
    /**
     * @description 管理节点回复时，如果修改属性组中的某些属性时需要将属性组属性进行拆分，原属性组属性删除，新增新属性组属性
     * @author xunyl
     * @date 2014-09-06
     */
    private static void dealAttrGroupParams(IDataset attrInfoList)throws Exception{
        //1- 如果产品参数为空，则直接退出       
        if(IDataUtil.isEmpty(attrInfoList)){
            return;
        }
        
        //2- 循环产品参数，处理属性组属性
        for(int i=0;i<attrInfoList.size();i++){
            IData attrInfo = attrInfoList.getData(i);
            //2-1 非属性组属性直接退出
            String attrGroup = attrInfo.getString("ATTR_GROUP");
            if(StringUtils.isEmpty(attrGroup)){
                continue;
            }
            
            //2-2 属性状态非新增的场合直接退出
            String modifyTag = attrInfo.getString("MODIFY_TAG");
            if(!GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(modifyTag)){
                continue;
            }
            
            //2-3 获取同一属性组内的所有属性编号，用","隔开
            String attrCode = attrInfo.getString("ATTR_CODE");
            String strGroupParam = getAllParamsInGroup(attrCode);
            
            //2-4 判定属性组属性是否为修改(原则上属性组属性只能删除一组或者新增一组，但是管理节点中的某些情况是针对特定的属性做修改操作)
            boolean isModifyPartParam = false;
            isModifyPartParam = isModifyPartParam(attrInfoList,attrGroup,strGroupParam);            
            
            //2-5 如果修改了属性组属性，那么需要对属性组进行拆分，拆成一组删除的老数据，一组新增的新数据
            if(isModifyPartParam){
                modifyAttrInfoList(attrInfoList,strGroupParam);
            }            
        }
    }    
    
    /**
     * @description  判定属性组属性是否为修改
     * @author xunyl
     * @date 2014-09-06
     */
    private static boolean isModifyPartParam(IDataset attrInfoList,String curAttrGroup,String strGroupParam)throws Exception{
        //1- 定义返回结果
        boolean isModifyPartParam = false;
        
        //2- 循环产品参数，判定属性组属性是否为修改
        for(int i=0;i<attrInfoList.size();i++){
            IData attrInfo = attrInfoList.getData(i);
            String modifyTag = attrInfo.getString("MODIFY_TAG");            
            //2-1 新增状态的属性无法说明属性组属性是否为修改
            if(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(modifyTag)){
                continue;
            }
            
            // 2-2 同一属性组内属性的状态既有新增的，又有删除或者不变的可以说明属性组属性为修改
            String attrCode = GrpCommonBean.nullToString(attrInfo.getString("ATTR_CODE"));
            String attrGroup = GrpCommonBean.nullToString(attrInfo.getString("ATTR_GROUP"));
            if (StringUtils.equals(curAttrGroup, attrGroup) && strGroupParam.contains(attrCode))
            {
                isModifyPartParam = true;
            }
        }
        
        //3- 返回结果
        return isModifyPartParam;
    }
    
    /**
     * @description 获取同一属性组内的所有属性编号，用","隔开
     * @author xunyl
     * @2014-09-17
     */
    private static String getAllParamsInGroup(String attrCode)throws Exception{
        //1- 定义返回串
        StringBuilder strGroupParam = new StringBuilder("");
        
        //2- 拼装返回串
        IDataset paramInfoList = BBossAttrQry.qryBBossAttrByAttrCode(attrCode);
        if(IDataUtil.isEmpty(paramInfoList)){
            return "";
        }
        IData paramInfo = paramInfoList.getData(0);
        String groupAttr = paramInfo.getString("GROUPATTR");
        IDataset groupParamInfoList = BBossAttrQry.qryBBossAttrByGroupAttrBizType(groupAttr,"1");
        if(IDataUtil.isEmpty(groupParamInfoList)){
            return "";
        }
        for(int i=0;i<groupParamInfoList.size();i++){
            IData groupParamInfo = groupParamInfoList.getData(i);
            String groupAttrCode = groupParamInfo.getString("ATTR_CODE");
            if(StringUtils.isNotEmpty(strGroupParam)){
                strGroupParam.append(",");
            }
            strGroupParam.append(groupAttrCode);
        }
        
        //3- 返回结果
        return strGroupParam.toString();
    }
    
    /**
     * @description 属性组拆分，将被修改的属性组拆成一组删除的老数据，一组新增的新数据
     * @author xunyl
     * @date 2014-09-17
     */
    private static void modifyAttrInfoList(IDataset attrInfoList,String strGroupParam)throws Exception{
        for(int i=0;i<attrInfoList.size();i++){
            IData attrInfo = attrInfoList.getData(i);
            String attrCode = attrInfo.getString("ATTR_CODE");
            String modifyTag = attrInfo.getString("MODIFY_TAG"); 
            String attrGroup = attrInfo.getString("ATTR_GROUP");
            if(!strGroupParam.contains(attrCode)){
            	continue;
            }
            if(TRADE_MODIFY_TAG.EXIST.getValue().equals(modifyTag)){
                IData addAttrInfo = new DataMap();
                addAttrInfo.put("ATTR_CODE", attrCode);
                addAttrInfo.put("ATTR_GROUP", attrGroup);
                addAttrInfo.put("ATTR_VALUE", attrInfo.getString("ATTR_VALUE"));
                addAttrInfo.put("MODIFY_TAG", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue());
                addAttrInfo.put("ATTR_NAME", attrInfo.getString("ATTR_NAME"));
                attrInfoList.add(attrInfoList.size(),addAttrInfo);                
                attrInfo.put("MODIFY_TAG", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue());
            }
        }
    }
    
    /**
     * @description 更新产品参数
     * @author xunyl
     * @date 2014-08-21
     * @modifyBy chenkh
     * @modifyFor: 针对必填属性，即使没有修改，也同样需要入表
     * @modifyTime 2014-12-26
     */
    private static void updateTradeAttr(IData rspInfo, IDataset attrInfoList, String manageNode) throws Exception
    {
        // 1- 如果产品参数不存在，直接退出更新处理
        if (IDataUtil.isEmpty(attrInfoList))
        {
            return;
        }

        // 2- 循环处理产品参数
        for (int i = 0; i < attrInfoList.size(); i++)
        {
            IData attrInfo = attrInfoList.getData(i);
            String modifyTag = attrInfo.getString("MODIFY_TAG");
            String attrCode = attrInfo.getString("ATTR_CODE");
            boolean isMandatory = GrpCommonBean.isMandatroyAttribute(attrCode, manageNode);
            // 2-1 产品参数没做任何修改，并且不为必填参数
            if (TRADE_MODIFY_TAG.EXIST.getValue().equals(modifyTag) && !isMandatory)
            {
                continue;
            }
            // 2-2 新增产品参数
            if (GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(modifyTag))
            {
                dealAddAttrInfo(rspInfo, attrInfo);
            }
            // 2-3 删除产品参数
            if (GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue().equals(modifyTag))
            {
                dealDelAttrInfo(rspInfo, attrInfo, GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue());
            }
            // 2-4 处理必填参数
            if (TRADE_MODIFY_TAG.EXIST.getValue().equals(modifyTag) && isMandatory)
            {
                dealMandatoryInfo(rspInfo, attrInfo);
            }
        }
    }

    /**
     * @description 更新产品资费
     * @author xunyl
     * @date 2014-08-26
     */
    private static void updateTradeDiscnt(IData rspInfo, IDataset discntInfoList) throws Exception
    {
        // 1- 产品资费不存在，直接退出
        if (IDataUtil.isEmpty(discntInfoList))
        {
            return;
        }

        // 2- 判断当前回复的管理节点是否为预受理的管理节点
        boolean isPreCrt = false;
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");
        IDataset tradeGrpMerchpInfoList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(merchpTradeId);
        if (IDataUtil.isEmpty(tradeGrpMerchpInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData tradeGrpMerchpInfo = tradeGrpMerchpInfoList.getData(0);
        String productOpType = tradeGrpMerchpInfo.getString("RSRV_STR1");
        if ("10".equals(productOpType))
        {
            isPreCrt = true;
        }

        // 3- 循环处理产品参数
        for (int i = 0; i < discntInfoList.size(); i++)
        {
            // 3-1 预受理阶段，过滤集团BBOSS侧资费
            IData discntInfo = discntInfoList.getData(i);
            String discntCode = discntInfo.getString("ELEMENT_ID");
            String merchpDiscntCode = GrpCommonBean.productToMerch(discntCode, 1);
            if (StringUtils.isNotBlank(merchpDiscntCode) && isPreCrt)
            {
                continue;
            }
            // 3-2 资费状态为新增
            String modifyTag = discntInfo.getString("MODIFY_TAG");
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                dealAddDiscntInfo(rspInfo, discntInfo);
            }
            // 3-3 资费状态为删除
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                dealDelDiscntInfo(rspInfo, discntInfo);
            }
            // 3-4 资费状态为变更
            if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag))
            {
                dealModiDiscntInfo(rspInfo, discntInfo);
            }
        }
    }

    /**
     * @param
     * @desciption 管理流程报文发送特殊信息更新（后续特殊的更新统一放到这里面）
     * @author fanti
     * @version 创建时间：2014年9月15日 下午4:30:52
     */
    private static void updateSpecialInfo(IData rspInfo) throws Exception
    {

        // 1- 更新台帐表和订单表信息状态(便于AEE与服开的正常通讯)
        updateStatus(rspInfo);

        // 2- 针对一单多线拆单BBOSS产生多笔商品订单的情况，依据产品订单号更新更新商品订单号
        updateMerchInfo(rspInfo);
    }

    /**
     * @param
     * @desciption 针对一单多线拆单BBOSS产生多笔商品订单的情况，依据产品订单号更新更新商品订单号
     * @author fanti
     * @version 创建时间：2014年9月15日 下午4:35:04
     */
    private static void updateMerchInfo(IData rspInfo) throws Exception
    {

        // 1- 获取商品tradeId和产品tradeId
        IData tradeInfo = rspInfo.getData("TRADE_INFO");
        if (IDataUtil.isEmpty(tradeInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_370);
        }
        String merchpTradeId = tradeInfo.getString("TRADE_ID");

        // 2- 获取产品订单号
        IDataset merchpInfo = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(merchpTradeId);
        if (IDataUtil.isEmpty(merchpInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        String merchpOrderId = merchpInfo.getData(0).getString("PRODUCT_ORDER_ID");

        // 3- 根据商品tradeId 和 产品订单号的前16位 更新商品订单号
        if (merchpOrderId.length() > 16)
        {
            String merchOrderId = merchpOrderId.substring(0, 16);
            String merchTradeId = tradeInfo.getString("MERCH_TRADE_ID");

            TradeGrpMerchInfoQry.updateMerchOrderIdByTradeId(merchTradeId, merchOrderId);
        }
    }
	
	/**
     * 处理页面传递进来的商品合同附件的信息
     * @author liuxx3
     * @date 2014 12 -8
     */
    private static void updateContractInfo(IData rspInfo)throws Exception {
		
        IData manageInfo = rspInfo.getData("MANAGE_INFO");        

    	String bbossUploadId = manageInfo.getString("UPLOAD_ID");
    	
    	if(StringUtils.isEmpty(bbossUploadId))
    	{
    		return;
    	}
    	   	
    	IData tradeInfo = rspInfo.getData("TRADE_INFO");
    	
    	String merchTradeId = tradeInfo.getString("MERCH_TRADE_ID");
    	
		TradeOtherInfoQry.updateBBossUploadForInNeedPf(merchTradeId,"ATT_INFOS","0");
    	
		IData attInfo = new DataMap();
		
		attInfo.put("TRADE_ID", merchTradeId);
		attInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchTradeId));
		
		IDataset tradeInfos = TradeInfoQry.getMainTradeByTradeIdForGrp(merchTradeId);
		String merchUserId = "";
		String merchProId = "";
		if(IDataUtil.isNotEmpty(tradeInfos))
		{
			merchUserId = tradeInfos.getData(0).getString("USER_ID");
			merchProId = tradeInfos.getData(0).getString("PRODUCT_ID");
		}
		attInfo.put("USER_ID", merchUserId);
		
		attInfo.put("RSRV_VALUE_CODE", "ATT_INFOS");
		attInfo.put("RSRV_STR1", merchProId);
		attInfo.put("RSRV_STR2", "1");
		attInfo.put("RSRV_STR3", "uploadAttCode");
		attInfo.put("RSRV_STR4", "uploadAttName");
		
		String attName = GrpCommonBean.checkFileState(bbossUploadId);
		attInfo.put("RSRV_STR5", attName);
		
		attInfo.put("START_DATE", SysDateMgr.getSysDate());
		attInfo.put("END_DATE", SysDateMgr.getTheLastTime());
		attInfo.put("MODIFY_TAG", "0");
		attInfo.put("UPDATE_TIME", SysDateMgr.getSysDate());
			
		attInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		attInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		
		attInfo.put("INST_ID", SeqMgr.getInstId());
		attInfo.put("IS_NEED_PF", "");
		
		TradeOtherInfoQry.inserOtherInfo(attInfo);
	}

    /**
     * @Title: dealMandatoryInfo
     * @Description: 处理必选数据
     * @param rspInfo
     * @param attrInfo
     * @throws Exception
     * @return void
     * @author chenkh
     * @time 2014年12月26日
     */
    private static void dealMandatoryInfo(IData rspInfo, IData attrInfo) throws Exception
    {
        // 1- 获取产品参数的新、老值
        String oldAttrValue = attrInfo.getString("PARAM_OLD_VALUE", "");
        String newAttrValue = attrInfo.getString("ATTR_VALUE", "");

        // 2- 新值为空，老值为空，不做处理,由于是必填属性，这个应该不会出现，但暂时先保留
        if (StringUtils.isEmpty(newAttrValue) && StringUtils.isEmpty(oldAttrValue))
        {
            return;
        }

        // 3- 新增ADD台帐记录
        insertMandatoryAttr(rspInfo, attrInfo, "M", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue(), CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());

        // 4- 新增DEL台账记录
        insertMandatoryAttr(rspInfo, attrInfo, "M", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue(), CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
    }
    

    /**
     * @description 针对必填未修改参数， 上发一条新增，一条删除 数据
     * @author chenkh
     * @date 2015-05-01
     */
    protected static void insertMandatoryAttr(IData rspInfo, IData attrInfo, String transUserAttrFlag, String bbossTag, String modifyTag) throws Exception
    {
        String merchpTradeId = rspInfo.getData("TRADE_INFO").getString("TRADE_ID");

        String attrCode = attrInfo.getString("ATTR_CODE");
        String attrGroup = attrInfo.getString("ATTR_GROUP");
        String attrValue = attrInfo.getString("ATTR_VALUE");
        IDataset tradeAttrInfoList = TradeAttrInfoQry.getTradeAttrByAttrCodeForGrp(attrCode, "0", attrGroup, merchpTradeId);

        // 2- 台帐信息存在并且值相等，则将台帐信息设置为删除状态，不转资料
        if (IDataUtil.isNotEmpty(tradeAttrInfoList))
        {
            IData tradeAttrInfo = tradeAttrInfoList.getData(0);
            String tradeAttrValue = tradeAttrInfo.getString("ATTR_VALUE");
            if (StringUtils.equals(attrValue, tradeAttrValue))
            {
                tradeAttrInfo.put("RSRV_STR5", bbossTag);// BBOSS侧参数状态
                tradeAttrInfo.put("MODIFY_TAG", modifyTag);
                tradeAttrInfo.put("INST_ID", SeqMgr.getInstId());
                tradeAttrInfo.put("IS_NEED_PF", "1");// 1或者是空： 发指令
                tradeAttrInfo.put("RSRV_STR1", "M");// "M"代表该记录不转资料
                tradeAttrInfo.put("RSRV_STR3", attrInfo.getString("ATTR_NAME"));// 属性名
                TradeAttrInfoQry.insertAttrInfo(tradeAttrInfo);
                return;
            }
        }
        // 3- 台帐信息不存在，则新增台帐信息，转资料
        String merchpUserId = rspInfo.getData("ATTR_DINS_INFO").getString("BBOSS_USER_ID");
        IDataset userAttrInfoDataset = UserAttrInfoQry.getUserAttrInfoByUserId(merchpUserId, attrCode, attrGroup);
        if (IDataUtil.isNotEmpty(userAttrInfoDataset))
        {
            IData userAttrInfo = userAttrInfoDataset.getData(0);
            userAttrInfo.put("TRADE_ID", merchpTradeId);
            userAttrInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchpTradeId));
            userAttrInfo.put("RSRV_STR5", bbossTag);// BBOSS侧参数状态
            userAttrInfo.put("MODIFY_TAG", modifyTag);
            userAttrInfo.put("IS_NEED_PF", "1");// 1或者是空： 发指令
            userAttrInfo.put("RSRV_STR1", "M");// "M"代表不转资料
            userAttrInfo.put("RSRV_STR3", attrInfo.getString("ATTR_NAME"));// 属性名
            if (GroupBaseConst.PARMA_STATUS.PARAM_ADD.equals(modifyTag))
            {
                userAttrInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            }
            else
            {
                userAttrInfo.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            }
            userAttrInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            TradeAttrInfoQry.insertAttrInfo(userAttrInfo);
        }
    }
}
