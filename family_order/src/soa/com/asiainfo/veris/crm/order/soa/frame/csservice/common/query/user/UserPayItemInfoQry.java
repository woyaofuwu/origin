
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserPayItemInfoQry
{
    /**
     * @Function: getGrpPayItemInfoByUserId
     * @Description:从集团库 根据USER_ID查询集团用户定制的付费计划明细
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午9:56:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getGrpPayItemInfoByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_PAYITEM", "SEL_BY_USERID", param, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 根据SQL_REF查询TF_F_USER_PAYITEM表
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPayItemInfo(IData inparams) throws Exception
    {
        // TODO SQL_REF传入
        String sqlref = inparams.getString("SQL_REF");
        return Dao.qryByCode("TF_F_USER_PAYITEM", sqlref, inparams);
    }

    /**
     * @Function: getPyitemByPk
     * @Description:主键查询payitem
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:13:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getPyitemByPk(String user_id, String plan_id, String payitem_code) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PLAN_ID", plan_id);
        param.put("PAYITEM_CODE", payitem_code);
        return Dao.qryByCode("TF_F_USER_PAYITEM", "SEL_BY_PK", param);
    }

    /**
     * @Function: getUserPayItemAndNoteItemByPayId
     * @Description: 根据用户ID和PLAN_ID,查
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:14:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserPayItemAndNoteItemByPayId(String user_id, String plan_id, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PLAN_ID", plan_id);
        return Dao.qryByCode("TF_F_USER_PAYITEM", "SEL_ITEM_NOTE_BY_USERID_PLANID", param, page);
    }

    /**
     * @Function: getUserPayItemByPlayId
     * @Description: 根据USER_ID、PLAN_ID查询用户付费计划帐目信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:14:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserPayItemByPlayId(String user_id, String plan_id, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PLAN_ID", plan_id);
        return Dao.qryByCode("TF_F_USER_PAYITEM", "SEL_BY_USERID_PLANID", param, page);
    }

}
