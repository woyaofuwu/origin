
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelin;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage ModifyNpApplyTradeMain(inBuf, outBuf); ModifyNpApplyTradeNp(inBuf,
 * outBuf);
 * 
 * @ClassName: InNpApplyCancelBean.java
 * @Description: 1503--携入申请取消（携入方落地） 前台发启
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-7 下午4:04:10 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-7 lijm3 v1.0.0 修改原因
 */
public class InNpApplyCancelBean extends CSBizBean
{

    private void createOrderData(IData param, IData orderInfo) throws Exception
    {

        MainOrderData mainOrderData = new MainOrderData();
        IData resultSet = null;

        // 查找tradeTypeInfo
        TradeTypeData tradeType = null;
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        resultSet = UTradeTypeInfoQry.getTradeType(tradeTypeCode, BizRoute.getRouteId());

        if (IDataUtil.isNotEmpty(resultSet))
        {
            tradeType = new TradeTypeData(resultSet);
        }

        String inModeCode = CSBizBean.getVisit().getInModeCode();
        String order_id = SeqMgr.getOrderId();
        param.put("ORDER_ID", order_id);
        param.put("PRIORITY", tradeType.getPriority());
        param.put("EPARCHY_CODE", orderInfo.getString("EPARCHY_CODE"));
        mainOrderData.setOrderTypeCode(tradeTypeCode);
        mainOrderData.setOrderId(order_id);
        mainOrderData.setTradeTypeCode(tradeTypeCode);
        mainOrderData.setOrderState("0");
        mainOrderData.setPriority(tradeType.getPriority());
        mainOrderData.setNextDealTag("0");
        mainOrderData.setInModeCode(inModeCode);
        mainOrderData.setTradeStaffId(CSBizBean.getVisit().getStaffId());
        mainOrderData.setTradeDepartId(CSBizBean.getVisit().getDepartId());
        mainOrderData.setTradeCityCode(CSBizBean.getVisit().getCityCode());
        mainOrderData.setTradeEparchyCode(CSBizBean.getTradeEparchyCode());
        mainOrderData.setFeeState("0");
        mainOrderData.setOperFee("0");
        mainOrderData.setForegift("0");
        mainOrderData.setAdvancePay("0");
        mainOrderData.setExecTime(param.getString("ExecTime"));

        mainOrderData.setCancelTag("0");

        // mainOrderData.setBatchId(dataBus.getBatchId());

        mainOrderData.setCustId(orderInfo.getString("CUST_ID"));
        mainOrderData.setCustName(orderInfo.getString("CUST_NAME"));
        mainOrderData.setPsptTypeCode(orderInfo.getString("PSPT_TYPE_CODE"));
        mainOrderData.setPsptId(orderInfo.getString("PSPT_ID"));
        mainOrderData.setEparchyCode(orderInfo.getString("EPARCHY_CODE"));
        mainOrderData.setCityCode(orderInfo.getString("CITY_CODE"));
        mainOrderData.setSubscribeType("0");

        IData data = mainOrderData.toData();
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(order_id));
        data.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        Dao.insert("TF_B_ORDER", data, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public IDataset getNpTradeInfos(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");

        return TradeInfoQry.qryTradeInfosBySnTrade(serialNumber, "0", "39", "40");
    }

    public void modifyNpApplyTradeNp(IData param) throws Exception
    {
        // NpApplyCancelOutReqData reqData = (NpApplyCancelOutReqData)btd.getRD();
        // IDataset ids = TradeBhQry.getTradeBhInfosByUserId(reqData.getUca().getUserId());
        // if(ids.size() != 1){
        // String serialNumber = reqData.getUca().getUser().getSerialNumber();
        // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527,
        // "115000 获取SERIAL_NUMBER=["+serialNumber+"],TRADE_TYPE_CODE=41的历史台账信息失败！");
        // }
        String tradeId = param.getString("TRADE_ID");// ids.getData(0).getString("TRADE_ID");
        IData iparam = new DataMap();
        iparam.put("TRADE_ID", tradeId);
        iparam.put("CANCEL_TAG", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_NP_CANCEL_TAG", iparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    private void moveBhOrder(IData orderInfo) throws Exception
    {
        if (IDataUtil.isNotEmpty(orderInfo))
        {
            orderInfo.put("CANCEL_TAG", "1");
            Dao.insert("TF_BH_ORDER", orderInfo, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
            orderInfo.put("CANCEL_TAG", "0");
            String[] keys = new String[]
            { "ORDER_ID", "ACCEPT_MONTH", "CANCEL_TAG" };
            Dao.delete("TF_B_ORDER", orderInfo, keys, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

        }

    }

    private void moveBhTrade(IData tradeInfo) throws Exception
    {

        if (IDataUtil.isNotEmpty(tradeInfo))
        {
            tradeInfo.put("CANCEL_TAG", "1");
            Dao.insert("TF_BH_TRADE", tradeInfo, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
            tradeInfo.put("CANCEL_TAG", "0");
            String[] keys = new String[]
            { "TRADE_ID", "ACCEPT_MONTH", "CANCEL_TAG" };
            // Dao.delete("TF_B_TRADE", tradeInfo);
            Dao.delete("TF_B_TRADE", tradeInfo, keys ,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        }

    }

    private void regBusiMainTradeData(IData param, IData tradeInfo) throws Exception
    {
        MainTradeData mainTradeData = new MainTradeData();

        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        mainTradeData.setTradeTypeCode(tradeTypeCode);

        String tradeId = SeqMgr.getTradeId();
        param.put("NEW_TRADE_ID", tradeId);

        mainTradeData.setOrderId(param.getString("ORDER_ID"));
        mainTradeData.setUserId(tradeInfo.getString("USER_ID"));
        mainTradeData.setCustId(tradeInfo.getString("CUST_ID"));
        mainTradeData.setAcctId(tradeInfo.getString("ACCT_ID"));
        mainTradeData.setCustName(tradeInfo.getString("CUST_NAME"));

        mainTradeData.setPriority(param.getString("PRIORITY"));
        mainTradeData.setOlcomTag("0");// 0不发指令
        mainTradeData.setExecTime(param.getString("ExecTime"));
        mainTradeData.setSerialNumber(tradeInfo.getString("SERIAL_NUMBER"));
        mainTradeData.setSubscribeState(BofConst.SUBSCRIBE_TYPE_NORMAL_NOW);

        mainTradeData.setSubscribeType(param.getString("SUBSCRIBE_TYPE"));
        mainTradeData.setNextDealTag("0");
        mainTradeData.setInModeCode(CSBizBean.getVisit().getInModeCode());
        mainTradeData.setProcessTagSet(BofConst.PROCESS_TAG_SET);
        mainTradeData.setCancelTag(BofConst.CANCEL_TAG_NO);

        mainTradeData.setNetTypeCode(tradeInfo.getString("NET_TYPE_CODE"));
        mainTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        mainTradeData.setProductId(tradeInfo.getString("PRODUCT_ID"));
        mainTradeData.setBrandCode(tradeInfo.getString("BRAND_CODE"));
        mainTradeData.setCityCode(CSBizBean.getVisit().getCityCode());

        mainTradeData.setRsrvStr1(param.getString("TRADE_ID"));

        IData data = mainTradeData.toData();
        data.put("TRADE_ID", tradeId);
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        data.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        Dao.insert("TF_B_TRADE", data, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        // Dao.inserts(TradeTableEnum.TRADE_MAIN, regData);
    }

    private void regNpUserTradeData(IData param) throws Exception
    {

        IData m = new DataMap();
        String trade_id = param.getString("NEW_TRADE_ID");
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        IDataset ids = TradeNpQry.getTradeNpByTradeId(param.getString("TRADE_ID", ""));
        if (IDataUtil.isNotEmpty(ids))
        {

            m.put("TRADE_ID", trade_id);
            m.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
            m.put("USER_ID", ids.getData(0).getString("USER_ID", "-1"));
            m.put("TRADE_TYPE_CODE", tradeTypeCode);
            m.put("NP_SERVICE_TYPE", ids.getData(0).getString("NP_SERVICE_TYPE"));
            m.put("SERIAL_NUMBER", ids.getData(0).getString("SERIAL_NUMBER"));
            m.put("PORT_OUT_NETID", ids.getData(0).getString("PORT_OUT_NETID"));
            m.put("PORT_IN_NETID", ids.getData(0).getString("PORT_IN_NETID"));
            m.put("HOME_NETID", ids.getData(0).getString("HOME_NETID"));
            m.put("B_NP_CARD_TYPE", ids.getData(0).getString("B_NP_CARD_TYPE"));
            m.put("A_NP_CARD_TYPE", ids.getData(0).getString("A_NP_CARD_TYPE"));
            m.put("CUST_NAME", ids.getData(0).getString("CUST_NAME"));
            m.put("CRED_TYPE", ids.getData(0).getString("CRED_TYPE"));
            m.put("PSPT_ID", ids.getData(0).getString("PSPT_ID"));
            m.put("PHONE", ids.getData(0).getString("PHONE"));
            m.put("ACTOR_CUST_NAME", ids.getData(0).getString("ACTOR_CUST_NAME"));
            m.put("ACTOR_CRED_TYPE", ids.getData(0).getString("ACTOR_CRED_TYPE"));
            m.put("ACTOR_PSPT_ID", ids.getData(0).getString("ACTOR_PSPT_ID"));
            m.put("FLOW_ID", ids.getData(0).getString("FLOW_ID"));
            m.put("RESULT_CODE", param.getString("RESULT_CODE"));
            m.put("RESULT_MESSAGE", param.getString("RESULT_MESSAGE"));
            m.put("ACCEPT_DATE", SysDateMgr.getSysTime());
            m.put("CREATE_TIME", SysDateMgr.getSysTime());
            m.put("CANCEL_TAG", "0");
            m.put("STATE", "000");
            m.put("BOOK_SEND_TIME", SysDateMgr.getSysTime());
            Dao.insert("TF_B_TRADE_NP", m, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        }

    }

    /**
     * @Function: tradeReg
     * @Description: 1503前台发启时在登记流里就会 把携入用户开户,携入复机 工单修改成返销，然后把原来主台账CANCEL_TAG改为1移动历史，删除原来主台账
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月18日 下午2:23:49
     */
    public IDataset tradeReg(IData param) throws Exception
    {
        String serialNumber = param.getString("NPCODE");
        String execTime = SysDateMgr.END_DATE_FOREVER;
        String result_message = "用户主动要求取消630";
        String result_code = "630";
        String tradeTypeCode = "1503";
        String SubscribeType = "1";// 1是预约

        if (StringUtils.isNotBlank(serialNumber))
        {// 表示工信部发启
            IDataset ids = TradeNpQry.getTradeNpByCsms(serialNumber);
            if (IDataUtil.isEmpty(ids) || ids.size() > 1)
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_116029, serialNumber);
            }
            param.put("TRADE_ID", ids.getData(0).getString("TRADE_ID"));// 接口没有传tradeid 携入用户开户,携入复机
            execTime = SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss");// 工信部发启立即
            result_message = "";
            SubscribeType = "0";
        }
        param.put("RESULT_MESSAGE", result_message);
        param.put("ExecTime", execTime);
        param.put("RESULT_CODE", result_code);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SUBSCRIBE_TYPE", SubscribeType);

        String tradeId = param.getString("TRADE_ID");
        IDataset ids = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
            String orderId = data.getString("ORDER_ID");
            String routeId = data.getString("EPARCHY_CODE");
            IData orderInfo = UOrderInfoQry.qryOrderByOrderId(orderId, routeId);

            createOrderData(param, orderInfo);// 老系统没有生成order台账
            regBusiMainTradeData(param, data);
            regNpUserTradeData(param);

            modifyNpApplyTradeNp(param);// 把原来 携入用户开户,携入复机 工单修改成返销
            moveBhTrade(data);// 把原来 携入用户开户,携入复机 主台账移至历史
            moveBhOrder(orderInfo);// 把原来 携入用户开户,携入复机 order台账移到历史 原来老统没有

            // 调资源接口IModifyTurnNetSimInfo 修改卡状态

        }
        else
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据TRADE_ID=【" + tradeId + "】获取主台账失败！");
        }
        IDataset set = new DatasetList();
        IData d = new DataMap();
        d.put("TRADE_ID", param.getString("NEW_TRADE_ID", ""));
        d.put("ORDER_ID", param.getString("ORDER_ID", ""));
        set.add(d);
        return set;
    }
}
