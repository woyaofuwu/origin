
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.URelaRoleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class QueryRelationBean extends CSBizBean
{

    // 海南一卡双号绑定付费不能取消一卡双号关系
    // 根据服务号码获取帐户
    public IData getAcct(String userId) throws Exception
    {
        IDataset account = null;
        IData param = new DataMap();
        param.put("USER_ID", userId);
        // 获取用户当前的默认帐户
        account = PayRelaInfoQry.getPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(account))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "无默认付费帐户！");
        }
        String accountId = account.getData(0).getString("ACCT_ID");
        return queryAcctInfo(accountId);
    }

    public int getScore(String userId) throws Exception
    {
        // 查用户积分
        IDataset scoreInfo = AcctCall.queryUserScore(userId);
        if (IDataUtil.isEmpty(scoreInfo))
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        int score = scoreInfo.getData(0).getInt("SUM_SCORE");
        return score;
    }

    /**
     * 查询用户资源信息 restypecode 1 sim卡号 0 手机号
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public IData getUserResinfo(String userId) throws Exception
    {
        IDataset resInfos = UserResInfoQry.getUserResInfoByUserId(userId);
        if (IDataUtil.isEmpty(resInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户资源信息出错,无资源信息资料");
        }
        for (int i = 0; i < resInfos.size(); i++)
        {
            String resTypeCode = resInfos.getData(i).getString("RES_TYPE_CODE", "");
            if (resTypeCode.equals("1"))
            {
                return resInfos.getData(i);
            }
        }
        return null;
    }

    public IData queryAcctInfo(String accountId) throws Exception
    {
        IData accountInfo = null;
        IData param = new DataMap();
        param.put("ACCT_ID", accountId);
        // 获取用户当前的默认帐户资料
        accountInfo = UcaInfoQry.qryAcctInfoByAcctId(accountId);
        if (IDataUtil.isEmpty(accountInfo))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "无帐户资料！");
        }

        return accountInfo;
    }

    /**
     * 获取一卡双号关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryRelationInfos(String sn) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (userInfo == null)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户信息出错，无用户信息!");
        }
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));

        IDataset relationInfosB = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userInfo.getString("USER_ID"), "30", null);
        if (IDataUtil.isEmpty(relationInfosB))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户关系信息出错,无用户关系资料!");
        }
        IDataset relationInfosA = RelaUUInfoQry.getUserRelationAll(relationInfosB.getData(0).getString("USER_ID_A", ""), "30");
        if (IDataUtil.isEmpty(relationInfosA))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户关系信息出错,无用户关系资料!");
        }

        IData acct = new DataMap();
        IData acctOther = new DataMap();

        IDataset dataset = new DatasetList();
        ;
        IData data = new DataMap();
        IData userInfoPK = new DataMap();

        for (int i = 0; i < relationInfosA.size(); i++)
        {
            IData da = new DataMap();
            da.clear();
            userInfoPK.clear();
            custInfo.clear();
            IData relationInfoA = relationInfosA.getData(i);
            IData resInfo = getUserResinfo(relationInfoA.getString("USER_ID_B"));

            IDataset simCardInfos = ResCall.getSimCardInfo("0", resInfo.getString("RES_CODE", ""), null, "1");

            if (IDataUtil.isEmpty(simCardInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现记录！");
            }
            else
            {
                String simTypeCode = simCardInfos.getData(0).getString("RES_TYPE_CODE");
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X") && !simTypeCode.equals("1I") &&
                // !simTypeCode.equals("1J"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询： 当前卡非一卡双号卡！"+simTypeCode);
                // }
                // else
                // {
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！"+simTypeCode);
                // }
                // }
            }
            IData simCardInfo = simCardInfos.getData(0);

            da.put("SIM_CARD_NO", simCardInfo.getString("SIM_CARD_NO"));
            da.put("EMPTY_CARD_ID", simCardInfo.getString("EMPTY_CARD_ID", ""));
            data.put("ROLE_CODE_B", relationInfoA.getString("ROLE_CODE_B"));
            String roleName = URelaRoleInfoQry.getRoleBNameByRelaTypeCodeRoleCodeB("30", relationInfoA.getString("ROLE_CODE_B"));

            userInfoPK = UcaInfoQry.qryUserInfoByUserId(relationInfoA.getString("USER_ID_B"));
            custInfo = UcaInfoQry.qryCustInfoByCustId(userInfoPK.getString("CUST_ID"));

            IDataset userPro = UserProductInfoQry.getProductInfo(relationInfoA.getString("USER_ID_B"), "-1");
            String brand_code = userPro.getData(0).getString("BRAND_CODE");
            String product_id = userPro.getData(0).getString("PRODUCT_ID");

            if (custInfo.size() == 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取客户出错,无客户资料");
            }
            da.put("USER_STATE_CODESET", userInfoPK.getString("USER_STATE_CODESET"));
            da.put("BRAND_CODE", brand_code);
            da.put("PRODUCT_ID", product_id);
            da.put("SCORE_VALUE", userInfoPK.getString("SCORE_VALUE"));
            da.put("OPEN_DATE", userInfoPK.getString("OPEN_DATE"));
            da.put("CITY_CODE", userInfoPK.getString("CITY_CODE"));
            da.put("LAST_STOP_TIME", userInfoPK.getString("LAST_STOP_TIME"));
            da.put("CUST_NAME", custInfo.getString("CUST_NAME"));
            da.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
            da.put("PSPT_ID", custInfo.getString("PSPT_ID"));
            da.put("ROLE_B", roleName);
            da.put("SERIAL_NUMBER_B", relationInfoA.getString("SERIAL_NUMBER_B"));
            da.put("SCORE_VALUE", getScore(relationInfoA.getString("USER_ID_B")));
            if (relationInfoA.getString("ROLE_CODE_B").equals("1"))
            {
                acct = getAcct(relationInfoA.getString("USER_ID_B"));
            }
            if (relationInfoA.getString("ROLE_CODE_B").equals("2"))
            {
                acctOther = getAcct(relationInfoA.getString("USER_ID_B"));
            }
            dataset.add(da);
        }
        if (!(IDataUtil.isEmpty(acct) || IDataUtil.isEmpty(acctOther)))
        {
            if (acct.getString("ACCT_ID").equals(acctOther.getString("ACCT_ID")))
            {
                dataset.getData(0).put("ISSHOW", "0");
            }
            else
            {
                dataset.getData(0).put("ISSHOW", "1");
            }
        }
        else
        {
            dataset.getData(0).put("ISSHOW", "1");
        }
        return dataset;
    }
}
