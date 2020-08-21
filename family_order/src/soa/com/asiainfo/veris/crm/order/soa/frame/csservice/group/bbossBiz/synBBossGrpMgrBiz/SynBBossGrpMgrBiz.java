
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.synBBossGrpMgrBiz;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser.ChangeBBossUserSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.SendDataToEsopBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.UpdateAttrInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser.CreateBBossUserSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyUser.DestroyBBossUserSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * 输入数据处理类BBOSS 业务流程管理接口
 * 
 * @author weixb3 modify chenyi
 * @date 2013-8-19
 */
public class SynBBossGrpMgrBiz
{
    /*
     * @description 管理节点信息异常检测
     * @author xunyl
     * @date 2013-10-10
     */
    protected static void checkManageException(IData data) throws Exception
    {
        // 1- 管理节点不存在
        String manageNode = data.getString("PR_MN_OPERATE_CODE");
        if (StringUtils.isBlank(manageNode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_516);
        }

        // 2- 管理属性名称、管理属性值、管理属性编码数目不匹配
        IDataset proAttrCodeList = IDataUtil.getDataList(data, "PR_CHARACTER_ID");// 管理属性编码
        IDataset attrNameList = IDataUtil.getDataList(data, "PR_CHARACTER_NAME");// 管理属性名称
        IDataset attrValueList = IDataUtil.getDataList(data, "PR_CHARACTER_VALUE");// 管理属性值
        IDataset attrActionList = IDataUtil.getDataList(data, "PR_CHARACTER_DESC");// 管理属性附加描述
        if (attrNameList.size() != attrValueList.size() || attrNameList.size() != attrActionList.size() || attrNameList.size() != proAttrCodeList.size())
        {
            CSAppException.apperr(GrpException.CRM_GRP_515);
        }
    }

    /*
     * @description 特殊管理节点处理(典型场景:集团客户一点支付进行集团产品注销时，需要在1113管理节点注销成员代付关系)
     * @author xunyl
     * @date 2013-11-07
     */
    protected static void dealSpecialManageCode(String manageNode, IData data) throws Exception
    {
        // 1- 集团客户一点支付进行集团产品注销时，注销成员代付关系
        if ("1113".equals(manageNode))
        {
            return;
            //delBatOnePayMem(data); 现在手工完成注销
        }
    }

    /*
     * @description 集团客户一点支付的暂停与恢复的场合，暂停或者恢复成员的付费关系
     * @author xunyl
     * @date 2013-11-05
     */
    protected static void delBatOnePayMem(IData map) throws Exception
    {
        // 1- 拼装服务参数
        IData memCond = new DataMap();

        IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", map); // 产品订购关系ID
        String productOfferingId = rsrvstr4Set.get(0).toString();
        String productUserId = GrpCommonBean.getMerchpUserIdByProdId(productOfferingId);
        memCond.put("USER_ID", productUserId);

        IDataset productNumberSet = IDataUtil.getDatasetSpecl("PRSRV_STR10", map); // 多条产品规格编号
        String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(0));
        String productId = GrpCommonBean.merchToProduct(productSpecNumber, 0, null);// 产品编号转化为本地产品编号
        memCond.put("PRODUCT_ID", productId);

        IData productUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(productUserId);
        String custId = productUserInfo.getString("CUST_ID");
        memCond.put("CUST_ID", custId);

        String groupId = UcaInfoQry.qryGrpInfoByCustId(custId).getString("GROUP_ID");
        memCond.put("GROUP_ID", groupId);

        memCond.put("USER_EPARCHY_CODE", map.getString(Route.USER_EPARCHY_CODE));
        memCond.put("GRP_SERIAL_NUMBER", productUserInfo.getString("SERIAL_NUMBER"));

