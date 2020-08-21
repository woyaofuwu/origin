
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class UserCheckIdentitySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 记录用户身份校验
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset logUserCheckInfo(IData input) throws Exception
    {
        // 验证方式 :本人证件号码=0, 用户密码=1
        String checkMode = input.getString("CHECK_MODE", "");
        String resultCode = input.getString("RESULT_CODE", ""); // 认证校验结果
        String resultInfo = input.getString("RESULT_INFO", ""); // 认证校验结果:错误结果
        String userId = input.getString("USER_ID", "");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "347");
        String eparchyCode = input.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IDataset resultSet = new DatasetList();
        IData resultData = new DataMap();

        String inputStr = null, processTag = null, result;
        result = resultCode.equals("0") ? "1" : "0";
        // 证件校验方式
        if (checkMode.equals("0"))
        {
            inputStr = input.getString("PSPT_ID", "");// 输入的号码
            processTag = "0000000000000000000E";
            if (resultCode.equals("0"))
            {
                resultInfo = "身份证件号码校验正确！";
            }
        }
        else if (checkMode.equals("1"))
        {
            inputStr = input.getString("USER_PASSWD", "");// 输入的密码
            processTag = "0000000000000000000B";
            if (resultCode.equals("0"))// true 验证通过
            {
                resultInfo = "密码校验正确！";
            }
        }
        resultData.put("RESULT_CODE", resultCode);
        resultData.put("RESULT_INFO", resultInfo);

        IData iparam = new DataMap();
        String tradeId = SeqMgr.getTradeId();
        IData tradeTypes = UTradeTypeInfoQry.getTradeType(tradeTypeCode, eparchyCode);

        String sysDate = SysDateMgr.getSysTime();

        iparam.put("TRADE_ID", tradeId);
        iparam.put("BATCH_ID", SeqMgr.getBatchId());
        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        iparam.put("ORDER_ID", SeqMgr.getOrderId());
        iparam.put("PROD_ORDER_ID", "");
        iparam.put("BPM_ID", "");
        iparam.put("CAMPN_ID", "");
        iparam.put("TRADE_TYPE_CODE", tradeTypeCode);
        iparam.put("PRIORITY", tradeTypes.getString("PRIORITY"));
        iparam.put("SUBSCRIBE_TYPE", "0");
        iparam.put("SUBSCRIBE_STATE", "9");
        iparam.put("NEXT_DEAL_TAG", "0");
        iparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        iparam.put("USER_ID", userId);
        iparam.put("CUST_NAME", input.getString("CUST_NAME", ""));
        iparam.put("ACCT_ID", input.getString("ACCT_ID", "0"));
        iparam.put("CUST_ID", input.getString("CUST_ID"));
        iparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        iparam.put("NET_TYPE_CODE", "00");
        iparam.put("EPARCHY_CODE", eparchyCode);
        iparam.put("CITY_CODE", input.getString("CITY_CODE", ""));
        iparam.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
        iparam.put("BRAND_CODE", input.getString("BRAND_CODE"));
        iparam.put("CUST_ID_B", "");
        iparam.put("ACCT_ID_B", "");
        iparam.put("USER_ID_B", "");
        iparam.put("SERIAL_NUMBER_B", "");
        iparam.put("CUST_CONTACT_ID", "");
        iparam.put("SERV_REQ_ID", "");
        iparam.put("INTF_ID", "");
        iparam.put("ACCEPT_DATE", sysDate);
        iparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        iparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        iparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        iparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        iparam.put("TERM_IP", getVisit().getRemoteAddr());
        iparam.put("OPER_FEE", "0");
        iparam.put("FOREGIFT", "0");
        iparam.put("ADVANCE_PAY", "0");
        iparam.put("INVOICE_NO", "");
        iparam.put("FEE_STATE", "0");
        iparam.put("FEE_TIME", "");
        iparam.put("FEE_STAFF_ID", "");
        iparam.put("PROCESS_TAG_SET", processTag);
        iparam.put("OLCOM_TAG", "0");
        iparam.put("FINISH_DATE", sysDate);
        iparam.put("EXEC_TIME", sysDate);
        iparam.put("EXEC_ACTION", "0");
        iparam.put("EXEC_RESULT", "");
        iparam.put("EXEC_DESC", "");
        iparam.put("CANCEL_TAG", "0");
        iparam.put("CANCEL_DATE", "");
        iparam.put("CANCEL_STAFF_ID", "");
        iparam.put("CANCEL_DEPART_ID", "");
        iparam.put("CANCEL_CITY_CODE", "");
        iparam.put("CANCEL_EPARCHY_CODE", "");
        iparam.put("UPDATE_TIME", sysDate);
        iparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        iparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        iparam.put("REMARK", ""); // 备注
        iparam.put("RSRV_STR1", result); // 验证结果 0=错误 1=正确
        iparam.put("RSRV_STR2", inputStr); // 输入的值
        iparam.put("RSRV_STR3", "");
        iparam.put("RSRV_STR4", "");
        iparam.put("RSRV_STR5", "");
        iparam.put("RSRV_STR6", "");
        iparam.put("RSRV_STR7", "");
        iparam.put("RSRV_STR8", "");
        iparam.put("RSRV_STR9", "");
        iparam.put("RSRV_STR10", resultInfo);

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_ALL", iparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        resultSet.add(resultData);

        return resultSet;
    }
}
