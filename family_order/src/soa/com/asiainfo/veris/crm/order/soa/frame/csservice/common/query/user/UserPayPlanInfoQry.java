
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class UserPayPlanInfoQry
{
    /**
     * @Function: getGrpPayPlanByUserId
     * @Description:从CMR库 根据USER_ID查询成员付费计划
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2014-3-20 上午9:58:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getGrpMemPayPlanByUserId(String user_id, String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_BY_USERID", param);
    }

    /**
     * @Function: getGrpPayPlanByUserId
     * @Description:从集团库 根据USER_ID查询集团用户定制的付费计划
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午9:58:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getGrpPayPlanByUserId(String userId, String userIdA) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);

        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_BY_USERID", param, Route.CONN_CRM_CG);
    }

    /**
     * @author xiajj
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserPayPlanByPlanType(IData data) throws Exception
    {
        // TODO code_code 表里没有

        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_BY_USERID_PLANTYPE", data);
    }

    /**
     * @Function: getUserPayPlanByRsrvstr1
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:17:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserPayPlanByRsrvstr1(String user_id, String rsrv_str1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_STR1", rsrv_str1);

        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_GRP_BY_RSRVSTR1", param);
    }

    /**
     * @Function: getUserPayPlanByUserId
     * @Description: 根据USER_ID、USER_ID_A查询用户付费计划
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:19:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserPayPlanByUserId(String user_id, String user_id_a, Pagination page) throws Exception
    {
        if (StringUtils.isBlank(user_id))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        return Dao.qryByCodeParser("TF_F_USER_PAYPLAN", "SEL_BY_USERID", param, page);
    }

    /**
     * @Function: getUserPayPlanByUserIdPlanId
     * @Description: 根据USER_ID、PLAN_ID查询用户付费计划
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:20:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserPayPlanByUserIdPlanId(String user_id, String plan_id, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PLAN_ID", plan_id);
        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_BY_USERID_PLANID", param, page);
    }

    public static IDataset getUserPayPlanInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_BY_USERID1", param);

    }
    
    public static IDataset getAttrGrpMemPayPlanByUserId(String user_id, String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        return Dao.qryByCode("TF_F_USER_PAYPLAN", "SEL_BY_USERID_VAILD", param);
    }
}
