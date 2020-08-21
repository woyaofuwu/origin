
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ScoreAcctInfoQry
{

    public static void delAcctInfoByAcctId(String endDate, String status, String integralAcctId) throws Exception
    {
        IData param = new DataMap();
        param.put("END_DATE", endDate);
        param.put("STATUS", status);
        param.put("INTEGRAL_ACCT_ID", integralAcctId);
        Dao.qryByCode("TF_F_INTEGRAL_ACCT", "DEL_FINISH", param);
    }

    /**
     * 查询被赠送积分
     * 
     * @param userId
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-03 10:10:04
     */
    public static IDataset queryBzsScoreByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_COMMPARA", "searchBZSScore", param);
    }

    /**
     * 根据积分账号查询CG库，判断该积分账户是否关联到移动积分账户
     * 
     * @param integralAcctId
     * @return 关联移动手机号码 -1没有在CG库查询到资料 为空表示没有关联到移动用户
     * @throws Exception
     */
    public static String queryIsScoreAcctCorrelation(String integralAcctId) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_TAG1", "1");// 0不关联 1 已关联
        param.put("RSRV_STR1", integralAcctId);// 移动用户的积分账户ID
        IDataset ids = Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_CORRELATION_BY_ID", param, Route.CONN_CRM_CG);
        String sn = "";
        if (IDataUtil.isNotEmpty(ids))
        {
            sn = ids.getData(0).getString("CONTRACT_PHONE");
        }
        return sn;
    }

    /**
     * 根据关联手机号码查询积分账户
     * 
     * @param sn
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoByContractSn(String sn, String status, Pagination pagin) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTRACT_PHONE", sn);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_CONTRACT_PHONE", param, pagin,Route.CONN_CRM_CEN);
    }

    /**
     * 根据邮箱地址查询积分账户
     * 
     * @param email
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoByEmail(String email, String status, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("EMAIL", email);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_EMAIL", param, pagination,Route.CONN_CRM_CEN);
    }

    /**
     * 根据邮箱地址查询积分账户
     * 
     * @param email
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoByEmail(String email, String status, String route) throws Exception
    {
        IData param = new DataMap();
        param.put("EMAIL", email);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_EMAIL", param, route);
    }

    /**
     * 查询积分账户信息
     * 
     * @param integralAcctId
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoByIntegralAcctId(String integralAcctId) throws Exception
    {
        IData param = new DataMap();
        param.put("INTEGRAL_ACCT_ID", integralAcctId);
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_PK", param,Route.CONN_CRM_CEN);
    }

    public static IDataset queryScoreAcctInfoByPsptId(String psptId, Pagination pagin) throws Exception
    {
        IData param = new DataMap();
        param.put("VALUE", psptId);
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_PSPT_ID", param, pagin,Route.CONN_CRM_CEN);
    }

    /**
     * 根据证件类型和证件id查询积分账户(不包含生效)
     * 
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoByPsptId(String psptTypeCode, String psptId, String status, String route) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_TYPE_CODE", psptTypeCode);
        param.put("PSPT_ID", psptId);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_PSPTID", param, route);
    }

    /**
     * 根据证件类型和证件id查询积分账户(包含未生效)
     * 
     * @param psptTypeCode
     * @param psptId
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoByPsptIdStatus(String psptTypeCode, String psptId, String status, String route) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_TYPE_CODE", psptTypeCode);
        param.put("PSPT_ID", psptId);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_PSPTID_STATUS", param, route);
    }

    /**
     * 根据关联手机号码查询积分账户
     * 
     * @param sn
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreAcctInfoBySn(String sn, String status, String route) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTRACT_PHONE", sn);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_CONTRACT_PHONE", param, route);
    }

    public static IDataset queryScoreAcctInfoByUserId(String userId, String status, String route) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("STATUS", status);// 状态：10A 正常 10E 销户
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_USER_ID", param, route);
    }

    /**
     * 查询赠送积分
     * 
     * @param userId
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-03 10:09:38
     */
    public static IDataset queryZsScoreByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_COMMPARA", "searchZSScore", param);
    }
}
