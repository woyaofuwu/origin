
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserImpuInfoQry
{

    public static IDataset getImpuInfoByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        IDataset dataset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_USERIDRSRVSTR1", param);
        return dataset;
    }

    public static IDataset getInfoByUserIdRsrvStr1(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        IDataset dataset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_USERID_AND_RSRVSTR1", param);
        return dataset;
    }

    public static IDataset getInfoByUUandVpn(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_F_USER_IMPU", "SEL_BY_UU_VPN", param);
    }

    public static IDataset getRelaInfoByUerId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_F_USER_IMPU", "SEL_BY_USERID", param);
    }

    /**
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset queryUserImpuInfo(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        IDataset dateset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_USER", param);
        return dateset.size() > 0 ? dateset : null;
    }

    /**
     * @Function: queryUserImpuInfo
     * @Description: 根据USER_ID查询用户IMPU信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:51:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset queryUserImpuInfo(String user_id, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        // getVisit().setRouteEparchyCode( eparchyCode);

        IDataset dateset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_USER", param, eparchyCode);
        return dateset.size() > 0 ? dateset : null;
    }

    /**
     * @Function: queryUserImpuInfoByUserType
     * @Description: 根据USER_ID和USER_TYPE查询用户IMPU信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:52:48 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset queryUserImpuInfoByUserType(String user_id, String rsrv_str1, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RSRV_STR1", rsrv_str1);

        IDataset dateset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_USERTYPE", param, eparchyCode);
        return dateset.size() > 0 ? dateset : null;
    }

    /**
     * @Function: selShortCodeByUserId
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:55:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset selShortCodeByUserId(String user_id_a, String short_code, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        param.put("SHORT_CODE", short_code);

        IDataset dataset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_SHORTCODE_BY_USERIDB", param, eparchy_code);
        return dataset;
    }

    /**
     * 根据custId查询集团下的所有成员的IMPU的密码
     * @param custId
     * @param eparchyCode
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset queryUserImpuPasswdInfoByCustId(String custId, String eparchyCode,Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_USERIMPU_PASSWD_BY_GRPCUSTID", param,pg);
        return dataset;
    }
    
    /**
     * 获取impu信息通过userid和时间点
     * @param userid
     * @param Date
     * @return
     * @throws Exception
     */
    public static IDataset queryUserImpuByUidDate(String userid, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userid);
        param.put("TIME_POINT", timePoint);
        IDataset dataset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_UID_DATE", param);
        return dataset;
    }
    
    /**
     * @Function: selShortCodeByUserId
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     */
    public static IDataset selImsVpnShortCodeByUserId(String user_id_a, String short_code, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        param.put("SHORT_CODE", short_code);

        IDataset dataset = Dao.qryByCode("TF_F_RELATION_UU", "SEL_IMS_SHORTCODE_BY_PK", param, eparchy_code);
        return dataset;
    }
    
    /**
     * 根据userId查询IMS的密码
     * @param userId
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset queryUserImpuPasswdByUserId(String userId,Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userId);
        IDataset dateset = Dao.qryByCode("TF_F_USER_IMPU", "SEL_BY_USERID_IN_RSRVSTR1", param,pg);
        return dateset;
    }
    
}
