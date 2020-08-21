
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserProductInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 通过用户ID,集团用户ID,产品ID查询用户产品表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getProductInfoByUserIdUserIdAProdId(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String user_id_a = param.getString("USER_ID_A");
        String product_id = param.getString("PRODUCT_ID");
        return UserProductInfoQry.getUserProductInfoByUserIdAndUserIdAProductId(user_id, user_id_a, product_id);
    }

    /*
     * @description 根据用户ID查询用户订购的产品信息 @author xunyl @date 2013-03-20
     */
    public static IDataset GetUserProductInfo(IData param) throws Exception
    {
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
        String user_id = param.getString("USER_ID");
        String user_id_a = param.getString("USER_ID_A");
        String product_id = param.getString("PRODUCT_ID");
        String product_mode = param.getString("PRODUCT_MODE");
        return UserProductInfoQry.GetUserProductInfo(user_id, user_id_a, product_id, product_mode, eparchyCode);
    }

    public static IDataset queryUserMainProductByUserId(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        return UserProductInfoQry.queryUserMainProduct(user_id);
    }
    
    public static IDataset getUserChangeXiangProductByUserId(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        return UserProductInfoQry.getUserChangeXiangProductByUserId(user_id);
    }
    
    public static IDataset getGrpProductByCustIdForUPGP(IData param) throws Exception
    {
        String custId = param.getString("CUST_ID");
        return UserProductInfoQry.getGrpProductByCustIdForUPGP(custId);
    }
}
