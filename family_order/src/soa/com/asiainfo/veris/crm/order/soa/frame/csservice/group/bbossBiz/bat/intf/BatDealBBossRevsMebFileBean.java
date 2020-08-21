
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;

/**
 * 行业应用卡和一点支付，反向批量处理 BBOSS下发配合省成员[行业应用卡] BBOSS下发配合省成员[一点支付] BBOSS下发配合省反馈成员开通结果[行业应用卡] BBOSS下发配合省反馈成员开通结果[一点支付]
 * BBOSS下发配合省反馈成员确认结果[一点支付]
 * 
 * @author fanti3
 */
public class BatDealBBossRevsMebFileBean
{

    // 一点支付产品编码
    private final static String PRO_SPEC_CODE_YDZF = "99903";

    // 行业应用卡产品编码
    private final static String PRO_SPEC_CODE_HYYYK = "911601";

    private static void dealBatDealInfo(IData data, IData batDealParam) throws Exception
    {

        // 1-3 处理TF_B_TRADE_BATDEAL公用信息
        dealCommonBatDealInfo(data, batDealParam);

        // 1-2 处理BBOSS成员附件信息
        dealBBossMebFileInfo(data, batDealParam);
    }

    /**
     * 处理批量批次号信息
     * 
     * @param data
     * @param memParam
     * @throws Exception
     */
    private static void dealBatInfo(IData data, IData batDealParam, IData batTaskParam, IData batMainParam) throws Exception
    {

        // 处理批量明细表信息
        dealBatDealInfo(data, batDealParam);

        // 根据业务流水IBSYSID（DATA12）查询批量明细表信息
        IDataset batDealInfo = BatTradeInfoQry.batchDetialQueryByData12(batDealParam);

        // 获取批量任务ID、批次号、操作流水ID
        if (IDataUtil.isNotEmpty(batDealInfo))
        {
            // 处理批量明细表信息
            batDealParam.put("BATCH_TASK_ID", ((DataMap) batDealInfo.get(0)).getString("BATCH_TASK_ID"));
            batDealParam.put("BATCH_ID", ((DataMap) batDealInfo.get(0)).getString("BATCH_ID"));
            batDealParam.put("OPERATE_ID", SeqMgr.getBatchId());
        }
        else
        {

            // 无批量明细表信息则，生成系统生产
            batDealParam.put("BATCH_TASK_ID", SeqMgr.getBatchId());
            batDealParam.put("BATCH_ID", SeqMgr.getBatchId());
            batDealParam.put("OPERATE_ID", SeqMgr.getBatchId());

            // TF_B_TRADE_BAT的BATCH_COUNT加1
            BatDealBean bean = new BatDealBean();
            bean.addBatchCount(batDealParam);
        }

        // 处理批量任务表信息
        dealBatTaskInfo(data, batDealParam, batTaskParam);

        // 处理批量主表信息
        dealBatMainInfo(data, batDealParam, batMainParam);

    }

