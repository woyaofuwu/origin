
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserSvcInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

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
    public static IDataset getValidElementFromPackageByUserA(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID");
        String user_id_a = data.getString("USER_ID_A");
        return UserSvcInfoQry.getValidElementFromPackageByUserA(user_id, user_id_a);
    }

    public IDataset getByPMode(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String productMode = input.getString("PRODUCT_MODE");

        return UserSvcInfoQry.getByPMode(userId, productMode);
    }

    /**
     * @Description:查询tf_f_user_element 查询用户选择了那些元素
     * @author weixb3
     * @date 2013-3-18
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getElementFromPackageByUser(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String product_id = param.getString("PRODUCT_ID");
        return UserSvcInfoQry.getElementFromPackageByUser(user_id, product_id, getPagination());
    }

    public IDataset getSerByBS(IData input) throws Exception
    {
        String biz_code = input.getString("BIZ_CODE");

        return UserSvcInfoQry.getSerByBS(biz_code);
    }

    public IDataset getSvcUserId(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String user_id_a = param.getString("USER_ID_A");
        String service_id = param.getString("SERVICE_ID");
        return UserSvcInfoQry.getSvcUserId(user_id, user_id_a, service_id);
    }

    public IDataset getUserLastStateByUserSvc(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String service_id = param.getString("SERVICE_ID");
        return UserSvcStateInfoQry.getUserLastStateByUserSvc(user_id, service_id);
    }

    public IDataset getUserProductSvc(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String user_id_a = param.getString("USER_ID_A");
        return UserSvcInfoQry.getUserProductSvc(userId, user_id_a, this.getPagination());
    }

    public IDataset getUserSvcBycon(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String user_id_a = param.getString("USER_ID_A");
        String product_id = param.getString("PRODUCT_ID");
        String package_id = param.getString("PACKAGE_ID");
        String service_id = param.getString("SERVICE_ID");
        return UserSvcInfoQry.getUserSvcBycon(user_id, user_id_a, product_id, package_id, service_id);
    }

    public IDataset getUserSvcByUserIdAB(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String user_id_a = param.getString("USER_ID_A");
        return UserSvcInfoQry.getUserSvcByUserIdAB(userId, user_id_a);
    }

    public IDataset qryUserSvcByUserSvcId(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String serviceId = param.getString("SERVICE_ID");

        return UserSvcInfoQry.qryUserSvcByUserSvcId(userId, serviceId);
    }

    public IDataset querySvcByUserIDandSVC(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String service_id = param.getString("SERVICE_ID");
        return UserSvcInfoQry.qrySvcInfoByUserIdSvcId(user_id, service_id);
    }

    public IDataset queryUserServices(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");

        return UserSvcInfoQry.queryUserServices(user_id);
    }

    public IDataset queryUserSvcByUserIdAll(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");

        return UserSvcInfoQry.queryUserSvcByUserIdAll(user_id);
    }
    
    
    public IDataset getUserLastNextStateByUserSvc(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String serviceId = param.getString("SERVICE_ID");
        String stateCode = param.getString("STATE_CODE");
        return UserSvcStateInfoQry.getUserLastNextStateByUserSvc(userId, serviceId,stateCode);
    }
    
    public IDataset getUserSvcBetweenStateByUserID(IData param) throws Exception
    {
    	 String userId = param.getString("USER_ID");
         String serviceId = param.getString("SERVICE_ID");
         String stateCode = param.getString("STATE_CODE");
        return UserSvcStateInfoQry.getUserSvcBetweenStateByUserID(userId, serviceId,stateCode);
    }
    
    public IDataset queryUserSvcByUserIdAndInstId(IData param) throws Exception
    {
    	String userId = param.getString("USER_ID");
    	String serviceId = param.getString("SERVICE_ID");
    	String instId = param.getString("INST_ID");
        return UserSvcInfoQry.queryUserSvcByUserIdAndInstId(userId, serviceId,instId);
    }
    
}
