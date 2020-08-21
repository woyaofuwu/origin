
package com.asiainfo.veris.crm.order.soa.person.busi.acctfeetradereg;

import com.ailk.biz.bean.BizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acctfeetradereg.TradeFeeRegQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.canceltrade.CancelTradeBean;

public class TradeFeeRegBean extends CSBizBean
{

    public void cancelTradeReg(IData hisTrade) throws Exception
    {
        IData param = new DataMap();
        String tradeId = hisTrade.getString("TRADE_ID");
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("BATCH_ID", hisTrade.getString("BATCH_ID", ""));
        param.put("ORDER_ID", hisTrade.getString("ORDER_ID", ""));
        param.put("PROD_ORDER_ID", hisTrade.getString("PROD_ORDER_ID", ""));
        param.put("BPM_ID", hisTrade.getString("BPM_ID", ""));
        param.put("TRADE_TYPE_CODE", hisTrade.getString("TRADE_TYPE_CODE", "8006"));
        param.put("PRIORITY", hisTrade.getString("PRIORITY", "0"));
        param.put("SUBSCRIBE_STATE", "0");
        param.put("SUBSCRIBE_TYPE", "0");
        param.put("CAMPN_ID", "");
        param.put("NEXT_DEAL_TAG", "0");
        param.put("IN_MODE_CODE", hisTrade.getString("IN_MODE_CODE", "0"));
        param.put("CUST_ID", hisTrade.getString("CUST_ID", ""));
        param.put("CUST_NAME", hisTrade.getString("CUST_NAME", ""));
        param.put("USER_ID", hisTrade.getString("USER_ID", ""));
        param.put("ACCT_ID", hisTrade.getString("ACCT_ID", ""));
        param.put("SERIAL_NUMBER", hisTrade.getString("SERIAL_NUMBER", ""));
        param.put("NET_TYPE_CODE", hisTrade.getString("NET_TYPE_CODE", "00"));
        param.put("EPARCHY_CODE", hisTrade.getString("EPARCHY_CODE", ""));
        param.put("CITY_CODE", hisTrade.getString("CITY_CODE", ""));
        param.put("PRODUCT_ID", hisTrade.getString("PRODUCT_ID", ""));
        param.put("BRAND_CODE", hisTrade.getString("BRAND_CODE", ""));
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate());
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("TERM_IP", hisTrade.getString("TERM_IP", ""));
        param.put("OPER_FEE", hisTrade.getString("OPER_FEE", "0"));
        param.put("FOREGIFT", hisTrade.getString("FOREGIFT", "0"));
        param.put("ADVANCE_PAY", hisTrade.getString("ADVANCE_PAY", "0"));
        param.put("INVOICE_NO", hisTrade.getString("INVOICE_NO", ""));
        param.put("FEE_STATE", hisTrade.getString("FEE_STATE", "0"));
        param.put("FEE_TIME", hisTrade.getString("FEE_TIME", ""));
        param.put("FEE_STAFF_ID", hisTrade.getString("FEE_STAFF_ID", ""));
        param.put("PROCESS_TAG_SET", hisTrade.getString("PROCESS_TAG_SET", " "));
        param.put("OLCOM_TAG", hisTrade.getString("OLCOM_TAG", "0"));
        param.put("FINISH_DATE", SysDateMgr.getSysDate());
        param.put("EXEC_TIME", SysDateMgr.getSysDate());
        param.put("EXEC_ACTION", hisTrade.getString("EXEC_ACTION", ""));
        param.put("EXEC_RESULT", hisTrade.getString("EXEC_RESULT", ""));
        param.put("EXEC_DESC", hisTrade.getString("EXEC_DESC", ""));
        param.put("CANCEL_TAG", "2");
        param.put("CANCEL_DATE", SysDateMgr.getSysDate());
        param.put("CANCEL_STAFF_ID", hisTrade.getString("CANCEL_STAFF_ID", ""));
        param.put("CANCEL_DEPART_ID", hisTrade.getString("CANCEL_DEPART_ID", ""));
        param.put("CANCEL_CITY_CODE", hisTrade.getString("CANCEL_CITY_CODE", ""));
        param.put("CANCEL_EPARCHY_CODE", hisTrade.getString("CANCEL_EPARCHY_CODE", ""));
        param.put("UPDATE_TIME", hisTrade.getString("UPDATE_TIME", ""));
        param.put("UPDATE_STAFF_ID", hisTrade.getString("UPDATE_STAFF_ID", ""));
        param.put("UPDATE_DEPART_ID", hisTrade.getString("UPDATE_DEPART_ID", ""));
        param.put("RSRV_STR1", hisTrade.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", hisTrade.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", hisTrade.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", hisTrade.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", hisTrade.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", hisTrade.getString("RSRV_STR6", ""));
        param.put("RSRV_STR7", hisTrade.getString("RSRV_STR7", ""));
        param.put("RSRV_STR8", hisTrade.getString("RSRV_STR8", ""));
        param.put("RSRV_STR9", hisTrade.getString("RSRV_STR9", ""));
        param.put("RSRV_STR10", hisTrade.getString("RSRV_STR10", ""));
        param.put("REMARK", hisTrade.getString("REMARK", ""));
        param.put("CUST_ID_B", hisTrade.getString("CUST_ID_B", ""));
        param.put("USER_ID_B", hisTrade.getString("USER_ID_B", ""));
        param.put("ACCT_ID_B", hisTrade.getString("ACCT_ID_B", ""));
        param.put("SERIAL_NUMBER_B", hisTrade.getString("SERIAL_NUMBER_B", ""));
        param.put("CUST_CONTACT_ID", hisTrade.getString("CUST_CONTACT_ID", ""));
        param.put("SERV_REQ_ID", hisTrade.getString("SERV_REQ_ID", ""));
        param.put("INTF_ID", hisTrade.getString("INTF_ID", ""));

        // wide
        param.put("PF_TYPE", hisTrade.getString("PF_TYPE", "0"));
        param.put("IS_NEED_HUMANCHECK", hisTrade.getString("IS_NEED_HUMANCHECK", ""));
        param.put("FREE_RESOURCE_TAG", hisTrade.getString("FREE_RESOURCE_TAG", ""));
        int j = TradeFeeRegQry.regForMainTrade(param);
        if (j < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "返销登记台账表 [TF_B_TRADE]失败!");
        }
    }

    public void insFeeBalance(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "TRADE_ID");
        String strTradeId = data.getString("TRADE_ID");
        // String strTradeTypeCode = data.getString("TRADE_TYPE_CODE","");
        String strEparchyCode = data.getString("TRADE_EPARCHY_CODE", "");
        String routeEparchyCode = Route.ROUTE_EPARCHY_CODE;
        String strCancelTag = data.getString("CANCEL_TAG", "");
        IData idata = new DataMap();
        if (StringUtils.isEmpty(routeEparchyCode))
        {
            routeEparchyCode = data.getString("EPARCHY_CODE");
        }
        else
        {
            routeEparchyCode = data.getString("TRADE_EPARCHY_CODE");
        }
        IDataset feeDatas = TradeFeeRegQry.selTradeFeesubByTradeId(strTradeId);

        if (IDataUtil.isEmpty(feeDatas))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "营业日报费用统计接口:查询费用子表出错!");
        }
        IDataset feePayMoneys = TradeFeeRegQry.selTradeFeePayMoney(strTradeId);
        if (IDataUtil.isEmpty(feePayMoneys))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "营业日报费用统计接口:查询TF_B_TRADEFEE_PAYMONEY表出错!");
        }
        // 处理数据
        for (int i = 0, size = feeDatas.size(); i < size; i++)
        {
            IData feeData = feeDatas.getData(i);
            String strFeeMode = feeData.getString("FEE_MODE", "");

            int iPresent = 0; // 是否有折扣的标志
            int iPresentFee = 0;
            int iPresentFeeC = 0;
            int isubfee = 0; // sub表实收费用
            int isuboldfee = 0; // sub表应收费用
            String paymoneyCode = "0"; // 付费方式
            isubfee = Integer.parseInt(feeData.getString("FEE", "0"));
            isuboldfee = Integer.parseInt(feeData.getString("OLDFEE", "0"));

            if (strCancelTag == "0")
            {
                iPresentFee = isuboldfee - isubfee;
                iPresentFeeC = isubfee - isuboldfee;
            }
            else
            {
                isuboldfee = 0 - isuboldfee;
                isubfee = 0 - isubfee;
                iPresentFee = isuboldfee - isubfee;
                iPresentFeeC = isubfee - isuboldfee;
            }
            // 取较大付费方式编码
            String strPayMoneyCode = paymoneyCode;
            String strTempPayCode;
            for (int j = 0, m = feePayMoneys.size(); j < m; j++)
            {
                IData feePayMoney = feePayMoneys.getData(j);
                strTempPayCode = feePayMoney.getString("PAY_MONEY_CODE");
                if (Integer.parseInt(strTempPayCode) > Integer.parseInt(strPayMoneyCode))
                {
                    strPayMoneyCode = strTempPayCode;
                }
            }
            paymoneyCode = strPayMoneyCode;

            int iFeeItemTypeCode = feeData.getInt("FEE_TYPE_CODE", 0);
            String strSysCode = data.getString("SYS_CODE", "NGTRAD");
            String strlogId = SeqMgr.getFeeLogId();
            if ("" == strlogId)
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "生成会计凭证号(SEQ_FEE_LOG_ID)失败!");
            String strSubLogId = data.getString("TRADE_ID", "");
            String strpayMoneyCode = paymoneyCode;
            String StrTradeTypeCode = data.getString("TRADE_TYPE_CODE");
            String StrTradeDate = data.getString("TRADE_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            String strDcTag = data.getString("DC_TAG", "D");
            idata.put("SYS_CODE", strSysCode);
            idata.put("LOG_ID", strlogId);
            idata.put("SUB_LOG_ID", strSubLogId);
            idata.put("PARTITION_ID", StrUtil.getAcceptMonthById(strTradeId));
            idata.put("TRADE_EPARCHY_CODE", strEparchyCode);
            idata.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            idata.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            idata.put("OPER_STAFF_ID", CSBizBean.getVisit().getStaffId());
            idata.put("PAY_MONEY_CODE", strpayMoneyCode);
            idata.put("SALE_TYPE_CODE", data.getString("SALE_TYPE_CODE", ""));
            idata.put("PURCHASE_ATTR", data.getString("PURCHASE_ATTR", ""));
            idata.put("TRADE_TYPE_CODE", StrTradeTypeCode);
            idata.put("PAY_MODE_CODE", strPayMoneyCode);
            idata.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));
            idata.put("PROFIT_CEN_ID", data.getString("PROFIT_CEN_ID", ""));
            idata.put("TRADE_DATE", StrTradeDate);
            idata.put("CHECK_NUMBER", data.getString("CHECK_NUMBER", ""));
            idata.put("FEE_TYPE_CODE", strFeeMode);
            idata.put("FEE_ITEM_TYPE_CODE", iFeeItemTypeCode);
            idata.put("CHANNEL_ID", data.getString("DEVELOP_DEPART_ID", ""));
            idata.put("FEE", isubfee);
            idata.put("PRESENT_FEE", iPresentFee);
            idata.put("FORM_FEE", iPresentFee);
            idata.put("SCORE", 0);
            idata.put("DC_TAG", strDcTag);
            idata.put("CANCEL_TAG", strCancelTag);
            idata.put("RES_TYPE", "");
            idata.put("NUMS", "0");
            idata.put("ACC_DATE", data.getString("ACC_DATE", ""));
            idata.put("ACC_TAG", data.getString("ACC_TAG", "0"));
            idata.put("ACC_NO", data.getString("ACC_NO", ""));
            idata.put("REMARK", data.getString("REMARK", ""));
            idata.put("RSRV_TAG1", data.getString("RSRV_TAG1", ""));
            idata.put("RSRV_TAG2", data.getString("RSRV_TAG2", ""));
            idata.put("RSRV_TAG3", data.getString("RSRV_TAG3", ""));
            idata.put("RSRV_DATE1", data.getString("RSRV_DATE1", ""));
            idata.put("RSRV_DATE2", data.getString("RSRV_DATE2", ""));
            idata.put("RSRV_DATE3", data.getString("RSRV_DATE3", ""));
            idata.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
            idata.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
            idata.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
            idata.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
            idata.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
            idata.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
            idata.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
            idata.put("RSRV_NUM1", data.getInt("RSRV_NUM1", 0));
            idata.put("RSRV_NUM2", data.getInt("RSRV_NUM2", 0));
            idata.put("RSRV_NUM3", data.getInt("RSRV_NUM3", 0));

            int m = TradeFeeRegQry.insertFeeBalance(idata);
            if (m < 1)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "对表[TF_A_FEE_BALANCE]添加资料失败!");
            }
        }
    }

    /**
     * 押金转预存返销
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData tradeCancelForAcct(IData input) throws Exception
    {
        // 查询历史台账
        IDataUtil.chkParam(input, "TRADE_ID");
        IDataUtil.chkParam(input, "CANCEL_TAG");

        String tradeId = input.getString("TRADE_ID");
        String cancelTag = input.getString("CANCEL_TAG", "0");
        IData historyTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, cancelTag, null);
        if (IDataUtil.isEmpty(historyTrade))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取您输入的TRADE_ID历史表资料:没有该笔业务！");
        }
        IData hisTrade = historyTrade;
        IData pubData = new DataMap();
        pubData.put("TRADE_ID", tradeId);
        pubData.put("SYS_TIME", SysDateMgr.getSysDate());
        pubData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("LOGIN_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        // 返销台账
        CancelTradeBean cancelBean = new CancelTradeBean();
        cancelBean.modifyHisTradeAndTradeStaff(hisTrade, pubData);
        this.cancelTradeReg(hisTrade);
        String foregift = hisTrade.getString("FOREGIFT", "0");
        String userId = hisTrade.getString("USER_ID", "0");
        if (!"0".equals(foregift) && !"-1".equals(userId))
        {
            UndoModifyUserForegift(hisTrade);
        }
        pubData.clear();
        return pubData;
    }

    public IData tradeRegForAcct(IData input) throws Exception
    {
        // 插入台账数据准备开始 ---------------------------------------------------------
        IData param = new DataMap();
        IDataset dataset = new DatasetList();
        dataset = input.getDataset("FEE_DATA_SET");
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData data = new DataMap();
            data = dataset.getData(i);
            IDataUtil.chkParam(data, "USER_ID");
            IDataUtil.chkParam(data, "TRADE_ID");
            IDataUtil.chkParam(data, "TRADE_TYPE_CODE");
            IDataUtil.chkParam(data, "CANCEL_TAG");
            String userId = data.getString("USER_ID", "");
            // 判断用户资料是否存在
//            IData userData = UcaInfoQry.qryUserInfoByUserId(userId);
            IData userData=null;
            IDataset userDatas =UserInfoQry.queryAllUserBySerialNumberOrUserId(null, userId);
            if (IDataUtil.isEmpty(userDatas))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取业务所需用户资料出错！");
            }
            userData=userDatas.getData(0);
            
            // 客户资料
            IData custData = UcaInfoQry.qryCustInfoByCustId(userData.getString("CUST_ID", ""));
            if (IDataUtil.isEmpty(custData))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取业务所需客户资料出错！");
            }
            // 产品信息
            IData proData = UcaInfoQry.qryMainProdInfoByUserId(userId);
            if (IDataUtil.isEmpty(proData))
            {
//                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户拥有的产品信息未找到!");
            	proData=new DataMap();
            }
            // 业务类型
            IData typeSet = UTradeTypeInfoQry.getTradeType(data.getString("TRADE_TYPE_CODE", ""), CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isEmpty(typeSet))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_200, data.getString("TRADE_TYPE_CODE", ""));
            }
            /*
             * // 账户资料 if ("1".equals(typeSet.getData(0).getString("INFO_TAG_SET", "") .substring(2, 3))) // 第3位：获取帐户资料
             * { IDataset payRelation = PayRelaInfoQry .getPayRelationByUserID(userId); if
             * (IDataUtil.isEmpty(payRelation)) { // 账户没找到 再找最后一位的默认账户 payRelation = AcctInfoQry
             * .getDefaultAcctInfoByUserId(userId); if (IDataUtil.isEmpty(payRelation)) {
             * CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户无默认的付费账户!"); } } // 找到账户信息 String acctId =
             * payRelation.getData(0).getString("ACCT_ID", ""); param.put("ACCT_ID", acctId); IDataset acctDatas =
             * TradeFeeRegQry.getAcctInfoByCustId(param); if (IDataUtil.isEmpty(acctDatas)) {
             * CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户账户信息资料不存在!"); } }
             */
            // 插入台账数据准备结束
            // ---------------------------------------------------------------------------------
            // 登记台账 和 是否更新 押金
            tradeRegForTradeFeesub(data, userData, custData, proData);

            // 统计日报接口
            insFeeBalance(data);
        }
        param.clear();
        return param;
    }

    // 返销登记台账 jdbc

    /**
     * 拼台账 修改资料
     * 
     * @param data
     * @param acctData
     * @throws Exception
     */
    public void tradeRegForTradeFeesub(IData data, IData userData, IData custData, IData proData) throws Exception
    {
        IData param = new DataMap();
        int operFee = 0, foregift = 0, advancePay = 0, fee = 0;
        String oldFee, feeMode;
        IDataUtil.chkParam(data, "TRADE_ID");
        IDataUtil.chkParam(data, "FEE");
        IDataUtil.chkParam(data, "FEE_MODE");
        IDataUtil.chkParam(data, "FEE_TYPE_CODE");
        // 处理费用
        if (StringUtils.isEmpty(data.getString("OLDFEE", "")))
        {
            oldFee = data.getString("OLDFEE", "0");
        }
        else
        {
            oldFee = data.getString("FEE", "0");
        }
        fee = Integer.parseInt(data.getString("FEE", ""));
        feeMode = data.getString("FEE_MODE", "");

        // -------------插入台账----------------------------------------------------------------------------
        String tradeId = data.getString("TRADE_ID", "");

        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("USER_ID", data.getString("USER_ID", ""));
        param.put("FEE_MODE", feeMode);
        param.put("FEE_TYPE_CODE", data.getString("FEE_TYPE_CODE", ""));
        param.put("OLDFEE", oldFee);
        param.put("FEE", data.getString("FEE", ""));
        param.put("CHARGE_ID", data.getString("CHARGE_ID", ""));
        int discntGiftId = Integer.parseInt(data.getString("DISCNT_GIFT_ID", "0"));
        param.put("DISCNT_GIFT_ID", discntGiftId);
        int limitMoney = Integer.parseInt(data.getString("LIMIT_MONEY", "0"));
        param.put("LIMIT_MONEY", limitMoney);
        int months = Integer.parseInt(data.getString("MONTHS", "0"));
        param.put("MONTHS", months);
        param.put("EFFICET_DATE", data.getString("EFFICET_DATE", ""));
        param.put("UPDATE_TIME", data.getString("UPDATE_TIME", ""));
        param.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID", ""));
        param.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID", ""));
        param.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
        param.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
        param.put("RSRV_STR8", data.getString("RSRV_STR8", ""));
        param.put("RSRV_STR9", data.getString("RSRV_STR9", ""));
        param.put("RSRV_STR10", data.getString("RSRV_STR10", ""));
        param.put("REMARK", data.getString("REMARK", ""));

        int i = TradeFeeRegQry.regForTradeFeesub(param);
        if (i < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "登记台账表 [TF_B_TRADEFEE_SUB]失败!");
        }

        // 插入主台账数据准备--------------------------------------------------------------------------------------
        if ("0".equals(feeMode))
            operFee += fee;
        else if ("1".equals(feeMode))
            foregift += fee;
        else if ("2".equals(feeMode))
            advancePay += fee;
        int allMoney = operFee + foregift + advancePay;
        String feeState, feeTime, feeStaffId;
        if (allMoney != 0)
        {
            feeState = "1";
            feeTime = SysDateMgr.getSysDate();
            feeStaffId = data.getString("TRADE_STAFF_ID", "");
        }
        else
        {
            // 无费用时，费用相关属性清空
            feeState = "0";
            feeTime = "";
            feeStaffId = "";
        }
        param.clear();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("BATCH_ID", data.getString("BATCH_ID", ""));
        String orderid = SeqMgr.getOperId();
        param.put("ORDER_ID", orderid);
        param.put("PROD_ORDER_ID", data.getString("PROD_ORDER_ID", ""));
        param.put("BPM_ID", data.getString("BPM_ID", ""));
        param.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        int priority = Integer.parseInt(data.getString("PRIORITY", "0"));
        param.put("PRIORITY", priority);
        param.put("SUBSCRIBE_STATE", "0");
        param.put("SUBSCRIBE_TYPE", "0");
        param.put("CAMPN_ID", "");
        param.put("NEXT_DEAL_TAG", "0");
        param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0"));
        param.put("CUST_ID", custData.getString("CUST_ID", ""));
        param.put("CUST_NAME", custData.getString("CUST_NAME", ""));
        param.put("USER_ID", data.getString("USER_ID", ""));
        param.put("ACCT_ID", data.getString("ACCT_ID", ""));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        param.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));
        param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));
        param.put("CITY_CODE", data.getString("CITY_CODE", ""));
        param.put("PRODUCT_ID", proData.getString("PRODUCT_ID", ""));
        param.put("BRAND_CODE", proData.getString("BRAND_CODE", ""));
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        param.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()));
        param.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
        param.put("TERM_IP", data.getString("TERM_IP", ""));
        param.put("OPER_FEE", operFee);
        param.put("FOREGIFT", foregift);
        param.put("ADVANCE_PAY", advancePay);
        param.put("INVOICE_NO", data.getString("INVOICE_NO", ""));
        param.put("FEE_STATE", feeState);
        param.put("FEE_TIME", feeTime);
        param.put("FEE_STAFF_ID", feeStaffId);
        param.put("PROCESS_TAG_SET", data.getString("PROCESS_TAG_SET", " "));
        param.put("OLCOM_TAG", data.getString("OLCOM_TAG", "0"));
        param.put("FINISH_DATE", SysDateMgr.getSysDate());
        param.put("EXEC_TIME", SysDateMgr.getSysDate());
        param.put("EXEC_ACTION", data.getString("EXEC_ACTION", ""));
        param.put("EXEC_RESULT", data.getString("EXEC_RESULT", ""));
        param.put("EXEC_DESC", data.getString("EXEC_DESC", ""));
        param.put("CANCEL_TAG", data.getString("CANCEL_TAG", "0"));
        param.put("CANCEL_DATE", data.getString("CANCEL_DATE", ""));
        param.put("CANCEL_STAFF_ID", data.getString("CANCEL_STAFF_ID", ""));
        param.put("CANCEL_DEPART_ID", data.getString("CANCEL_DEPART_ID", ""));
        param.put("CANCEL_CITY_CODE", data.getString("CANCEL_STAFF_ID", ""));
        param.put("CANCEL_EPARCHY_CODE", data.getString("CANCEL_EPARCHY_CODE", ""));
        param.put("UPDATE_TIME", data.getString("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")));
        param.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID", ""));
        param.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        param.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
        param.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
        param.put("RSRV_STR8", data.getString("RSRV_STR8", ""));
        param.put("RSRV_STR9", data.getString("RSRV_STR9", ""));
        param.put("RSRV_STR10", data.getString("RSRV_STR10", ""));
        param.put("REMARK", data.getString("REMARK", "Tradefeereg"));

        param.put("CUST_ID_B", data.getString("CUST_ID_B", ""));
        param.put("USER_ID_B", data.getString("USER_ID_B", ""));
        param.put("ACCT_ID_B", data.getString("ACCT_ID_B", ""));
        param.put("SERIAL_NUMBER_B", data.getString("SERIAL_NUMBER_B", ""));
        param.put("CUST_CONTACT_ID", data.getString("CUST_CONTACT_ID", ""));
        param.put("SERV_REQ_ID", data.getString("SERV_REQ_ID", ""));
        param.put("INTF_ID", data.getString("INTF_ID", ""));

        // wide
        param.put("PF_TYPE", data.getString("PF_TYPE", "0"));
        param.put("IS_NEED_HUMANCHECK", data.getString("IS_NEED_HUMANCHECK", ""));
        param.put("FREE_RESOURCE_TAG", data.getString("FREE_RESOURCE_TAG", ""));
        int j = TradeFeeRegQry.regForMainTrade(param);
        if (j < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "登记台账表 [TF_B_TRADE]失败!");
        }
        // ----------------------------------------------------------------------
        param.clear();
        param.put("ORDER_ID", orderid);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("BATCH_ID", data.getString("BATCH_ID", ""));
        param.put("BATCH_COUNT", data.getString("BATCH_COUNT", ""));
        param.put("SUCC_TOTAL", -1);
        param.put("FAIL_TOTAL", -1);
        param.put("ORDER_TYPE_CODE", "-1");
        param.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        param.put("PRIORITY", priority);
        param.put("ORDER_STATE", "0");
        param.put("NEXT_DEAL_TAG", "0");
        param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0"));
        param.put("CUST_ID", custData.getString("CUST_ID", ""));
        param.put("CUST_NAME", custData.getString("CUST_NAME", ""));
        param.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE", ""));
        param.put("PSPT_ID", data.getString("PSPT_ID", ""));
        param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));
        param.put("CITY_CODE", data.getString("CITY_CODE", ""));
        param.put("AUTH_CODE", "");
        param.put("ACTOR_NAME", "");
        param.put("ACTOR_PHONE", "");
        param.put("ACTOR_PSPT_TYPE_CODE", "");
        param.put("ACTOR_PSPT_ID", "");
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        param.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()));
        param.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
        param.put("TERM_IP", data.getString("TERM_IP", ""));
        param.put("OPER_FEE", operFee);
        param.put("FOREGIFT", foregift);
        param.put("ADVANCE_PAY", advancePay);
        param.put("INVOICE_NO", data.getString("INVOICE_NO", ""));
        param.put("FEE_STATE", feeState);
        param.put("FEE_TIME", feeTime);
        param.put("FEE_STAFF_ID", feeStaffId);
        param.put("PROCESS_TAG_SET", data.getString("PROCESS_TAG_SET", " "));
        param.put("FINISH_DATE", "");
        param.put("EXEC_TIME", SysDateMgr.getSysDate());
        param.put("EXEC_ACTION", data.getString("EXEC_ACTION", ""));
        param.put("EXEC_RESULT", data.getString("EXEC_RESULT", ""));
        param.put("EXEC_DESC", data.getString("EXEC_DESC", ""));
        param.put("CUST_IDEA", "");
        param.put("HQ_TAG", "");
        param.put("DECOMPOSE_RULE_ID", "");
        param.put("DISPATCH_RULE_ID", "");
        param.put("CUST_CONTACT_ID", "");
        param.put("SERV_REQ_ID", "");
        param.put("CONTRACT_ID", "");
        param.put("SOLUTION_ID", "");
        param.put("CANCEL_TAG", data.getString("CANCEL_TAG", "0"));
        param.put("CANCEL_DATE", data.getString("CANCEL_DATE", ""));
        param.put("CANCEL_STAFF_ID", data.getString("CANCEL_STAFF_ID", ""));
        param.put("CANCEL_DEPART_ID", data.getString("CANCEL_DEPART_ID", ""));
        param.put("CANCEL_CITY_CODE", data.getString("CANCEL_STAFF_ID", ""));
        param.put("CANCEL_EPARCHY_CODE", data.getString("CANCEL_EPARCHY_CODE", ""));
        param.put("UPDATE_TIME", data.getString("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")));
        param.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID", ""));
        param.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        param.put("REMARK", data.getString("REMARK", "Tradefeereg"));
        param.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
        param.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
        param.put("RSRV_STR8", data.getString("RSRV_STR8", ""));
        param.put("RSRV_STR9", data.getString("RSRV_STR9", ""));
        param.put("RSRV_STR10", data.getString("RSRV_STR10", ""));
        param.put("ORDER_INSTANCE_STATE", "");
        param.put("APP_TYPE", "");
        param.put("PRIORITY_TYPE", "");
        param.put("TRADE_DBSRCNAMES", "");
        param.put("IS_NEED_HUMANCHECK", data.getString("IS_NEED_HUMANCHECK", ""));
        param.put("ORDER_KIND_CODE", data.getString("ORDER_KIND_CODE", "0"));
        param.put("SUBSCRIBE_TYPE", "0");
        int h = TradeFeeRegQry.regForMainOrder(param);
        if (h < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "登记台账表 [TF_B_ORDER]失败!");
        }
        // ----------------------------------------------------------------------
        param.clear();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("PAY_MONEY_CODE", data.getString("PAY_MONEY_CODE", "0"));
        param.put("MONEY", allMoney);
        param.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        param.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
        param.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
        param.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
        param.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", data.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", data.getString("RSRV_STR6", ""));
        param.put("RSRV_STR7", data.getString("RSRV_STR7", ""));
        param.put("RSRV_STR8", data.getString("RSRV_STR8", ""));
        param.put("RSRV_STR9", data.getString("RSRV_STR9", ""));
        param.put("RSRV_STR10", data.getString("RSRV_STR10", ""));
        param.put("REMARK", data.getString("REMARK", ""));
        int k = TradeFeeRegQry.regForPayMoney(param);
        if (k < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "登记台账表 [TF_B_TRADEFEE_PAYMONEY]失败!");
        }
        // --------------------------------------------------------------------------------------
        if (foregift != 0)
        {
            String userId = data.getString("USER_ID", "");
            int tradeTypeCode = data.getInt("TRADE_TYPE_CODE");
            String custNameOrg = custData.getString("CUST_NAME", "");// 客户名称
            String inDate = data.getString("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            String execTime = data.getString("EXEC_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            String remark = data.getString("REMARK", "");
            String psptIdOrg, outDate;
            psptIdOrg = custData.getString("PSPT_ID", "");
            String PrintCustName = custNameOrg;
            String PrintPsptId = psptIdOrg;
            IDataset tradefeeSub = TradeFeeRegQry.getTradeFeesubByTradeId(tradeId);
            if (IDataUtil.isNotEmpty(tradefeeSub))
            {
                for (int l = 0, size = tradefeeSub.size(); l < size; l++)
                {
                    IData para = new DataMap();
                    para = tradefeeSub.getData(l);
                    int foregiftCode = para.getInt("FEE_TYPE_CODE");
                    int foregiftValue = para.getInt("FEE", 0);
                    String update_staff_id = para.getString("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    String update_depart_id = para.getString("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    if (foregiftValue > 0)
                    { // foregiftValue小于0时为清退押金
                        outDate = "";
                    }
                    else
                    {
                        outDate = execTime;
                    }
                    IData foregiftData = TradeFeeRegQry.getUserForegift(userId, foregiftCode + "");
                    if (IDataUtil.isNotEmpty(foregiftData))
                    {	
                    	int oldMoney = foregiftData.getInt("MONEY",0);
                        para.clear();
                        para.put("MONEY", String.valueOf(foregiftValue) );
                        para.put("FOREGIFT_IN_DATE", inDate);
                        para.put("FOREGIFT_OUT_DATE", outDate);
                        para.put("UPDATE_TIME", execTime);
                        para.put("CUST_NAME", PrintCustName);
                        para.put("PSPT_ID", PrintPsptId);
                        para.put("USER_ID", userId);
                        para.put("FOREGIFT_CODE", foregiftCode);
                        para.put("UPDATE_STAFF_ID", update_staff_id);
                        para.put("UPDATE_DEPART_ID", update_depart_id);
                        para.put("REMARK", remark);
                        para.put("RSRV_STR5", String.valueOf(oldMoney));
                        
                        int m = TradeFeeRegQry.updataMoneyNum(para);
                        if (m < 1)
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "修改资料表 [TF_F_USER_FOREGIFT]失败!");
                        }
                        if (tradeTypeCode == 8006)
                        { // /押金转预存处理
                            IDataset otherDatas = TradeFeeRegQry.getUserOtherServBuf(userId, foregiftCode + "");
                            if (IDataUtil.isNotEmpty(otherDatas))
                            {
                                int iMoney = 0;
                                for (int n = 0, count = otherDatas.size(); n < count; n++)
                                {
                                    iMoney = iMoney - otherDatas.getData(n).getInt("RSRV_NUM2", 0);
                                }

                                if (iMoney == foregiftValue)
                                {
                                    /** @daoinvoke=UserDom::USER_OTHERSERV::TF_F_USER_OTHERSERV::UPD_TAG_TIME_HAIN */
                                    para.clear();
                                    para.put("USER_ID", userId);
                                    para.put("SERVICE_MODE", "FG");
                                    para.put("PROCESS_TAG", "1");
                                    para.put("RSRV_NUM1", foregiftCode);
                                    para.put("STAFF_ID", update_staff_id);
                                    para.put("DEPART_ID", update_depart_id);
                                    para.put("RSRV_STR3", tradeId); // 押金转预存流水号
                                    para.put("REMARK", "已经办理押金转预存");
                                    TradeFeeRegQry.updataUserOtherServ(para);
                                }
                                else
                                {
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户其他服务资料金额总数不对!");
                                }
                            }
                        }
                    }
                    else
                    { // 就做添加
                        para.clear();
                        para.put("USER_ID", userId);
                        para.put("FOREGIFT_CODE", foregiftCode);
                        para.put("MONEY", foregiftValue);
                        para.put("CUST_NAME", PrintCustName);
                        para.put("PSPT_ID", PrintPsptId);
                        para.put("FOREGIFT_IN_DATE", inDate);
                        para.put("FOREGIFT_OUT_DATE", outDate);
                        para.put("UPDATE_TIME", execTime);
                        para.put("UPDATE_STAFF_ID", update_staff_id);
                        para.put("UPDATE_DEPART_ID", update_depart_id);
                        para.put("REMARK", remark);
                        para.put("RSRV_NUM1", -1);
                        para.put("RSRV_NUM2", -1);
                        para.put("RSRV_NUM3", -1);
                        para.put("RSRV_NUM4", -1);
                        para.put("RSRV_NUM5", -1);
                        para.put("RSRV_STR1", "");
                        para.put("RSRV_STR2", "");
                        para.put("RSRV_STR3", "");
                        para.put("RSRV_STR4", "");
                        para.put("RSRV_STR5", "");
                        para.put("RSRV_DATE1", "");
                        para.put("RSRV_DATE2", "");
                        para.put("RSRV_DATE3", "");
                        para.put("RSRV_TAG1", "");
                        para.put("RSRV_TAG2", "");
                        para.put("RSRV_TAG3", "");
                        TradeFeeRegQry.insUserForegift(para);
                    }
                }
            }
        }
        
        //插入tf_bh_trade_staff表        
        param.clear();
        String day = SysDateMgr.getCurDay();
        param.put("TRADE_ID", tradeId);
        param.put("DAY", day);

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO tf_bh_trade_staff");
        sql.append(" (trade_id, accept_month, batch_id, order_id, prod_order_id, bpm_id, campn_id, trade_type_code, priority, subscribe_type, subscribe_state, next_deal_tag, in_mode_code, cust_id, cust_name, user_id, acct_id, serial_number, net_type_code, eparchy_code, city_code, product_id, brand_code, cust_id_b, user_id_b, acct_id_b, serial_number_b, cust_contact_id, serv_req_id, intf_id, accept_date, trade_staff_id, trade_depart_id, trade_city_code, trade_eparchy_code, term_ip, oper_fee, foregift, advance_pay, invoice_no, fee_state, fee_time, fee_staff_id, process_tag_set, olcom_tag, finish_date, exec_time, exec_action, exec_result, exec_desc, cancel_tag, cancel_date, cancel_staff_id, cancel_depart_id, cancel_city_code, cancel_eparchy_code, update_time, update_staff_id, update_depart_id, remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10,day) ");
        sql.append("SELECT trade_id, accept_month, batch_id, order_id, prod_order_id, bpm_id, campn_id, trade_type_code, priority, subscribe_type, subscribe_state, next_deal_tag, in_mode_code, cust_id, cust_name, user_id, acct_id, serial_number, net_type_code, eparchy_code, city_code, product_id, brand_code, cust_id_b, user_id_b, acct_id_b, serial_number_b, cust_contact_id, serv_req_id, intf_id, accept_date, trade_staff_id, trade_depart_id, trade_city_code, trade_eparchy_code, term_ip, oper_fee, foregift, advance_pay, invoice_no, fee_state, fee_time, fee_staff_id, process_tag_set, olcom_tag, finish_date, exec_time, exec_action, exec_result, exec_desc, cancel_tag, cancel_date, cancel_staff_id, cancel_depart_id, cancel_city_code, cancel_eparchy_code, update_time, update_staff_id, update_depart_id, remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10 ");
        sql.append(",:DAY ");
        sql.append("FROM tf_b_trade ");
        sql.append("WHERE trade_id= :TRADE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        
    }

    /**
     * 返销预存
     * 
     * @param hisTrade
     * @throws Exception
     */
    public void UndoModifyUserForegift(IData hisTrade) throws Exception
    {
        String tradeId = hisTrade.getString("TRADE_ID");
        String userId = hisTrade.getString("USER_ID", "");
        IData datas = new DataMap();
        IDataset tradeFeeSub = TradeFeeRegQry.getTradeFeesubByTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeFeeSub))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "需要返销的费用台账数据为空!");
        }
        for (int i = 0, size = tradeFeeSub.size(); i < size; i++)
        {
            IData feeSub = tradeFeeSub.getData(i);
            IData userForegift = TradeFeeRegQry.getUserForegift(userId, feeSub.getString("FEE_TYPE_CODE", ""));
            if (IDataUtil.isNotEmpty(userForegift))
            {
                datas.clear();
                datas.put("MONEY", feeSub.get("FEE"));
                datas.put("USER_ID", userId);
                datas.put("FOREGIFT_CODE", feeSub.get("FEE_TYPE_CODE"));
                int m = TradeFeeRegQry.updataMoneyCancel(datas);
                if (m < 1)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, " 返销用户押金表资料[TF_F_USER_FOREGIFT]失败!");
                }
            }
        }
        datas.clear();
        datas.put("USER_ID", userId);
        datas.put("SERVICE_MODE", "FG");
        datas.put("PROCESS_TAG", "0");
        datas.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        datas.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        datas.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        datas.put("RSRV_STR3", tradeId); // 清退流水号
        datas.put("REMARK", "押金转预存返销");
        int j = TradeFeeRegQry.updateOtherServ(datas);
        if (j < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, " 返销用户其他资料表资料[TF_F_USER_OTHERSERV]失败!");
        }
    }
}
