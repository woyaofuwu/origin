
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ScoreRelationQry
{

    /**
     * 根据userid查询有效的积分账户和用户之间的关系(包含未生效)
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryEffectiveRelByUserId(String userId, String actTag, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACT_TAG", actTag);
        return Dao.qryByCode("TF_F_SCORERELATION", "SEL_BY_EFFECTIVE", param, routeId);
    }

    /**
     * 查询积分账户和用户之间的关系
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreRelByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_SCORERELATION", "SEL_BY_PK", param);
    }

    /**
     * 根据积分账户ID查询userId(包含未生效)
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String queryUserIdByIntegralAcctId(String integralAccountId) throws Exception
    {
        IData param = new DataMap();
        param.put("INTEGRAL_ACCOUNT_ID", integralAccountId);
        IDataset ids = Dao.qryByCode("TF_F_SCORERELATION", "SEL_BY_INTEGRAL_ACCOUNT_ID", param);
        String userId = "";
        if (IDataUtil.isNotEmpty(ids))
        {
            userId = ids.getData(0).getString("USER_ID");
        }

        return userId;
    }
}
