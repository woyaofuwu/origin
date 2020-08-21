
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class M2MOrderIntf
{

    /**
     * 响应接口，将BOSS处理结果实时调用一级boss提供的接口反馈给一级boss
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @throws Throwable
     * @author liaoyi
     */
    public static IDataset IntfM2MAnswerDeal(IData data) throws Exception, Throwable
    {
        String busiSign = data.getString("BUSI_SIGN", "");// 报文类型
        if ("".equals(busiSign) || busiSign == null)
        {
            // common.error(IntfField.OTHER_ERR[0], IntfField.OTHER_ERR[1] + ",BUSI_SIGN报文类型不能为空!");
            CSAppException.apperr(IBossException.CRM_IBOSS_1);
        }

        // 返回数据定义
        IDataset ids = null;

        /*
         * if (pd.getContext().getLoginEpachyId() == null || "".equals(pd.getContext().getLoginEpachyId())) {
         * CSBizBean.setDbConCode(pd, BaseFactory.CENTER_CONNECTION_NAME); SecurityBean bean = new SecurityBean();
         * boolean isValidstaff = bean.setStaffInfo(pd, IntfField.TRADE_STAFF_ID[1], null); if (!(isValidstaff)) {
         * common.error(IntfField.OTHER_ERR[0], IntfField.OTHER_ERR[1] + ",无法查询到员工[" + IntfField.TRADE_STAFF_ID[1] +
         * "]的资料"); } }
         */

        // M2M个人帐户余额查询
        if (IntfField.SubTransCode.M2MQueryPersonLeaveRealFeeBiz.value.equals(busiSign))
        {
            ids = queryPersonLeaveRealFee(data);
        }

        // M2M集团帐户余额查询
        else if (IntfField.SubTransCode.M2MQueryECLeaveRealFeeBiz.value.equals(busiSign))
        {
            ids = queryECLeaveRealFee(data);
        }

        // M2M个人状态查询
        else if (IntfField.SubTransCode.M2MQueryUserInfoBiz.value.equals(busiSign))
        {
            ids = queryUserStateInfo(data);
        }

        return ids;
    }

    /**
     * EC余额查询
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @throws Throwable
     * @author liaoyi
     */
    public static IDataset queryECLeaveRealFee(IData data) throws Exception, Throwable
    {
        // 组织返回结果集
        IData tmp = new DataMap();
        IDataset result = new DatasetList();

        /*
         * 错误编码表： 错误编码 描述 00 受理成功 01 操作代码错误。 02 EC编码不存在 99 其它错误，由落地方自行解释。
         */
        String groupID = StrUtil.getString("GROUP_ID", data, false);
        tmp.put(IntfField.GROUP_ID[0], groupID);

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", groupID); // 成员号码
        tmp.put("ECNo", groupID); // 用户号码
        tmp.put("BALANCETYPE", "0"); // 该账号账户付费类型
        tmp.put("ECACCTID", "0"); // 用户账号id
        tmp.put("ECACCTNAME", "*"); // 用户账号名称
        // 获取路由地州
        IData UserInfo = UserInfoQry.getUserInfoBySN(groupID);
        if (UserInfo == null)
        {
            // tmp.put("X_RESULTCODE", "02");
            // tmp.put("X_RESULTINFO", "集团编码["+groupID+"]不存在");
            // result.add(tmp);
            // return result;
            CSAppException.apperr(GrpException.CRM_GRP_486);
        }

        // 取账户资料
        param.clear();
        param.put("USER_ID", UserInfo.getString("USER_ID"));
        IData acctids = UcaInfoQry.qryAcctInfoByUserId(UserInfo.getString("USER_ID"));
        if (acctids == null || acctids.size() < 1)
        {
            // tmp.put("X_RESULTCODE", "99");
            // tmp.put("X_RESULTINFO", "帐户资料不存在");
            // result.add(tmp);
            // return result;
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_79);
        }

        IData acctres = (IData) (acctids.get(0));

        param.put("EPARCHY_CODE", UserInfo.getString("EPARCHY_CODE"));
        param.put("USER_ID", UserInfo.getString("USER_ID"));
        param.put("ID", UserInfo.getString("USER_ID"));
        param.put("ID_TYPE", "1");
        param.put(Route.ROUTE_EPARCHY_CODE, UserInfo.getString("EPARCHY_CODE"));

        IData oweFee = new DataMap();
        // oweFee = UserInfoQry.getOweFeeByUserId( param);
        oweFee = AcctCall.getOweFeeByUserId(UserInfo.getString("USER_ID"));
        int rec;

        rec = oweFee.getInt("X_RESULTCODE");

        if (rec != 0)
        {
            String reinfo = "701014" + oweFee.getString("X_RESULTINFO");
            tmp.put("X_RESULTCODE", "99");
            tmp.put("X_RESULTINFO", reinfo);
            result.add(tmp);
            return result;
        }
        else
        {
            double fee3 = Double.valueOf(oweFee.getString("RSRV_NUM3"));
            tmp.put("BALANCE", fee3); // 该账号当前余额
            tmp.put("ECACCTID", acctres.getString("ACCT_ID")); // 用户账号id
            tmp.put("ECACCTNAME", acctres.getString("PAY_NAME")); // 用户账号名称

            tmp.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            tmp.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            result.add(tmp);
            return result;
        }
    }

    //	
    // /**
    // * 个人余额查询
    // *
    // * @param pd
    // * @param data
    // * @return
    // * @throws Exception
    // * @throws Throwable
    // * @author liaoyi
    // */
    public static IDataset queryPersonLeaveRealFee(IData data) throws Exception, Throwable
    {
        // 组织返回结果集
        IData tmp = new DataMap();
        IDataset result = new DatasetList();

        /*
         * 错误编码表： 错误编码 描述 00 受理成功 01 操作代码错误。 02 MSISDN号码不存在 99 其它错误，由落地方自行解释。
         */
        String memSerialNumber = StrUtil.getString("SERIAL_NUMBER", data, false);
        tmp.put(IntfField.SERIAL_NUMBER[0], memSerialNumber);

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", memSerialNumber); // 成员号码
        tmp.put("MSISDN", memSerialNumber); // 用户号码
        tmp.put("BALANCE", "0"); // 余额
        tmp.put("USERACCTID", "0"); // 用户账号id
        tmp.put("USERACCTNAME", "*"); // 用户账号名称
        tmp.put("ISPRIMARYACCT", "0"); // 是否为代付账户
        tmp.put("BALANCETYPE", "0"); // 该账号账户付费类型
        tmp.put("SUBJECTID", "0"); // 代付科目
        tmp.put("PRIORITY", "0"); // 代付优先级

        // 获取路由地州
        IData UserInfo = UserInfoQry.getUserInfoBySN(memSerialNumber);

        if (UserInfo == null)
        {
            // tmp.put("X_RESULTCODE", "02");
            // tmp.put("X_RESULTINFO", "号码["+memSerialNumber+"]不是本省号码，或者没有开户");
            // result.add(tmp);
            // return result;
            CSAppException.apperr(CrmUserException.CRM_USER_943, memSerialNumber);
        }

        // 取账户资料
        param.clear();
        param.put("USER_ID", UserInfo.getString("USER_ID"));
        IData acctids = UcaInfoQry.qryAcctInfoByUserId(UserInfo.getString("USER_ID"));
        if (acctids == null || acctids.size() < 1)
        {
            // tmp.put("X_RESULTCODE", "99");
            // tmp.put("X_RESULTINFO", "帐户资料不存在");
            // result.add(tmp);
            // return result;
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_79);
        }

        IData acctres = acctids;

        param.put("EPARCHY_CODE", UserInfo.getString("EPARCHY_CODE"));
        param.put("USER_ID", UserInfo.getString("USER_ID"));
        param.put("ID", UserInfo.getString("USER_ID"));
        param.put("ID_TYPE", "1");
        param.put(Route.ROUTE_EPARCHY_CODE, UserInfo.getString("EPARCHY_CODE"));

        IData oweFee = new DataMap();
        // oweFee = UserInfoQry.getOweFeeByUserId( param);
        oweFee = AcctCall.getOweFeeByUserId(UserInfo.getString("USER_ID"));

        int rec;

        rec = oweFee.getInt("X_RESULTCODE");

        if (rec != 0)
        {
            String reinfo = "701014" + oweFee.getString("X_RESULTINFO");
            tmp.put("X_RESULTCODE", "99");
            tmp.put("X_RESULTINFO", reinfo);
            result.add(tmp);
            return result;
        }
        else
        {
            double fee3 = Double.valueOf(oweFee.getString("RSRV_NUM3"));
            tmp.put("BALANCE", fee3); // 该账号当前余额
            tmp.put("USERACCTID", acctres.getString("ACCT_ID")); // 用户账号id
            tmp.put("USERACCTNAME", acctres.getString("PAY_NAME")); // 用户账号名称

            tmp.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            tmp.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            result.add(tmp);
            return result;
        }
    }

    /**
     * 个人状态查询
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @throws Throwable
     * @author liaoyi
     */
    public static IDataset queryUserStateInfo(IData data) throws Exception, Throwable
    {

        /*
         * 错误编码表： 错误编码 描述 00 受理成功 01 操作代码错误。 02 MSISDN号码不存在 99 其它错误，由落地方自行解释。
         */
        // 组织返回结果集
        IData tmp = new DataMap();
        IDataset result = new DatasetList();

        String memSerialNumber = StrUtil.getString("SERIAL_NUMBER", data, false);
        tmp.put(IntfField.SERIAL_NUMBER[0], memSerialNumber);
        tmp.put("MSISDN", memSerialNumber); // 用户号码
        tmp.put("STATUS", "0");
        tmp.put("STATUSDATE", "0");

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", memSerialNumber); // 成员号码

        // 获取路由地州
        IData UserInfo = UserInfoQry.getUserInfoBySN(memSerialNumber);
        if (UserInfo == null)
        {
            // tmp.put("X_RESULTCODE", "02");
            // tmp.put("X_RESULTINFO", "号码["+memSerialNumber+"]不是本省号码，或者没有开户");
            // result.add(tmp);
            // return result;
            CSAppException.apperr(CrmUserException.CRM_USER_943, memSerialNumber);
        }

        param.clear();
        param.put("USER_ID", UserInfo.getString("USER_ID"));
        IDataset userSvcState = new DatasetList();
        userSvcState = UserSvcStateInfoQry.getUserValidMainSVCState(UserInfo.getString("USER_ID"), null);
        if (userSvcState != null && userSvcState.size() == 1)
        {
            IData tempInfo = (IData) userSvcState.get(0);
            tmp.put("STATUS", tempInfo.getString("STATE_CODE"));
            // tmp.put("STATUSDATE", DualMgr.dataToData(tempInfo.get("START_DATE").toString(),"yyyy-MM-dd HH:mm:ss"));
            tmp.put("STATUSDATE", SysDateMgr.string2Date(tempInfo.get("START_DATE").toString(), "yyyy-MM-dd HH:mm:ss").toString());
            tmp.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            tmp.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            result.add(tmp);
            return result;
        }
        else
        {
            tmp.put("X_RESULTCODE", "99");
            tmp.put("X_RESULTINFO", "获取用户状态异常");
            result.add(tmp);
            return result;
        }
    }

    // /**
    // * 账单查询
    // *
    // * @param pd
    // * @param data
    // * @return
    // * @throws Exception
    // * @throws Throwable
    // * @author liaoyi
    // */
    // public static IDataset queryMasterBill(PageData pd, IData data) throws Exception, Throwable
    // {
    //
    // /*
    // 错误编码表：
    // 错误编码 描述
    // 00 受理成功
    // 01 操作代码错误。
    // 02 MSISDN号码不存在
    // 99 其它错误，由落地方自行解释。
    // */
    // // 组织返回结果集
    // IData tmp = new DataMap();
    // IDataset result = new DatasetList();
    //
    // String memSerialNumber = GroupUtil.getString("SERIAL_NUMBER", data, false);
    // tmp.put(IntfField.SERIAL_NUMBER[0], memSerialNumber);
    // tmp.put("MSISDN",memSerialNumber); // 用户号码
    // tmp.put("STATUS","0");
    // tmp.put("STATUSDATE","0");
    //
    // IData param = new DataMap();
    // param.put("SERIAL_NUMBER", memSerialNumber); //成员号码
    //		
    // // 获取路由地州
    // IData UserInfo = UserGrpQry.getUserInfoBySN(pd, param);
    // if(UserInfo==null)
    // {
    // tmp.put("X_RESULTCODE", "02");
    // tmp.put("X_RESULTINFO", "号码["+memSerialNumber+"]不是本省号码，或者没有开户");
    // result.add(tmp);
    // return result;
    // }
    //
    // param.clear();
    // param.put("USER_ID",UserInfo.getString("USER_ID"));
    // IDataset userSvcState = new DatasetList();
    // userSvcState = UserSvcStateQry.getUserValidMainSVCState(pd, param, null);
    // if (userSvcState != null && userSvcState.size() == 1)
    // {
    // IData tempInfo = (IData) userSvcState.get(0);
    // tmp.put("STATUS",tempInfo.getString("STATE_CODE"));
    // tmp.put("STATUSDATE", DualMgr.dataToData(tempInfo.get("START_DATE").toString(),"yyyy-MM-dd HH:mm:ss"));
    // tmp.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
    // tmp.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
    // result.add(tmp);
    // return result;
    // }else{
    // tmp.put("X_RESULTCODE", "99");
    // tmp.put("X_RESULTINFO", "获取用户状态异常");
    // result.add(tmp);
    // return result;
    // }
    // }
    //	
    //	

}
