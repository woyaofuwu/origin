
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

public class UserPlatSvcInfoQrySvc extends CSBizService
{
    /**
     * 查询用户选择了那些元素(USER_ID,USER_ID_A)
     * 
     * @author xunyl
     * @param data
     * @param eparchyCode
     * @date 2013-03-20
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPlatSvcByUserId(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID");
        String product_id = data.getString("PRODUCT_ID");
        return UserPlatSvcInfoQry.getGrpPlatSvcByUserId(user_id, product_id);
    }

    public static IDataset getPlatSvcByUserBizType(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID");
        String biz_type_code = data.getString("BIZ_TYPE_CODE");
        return UserPlatSvcInfoQry.getPlatSvcByUserBizType(user_id, biz_type_code);
    }

    /**
     * 通过用户ID和服务ID查询用户的平台服务
     * 
     * @param userId
     * @param platSvcId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserPlatSvcByUserIdAndServiceId(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String serviceId = data.getString("SERVICE_ID");
        return UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, serviceId);
    }
    /**
     * 查询用户是否存在此服务
     * author: zhangbo18
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset querySvcInfoByUserIdAndSvcIdPf(IData data) throws Exception
    {
    	String userId = data.getString("USER_ID");
        String serviceId = data.getString("SERVICE_ID");
        return UserPlatSvcInfoQry.querySvcInfoByUserIdAndSvcIdPf(userId, serviceId);
    }
    /**
     * 查询服务信息
     * author:zhangbo18
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset querySvcBySpCodeAndBizCode(IData data) throws Exception
    {
    	String spCode = data.getString("SP_CODE");
        String bizCode = data.getString("BIZ_CODE");
        return PlatSvcInfoQry.querySvcBySpCodeAndBizCode(spCode, bizCode);
    }
    
    /**
     * 查询老服务信息
     * author:songxw
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset querySvcAllBySpCodeAndBizCode(IData data) throws Exception
    {
    	String spCode = data.getString("SP_CODE");
    	String bizCode = data.getString("BIZ_CODE");
    	String bizTypeCode = data.getString("BIZ_TYPE_CODE");
    	return PlatSvcInfoQry.querySvcAllBySpCodeAndBizCode(spCode, bizCode, bizTypeCode);
    }
}
