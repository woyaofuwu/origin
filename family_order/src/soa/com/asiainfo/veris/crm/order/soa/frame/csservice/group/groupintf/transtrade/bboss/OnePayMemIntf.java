
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;

/**
 * 一点支付成员处理接口
 * 
 * @author doremi
 */
public class OnePayMemIntf
{

    // 一点支付产品编码
    public final static String PRO_SPEC_CODE_YDZF = "99903";

    // 行业应用卡产品编码
    public final static String PRO_SPEC_CODE_HYYYK = "911601";

    /**
     * @param pd
     * @param data
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public static IDataset importData(IData data) throws Exception
    {

        String tradeStaffId = IDataUtil.getMandaData(data, "TRADE_STAFF_ID");
        String tradeDepartId = IDataUtil.getMandaData(data, "TRADE_DEPART_ID");
        String tradeCityCode = IDataUtil.getMandaData(data, "TRADE_CITY_CODE");
        String tradeEparchyCode = IDataUtil.getMandaData(data, "TRADE_EPARCHY_CODE");

        IData batParam = new DataMap();
        IData taskParam = new DataMap();
        IData param = new DataMap();
        IDataset results = new DatasetList();
        IData res = new DataMap();

        String tag1 = data.getString("USERSTATUS", "");
        String tag2 = data.getString("CENTROLPAYSTATUS", "");
        String tag3 = data.getString("ISNEWUSER", "");
        String productSpecCode = IDataUtil.getMandaData(data, "RSRV_STR4");
        if (productSpecCode.equals(PRO_SPEC_CODE_YDZF))
        {
            if (!"".equals(tag1))
            {
                param.put("BATCH_OPER_TYPE", "BATCONFIRMYDZFINTF");
            }
            else if (!"".equals(tag2))
            {
                param.put("BATCH_OPER_TYPE", "BATOPENYDZFINTF");
            }
            else
            {
                param.put("BATCH_OPER_TYPE", "BATADDYDZFMEMINTF");
            }
        }
        else if (productSpecCode.equals(PRO_SPEC_CODE_HYYYK))
        {
            if (!"".equals(tag3))
            {
                param.put("BATCH_OPER_TYPE", "BATOPENHYYYKINTF");
            }
            else
            {
                param.put("BATCH_OPER_TYPE", "BATADDHYYYKMEMINTF");
            }
        }
        // 一个文件中的最后一条记录标记
        String eof = data.getString("EOF", "");
        String ibsysId = IDataUtil.getMandaData(data, "IBSYSID");
        param.put("DATA12", ibsysId);

        IDataset set = BatTradeInfoQry.batchDetialQueryByData12(param);

        // 根据是否已保存有相同的IBSYSID，判断是否是同一批
        if (set.size() == 0)
        {
            // 生成批次号
            param.put("BATCH_TASK_ID", SeqMgr.getBatchId());
            param.put("BATCH_ID", SeqMgr.getBatchId());
            param.put("OPERATE_ID", SeqMgr.getBatchId());
        }
        else
        {
            param.put("BATCH_TASK_ID", ((DataMap) set.get(0)).getString("BATCH_TASK_ID"));
            param.put("BATCH_ID", ((DataMap) set.get(0)).getString("BATCH_ID"));
            param.put("OPERATE_ID", SeqMgr.getBatchId());
        }
        String strSysDate = SysDateMgr.getSysTime();
        String accept_month = SysDateMgr.getCurMonth();

        param.put("PRIORITY", "10");
        param.put("REFER_TIME", strSysDate);
        param.put("EXEC_TIME", strSysDate);
        param.put("ACCEPT_MONTH", accept_month);
        param.put("CANCEL_TAG", "0");
        param.put("DEAL_STATE", "0");
        param.put("SERIAL_NUMBER", data.getString("MSISDN", ""));
        param.put("DATA1", IDataUtil.getMandaData(data, "ORG_DOMAIN"));
        param.put("DATA11", IDataUtil.getMandaData(data, "IBSYSID_SUB"));
        param.put("DATA12", ibsysId);
        param.put("DATA4", IDataUtil.getMandaData(data, "PROVCODE"));
        param.put("DATA5", IDataUtil.getMandaData(data, "ORDER_NO"));
        param.put("DATA6", IDataUtil.getMandaData(data, "RSRV_STR4"));
        param.put("DATA18", eof);
        param.put("DATA19", IDataUtil.getMandaData(data, "BUSI_SIGN"));
        param.put("DATA20", IDataUtil.getMandaData(data, "OPER_TYPE"));
        if (productSpecCode.equals(PRO_SPEC_CODE_YDZF))
        {
            if (!"".equals(tag1))
            {
                // 自己是主办省
                param.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
                param.put("BATCH_OPER_NAME", "BBOSS下发配合省反馈成员确认结果[一点支付]");
                param.put("DATA7", data.getString("NAMEMATCH", ""));
                param.put("DATA13", data.getString("CURRFEEPLAN", ""));
                param.put("DATA9", data.getString("USERSTATUS", ""));
            }
            else if (!"".equals(tag2))
            {
                // 自己是主办省
                param.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
                param.put("BATCH_OPER_NAME", "BBOSS下发配合省反馈成员开通结果[一点支付]");
                param.put("DATA13", data.getString("FEEPLAN", ""));
                param.put("DATA8", data.getString("PAYTYPE", ""));
                param.put("DATA9", data.getString("PAYAMOUNT", ""));
                param.put("DATA10", data.getString("EFFRULE", ""));
                param.put("DATA2", data.getString("CENTROLPAYSTATUS", ""));
                param.put("DATA14", data.getString("ACCOUNTNAME", ""));
                param.put("DATA15", data.getString("FAILDESC", ""));
                param.put("DATA3", data.getString("ISNEWUSER", ""));
                param.put("DATA16", data.getString("NEWUSERFAILDESC", ""));
            }
            else
            {
                // 自己是配合省
                param.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
                param.put("BATCH_OPER_NAME", "BBOSS下发配合省成员[一点支付]");
                param.put("DATA13", data.getString("FEEPLAN", ""));
                param.put("DATA8", data.getString("PAYTYPE", ""));
                param.put("DATA9", data.getString("PAYAMOUNT", ""));
                param.put("DATA10", data.getString("EFFRULE", ""));
                param.put("DATA2", data.getString("NEWUSERCOUNT", ""));
                param.put("DATA3", data.getString("OPERCODE", ""));
                param.put("DATA14", data.getString("ACCOUNTNAMEREQ", ""));
                param.put("DATA7", IDataUtil.getMandaData(data, "INFO_TYPE"));
            }
        }
        else if (productSpecCode.equals(PRO_SPEC_CODE_HYYYK))
        {
            if (!"".equals(tag3))
            {
                param.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
                param.put("BATCH_OPER_NAME", "BBOSS下发配合省反馈成员开通结果[行业应用卡]");
                param.put("DATA2", data.getString("BUSISTATUS", ""));
                param.put("DATA3", data.getString("ISNEWUSER", ""));
                param.put("DATA13", data.getString("ERRDESC", ""));
                param.put("DATA14", data.getString("NEWUSERFAILDESC", ""));
            }
            else
            {
                param.put("BATCH_TASK_NAME", "BATCH_TASK_NAME");
                param.put("BATCH_OPER_NAME", "BBOSS下发配合省成员[行业应用卡]");
                param.put("DATA13", data.getString("FEEPLAN", ""));
                param.put("DATA14", data.getString("PRODINFO", ""));
                param.put("DATA2", data.getString("NEWUSERCOUNT", ""));
                param.put("DATA3", data.getString("OPERCODE", ""));
                param.put("DATA7", IDataUtil.getMandaData(data, "INFO_TYPE"));
            }
        }
        Dao.insert("TF_B_TRADE_BATDEAL", param, Route.getJourDb(Route.CONN_CRM_CG));

        if (set.size() == 0)
        {
            taskParam.put("BATCH_TASK_ID", param.getString("BATCH_TASK_ID"));
            taskParam.put("BATCH_TASK_NAME", param.getString("BATCH_TASK_NAME"));
            taskParam.put("BATCH_OPER_CODE", param.getString("BATCH_OPER_TYPE"));
            taskParam.put("BATCH_OPER_NAME", param.getString("BATCH_OPER_NAME"));
            taskParam.put("START_DATE", strSysDate);
            taskParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            taskParam.put("CREATE_TIME", SysDateMgr.getSysDate());
            taskParam.put("CREATE_STAFF_ID", tradeStaffId);
            taskParam.put("CREATE_DEPART_ID", tradeDepartId);
            taskParam.put("CREATE_CITY_CODE", tradeCityCode);
            taskParam.put("CREATE_EPARCHY_CODE", tradeEparchyCode);
            taskParam.put("SMS_FLAG", "0");
            Dao.insert("TF_B_TRADE_BAT_TASK", taskParam, Route.getJourDb(Route.CONN_CRM_CG));

            batParam.put("BATCH_TASK_ID", param.getString("BATCH_TASK_ID"));
            batParam.put("BATCH_ID", param.getString("BATCH_ID"));
            batParam.put("BATCH_OPER_TYPE", param.getString("BATCH_OPER_TYPE"));
            batParam.put("BATCH_COUNT", "1");
            batParam.put("ACCEPT_DATE", strSysDate);
            batParam.put("ACCEPT_MONTH", accept_month);
            batParam.put("STAFF_ID", tradeStaffId);
            batParam.put("DEPART_ID", tradeDepartId);
            batParam.put("CITY_CODE", tradeCityCode);
            batParam.put("EPARCHY_CODE", tradeEparchyCode);
            batParam.put("TERM_IP", "127.0.0.1");
            batParam.put("IN_MODE_CODE", "v");
            batParam.put("REMOVE_TAG", "0");
            batParam.put("ACTIVE_FLAG", "0");
            batParam.put("AUDIT_STATE", "0");
            batParam.put("AUDIT_REMARK", "批量任务名称");
            Dao.insert("TF_B_TRADE_BAT", batParam, Route.getJourDb(Route.CONN_CRM_CG));
        }
        else
        {
            // TF_B_TRADE_BAT的BATCH_COUNT加1
            BatDealBean bean = new BatDealBean();
            bean.addBatchCount(param);
        }
        res.put("X_RESULTCODE", "0");
        res.put("X_RESULTINFO", "处理成员成功");

        results.add(res);
        return results;
    }
}
