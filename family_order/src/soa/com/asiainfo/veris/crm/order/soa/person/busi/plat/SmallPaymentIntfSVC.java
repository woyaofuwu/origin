
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class SmallPaymentIntfSVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static IData getOweFeeByUserId(String userId, String acctId) throws Exception
    {

        // 调用账务接口，查询余额
        // IDataset results = TuxedoHelper.callTuxSvc("QAM_OWEFEE_QUERY", inparam, true);

        IDataset resultset = AcctCall.getCalcOweFeeByUserAcctId(userId, acctId, "0");
        IData results = null;
        if (IDataUtil.isNotEmpty(resultset))
        {
            results = resultset.getData(0);
        }
        if (results != null && results.size() > 0)
        {
            int resultCode = results.getInt("X_RESULTCODE");
            String resultInfo = results.getString("X_RESULTINFO");

            if (resultCode != 0)
            {
                results.put("ALL_NEW_BALANCE", "0");
                return results;
            }

            String allnewbalance = results.getString("ALL_NEW_BALANCE");
            if (allnewbalance == null || "".equals(allnewbalance) || allnewbalance.length() <= 0)
            {
                results.put("ALL_NEW_BALANCE", "0");
            }
        }
        else
        {
            /* ============没有查询到欠费的用户默认欠费为0======== */
            results.put("ALL_NEW_BALANCE", "0");
        }
        return results;
    }

    public static IData PaymentRollback(IData inparam) throws Exception
    {
        // 按USER_ID 在QAM_OWEFEE_QUERY实现

        // 调用账务接口
        // IDataset results = TuxedoHelper.callTuxSvc(pd,"TAM_ACCOUNT_FEE_PAYMENT", inparam, true);
        IDataset results = new DatasetList();

        IData result = new DataMap();
        if (results != null && results.size() > 0)
        {
            result = (IData) results.get(0);
        }
        return result;
    }

    /**
     * 检查帐务侧余额
     * 
     * @param pd
     * @param param
     * @throws Exception
     */
    public boolean checkOweFee(String userId, String acctId, String adjustfee, String bfee) throws Exception
    {
        IData oweFee = this.getOweFeeByUserId(userId, acctId);
        // 判断用户是否有足够实时结余:账务返回单位为 分
        if (Double.parseDouble(adjustfee) / 10 + Double.parseDouble(bfee) / 10 > oweFee.getDouble("ALL_NEW_BALANCE"))
        {
            return false;
        }
        return true;
    }

    private void smallPaymentLogMgr(String serialNumber, String adjustFee, String intfTradeId, String chnlCode, String spId, String spName, String remark, String operTime, String chargeId, String rsrvStr1, String userId, String bfee,
            String acceptDate, String staffid, String departCode) throws Exception
    {
        IData smallPayData = new DataMap();
        smallPayData.put("SERIAL_NUMBER", serialNumber);
        smallPayData.put("PAY_FEE", adjustFee);
        smallPayData.put("INTF_TRADE_ID", intfTradeId);
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
        smallPayData.put("TRADE_STAFF_ID", staffid);
        smallPayData.put("TRADE_DEPART_ID", departCode);
        smallPayData.put("CHARGE_ID", chargeId);
        smallPayData.put("RSRV_STR1", rsrvStr1);
        smallPayData.put("RSRV_STR2", userId);
        smallPayData.put("RSRV_STR3", bfee);

        Dao.executeUpdateByCodeCode("TF_B_SMALL_PAYMENT_LOG", "INS_SMALL_PAYMENT_LOG", smallPayData, Route.CONN_CRM_CEN);
    }

    /**
     * 提供给短一级BOSS的扣费接口
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData smallPaymentRCVFee(IData data) throws Exception
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
           //CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);//CRM_USER_207
            CSAppException.apperr(CrmUserException.CRM_USER_207, serialNumber); 
        }
        // 判断用户是否激活
        if (!userInfos.getString("ACCT_TAG").equals("0"))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_770, serialNumber);
             CSAppException.apperr(CrmUserException.CRM_USER_207, serialNumber);
        }

        // 判断用户是否开机状态
        if (!userInfos.getString("USER_STATE_CODESET").equals("0") && !userInfos.getString("USER_STATE_CODESET").equals("N"))
        {
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码用户状态非开通状态");
             CSAppException.apperr(CrmUserException.CRM_USER_207, "该号码用户状态非开通状态");
        }

        // 判断开户时间
        if (SysDateMgr.addDays(userInfos.getString("OPEN_DATE"), 90).compareTo(SysDateMgr.getSysTime()) > 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_291, "90");
        }

        String strUserId = userInfos.getString("USER_ID");
        IData acctInfos = UcaInfoQry.qryPayRelaByUserId(strUserId);
        if (IDataUtil.isEmpty(acctInfos))
        {
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取账户资料异常");
            CSAppException.apperr(CrmUserException.CRM_USER_207, "获取账户资料异常");
        }
        String strAcctId = acctInfos.getString("ACCT_ID");
        String routeEparchyCode = userInfos.getString("EPARCHY_CODE", "0898");
        String vipClassId = "0";
        // 设置用户保底金额10元：单位为厘
        String bfee = "10000";
        String dataId = "VIP_CLASS_0";

        // 判断是否大客户，默认保底金额为0元
        IDataset userVipInfos = CustVipInfoQry.getCustVipClassByUserId(strUserId);
        if (userVipInfos != null && userVipInfos.size() > 0)
        {
            vipClassId = userVipInfos.getData(0).getString("VIP_CLASS_ID");
        }
        // 大客户，保底金额为0元
        if ("0".compareTo(vipClassId) < 0 && "4".compareTo(vipClassId) >= 0)
        {
            dataId = "VIP_CLASS_" + vipClassId;
        }
        else
        {// 若非大客户，则判断移动集团内部员工，移动集团内部员工保底金额为0元
            // 判断用户是否有JTZ套餐(移动公司员工套餐)
            IDataset userDiscntInfos = UserDiscntInfoQry.getUserByDiscntCode(strUserId, "270");
            if (userDiscntInfos != null && userDiscntInfos.size() > 0)
            {
                // 移动集团内部员工保底金额为0元
                dataId = "MOBILE_GROUP_USER";
            }
        }

        // 获取保底金额
        IDataset staticLimitFee = StaticUtil.getStaticList("SMALL_PAYMENT_LIMIT_FEE", dataId);
        if (staticLimitFee != null && staticLimitFee.size() > 0)
        {
            bfee = staticLimitFee.getData(0).getString("DATA_NAME");
        }

        String departCode = data.getString("TRADE_DEPART_ID", "ITF00");
        String staffId = data.getString("TRADE_STAFF_ID", "ITF00000");
        IData param = new DataMap();
        param.put("ADJUST_TYPE", "0");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", strUserId);
        param.put("BFEE", bfee);// 保底金额
        param.put("ADJUST_FEE", adjustfee);// 商品金额
        param.put("INTF_TRADE_ID", intfTradeId);
        param.put("CHNL_CODE", chnlCode);
        param.put("PAY_FEE", adjustfee); // 商品金额
        param.put("SP_ID", spId);
        param.put("SP_NAME", spName);
        param.put("REMARK", remark);
        param.put("ACCEPT_DATE", acceptDate);
        param.put("OPER_TIME", operTime);
        param.put("TRADE_DEPART_ID", departCode);
        param.put("TRADE_STAFF_ID", staffId);
        param.put("TRADE_EPARCHY_CODE", routeEparchyCode);
        param.put("TRADE_CITY_CODE", "HNSJ");

        if (!checkOweFee(strUserId, strAcctId, adjustfee, bfee))
        {
        	/*BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "时长套餐不能为空");
            return false;*/
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_142 );
        }

        String strResCode = "";
        String strResInfo = "";
        String strTransRes = "";
        // 调用账务接口TAM_PRODUCT_PAYMENT
        IDataset infos = AcctCall.microPayMent(param);
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
                param.clear();
                // param.put("ROUTE--_EPARCHY_CODE", routeEparchyCode);// 加上公用参数
                param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", "ITF00"));
                param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", "ITF00000"));
                param.put("TRADE_EPARCHY_CODE", routeEparchyCode);
                param.put("TRADE_CITY_CODE", "HNSJ");

                param.put("USER_ID", strUserId);
                param.put("TAG", 0);
                // IData result = PaymentRollback(param);
                if ("100001".equals(resultCode))
                {
                    //CSAppException.apperr(CrmCommException.CRM_COMM_103, "支付余额不足!" + info.getString("X_RESULTINFO")); CRM_ACCOUNT_142
                      CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_142 );
                }
                else
                { 
                    //CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口错误！" + info.getString("X_RESULTINFO"));//CRM_COMM_272
                    CSAppException.apperr(CrmCommException.CRM_COMM_272, "调用账务接口错误！" + info.getString("X_RESULTINFO")); 
                }
            }

            res.put("X_RSPCODE", "0000");
            res.put("X_RSPTYPE", "0");
            res.put("X_RESULTCODE", "0000");
            res.put("X_RESULTINFO", strResInfo);
            res.put("TRANS_RES", strTransRes);
            smallPaymentLogMgr(serialNumber, adjustfee, intfTradeId, chnlCode, spId, spName, remark, operTime, "", "", strUserId, bfee, acceptDate, staffId, departCode);

        }
        else
        {
            param.clear();
            // param.put("--ROUTE--_EPARCHY_CODE", routeEparchyCode);// 加上公用参数
            param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", "ITF00"));
            param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", "ITF00000"));
            param.put("TRADE_EPARCHY_CODE", routeEparchyCode);
            param.put("TRADE_CITY_CODE", "HNSJ");

            param.put("USER_ID", strUserId);
            param.put("TAG", 0);
            // IData result = PaymentRollback(param);
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口错误！");
              CSAppException.apperr(CrmCommException.CRM_COMM_272, "调用账务接口错误！");
        }
        return res;
    }

}