        // 2- 拼装存储过程参数
        IData paramValue = new DataMap();
        paramValue.put("v_User_Id", productUserId);
        paramValue.put("v_Relation_Type_Code", "97");
        paramValue.put("v_Staff_Id", map.getString("TRADE_STAFF_ID"));
        paramValue.put("v_Depart_Id", map.getString("TRADE_DEPART_ID"));
        paramValue.put("v_City_Code", map.getString("TRADE_CITY_CODE"));
        paramValue.put("v_Eparchy_Code", map.getString("TRADE_EPARCHY_CODE"));
        paramValue.put("v_MemConding", memCond.toString());
        paramValue.put("v_TASK_NAME", "P_CMS_DESTROY_GRPMEB_AUTO");

        // 3- 调用存储过程，批量恢复或者暂停成员的代付关系
        String[] paramName =
        { "v_User_Id", "v_Relation_Type_Code", "v_Staff_Id", "v_Depart_Id", "v_City_Code", "v_Eparchy_Code", "v_MemConding", "v_TASK_NAME", "v_Result_Code", "v_Result_Info" };
        Dao.callProc("P_CMS_DESTROY_GRPMEB_AUTO", paramName, paramValue, Route.CONN_CRM_CEN);
    }

    /*
     * @description 更新管理节点报文中的产品参数至台账表
     * @author xunyl
     * @date 2013-10-10
     */
    protected static void getManageParamInfoList(IData data, String tradeId) throws Exception
    {
        IDataset attrCodeset = IDataUtil.getDataList(data, "RSRV_STR15");// 产品属性编码
        IDataset attrNameset = IDataUtil.getDataList(data, "RSRV_STR17");// 产品属性名称
        IDataset attrValueset = IDataUtil.getDataList(data, "RSRV_STR16");// 产品属性值
        IDataset attrCGroupset = IDataUtil.getDataList(data, "CGROUP");// 产品属性组
        IDataset attrActionset = IDataUtil.getDataList(data, "RSRV_STR18");// 产品属性操作 0 删除，1 增加
        IDataset psubscribeIdSet = IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data); // 产品订单号

        for (int i = 0, sizei = psubscribeIdSet.size(); i < sizei; i++)
        {
            IDataset attrCodeDataset = attrCodeset.getDataset(i);
            IDataset attrNameDataset = attrNameset.getDataset(i);
            IDataset attrValueDataset = attrValueset.getDataset(i);
            IDataset attrCGroupDataset = attrCGroupset.getDataset(i);
            IDataset attrActionDataset = attrActionset.getDataset(i);
            IDataset productParamInfo = new DatasetList();
            for (int j = 0, sizej = attrCodeDataset.size(); j < sizej; j++)
            {
                IData param = new DataMap();
                param.put("ATTR_CODE", attrCodeDataset.get(j));
                param.put("ATTR_NAME", attrNameDataset.get(j));
                param.put("ATTR_VALUE", attrValueDataset.get(j));
                param.put("ATTR_GROUP", attrCGroupDataset.get(j));
                param.put("STATE", attrActionDataset.get(j).equals("1") ? "ADD" : "DEL");// 默认为新增
                productParamInfo.add(param);
            }
            // 更新产品参数台账信息
            UpdateAttrInfoBean.dealAttrTradeInfo(productParamInfo, tradeId);

            // IS_NEND_PF是否发指令： 1或者是空： 发指令 0： 不发指令
            TradeAttrInfoQry.updIsSendPfTradeid(tradeId);
        }
    }

    /*
     * @description 拼装反馈报文
     * @author xunyl
     * @date 2013-10-10
     */
    protected static IData getRspInfo(IData data) throws Exception
    {
        // 1- 定义返回结果
        IData result = new DataMap();

        // 2- 封装数据
        result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        result.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
        result.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
        result.put("PRODUCT_ORDERNUMBER", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
        IDataset temp = new DatasetList();
        temp.add("00");
        result.put("PRODUCT_ORDER_RSP_CODE", temp);
        IDataset temp1 = new DatasetList();
        temp1.add(IntfField.SUUCESS_CODE[1]);
        result.put("PRODUCT_ORDER_RSP_DESC", temp1);
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);

        // 3- 返回结果
        return result;
    }

    /*
     * @description 更新产品相关信息
     * @author xunyl
     * @date 2013-10-10
     */
    protected static void updateMerchpRelInfo(IData data) throws Exception
    {
        // 1- 获取产品订单编号
        IDataset psubscribeIdSet = IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data); // 产品订单号
        if (IDataUtil.isEmpty(psubscribeIdSet))
        {
            return;
        }

        // 2- 循环产品订单编号，更新产品相关表信息
        for (int i = 0, sizei = psubscribeIdSet.size(); i < sizei; i++)
        {
            // 2-1 获取产品台账编号
            IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", data); // 产品订购关系ID
            if (IDataUtil.isEmpty(rsrvstr4Set))
            {
                CSAppException.apperr(GrpException.CRM_GRP_349);
            }
            String productOfferringId = rsrvstr4Set.get(i).toString();
            IDataset tradeInfos = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferringId, null);
            if (IDataUtil.isEmpty(tradeInfos))
            {
                continue;
            }
            String tradeId = tradeInfos.getData(0).getString("TRADE_ID", "");

            // 2-2 更新产品参数信息
            getManageParamInfoList(data, tradeId);

            // 2-4 IS_NEND_PF是否发指令： 1或者是空： 发指令 0： 不发指令
            TradeGrpMerchpDiscntInfoQry.updateMechpDiscntIsSendPfByTradeid(tradeId);

            // 2-5 更新之前的管理节点信息(防止再次发服开)
            TradeOtherInfoQry.updateManageInfo(tradeId, "BBOSS_MANAGE", "0");

            // 2-6 特殊管理节点处理(典型场景:集团客户一点支付进行集团产品注销时，需要在1113管理节点注销成员代付关系)
            String manageNode = data.getString("PR_MN_OPERATE_CODE");
            dealSpecialManageCode(manageNode, data);

            // 2-6 新增管理节点信息
            // 2-6-1 获取商品编号
            String poNumber = IDataUtil.chkParam(data, "RSRV_STR1");
            String merchProductId = GrpCommonBean.merchToProduct(poNumber, 0, null);
            // 2-6-2 获取产品编号
            IDataset productNumberSet = IDataUtil.getDatasetSpecl("PRSRV_STR10", data); // 多条产品规格编号
            String merchpProductId = GrpCommonBean.merchToProduct(productNumberSet.get(i).toString(), 2, null);
            // 2-6-3 获取管理节点属性编码和属性值
            IDataset proAttrCodeList = IDataUtil.getDataList(data, "PR_CHARACTER_ID");// 管理属性编码
            IDataset characterIdList = IDataUtil.modiIDataset(proAttrCodeList.get(i), "PR_CHARACTER_ID");
            IDataset proAttrValueList = IDataUtil.getDataList(data, "PR_CHARACTER_VALUE");
            IDataset characterValueList = IDataUtil.modiIDataset(proAttrValueList.get(i), "PR_CHARACTER_VALUE");
            String ibsysid = data.getString("IBSYSID");
            // 2-6-4 管理节点信息入表
            TradeOtherInfoQry.insertManagerTrade(tradeId, merchpProductId, merchProductId, manageNode, productOfferringId, characterIdList, characterValueList, ibsysid);

            // 2-7 更新BBOSS侧产品表(GRP_MERCHP)的产品订单编号
            String hostCompany = IDataUtil.chkParam(data, "PROVINCE");
            if (!StringUtils.equals(ProvinceUtil.getProvinceCodeGrpCorp(), hostCompany))
            {
                IData inparams = new DataMap();
                inparams.put("TRADE_ID", tradeId);
                inparams.put("PRODUCT_ORDER_ID", psubscribeIdSet.get(i));
                inparams.put("PRODUCT_OFFER_ID", rsrvstr4Set.get(i));
                TradeGrpMerchpInfoQry.updateMerchpOrderIdByTradeIdOfferId(inparams);
            }
        }
    }

    /*
     * @description 更新商品相关信息
     * @author xunyl
     * @date 2013-10-10
     */
    protected static void updateMerchRelInfo(IData data) throws Exception
    {
        // 主办省的情况，需要更新商品台账表的商品订单编号
        String operType = IDataUtil.chkParam(data, "OPERA_TYPE");
        IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", data); // 产品订购关系ID
        // 更新当前产品对应的商品订单信息，主办省一单多线，配合省一单一线
        for (int i = 0; i < rsrvstr4Set.size(); i++)
        {
            String productOfferringId = rsrvstr4Set.get(i).toString();
            IDataset tradeInfos = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferringId, null);
            // 如果根据产品offer_id 查不到数据则是新增订单
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                String tradeID = tradeInfos.getData(0).getString("TRADE_ID");
                // 根据当前产品订单 查寻商品订单 并更新对应的商品
                IDataset relationDataset = TradeRelaBBInfoQry.qryRelaBBInfoListByTradeIdForGrp(tradeID);
                if (IDataUtil.isNotEmpty(relationDataset))
                {
                    String user_id_a = relationDataset.getData(0).getString("USER_ID_A");
                    IDataset merchMainTradeInfo = TradeInfoQry.getMainTradeByUserId(user_id_a);

                    if (IDataUtil.isNotEmpty(merchMainTradeInfo))
                    {
                        IData param = new DataMap();
                        param.put("TRADE_ID", merchMainTradeInfo.getData(i).getString("TRADE_ID"));
                        String merch_order_id = IDataUtil.chkParam(data, "SUBSCRIBE_ID");
                        param.put("MERCH_ORDER_ID", merch_order_id);
                        TradeGrpMerchInfoQry.updateTradeForGrpBBoss(param);
                    }
                }
            }
            else
            {
                if (StringUtils.equals("1", operType))
                {
                    // 作为配合省没有该订单信息 需在本地生成订单 调BBOSS 集团客户商品订单订购接口
                    // TcsGrpIntf.dealBbossGroupBiz(data);
                    data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记
                    CreateBBossUserSVC svc = new CreateBBossUserSVC();
                    svc.crtOrder(data);

                }
                else if (StringUtils.equals("2", operType))
                {

                    data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记
                    DestroyBBossUserSVC svc = new DestroyBBossUserSVC();
                    svc.dealDelBBossBiz(data);
                }
                else
                {
                    data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记
                    ChangeBBossUserSVC svc = new ChangeBBossUserSVC();
                    svc.crtOrder(data);
                }

            }

        }
    }

    public IData dealCommonData(IData data) throws Exception
    {
        // 1- 异常检测
        checkManageException(data);

        // 2- 更新商品相关信息
        updateMerchRelInfo(data);

        // 3- 发送ESOP
        if (BizEnv.getEnvBoolean("isesop", true))
        {
            String merch_offering_id = IDataUtil.chkParam(data, "RSRV_STR2");
            IDataset merchTradeInfo = TradeGrpMerchInfoQry.qryMerchOnlineInfoByMerchOfferId(merch_offering_id, null);
            if (IDataUtil.isNotEmpty(merchTradeInfo) && IDataUtil.isNotEmpty(merchTradeInfo))
            {
                String tradeId = merchTradeInfo.getData(0).getString("TRADE_ID");
                IData httpResult = SendDataToEsopBean.synEsopData(tradeId, data);
                // ESOP接口调用成功
                if (IDataUtil.isNotEmpty(httpResult) && StringUtils.equals("0", httpResult.getString("X_RESULTCODE")))
                {
                    String resultIbsysId = httpResult.getString("IBSYSID");
                    SendDataToEsopBean.updateIbSysIdForTradeExt(resultIbsysId, tradeId, data);
                    // 4- 更新产品相关信息
                    data.put("IBSYSID", resultIbsysId);
                }
            }
        }

        // 4- 更新产品相关信息
        updateMerchpRelInfo(data);

        // 5- 返回结果
        IData result = getRspInfo(data);
        return result;
    }
}
