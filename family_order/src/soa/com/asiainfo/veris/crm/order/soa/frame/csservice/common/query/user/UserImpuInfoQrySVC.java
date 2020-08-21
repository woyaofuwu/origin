
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserImpuInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询用户impu信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserImpuInfo(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        IDataset data = UserImpuInfoQry.queryUserImpuInfo(user_id);

        return data;
    }

    /**
     * 根据USER_ID和USER_TYPE查询用户IMPU信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserImpuInfoByUserType(IData input) throws Exception
    {
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, getRouteId());
        String user_id = input.getString("USER_ID");
        String rsrv_str1 = input.getString("RSRV_STR1");

        IDataset data = UserImpuInfoQry.queryUserImpuInfoByUserType(user_id, rsrv_str1, eparchyCode);
        if (IDataUtil.isNotEmpty(data))
        {
            IData idata = data.getData(0);
            idata.put("USER_TYPE_NAME", StaticUtil.getStaticValue("TF_F_USER_IMPU_USERTYPE", idata.getString("RSRV_STR1")));
        }

        return data;
    }

    public IDataset selShortCodeByUserId(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A");
        String short_code = input.getString("SHORT_CODE");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        IDataset data = UserImpuInfoQry.selShortCodeByUserId(user_id_a, short_code, eparchyCode);

        return data;
    }
    
    /**
     * 根据custId查询集团下的所有成员的IMPU的密码
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserImpuPasswdInfoByCustId(IData input) throws Exception
    {
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, getRouteId());
        String custId = input.getString("CUST_ID");
        IDataset data = UserImpuInfoQry.queryUserImpuPasswdInfoByCustId(custId, eparchyCode, this.getPagination());
        return data;
    }
    
    public IDataset selImsVpnShortCodeByUserId(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A");
        String short_code = input.getString("SHORT_CODE");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        IDataset data = UserImpuInfoQry.selImsVpnShortCodeByUserId(user_id_a, short_code, eparchyCode);

        return data;
    }
    
    /**
     * 根据userId查询IMS的密码
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserImpuPasswdByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IDataset data = UserImpuInfoQry.queryUserImpuPasswdByUserId(userId, this.getPagination());
        return data;
    }
    
}
