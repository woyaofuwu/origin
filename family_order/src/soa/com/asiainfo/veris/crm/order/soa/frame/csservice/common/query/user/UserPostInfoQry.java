
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserPostInfoQry
{

    public static IDataset qryPostInfosByIdIdType(String id, String id_type) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", id_type);
        return Dao.qryByCode("TF_F_POSTINFO", "SEL_BY_ID_IDTYPE", param);
    }

    /**
     * 执行邮寄信息查询
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserPostInfo(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_POSTINFO", "SEL_BY_SERIAL", inparams, pagination);
    }

    /**
     * @Function: getUserPostInfo
     * @Description: 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:40:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryUserPostInfo(String id, String id_type) throws Exception
    {
        return qryUserPostInfo(id, id_type, null);
    }

    public static IDataset qryUserPostInfo(String id, String id_type, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", id_type);

        return Dao.qryByCode("TF_F_POSTINFO", "SEL_BY_USERID", param, routeId);
    }

    /**
     * @Function: getUserPostInfoForGrp
     * @Description: 从集团库根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午7:41:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryUserPostInfoForGrp(String id, String id_type) throws Exception
    {
        return qryUserPostInfo(id, id_type, Route.CONN_CRM_CG);
    }
}
