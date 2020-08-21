/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.contractsale;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.StaffException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: GroupContractSaleTradeSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-30 下午09:02:58 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
 */

public class GroupContractSaleTradeSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: （适配接口）社会渠道合约计划受理（由于一级BOSS只做透传，而集团规范中的SALES_PERSON_ID才是真正的受理工号 而框架又不允许在服务层修改visit中的staffId等信息
     *               故这个接口调用服务的时候是用的远程调用，将自己当成CRM外的一部分 这样可以将visit中的staffId等信息设置对
     *               千万注意，由于是远程调用会存在事务问题，故这个接口不允许有任何带事务的操作，而且基本上不应该有业务逻辑）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:09:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IData contractSaleDealEntry(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "ORDER_TIME");
        IDataUtil.chkParam(input, "ORDER_CODE");
        IDataUtil.chkParam(input, "OPR_IDENTIFY");

        String orderCode = input.getString("ORDER_CODE");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(orderCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + orderCode + "】记录!");
        }
        IData orderPreData = result.getData(0);
        StringBuilder acceptDataStr = new StringBuilder(4000);
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA1", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA2", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA3", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA4", ""));
        IData acceptData = new DataMap(acceptDataStr.toString());

        String url = GlobalCfg.getProperty("service.router.addr", null);
        IDataInput inputData = DataHelper.createDataInput(getVisit(), input, null);

        inputData.getHead().put("TRADE_STAFF_ID", acceptData.getString("TRADE_STAFF_ID"));
        inputData.getHead().put("TRADE_DEPART_ID", acceptData.getString("TRADE_DEPART_ID"));
        inputData.getHead().put("TRADE_CITY_CODE", acceptData.getString("TRADE_CITY_CODE"));
        inputData.getHead().put("TRADE_EPARCHY_CODE", acceptData.getString("TRADE_EPARCHY_CODE"));
        inputData.getHead().put("IN_MODE_CODE", acceptData.getString("IN_MODE_CODE"));

        CSAppCall.call(url, "SS.GroupContractSaleTradeSVC.deal", inputData, true);

        return null;
    }

    /**
     * @Function:
     * @Description: （适配接口）社会渠道合约计划退订（由于一级BOSS只做透传，而集团规范中的SALES_PERSON_ID才是真正的受理工号 而框架又不允许在服务层修改visit中的staffId等信息
     *               故这个接口调用服务的时候是用的远程调用，将自己当成CRM外的一部分 这样可以将visit中的staffId等信息设置对
     *               千万注意，由于是远程调用会存在事务问题，故这个接口不允许有任何带事务的操作，而且基本上不应该有业务逻辑）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:16:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IData contractSaleStopDealEntry(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "UNSUB_TIME");
        IDataUtil.chkParam(input, "UNSUB_CODE");
        IDataUtil.chkParam(input, "OPR_IDENTIFY");

        String unSubCode = input.getString("UNSUB_CODE");
        // String oprIdentify = input.getString("OPR_IDENTIFY");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(unSubCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + unSubCode + "】记录!");
        }
        IData orderPreData = result.getData(0);

        StringBuilder acceptDataStr = new StringBuilder(4000);
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA1", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA2", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA3", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA4", ""));
        IData acceptData = new DataMap(acceptDataStr.toString());

        String url = GlobalCfg.getProperty("service.router.addr", null);
        IDataInput inputData = DataHelper.createDataInput(getVisit(), input, null);
        inputData.getHead().put("TRADE_STAFF_ID", acceptData.getString("TRADE_STAFF_ID"));
        inputData.getHead().put("TRADE_DEPART_ID", acceptData.getString("TRADE_DEPART_ID"));
        inputData.getHead().put("TRADE_CITY_CODE", acceptData.getString("TRADE_CITY_CODE"));
        inputData.getHead().put("TRADE_EPARCHY_CODE", acceptData.getString("TRADE_EPARCHY_CODE"));
        inputData.getHead().put("IN_MODE_CODE", acceptData.getString("IN_MODE_CODE"));

        CSAppCall.call(url, "SS.GroupContractSaleTradeSVC.GroupContractStopDeal", inputData, true);

        return null;
    }

    /**
     * @Function:
     * @Description: 社会渠道合约计划受理
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:11:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public void deal(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "ORDER_TIME");
        IDataUtil.chkParam(input, "ORDER_CODE");
        IDataUtil.chkParam(input, "OPR_IDENTIFY");

        String orderCode = input.getString("ORDER_CODE");
        String operIdentify = input.getString("OPR_IDENTIFY");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(orderCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + orderCode + "】记录!");
        }
        IData orderPreData = result.getData(0);
        if (!orderPreData.getString("REPLY_STATE").equals("0"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此订单号【" + orderCode + "】记录,已经处理!");
        }

        if (operIdentify.equals("0"))// 撤单
        {
            // 删除订单
            orderPreData.put("END_DATE", SysDateMgr.getSysTime());
            orderPreData.put("REPLY_STATE", "1");
            orderPreData.put("ACCEPT_STATE", "1");
            Dao.save("TF_B_ORDER_PRE", orderPreData,Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if (operIdentify.equals("1"))// 执行
        {
            String acceptData = orderPreData.getString("ACCEPT_DATA1", "") + orderPreData.getString("ACCEPT_DATA2", "") + orderPreData.getString("ACCEPT_DATA3", "") + orderPreData.getString("ACCEPT_DATA4", "")
                    + orderPreData.getString("ACCEPT_DATA5", "");

            IData cond = new DataMap(acceptData);
            // CSAppException.apperr(CrmCommException.CRM_COMM_1);
            // 判断IMEI，如果在预受理的时候传了，则受理的时候不再对资源做预占，否则需做预占
            String preImei = cond.getString("IMEI");
            // String imei = input.getString("IMEI");
            if (StringUtils.isNotBlank(preImei))
            {
                cond.put("NEED_CHECK_RES", "0");
            }
            else if (StringUtils.isNotBlank(input.getString("IMEI")))
            {
                cond.put("RES_CODE", input.getString("IMEI"));
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_913, "IMEI");
            }
            if (cond.containsKey("PRE_TYPE"))
            {
                cond.remove("PRE_TYPE"); // 现在是正式执行，去掉PRE_TYPE
            }
            // end
            result = CSAppCall.call("SS.ContractSaleRegSVC.tradeRegGroupContract", cond);
            if (IDataUtil.isNotEmpty(result))
            {
                String orderId = result.getData(0).getString("ORDER_ID");
                orderPreData.put("ORDER_ID", orderId);
                orderPreData.put("END_DATE", SysDateMgr.getSysTime());
                orderPreData.put("REPLY_STATE", "1");
                orderPreData.put("ACCEPT_RESULT", "回填ORDER成功");
                orderPreData.put("ACCEPT_STATE", "9");

                Dao.save("TF_B_ORDER_PRE", orderPreData, Route.getJourDb(Route.CONN_CRM_CG));
            }
        }
    }

    public IData exchangeGoods(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "MODIFY_TIME");
        IDataUtil.chkParam(input, "MODIFY_CODE");
        IDataUtil.chkParam(input, "OPR_IDENTIFY");

        String oprIdentify = input.getString("OPR_IDENTIFY");
        String modifyCode = input.getString("MODIFY_CODE");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(modifyCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + modifyCode + "】记录!");
        }
        IData orderPreData = result.getData(0);
        if (!orderPreData.getString("REPLY_STATE").equals("0"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此订单号【" + modifyCode + "】记录,已经处理!");
        }

        if (oprIdentify.equals("0"))
        {
            // 删除订单
            orderPreData.put("END_DATE", SysDateMgr.getSysTime());
            orderPreData.put("REPLY_STATE", "1");
            orderPreData.put("ACCEPT_STATE", "1");
            Dao.save("TF_B_ORDER_PRE", orderPreData,Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if (oprIdentify.equals("1"))
        {
            String acceptData = orderPreData.getString("ACCEPT_DATA1", "") + orderPreData.getString("ACCEPT_DATA2", "") + orderPreData.getString("ACCEPT_DATA3", "") + orderPreData.getString("ACCEPT_DATA4", "")
                    + orderPreData.getString("ACCEPT_DATA5", "");

            IData cond = new DataMap(acceptData);
            result = CSAppCall.call("SS.GroupContractSaleTradeSVC.exchangeGoods", cond);
            if (IDataUtil.isNotEmpty(result))
            {
                String orderId = result.getData(0).getString("ORDER_ID");
                orderPreData.put("ORDER_ID", orderId);
                orderPreData.put("END_DATE", SysDateMgr.getSysTime());
                orderPreData.put("REPLY_STATE", "1");
                orderPreData.put("ACCEPT_RESULT", "回填ORDER成功");
                orderPreData.put("ACCEPT_STATE", "9");

                Dao.save("TF_B_ORDER_PRE", orderPreData, Route.getJourDb(Route.CONN_CRM_CG));
            }
        }

        return null;
    }

    /**
     * * 社会渠道合约计划换机执行 由于一级BOSS只做透传，而集团规范中的SALES_PERSON_ID才是真正的受理工号 而框架又不允许在服务层修改visit中的staffId等信息
     * 故这个接口调用服务的时候是用的远程调用，将自己当成CRM外的一部分 这样可以将visit中的staffId等信息设置对 千万注意，由于是远程调用会存在事务问题，故这个接口不允许有任何带事务的操作，而且基本上不应该有业务逻辑
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData exchangeGoodsEntry(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "ORDER_TIME");
        IDataUtil.chkParam(input, "ORDER_CODE");
        IDataUtil.chkParam(input, "OPR_IDENTIFY");

        String orderCode = input.getString("ORDER_CODE");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(orderCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + orderCode + "】记录!");
        }
        IData orderPreData = result.getData(0);
        StringBuilder acceptDataStr = new StringBuilder(4000);
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA1", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA2", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA3", ""));
        acceptDataStr.append(orderPreData.getString("ACCEPT_DATA4", ""));
        IData acceptData = new DataMap(acceptDataStr.toString());

        input.put("TRADE_STAFF_ID", acceptData.getString("TRADE_STAFF_ID"));
        input.put("TRADE_DEPART_ID", acceptData.getString("TRADE_DEPART_ID"));
        input.put("TRADE_CITY_CODE", acceptData.getString("TRADE_CITY_CODE"));
        input.put("TRADE_EPARCHY_CODE", acceptData.getString("TRADE_EPARCHY_CODE"));
        input.put("IN_MODE_CODE", acceptData.getString("IN_MODE_CODE"));

        CSAppCall.call("SS.GroupContractSaleTradeSVC.exchangeGoods", input);

        return null;
    }

    /**
     * @Function:
     * @Description: （适配接口）社会渠道合约计划预受理(由于一级BOSS只做透传，而集团规范中的SALES_PERSON_ID才是真正的受理工号 而框架又不允许在服务层修改visit中的staffId等信息
     *               故这个接口调用服务的时候是用的远程调用，将自己当成CRM外的一部分 这样可以将visit中的staffId等信息设置对
     *               千万注意，由于是远程调用会存在事务问题，故这个接口不允许有任何带事务的操作，而且基本上不应该有业务逻辑）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:08:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IData groupContractPreSaleEntry(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SALES_PERSON_ID");
        IDataUtil.chkParam(input, "MOBILE_NO");

        String staffId = input.getString("SALES_PERSON_ID");
        input.put("SERIAL_NUMBER", input.getString("MOBILE_NO"));
        String url = GlobalCfg.getProperty("service.router.addr", null);
        IDataInput inputData = DataHelper.createDataInput(getVisit(), input, null);
        if (StringUtils.isNotBlank(staffId))
        {
            IData staffInfo = UStaffInfoQry.qryStaffInfoByPK(staffId);
            if (IDataUtil.isEmpty(staffInfo))
            {
                CSAppException.apperr(StaffException.CRM_STAFF_1, staffId);
            }

            inputData.getHead().put("TRADE_STAFF_ID", staffInfo.getString("STAFF_ID"));
            inputData.getHead().put("TRADE_DEPART_ID", staffInfo.getString("DEPART_ID"));
            inputData.getHead().put("TRADE_CITY_CODE", staffInfo.getString("CITY_CODE"));
            inputData.getHead().put("TRADE_EPARCHY_CODE", staffInfo.getString("EPARCHY_CODE"));
            inputData.getHead().put("IN_MODE_CODE", getVisit().getInModeCode());
        }

        return CSAppCall.call(url, "SS.ContractSaleSVC.GroupContractPreSale", inputData, true).getData(0);
    }

    /**
     * @Function:
     * @Description: 社会渠道合约计划退订
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:17:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IData groupContractStopDeal(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "UNSUB_TIME");
        IDataUtil.chkParam(input, "UNSUB_CODE");
        IDataUtil.chkParam(input, "OPR_IDENTIFY");

        String unSubCode = input.getString("UNSUB_CODE");
        String oprIdentify = input.getString("OPR_IDENTIFY");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(unSubCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + unSubCode + "】记录!");
        }
        IData orderPreData = result.getData(0);
        if (!orderPreData.getString("REPLY_STATE").equals("0"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此订单号【" + unSubCode + "】记录,已经处理!");
        }

        if (oprIdentify.equals("0"))
        {
            // 删除订单
            orderPreData.put("END_DATE", SysDateMgr.getSysTime());
            orderPreData.put("REPLY_STATE", "1");
            orderPreData.put("ACCEPT_STATE", "1");
            Dao.save("TF_B_ORDER_PRE", orderPreData,Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if (oprIdentify.equals("1"))
        {
            StringBuilder acceptDataStr = new StringBuilder(4000);
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA1", ""));
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA2", ""));
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA3", ""));
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA4", ""));
            IData acceptData = new DataMap(acceptDataStr.toString());

            CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", acceptData);
        }

        return null;
    }

    /**
     * * 社会渠道合约计划换机预执行 由于一级BOSS只做透传，而集团规范中的SALES_PERSON_ID才是真正的受理工号 而框架又不允许在服务层修改visit中的staffId等信息
     * 故这个接口调用服务的时候是用的远程调用，将自己当成CRM外的一部分 这样可以将visit中的staffId等信息设置对 千万注意，由于是远程调用会存在事务问题，故这个接口不允许有任何带事务的操作，而且基本上不应该有业务逻辑
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData preExchangeGoodsEntry(IData input) throws Exception
    {
        IData returnResult = new DataMap();
        try
        {
            IDataUtil.chkParam(input, "SALES_PERSON_ID");
            IDataUtil.chkParam(input, "ORDER_CODE");
            IDataUtil.chkParam(input, "IMEI");
            IDataUtil.chkParam(input, "MODIFY_CODE");
            IDataUtil.chkParam(input, "COMPANY_CODE");
            IDataUtil.chkParam(input, "SHOP_CODE");
            IDataUtil.chkParam(input, "MATERIAL_CODE");
            IDataUtil.chkParam(input, "MAKEUP_FEE");

            String orderCode = input.getString("ORDER_CODE");
            IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(orderCode);
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到此订单号【" + orderCode + "】记录!");
            }
            IData orderPreData = result.getData(0);
            StringBuilder acceptDataStr = new StringBuilder(4000);
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA1", ""));
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA2", ""));
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA3", ""));
            acceptDataStr.append(orderPreData.getString("ACCEPT_DATA4", ""));
            IData saleRegAcceptData = new DataMap(acceptDataStr.toString());
            String oldImei = saleRegAcceptData.getString("RES_CODE");
            String serialNumber = saleRegAcceptData.getString("SERIAL_NUMBER");

            String staffId = input.getString("SALES_PERSON_ID");
            input.put("SERIAL_NUMBER", serialNumber);
            input.put("OLD_IMEI", oldImei);
            input.put("NEW_IMEI", input.getString("IMEI"));
            if (StringUtils.isNotBlank(staffId))
            {
                IData staffInfo = UStaffInfoQry.qryStaffInfoByPK(staffId);
                if (IDataUtil.isEmpty(staffInfo))
                {
                    CSAppException.apperr(StaffException.CRM_STAFF_1, staffId);
                }

                input.put("TRADE_STAFF_ID", staffInfo.getString("STAFF_ID"));
                input.put("TRADE_DEPART_ID", staffInfo.getString("DEPART_ID"));
                input.put("TRADE_CITY_CODE", staffInfo.getString("CITY_CODE"));
                input.put("TRADE_EPARCHY_CODE", staffInfo.getString("EPARCHY_CODE"));
                input.put("IN_MODE_CODE", getVisit().getInModeCode());
            }
            CSAppCall.call("SS.ExchangeGoodsSVC.tradeReg", input);
            returnResult.put("IS_MODIFY_SUCCESS", "1");
        }
        catch (Exception e)
        {
            returnResult.put("IS_MODIFY_SUCCESS", "0");
            returnResult.put("MODIFY_COMMENT", Utility.parseExceptionMessage(e));
        }

        returnResult.put("OPR_NUMB", input.getString("OPR_NUMB"));
        returnResult.put("RECEIVED_OPR_NUMB", input.getString("OPR_NUMB"));
        returnResult.put("MODIFY_CODE", input.getString("MODIFY_CODE"));

        return returnResult;
    }

    /**
     * @Function:
     * @Description: 社会渠道合约计划预退订
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:16:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IData preStop(IData input) throws Exception
    {
        String unsubCode = input.getString("UNSUB_CODE");
        String orderId = input.getString("ORDER_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        IData returnReuslt = new DataMap();
        IData saleactiveTrade = null;
        String sysdate = SysDateMgr.getSysTime();
        IDataset trades = TradeInfoQry.getHisMainTradeByOrderId(orderId, "0", CSBizBean.getUserEparchyCode());
        for (int i = trades.size() - 1; i >= 0; i--)
        {
            IData trade = trades.getData(i);
            if (trade.getString("TRADE_TYPE_CODE").equals("240"))
            {
                saleactiveTrade = trade;
                break;
            }
        }
        if (IDataUtil.isEmpty(saleactiveTrade))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有找到销售订单或订单未处理");
        }

        String advancePay = saleactiveTrade.getString("ADVANCE_PAY", "0");
        returnReuslt.put("RETURN_FEE", advancePay);

        // 插预受理表
        IData preOrderData = new DataMap();
        String preOrderId = SeqMgr.getOrderId();
        preOrderData.put("PRE_ID", preOrderId);
        preOrderData.put("PRE_TYPE", "groupContractSaleStop");
        preOrderData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(preOrderId));
        preOrderData.put("ACCEPT_STATE", "0");
        preOrderData.put("REQUEST_ID", unsubCode);
        preOrderData.put("START_DATE", sysdate);
        preOrderData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(12, sysdate));
        preOrderData.put("TRADE_TYPE_CODE", "256");
        preOrderData.put("SERIAL_NUMBER", serialNumber);
        preOrderData.put("REPLY_STATE", "0");

        IData acceptData = new DataMap();
        acceptData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        acceptData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        acceptData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        acceptData.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        acceptData.put("TRADE_ID", saleactiveTrade.getString("TRADE_ID"));
        acceptData.put("SERIAL_NUMBER", serialNumber);
        acceptData.put("IN_MODE_CODE", getVisit().getInModeCode());
        acceptData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        preOrderData.put("ACCEPT_DATA1", acceptData.toString());

        Dao.insert("TF_B_ORDER_PRE", preOrderData, Route.getJourDb(Route.CONN_CRM_CG));

        return returnReuslt;
    }

    /**
     * @Function:
     * @Description: (适配接口)社会渠道合约计划预退订（由于一级BOSS只做透传，而集团规范中的SALES_PERSON_ID才是真正的受理工号 而框架又不允许在服务层修改visit中的staffId等信息
     *               故这个接口调用服务的时候是用的远程调用，将自己当成CRM外的一部分 这样可以将visit中的staffId等信息设置对
     *               千万注意，由于是远程调用会存在事务问题，故这个接口不允许有任何带事务的操作，而且基本上不应该有业务逻辑）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 下午09:15:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IData preStopEntry(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "UNSUB_TIME");
        IDataUtil.chkParam(input, "UNSUB_CODE");
        IDataUtil.chkParam(input, "COMPANY_CODE");
        IDataUtil.chkParam(input, "SHOP_CODE");
        IDataUtil.chkParam(input, "SALES_PERSON_ID");
        IDataUtil.chkParam(input, "ORDER_CODE");

        IData returnResult = new DataMap();

        String orderCode = input.getString("ORDER_CODE");

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId2(orderCode);
        if (IDataUtil.isEmpty(result))
        {
            returnResult.put("IS_UNSUB_SUCCESS", "0");
            returnResult.put("UNSUB_COMMENT", "没有找到销售订单");
            return returnResult;
        }
        IData preOrder = result.getData(0);
        String orderId = preOrder.getString("ORDER_ID");
        String acceptState = preOrder.getString("ACCEPT_STATE", "");
        String serialNumber = preOrder.getString("SERIAL_NUMBER");
        if (!"9".equals(acceptState))
        {
            returnResult.put("IS_UNSUB_SUCCESS", "0");
            returnResult.put("UNSUB_COMMENT", "销售订单没处理");
            return returnResult;
        }

        String returnFee = "0";
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("ORDER_ID", orderId);
        params.put("UNSUB_CODE", input.getString("UNSUB_CODE"));

        String url = GlobalCfg.getProperty("service.router.addr", null);
        IDataInput inputData = DataHelper.createDataInput(getVisit(), params, null);

        String staffId = input.getString("SALES_PERSON_ID");
        if (StringUtils.isNotBlank(staffId))
        {
            IData staffInfo = UStaffInfoQry.qryStaffInfoByPK(staffId);
            if (IDataUtil.isEmpty(staffInfo))
            {
                CSAppException.apperr(StaffException.CRM_STAFF_1, staffId);
            }

            inputData.getHead().put("TRADE_STAFF_ID", staffInfo.getString("STAFF_ID"));
            inputData.getHead().put("TRADE_DEPART_ID", staffInfo.getString("DEPART_ID"));
            inputData.getHead().put("TRADE_CITY_CODE", staffInfo.getString("CITY_CODE"));
            inputData.getHead().put("TRADE_EPARCHY_CODE", staffInfo.getString("EPARCHY_CODE"));
            inputData.getHead().put("IN_MODE_CODE", getVisit().getInModeCode());
        }

        try
        {
            result = CSAppCall.call(url, "SS.GroupContractSaleTradeSVC.GroupContractPreStop", inputData, true);
            returnFee = result.getData(0).getString("RETURN_FEE", "0");
        }
        catch (Exception e)
        {

            returnResult.put("IS_UNSUB_SUCCESS", "0");
            returnResult.put("UNSUB_COMMENT", Utility.parseExceptionMessage(e));
            return returnResult;
        }

        returnResult.put("IS_UNSUB_SUCCESS", "1");
        returnResult.put("RETURN_FEE", returnFee);
        return returnResult;
    }
}
