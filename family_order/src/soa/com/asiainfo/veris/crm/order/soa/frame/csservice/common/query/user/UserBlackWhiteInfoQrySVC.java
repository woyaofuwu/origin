
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserBlackWhiteInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 通过GROUP_ID、SERVICE_ID、SERIAL_NUMBER查询成员黑白名单信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBlackWhitedataByGSS(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER", "");
        String ec_user_id = input.getString("EC_USER_ID", "");
        String service_id = input.getString("SERVICE_ID", "");
        String user_type_code = input.getString("USER_TYPE_CODE", "");
        String biz_code = input.getString("BIZ_CODE", "");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, this.getRouteId());

        IDataset output = UserBlackWhiteInfoQry.getBlackWhitedataByGSSAndUserType(serial_number, ec_user_id, service_id, user_type_code, biz_code, eparchyCode);
        return output;
    }
    
    /**
     * 通过USER_ID查询成员黑白名单信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBlackWhiteCountByEcUserId(IData input) throws Exception
    {
        String ec_user_id = input.getString("EC_USER_ID", "");

        IDataset output= UserBlackWhiteInfoQry.qryblackWhitecountByEcUserId(ec_user_id);
        return output;
    }
    
    /**
     * 通过GROUP_ID、SERVICE_ID、SERIAL_NUMBER查询成员黑白名单信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBlackWhitedataByGSSADC(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER", "");
        String ec_user_id = input.getString("EC_USER_ID", "");
        String service_id = input.getString("SERVICE_ID", "");
        String user_type_code = input.getString("USER_TYPE_CODE", "");
        String biz_code = input.getString("BIZ_CODE", "");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, this.getRouteId());

        IDataset output = UserBlackWhiteInfoQry.getBlackWhitedataByGSSAndUserType(serial_number, ec_user_id, service_id, user_type_code, biz_code, eparchyCode);
        return output;
    }

    /**
     * @description 通过sn/groupid/serviceid 查询黑白名单信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBlackWhiteInfo(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");
        String group_id = input.getString("GROUP_ID", "");
        String service_id = input.getString("SERVICE_ID");
        String eparchy_code = input.getString(Route.ROUTE_EPARCHY_CODE, this.getRouteId());

        IDataset output = UserBlackWhiteInfoQry.getBlackWhiteInfo(serial_number, group_id, service_id, eparchy_code);
        return output;
    }

    /**
     * 通过EC_USER_ID、ATTR_CODE查询成员黑白名单开关
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getBwOpenTag(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID", "");
        String attr_code = input.getString("ATTR_CODE", "");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, this.getRouteId());
        IDataset output = UserAttrInfoQry.getBwOpenTag(user_id, attr_code, eparchyCode);
        return output;
    }
}
