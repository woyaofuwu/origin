
package com.asiainfo.veris.crm.order.soa.script.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;

/**
 * 用户资料查询
 * 
 * @author Administrator
 */
public class BreQryForUser extends BreBase
{
    /**
     * 根据用户ID查询用户优惠信息
     * 
     * @param strUserId
     * @return
     * @throws Exception
     */
    public static IDataset getUserDiscntByUserId(String strUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserId);

        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", param);
    }

    /**
     * 根据用户ID查询用户信息
     * 
     * @param strUserId
     * @return
     * @throws Exception
     */
    public static IDataset getUserInfoByUserId(String strUserId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserId);

        IData userinfo = UcaInfoQry.qryUserInfoByUserId(strUserId);
        if (IDataUtil.isEmpty(userinfo))
        {
            return new DatasetList();
        }
        IDataset listUserProduct = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_MAIN_PRODUCT_BY_PK", param);

        IData userprod = listUserProduct.getData(0);

        userinfo.put("PRODUCT_ID", userprod.getString("PRODUCT_ID"));
        userinfo.put("BRAND_CODE", userprod.getString("BRAND_CODE"));

        IDataset list = new DatasetList();
        list.add(userinfo);

        return list;
    }

    /**
     * 根据UserIdB productId 查询用户关系UU资料
     * 
     * @param userId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getUserRelationByUserIdBAndProId(String userId, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userId);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_BY_GrpMebUUVpmn", param, true);
    }

    /**
     * 根据UserIdB RelationTypeCode 查询用户关系UU资料
     * 
     * @param strUserId
     * @param strRelationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserRelationByUserIdBAndRelationTypeCode(String strUserId, String strRelationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", strUserId);
        param.put("RELATION_TYPE_CODE", strRelationTypeCode);

        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", param);
    }
}
