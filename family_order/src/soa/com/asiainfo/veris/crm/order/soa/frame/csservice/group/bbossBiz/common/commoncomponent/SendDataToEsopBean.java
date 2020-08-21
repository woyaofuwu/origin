
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @description 该类用于处理落地报文时向ESOP发送数据，包括商产品订购信息同步、工单流转同步和管理报文三种典型场景需要调用ESOP接口
 * @author xunyl
 * @date 2013-09-27
 */

public class SendDataToEsopBean extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 从IBOSS反向接口的数据中获取产品操作类型
     * @author xunyl
     * @date 2013-09-27
     */
    protected static String getProductOperType(IData iBossData) throws Exception
    {
        // 1- 定义产品操作类型
        String productOperType = "";

        // 2- 获取产品操作类型
        IDataset operTypeList = iBossData.getDataset("RSRV_STR14");
        if (IDataUtil.isNotEmpty(operTypeList))
        {
            IDataset productOperTypes = operTypeList.getDataset(0);
            if (IDataUtil.isNotEmpty(productOperTypes))
            {
                productOperType = productOperTypes.get(0).toString();
            }
        }

        // 3- 返回产品操作类型
        return productOperType;
    }

    /*
     * @description 拼装ESOP参数数据
     * @author xunyl
     * @date 2013-09-26
     */
    protected static IData makeEsopParamData(IData mainTradeInfo, IData iBossData, IDataset esopTradeInfoList) throws Exception
    {
        // 1- 定义ESOP参数对象
        IData esopData = new DataMap();

        // 2- 添加用户编号
        String userId = mainTradeInfo.getString("USER_ID", "");
        esopData.put("USER_ID", userId);

        // 3- 添加ESOP标志
        String ibsysId = "";
        String busiSign = iBossData.getString("BUSI_SIGN", "");
        if (StringUtils.equals(IntfField.SubTransCode.BbossGrpUserBiz.value, busiSign))
        {
            ibsysId = (null != esopTradeInfoList && IDataUtil.isNotEmpty(esopTradeInfoList)) ? esopTradeInfoList.getData(0).getString("ATTR_VALUE", "") : "";
        }
        esopData.put("IBSYSID", ibsysId);

        // 4- 添加集团编号
        String groupId = "";
        String custId = mainTradeInfo.getString("CUST_ID", "");
        IData custGroupInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(custGroupInfo))
        {
            groupId = custGroupInfo.getString("GROUP_ID", "");
        }
        esopData.put("GROUP_ID", groupId);

        // 5- 添加台账编号
        String tradeId = mainTradeInfo.getString("TRADE_ID");
        esopData.put("TRADE_ID", tradeId);

        // 6- 添加产品订单号
        String productId = mainTradeInfo.getString("PRODUCT_ID", "");
        esopData.put("PRODUCT_ID", productId);

        // 7- 添加报文类型、业务开展模式、业务开展省、产品名称、客户名称、集团编码、员工编码
        String custName = mainTradeInfo.getString("CUST_NAME", "");
        String staffId = mainTradeInfo.getString("TRADE_STAFF_ID", "");
        IData transParam = new DataMap();
        transParam.put("PRODUCT_ID", productId);
        transParam.put("CUST_NAME", custName);
        transParam.put("GROUP_ID", groupId);
        transParam.put("STAFF_ID", staffId);
        IDataset parmDataset = makeParamInfoForEsop(iBossData, transParam);
        IDataset params = new DatasetList();
        params.add(parmDataset);
        // 8- 添加固定常量
        esopData.put("BPM_TEMPLET_ID", "BBOSS");
        esopData.put("OPER_CODE", "02");
        esopData.put("DEAL_STATE", "2");
        esopData.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
        esopData.put("ORIG_DOMAIN", "ECRM");
        esopData.put("HOME_DOMAIN", "ECRM");
        esopData.put("BUSI_SIGN", busiSign);
        esopData.put("WORK_TYPE", "04");
        esopData.put("X_SUBTRANS_CODE", "SaveAndSend");
        // 如果产品操作类型为预受理场合，则X_SUBTRANS_CODE应该为
        if (StringUtils.equals(IntfField.SubTransCode.BbossGrpUserBiz.value, busiSign))
        {
            String productOperType = getProductOperType(iBossData);
            if (!StringUtils.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue(), productOperType))
            {
                esopData.put("X_SUBTRANS_CODE", "SaveGrpbiz");
            }
        }

        // 9- 添加处理时间和受理时间
        esopData.put("PROCESS_TIME", SysDateMgr.getSysDate());
        esopData.put("ACCEPT_DATE", SysDateMgr.getSysDate());

        // 10- 添加产品用户编号

        // 11- 添加RELE_SUBSCRIBE_ID
        if (IDataUtil.isNotEmpty(esopTradeInfoList))
        {
            if (!StringUtils.equals(IntfField.SubTransCode.BbossGrpUserBiz.value, busiSign) || (StringUtils.equals(IntfField.SubTransCode.BbossGrpUserBiz.value, busiSign) && StringUtils.isEmpty(ibsysId)))
            {
                esopData.put("RELE_SUBSCRIBE_ID", esopTradeInfoList.getData(0).getString("ATTR_VALUE", ""));
            }
        }

        esopData.put("PARAMS", params);

        // 11- 添加前一次受理中的受理地州、员工工号、部门编号、地市编号
        esopData.put("TRADE_EPARCHY_CODE", mainTradeInfo.getString("TRADE_EPARCHY_CODE", "")); // 受理地州
        esopData.put("UPDATE_STAFF_ID", mainTradeInfo.getString("TRADE_STAFF_ID", "")); // 受理员工
        esopData.put("UPDATE_DEPART_ID", mainTradeInfo.getString("TRADE_DEPART_ID", "")); // 受理部门
        esopData.put("TRADE_CITY_CODE", mainTradeInfo.getString("TRADE_CITY_CODE", ""));

        // 12- 添加本次受理的地市编号、员工工号、员工名称、部门编号、部门名称
        esopData.put("CITY_CODE", getVisit().getCityCode());
        esopData.put("CITY_CODE", getVisit().getCityCode());
        esopData.put("DEPART_ID", getVisit().getDepartId());
        esopData.put("DEPART_NAME", getVisit().getDepartName());
        esopData.put("STAFF_ID", getVisit().getStaffId());
        esopData.put("STAFF_NAME", getVisit().getStaffName());
        esopData.put("STAFF_ID", "SUPERUSR");

        // 13- 返回ESOP参数数据
        return esopData;
    }

    /*
     * @description 拼装ESOP的PARAM信息，包括报文类型、业务开展模式、业务开展省、产品名称、客户名称、集团编码、员工编码
     * @author xunyl
     * @date 2013-09-26
     */
    protected static IDataset makeParamInfoForEsop(IData iBossData, IData transParam) throws Exception
    {
        // 1- 定义PARAM对象
        IDataset param = new DatasetList();

        // 2- 添加报文类型
        IData bizData = new DataMap();
        String busiSign = iBossData.getString("BUSI_SIGN", "");
        bizData.put("PARAM_CODE", "BUSI_SIGN");
        bizData.put("PARAM_NAME", "BBOSS报文类型");
        bizData.put("PARAM_VALUE", busiSign);
        param.add(bizData);

        // 3- 添加业务开展模式
        IData busiData = new DataMap();
        String bizMode = iBossData.getString("SI_BIZ_MODE");
        busiData.put("PARAM_CODE", "BIZ_MODE");
        busiData.put("PARAM_NAME", "BBOSS业务开展模式");
        busiData.put("PARAM_VALUE", bizMode);
        param.add(busiData);

        // 4- 添加产品名称
        IData productNameData = new DataMap();
        String productId = transParam.getString("PRODUCT_ID");
        String productName = UProductInfoQry.getProductNameByProductId(productId);
        productNameData.put("PARAM_CODE", "PRODUCT_NAME");
        productNameData.put("PARAM_NAME", "产品名称");
        productNameData.put("PARAM_VALUE", productName);
        param.add(productNameData);

        // 5- 添加业务开展省
        IData provData = new DataMap();
        String prov = iBossData.getString("PROVINCE", "");
        provData.put("PARAM_CODE", "HostCompany");
        provData.put("PARAM_NAME", "业务开展省");
        provData.put("PARAM_VALUE", prov);
        param.add(provData);

        // 6- 添加客户名称
        IData custNameData = new DataMap();
        String custName = transParam.getString("CUST_NAME");
        custNameData.put("PARAM_CODE", "CUST_NAME");
        custNameData.put("PARAM_NAME", "客户名称");
        custNameData.put("PARAM_VALUE", custName);
        param.add(custNameData);

        // 7- 添加集团编码
        IData groupIdData = new DataMap();
        String groupId = transParam.getString("GROUP_ID");
        groupIdData.put("PARAM_CODE", "GROUP_ID");
        groupIdData.put("PARAM_NAME", "集团编码");
        groupIdData.put("PARAM_VALUE", groupId);
        param.add(groupIdData);

        // 8- 添加员工编码
        IData tradeStaffIdData = new DataMap();
        String staffId = transParam.getString("STAFF_ID");
        tradeStaffIdData.put("PARAM_CODE", "TRADE_STAFF_ID");
        tradeStaffIdData.put("PARAM_NAME", "员工编码");
        tradeStaffIdData.put("PARAM_VALUE", staffId);
        param.add(tradeStaffIdData);

        // 9- 工单流转设置工单状态信息
        if (StringUtils.equals(IntfField.SubTransCode.BbossOrderStateBiz.value, busiSign) || StringUtils.equals(IntfField.SubTransCode.BbossOrderStateBiz.value, busiSign + "_1_0"))
        {
            // 订单状态
            IData tempData1 = new DataMap();
            tempData1.put("PARAM_CODE", "SYNC_STATE");
            tempData1.put("PARAM_NAME", "订单状态");
            String stateType = iBossData.getString("STATE_TYPE");
            String staticName = StaticUtil.getStaticValue("GRP_STATE_TYPE", stateType);
            tempData1.put("PARAM_VALUE", staticName);
            param.add(tempData1);

            // 订单下发时间
            IData tempData2 = new DataMap();
            tempData2.put("PARAM_CODE", "SYN_TIME");
            tempData2.put("PARAM_NAME", "订单下发时间");
            String syncTime = iBossData.getString("SYN_TIME");
            tempData2.put("PARAM_VALUE", syncTime);
            param.add(tempData2);

            // 是否需要反馈
            IData tempData3 = new DataMap();
            tempData3.put("PARAM_CODE", "IF_PROVCPRT");
            tempData3.put("PARAM_NAME", "是否需要反馈");
            String ifProvcrt = iBossData.getString("IF_PROVCPRT", "0");
            staticName = StaticUtil.getStaticValue("IF_PROVCPRT", ifProvcrt);
            tempData3.put("PARAM_VALUE", staticName);
            param.add(tempData3);
        }

        // 10- 集团业务反向接口
        if (StringUtils.equals(IntfField.SubTransCode.BbossGrpUserBiz.value, busiSign))
        {
            // 产品操作类型
            IData proOperData = new DataMap();
            String productOperType = getProductOperType(iBossData);
            proOperData.put("PARAM_VALUE", productOperType);
            proOperData.put("PARAM_CODE", "PRO_OPER");
            proOperData.put("PARAM_NAME", "产品操作类型");
            param.add(proOperData);

            // 商品操作类型
            IData merchOperData = new DataMap();
            merchOperData.put("PARAM_CODE", "MERCH_OPER");
            merchOperData.put("PARAM_NAME", "商品操作类型");
            String merchOperType = IDataUtil.getDataset("OPERA_TYPE", iBossData, false).get(0).toString();
            merchOperData.put("PARAM_VALUE", merchOperType);
            param.add(merchOperData);
        }

        // 11- 返回PARAM对象
        return param;
    }

    /**
     * chenyi 13-10-26 受理报文发送 当发送为最后一笔工单时需要掉esop
     * 
     * @param sendTradeDataset
     * @throws Exception
     */
    public static void sendEsopSLBefore(IData data) throws Exception
    {
        // 最后一笔订单走esop
        // 在最后一笔工单时候发ESOP
        String tradeIDS = data.getString("PRODUCT_TRADE_ID");
        tradeIDS = tradeIDS.replace("[", "");
        tradeIDS = tradeIDS.replace("]", "");
        String[] tradeStrings = tradeIDS.split(",");// 发送订单组
        String tradeId = StringUtils.replace(StringUtils.trim(tradeStrings[0]), "\"", "");

        IDataset relaInfos = RelaBBInfoQry.getUserRelationByTradeId(tradeId);

        // 查询需发送的台帐信息
        IData tradeData = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId).getData(0);
        String orderId = tradeData.getString("ORDER_ID");
        // 查出台帐表里面没发送的 台帐信息 rsrv_str10=1 为没发送的台帐
        IDataset tradeInfos = TradeInfoQry.getTradeInfobyOrdStr10(orderId, "1");

        String merchTrade_id = data.getString("MERCH_TRADEID");

        // 删除trade表里面的商品订单信息

        for (int i = 0; i < tradeInfos.size(); i++)
        {

            IData tradeInfo = tradeInfos.getData(i);
            if (tradeInfo.getString("TRADE_ID").equals(merchTrade_id))
            {
                tradeInfos.remove(i);
            }

        }
        if (tradeStrings.length == tradeInfos.size() || tradeInfos.size() == 0)
        {
            // 如果同时发送条数=台帐表数据 走esop
            IDataset infos = TradeInfoQry.getTradeForGrpBBoss(merchTrade_id);
            if (null != infos && !infos.isEmpty())
            {
                IDataset temps = TradeExtInfoQry.getTradeEsopInfoTradeId(merchTrade_id);
                if (temps != null && temps.size() > 0)
                {
                    IData eosDatainfo = new DataMap(data.getString("EOSDATA"));
                    IData eosData = eosDatainfo.getDataset("EOS").getData(0);

                    String workId = eosData.getString("WORK_ID", "");
                    if ("".equals(workId))
                    {
                        workId = data.getString("WORK_ID", "");
                    }
                    String ibsysId = eosData.getString("IBSYSID", "");
                    if ("".equals(ibsysId))
                    {
                        ibsysId = data.getString("IBSYSID", "");
                    }
                    String bpmId = eosData.getString("BPM_TEMPLET_ID", "");
                    if ("".equals(bpmId))
                    {
                        bpmId = data.getString("BPM_TEMPLET_ID", "");
                    }
                    String flowId = eosData.getString("FLOW_MAIN_ID", "");
                    if ("".equals(flowId))
                    {
                        flowId = data.getString("FLOW_MAIN_ID", "");
                    }
                    String nodeId = eosData.getString("NODE_ID", "");
                    if ("".equals(nodeId))
                    {
                        nodeId = data.getString("NODE_ID", "");
                    }
                    if (merchTrade_id.equals(""))
                    {
                        merchTrade_id = eosData.getString("TRADE_ID", "");
                    }
                    IData param = new DataMap();
                    param.put("USER_ID", temps.getData(0).getString("USER_ID"));
                    param.put("IBSYSID", ibsysId);
                    param.put("NODE_ID", nodeId);
                    param.put("TRADE_ID", merchTrade_id);
                    param.put("BPM_TEMPLET_ID", bpmId);
                    param.put("MAIN_TEMPLET_ID", flowId);
                    param.put("CITY_CODE", getVisit().getCityCode());
                    param.put("DEPART_ID", getVisit().getDepartId());
                    param.put("DEPART_NAME", getVisit().getDepartName());
                    param.put("EPARCHY_CODE", temps.getData(0).getString("TRADE_EPARCHY_CODE", ""));// gai
                    param.put("STAFF_ID", getVisit().getStaffId());
                    param.put("STAFF_NAME", getVisit().getStaffName());
                    param.put("DEAL_STATE", "2");
                    param.put("X_SUBTRANS_CODE", "SaveAndSend");
                    param.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
                    param.put("OPER_CODE", "01");
                    param.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
                    param.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
                    param.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
                    param.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
                    param.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断
                    param.put("WORK_TYPE", "00"); // 提交类型
                    param.put("PROCESS_TIME", SysDateMgr.getSysDate());
                    param.put("ACCEPT_DATE", SysDateMgr.getSysDate());
                    param.put("TRADE_EPARCHY_CODE", temps.getData(0).getString("TRADE_EPARCHY_CODE", "")); // 受理地州
                    param.put("UPDATE_STAFF_ID", temps.getData(0).getString("TRADE_STAFF_ID", "")); // 受理员工
                    param.put("UPDATE_DEPART_ID", temps.getData(0).getString("TRADE_DEPART_ID", "")); // 受理部门
                    param.put("TRADE_CITY_CODE", temps.getData(0).getString("TRADE_CITY_CODE", ""));

                    param.put("WORK_ID", workId); // BPM工作标识,
                    param.put("X_RESULTINFO", "TradeOk");
                    param.put("X_RESULTCODE", "0");
                    IDataset dealresult = new DatasetList();
                    IDataset temp2 = new DatasetList();
                    IData temp3 = new DataMap();
                    temp3.put("PARAM_CODE", "DEAL_STATE");
                    temp3.put("PARAM_NAME", "处理结果");
                    temp3.put("PARAM_VALUE", "0");
                    temp2.add(temp3);
                    dealresult.add(temp2);
                    param.put("PARAMS", dealresult);

                    ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", param);

                    // 清空tf_b_trade_ext rsrv_str8和rsrv_str9数据
                    TradeExtInfoQry.upDateDateTradeEsopExtSTR(eosData, merchTrade_id);

                }
            }
        }
        if (tradeStrings.length < tradeInfos.size())
        {
            // 如果同时发送条数<帐表数据 返回
            return;
        }

    }

    /*
     * @description 反向落地同步ESOP接口
     * @author xunyl
     * @date 2013-09-26
     */
    public static IData synEsopData(String tradeId, IData iBossData) throws Exception
    {
        // 是否走esop流程
        boolean sendpf = BizEnv.getEnvBoolean("isesop", false);

        if (!sendpf)
        {
            return new DataMap();
        }

        // 1- 获取主台账信息
        IDataset mainTradeInfoList = null;
        if (StringUtils.isNotBlank(tradeId))
        {
            mainTradeInfoList = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
        }
        // 一单多线 最后一单时主台帐已经完工，需从历史表找主台帐信息
        if (IDataUtil.isEmpty(mainTradeInfoList))
        {
            String merchOfferId = iBossData.getString("RSRV_STR2", "");
            String orderid = iBossData.getString("ORDER_ID", "");
            mainTradeInfoList = UserGrpMerchInfoQry.qryMerchBHtradeInfoByMerchOfferId(merchOfferId, orderid, null);
            if (IDataUtil.isEmpty(mainTradeInfoList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_836);
            }

        }
        IData mainTradeInfo = mainTradeInfoList.getData(0);

        tradeId = mainTradeInfo.getString("TRADE_ID");
        iBossData.put("MERCH_TRADE_ID", tradeId);

        // 2- 获取ESOP信息
        IDataset esopTradeInfoList = TradeInfoQry.getTradeForGrpBBoss(tradeId);

        String province = iBossData.getString("PROVINCE");

        String provincecode = BizEnv.getEnvString("crm.grpcorp.provincecode");// 获取当前省份编码
        // 主办省esopTradeInfoList信息不为空时发送，配合省全部发送esop
        if (provincecode.equals(province) && (IDataUtil.isNotEmpty(esopTradeInfoList)) || !provincecode.equals(province))
        {
            // 3- 拼装ESOP数据
            IData esopData = makeEsopParamData(mainTradeInfo, iBossData, esopTradeInfoList);

            // 4- 调用ESOP接口
            IDataset httpResultSet = ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", esopData);
            // 返回ESOP结果

            if (IDataUtil.isNotEmpty(httpResultSet))
            {
                return httpResultSet.getData(0);
            }
        }

        return null;

    }

    /*
     * @description 更改TF_B_TRADE_EXT表中的ibSysId（管理节点和工单流转落地会在ESOP测生成新的ibSysId）
     * @author xunyl
     * @date 2013-10-10
     */
    public static void updateIbSysIdForTradeExt(String resultIbsysId, String tradeId, IData data) throws Exception
    {
        IDataset esopInfos = TradeInfoQry.getTradeForGrpBBoss(tradeId);
        // TF_B_TRADE_EXT有记录更新
        if (null != esopInfos && IDataUtil.isNotEmpty(esopInfos))
        {
            IData esopInfo = esopInfos.getData(0);
            String esopTradeId = esopInfo.getString("TRADE_ID", "");
            String rsrv_str8 = esopInfo.getString("RSRV_STR8", ""); // 工单流转状态 的IBSYSID保存在RSRV_STR8
            StringBuilder buf_str8 = new StringBuilder();
            String busiSign = data.getString("BUSI_SIGN");
            if (StringUtils.equals(IntfField.SubTransCode.BbossOrderStateBiz.value, busiSign))
            {
                if ("".equals(rsrv_str8))
                {
                    buf_str8.append(resultIbsysId);
                }
                else
                {
                    buf_str8.append(rsrv_str8);
                    buf_str8.append(",").append(resultIbsysId);
                }
            }

            TradeExtInfoQry.upDateTradeEsopExt(buf_str8.toString(), esopTradeId);
        }
        // TF_B_TRADE_EXT没记录新增
        else
        {
            TradeExtInfoQry.insertTradeExe(tradeId, resultIbsysId, getVisit().getStaffId(), getVisit().getDepartId());
        }
    }

}