    private static void dealBatMainInfo(IData data, IData batDealParam, IData batMainParam) throws Exception
    {

        // 1-1 初始化用户信息
        String tradeStaffId = IDataUtil.getMandaData(data, "TRADE_STAFF_ID");
        String tradeDepartId = IDataUtil.getMandaData(data, "TRADE_DEPART_ID");
        String tradeCityCode = IDataUtil.getMandaData(data, "TRADE_CITY_CODE");
        String tradeEparchyCode = IDataUtil.getMandaData(data, "TRADE_EPARCHY_CODE");

        // 处理批量主表信息
        batMainParam.put("BATCH_TASK_ID", batDealParam.getString("BATCH_TASK_ID"));
        batMainParam.put("BATCH_ID", batDealParam.getString("BATCH_ID"));
        batMainParam.put("BATCH_OPER_TYPE", batDealParam.getString("BATCH_OPER_TYPE"));
        batMainParam.put("BATCH_COUNT", "1");
        batMainParam.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        batMainParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        batMainParam.put("STAFF_ID", tradeStaffId);
        batMainParam.put("DEPART_ID", tradeDepartId);
        batMainParam.put("CITY_CODE", tradeCityCode);
        batMainParam.put("EPARCHY_CODE", tradeEparchyCode);
        batMainParam.put("TERM_IP", "127.0.0.1");
        batMainParam.put("IN_MODE_CODE", "v");
        batMainParam.put("REMOVE_TAG", "0");
        batMainParam.put("ACTIVE_FLAG", "0");
        batMainParam.put("AUDIT_STATE", "0");
        batMainParam.put("AUDIT_REMARK", "批量任务名称");
        batMainParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batDealParam.getString("BATCH_TASK_ID")));
    }

    private static void dealBatTaskInfo(IData data, IData batDealParam, IData batTaskParam) throws Exception
    {

        // 1-1 初始化用户信息
        String tradeStaffId = IDataUtil.getMandaData(data, "TRADE_STAFF_ID");
        String tradeDepartId = IDataUtil.getMandaData(data, "TRADE_DEPART_ID");
        String tradeCityCode = IDataUtil.getMandaData(data, "TRADE_CITY_CODE");
        String tradeEparchyCode = IDataUtil.getMandaData(data, "TRADE_EPARCHY_CODE");

        // 处理批量任务表信息
        batTaskParam.put("BATCH_TASK_ID", batDealParam.getString("BATCH_TASK_ID"));
        batTaskParam.put("BATCH_TASK_NAME", batDealParam.getString("BATCH_TASK_NAME"));
        batTaskParam.put("BATCH_OPER_CODE", batDealParam.getString("BATCH_OPER_TYPE"));
        batTaskParam.put("BATCH_OPER_NAME", batDealParam.getString("BATCH_OPER_NAME"));
        batTaskParam.put("START_DATE", SysDateMgr.getSysTime());
        batTaskParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        batTaskParam.put("CREATE_TIME", SysDateMgr.getSysDate());
        batTaskParam.put("CREATE_STAFF_ID", tradeStaffId);
        batTaskParam.put("CREATE_DEPART_ID", tradeDepartId);
        batTaskParam.put("CREATE_CITY_CODE", tradeCityCode);
        batTaskParam.put("CREATE_EPARCHY_CODE", tradeEparchyCode);
        batTaskParam.put("SMS_FLAG", "0");
        batTaskParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batDealParam.getString("BATCH_TASK_ID")));
    }

    /**
     * 行业应用卡
     * 
     * @param isNewMeb
     * @param data
     * @param memParam
     */
    private static void dealBBossHYYYKMebFile(String isNewMeb, IData data, IData batDealParam) throws Exception
    {

        if (!"".equals(isNewMeb))
        {
            // 业务主办省，新开卡信息非空
            batDealParam.put("BATCH_OPER_TYPE", "BATOPENHYYYKINTF");
            batDealParam.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
            batDealParam.put("BATCH_OPER_NAME", "BBOSS下发配合省反馈成员开通结果[行业应用卡]");
            batDealParam.put("DATA2", data.getString("BUSISTATUS", ""));
            batDealParam.put("DATA3", data.getString("ISNEWUSER", ""));
            batDealParam.put("DATA13", data.getString("ERRDESC", ""));
            batDealParam.put("DATA14", data.getString("NEWUSERFAILDESC", ""));
        }
        else
        {
            // 业务配合省
            batDealParam.put("BATCH_OPER_TYPE", "BATADDHYYYKMEMINTF");
            batDealParam.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
            batDealParam.put("BATCH_OPER_NAME", "BBOSS下发配合省成员[行业应用卡]");
            batDealParam.put("DATA13", data.getString("FEEPLAN", ""));
            batDealParam.put("DATA14", data.getString("PRODINFO", ""));
            batDealParam.put("DATA2", data.getString("NEWUSERCOUNT", ""));
            batDealParam.put("DATA3", data.getString("OPERCODE", ""));
            batDealParam.put("DATA7", IDataUtil.getMandaData(data, "INFO_TYPE"));
        }
    }

    /**
     * 处理BBOSS批量成员附件
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset dealBBossMebFile(IData data) throws Exception
    {

        // 1-1 初始化批量明细表信息
        IData batDealParam = new DataMap();

        // 1-2 初始化批量任务表信息
        IData batTaskParam = new DataMap();

        // 1-3 初始化批量主表信息
        IData batMainParam = new DataMap();

        // 2 处理批量表insert信息，优先处理批量明细表信息，后处理批量任务表和批量主表信息
        dealBatInfo(data, batDealParam, batTaskParam, batMainParam);

        // 3-1 插批量明细表信息
        if (IDataUtil.isEmpty(batDealParam))
        {
            CSAppException.apperr(BatException.CRM_BAT_90);
        }
        Dao.insert("TF_B_TRADE_BATDEAL", batDealParam, Route.getJourDb(Route.CONN_CRM_CG));

        // 3-2 插批量任务表信息
        if (IDataUtil.isEmpty(batTaskParam))
        {
            CSAppException.apperr(BatException.CRM_BAT_92);
        }
        Dao.insert("TF_B_TRADE_BAT_TASK", batTaskParam, Route.getJourDb(Route.CONN_CRM_CG));

        // 3-3 插批量主表信息
        if (IDataUtil.isEmpty(batMainParam))
        {
            CSAppException.apperr(BatException.CRM_BAT_91);
        }
        Dao.insert("TF_B_TRADE_BAT", batMainParam, Route.getJourDb(Route.CONN_CRM_CG));

        // 4 返回处理成功
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "处理成员成功");

        return IDataUtil.idToIds(result);
    }

    /**
     * 处理成员附件信息
     * 
     * @param data
     * @param memParam
     * @throws Exception
     */
    private static void dealBBossMebFileInfo(IData data, IData batDealParam) throws Exception
    {
        // 用户状态信息
        String userState = data.getString("USERSTATUS", "");
        // 代付关系处理是否成功
        String isSuccessPay = data.getString("CENTROLPAYSTATUS", "");
        // 是否新开卡成员
        String isNewMeb = data.getString("ISNEWUSER", "");

        // 产品订购关系
        String productOfferId = IDataUtil.getMandaData(data, "RSRV_STR4");

        IDataset merchpInfos = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferId, null);

        if (IDataUtil.isEmpty(merchpInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_914, productOfferId);
        }

        String productSpecCode = merchpInfos.getData(0).getString("PRODUCT_SPEC_CODE");

        // 处理一点支付信息
        if ((PRO_SPEC_CODE_YDZF).equals(productSpecCode))
        {

            dealBBossYDZFMebFile(userState, isSuccessPay, data, batDealParam);
        }

        // 处理行业应用卡信息
        if ((PRO_SPEC_CODE_HYYYK).equals(productSpecCode))
        {

            dealBBossHYYYKMebFile(isNewMeb, data, batDealParam);
        }
    }

    /**
     * 一点支付
     * 
     * @param userState
     * @param isSuccessPay
     * @param data
     * @param memParam
     */
    private static void dealBBossYDZFMebFile(String userState, String isSuccessPay, IData data, IData batDealParam) throws Exception
    {

        if (!"".equals(userState))
        {
            // 业务主办省，用户状态非空
            batDealParam.put("BATCH_OPER_TYPE", "BATCONFIRMYDZFINTF");
            batDealParam.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
            batDealParam.put("BATCH_OPER_NAME", "BBOSS下发配合省反馈成员确认结果[一点支付]");
            batDealParam.put("DATA7", data.getString("NAMEMATCH", ""));
            batDealParam.put("DATA13", data.getString("CURRFEEPLAN", ""));
            batDealParam.put("DATA9", data.getString("USERSTATUS", ""));
        }
        else if (!"".equals(isSuccessPay))
        {
            // 业务主办省，代付关系非空
            batDealParam.put("BATCH_OPER_TYPE", "BATOPENYDZFINTF");
            batDealParam.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
            batDealParam.put("BATCH_OPER_NAME", "BBOSS下发配合省反馈成员开通结果[一点支付]");
            batDealParam.put("DATA13", data.getString("FEEPLAN", ""));
            batDealParam.put("DATA8", data.getString("PAYTYPE", ""));
            batDealParam.put("DATA9", data.getString("PAYAMOUNT", ""));
            batDealParam.put("DATA10", data.getString("EFFRULE", ""));
            batDealParam.put("DATA2", data.getString("CENTROLPAYSTATUS", ""));
            batDealParam.put("DATA14", data.getString("ACCOUNTNAME", ""));
            batDealParam.put("DATA15", data.getString("FAILDESC", ""));
            batDealParam.put("DATA3", data.getString("ISNEWUSER", ""));
            batDealParam.put("DATA16", data.getString("NEWUSERFAILDESC", ""));
        }
        else
        {
            // 业务配合省
            batDealParam.put("BATCH_OPER_TYPE", "BATADDYDZFMEMINTF");
            batDealParam.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
            batDealParam.put("BATCH_OPER_NAME", "BBOSS下发配合省成员[一点支付]");
            batDealParam.put("DATA13", data.getString("FEEPLAN", ""));
            batDealParam.put("DATA8", data.getString("PAYTYPE", ""));
            batDealParam.put("DATA9", data.getString("PAYAMOUNT", ""));
            batDealParam.put("DATA10", data.getString("EFFRULE", ""));
            batDealParam.put("DATA2", data.getString("NEWUSERCOUNT", ""));
            batDealParam.put("DATA3", data.getString("OPERCODE", ""));
            batDealParam.put("DATA14", data.getString("ACCOUNTNAMEREQ", ""));
            batDealParam.put("DATA7", IDataUtil.getMandaData(data, "INFO_TYPE"));
        }
    }

    private static void dealCommonBatDealInfo(IData data, IData batDealParam) throws Exception
    {

        // 是否附件的最后一条记录,只要不为空，就认为是最后一条
        String endFlag = data.getString("EOF", "");

        String sysDate = SysDateMgr.getSysTime();
        String acceptMonth = SysDateMgr.getCurMonth();

        // 业务流水,每个报文的流水不同
        String ibsysId = IDataUtil.getMandaData(data, "IBSYSID");

        batDealParam.put("PRIORITY", "10");
        batDealParam.put("REFER_TIME", sysDate);
        batDealParam.put("EXEC_TIME", sysDate);
        batDealParam.put("ACCEPT_MONTH", acceptMonth);
        batDealParam.put("CANCEL_TAG", "0");
        batDealParam.put("DEAL_STATE", "0");
        batDealParam.put("SERIAL_NUMBER", data.getString("MSISDN", ""));
        batDealParam.put("DATA1", IDataUtil.getMandaData(data, "ORG_DOMAIN"));
        batDealParam.put("DATA11", IDataUtil.getMandaData(data, "IBSYSID_SUB"));
        batDealParam.put("DATA4", IDataUtil.getMandaData(data, "PROVCODE"));
        batDealParam.put("DATA5", IDataUtil.getMandaData(data, "ORDER_NO"));
        batDealParam.put("DATA6", IDataUtil.getMandaData(data, "RSRV_STR4"));
        batDealParam.put("DATA12", ibsysId);
        batDealParam.put("DATA18", endFlag);
        batDealParam.put("DATA19", IDataUtil.getMandaData(data, "BUSI_SIGN"));
        batDealParam.put("DATA20", IDataUtil.getMandaData(data, "OPER_TYPE"));
    }
}
