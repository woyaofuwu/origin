
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat.PlatSmsAction;

public class SmallPaymentCancelIntfSVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 提供给短一级BOSS的小额支付退费接口
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    private static Log logger = LogFactory.getLog(PlatSmsAction.class);
    public IData smallPaymentCancelFee(IData data) throws Exception
    {
        IData res = new DataMap();

        // 传入的参数
        String intfTradeId = data.getString("INTF_TRADE_ID");
        String chnlCode = data.getString("CHNL_CODE");
        String serialNumber = data.getString("SERIAL_NUMBER");
        String adjustfee = data.getString("PAY_FEE");
        String spId = data.getString("SP_ID");
        String spName = data.getString("SP_NAME");
        String remark = data.getString("REMARK");
        String acceptDate = data.getString("ACCEPT_DATE");
        String operTime = data.getString("OPER_TIME");

        IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfos) || !StringUtils.equals("00", userInfos.getString("NET_TYPE_CODE")))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        // 判断用户是否激活
        if (!userInfos.getString("ACCT_TAG").equals("0"))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_770, serialNumber);
        }

        // 判断用户是否开机状态
        if (!userInfos.getString("USER_STATE_CODESET").equals("0") && !userInfos.getString("USER_STATE_CODESET").equals("L"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码用户状态非开通状态");
        }

        String strUserId = userInfos.getString("USER_ID");
        String routeEparchyCode = userInfos.getString("EPARCHY_CODE", "0898");
        // 单位为厘
        String bfee = "0";

        IData param = new DataMap();
        param.put("CANCEL_FLAG", "1");
        param.put("ADJUST_TYPE", "1");// 标识调用的TAM_PRODUCT_PAYMENT是退费操作。【ADJUST_TYPE】调帐类型 0: 补费，1：退费

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", strUserId);
        param.put("BFEE", "0");// 保底金额
        param.put("ADJUST_FEE", adjustfee);// 商品金额
        param.put("INTF_TRADE_ID", intfTradeId);
        param.put("CHNL_CODE", chnlCode);
        param.put("PAY_FEE", adjustfee); // 商品金额
        param.put("SP_ID", spId);
        param.put("SP_NAME", spName);
        param.put("REMARK", remark);
        param.put("ACCEPT_DATE", acceptDate);
        param.put("OPER_TIME", operTime);
        // String province = common.getProperty("project/province");
        param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", "ITF00"));
        param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", "ITF00000"));
        param.put("TRADE_EPARCHY_CODE", routeEparchyCode);
        param.put("TRADE_CITY_CODE", "HNSJ");
        param.put("MANUAL_FLAG","MICRO_PAYMENT_MANUAL_STAFF");

        String strResCode = "";
        String strResInfo = "";
        String strTransRes = "";
        // 调用账务接口TAM_PRODUCT_PAYMENT
        // IDataset infos = CSAppCall.call("SS.SmallPaymentIntfSVC.smallPaymentRCVFee", param);
        IDataset infos = AcctCall.microPayMent(param);
        IData infoset = new DataMap();
        infoset.put("X_RESULTCODE", "0");
        infoset.put("X_RESULTINFO", "1");
        infoset.put("CHARGE_ID", "1");
        infos.add(infoset);

        IData info = new DataMap();
        if (infos != null && infos.size() > 0)
        {
            strResCode = "0000"; // 业务默认成功
            info = infos.getData(0);
            String resultCode = info.getString("X_RESULTCODE");
            strResInfo = info.getString("X_RESULTINFO", "");
            strTransRes = info.getString("CHARGE_ID", "");
            if (!"0".equals(resultCode))
            {
                if ("100001".equals(resultCode))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "支付余额不足!");
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "系统内部错误!");
                }
            }

            res.put("X_RSPCODE", "0000");
            res.put("X_RSPTYPE", "0");
            res.put("X_RESULTCODE", "0000");
            res.put("X_RESULTINFO", strResInfo);
            res.put("TRANS_RES", strTransRes);
            smallPaymentLogMgr(serialNumber, adjustfee, intfTradeId, chnlCode, spId, spName, remark, operTime, "", "", strUserId, bfee, acceptDate);

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "系统内部错误!");
        }
        return res;
    }

    private void smallPaymentLogMgr(String serialNumber, String adjustFee, String intfTradeId, String chnlCode, String spId, String spName, String remark, String operTime, String chargeId, String rsrvStr1, String userId, String bfee,
            String acceptDate) throws Exception
    {   
        IData smallPayData = new DataMap();
        int SmallPaymentLogCount = 0;
        smallPayData.put("SERIAL_NUMBER", serialNumber);
        smallPayData.put("PAY_FEE", adjustFee);
        if (StringUtils.isEmpty(chnlCode))
        {
            chnlCode = "0001";
        }
        smallPayData.put("CHNL_CODE", chnlCode);
        smallPayData.put("SP_ID", spId);
        smallPayData.put("SP_NAME", spName);
        smallPayData.put("REMARK", remark);
        smallPayData.put("ACCEPT_DATE", acceptDate);
        smallPayData.put("OPER_TIME", operTime);
        smallPayData.put("CHARGE_ID", chargeId);
        smallPayData.put("RSRV_STR1", rsrvStr1);
        smallPayData.put("RSRV_STR2", userId);
        smallPayData.put("RSRV_STR3", bfee); 
        
        smallPayData.put("TRADE_STAFF_ID", CSBizService.getVisit().getStaffId());
        smallPayData.put("TRADE_DEPART_ID", CSBizService.getVisit().getDepartCode()); 
        smallPayData.put("INTF_TRADE_ID", intfTradeId);
        smallPayData.put("CANCEL_FLAG", "1"); //退费成功更改状态

        Dao.executeUpdateByCodeCode("TI_B_SMALL_PAYMENT_LOG_RESULT", "UPD_SMALLPAYMENT_BYID", smallPayData, Route.CONN_CRM_CEN); 
        //Dao.executeUpdateByCodeCode("TF_B_SMALL_PAYMENT_LOG", "INS_SMALL_PAYMENT_LOG", smallPayData, Route.CONN_CRM_CEN); 
       // Dao.insert("INS_SMALL_PAYMENT_LOG", smallPayData);
    }
}
