
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserRecommInfoQry
{

    public static IDataset getInformationByUserid(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_INFORMATION", "SEL_BY_USERID", inparams);
    }

    public static IDataset getRecommByUserid(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_INFORMATION", "SEL_RECOMM_BY_USERID", inparams);
    }

    public static IDataset getRecomminfo(IData inparam) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_RECOMM", "SEL_BY_MULTIELEMENT", inparam);
    }

    public static IDataset getUserRecomm(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_RECOMM", "SEL_USER_RECOMM", param);
    }

    public static IDataset getUserRecommByelement(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_RECOMM", "SEL_USER_RECOMM_BYELEMENT", inparams);
    }

    public static IDataset getUserRecommByelementId(String userId, String elementId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ELEMENT_ID", elementId);
        return Dao.qryByCode("TF_F_USER_RECOMM", "SEL_BY_USERID_ELEMENTID", param);
    }

    public static IDataset getUserRecommList(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_RECOMM", "SEL_HISTORY_RECOMD_LIST", param);
    }

    public static IDataset queryElementNameByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset set = Dao.qryByCode("TF_F_USER_RECOMM", "SEL_BY_USERID", param);

        return set;
    }

    /**
     * @Function: queryUserInformation
     * @Description: 查询用户提示信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:07:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserInformation(String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset set = Dao.qryByCode("TF_F_USER_INFORMATION", "SEL_CONTENT_BY_USERID", param);

        return set;
    }

    /**
     * @Function: queryUserRecommActive
     * @Description: 查询推荐给用户的活动
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:08:11 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserRecommActive(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset set = Dao.qryByCode("TF_F_USER_RECOMM", "SEL_USER_SALEACTIVE_RECOMM", param);

        return set;
    }

    /**
     * @Function: queryUserRecommDiscnt
     * @Description: 查询推荐给用户的资费
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:09:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserRecommDiscnt(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset set = Dao.qryByCode("TF_F_USER_RECOMM", "SEL_USER_DISCNT_RECOMM", param);

        return set;
    }

    /**
     * @Function: queryUserRecommProduct
     * @Description: 查询推荐给用户的产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:09:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserRecommProduct(String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset set = Dao.qryByCode("TF_F_USER_RECOMM", "SEL_USER_PRODUCT_RECOMM", param);

        return set;
    }

    /**
     * @Function: queryUserRecommService
     * @Description: 查询推荐给用户的服务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:10:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserRecommService(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset set = Dao.qryByCode("TF_F_USER_RECOMM", "SEL_USER_SVC_RECOMM", param);

        return set;
    }
}
