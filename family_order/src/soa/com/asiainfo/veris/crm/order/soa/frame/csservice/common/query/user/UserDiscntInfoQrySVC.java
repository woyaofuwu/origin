
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;

public class UserDiscntInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getByUIdPkId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String packageId = input.getString("PACKAGE_ID");
        return UserDiscntInfoQry.getByUIdPkId(userId, packageId);
    }

    /**
     * 根据userId查询用户正在生效的优惠信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNowValidDiscntByUserId(IData input) throws Exception
    {
        return UserDiscntInfoQry.getAllValidDiscntByUserId(input.getString("USER_ID"));
    }

    public IDataset getUserDiscntByUserIdAB(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String user_ida = input.getString("USER_ID_A");
        return UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, user_ida);

    }

    public IDataset getUserProdDisByUserIdProdIdPkgIdDisIdEndDate(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String product_id = input.getString("PRODUCT_ID");
        String package_id = input.getString("PACKAGE_ID");
        String discnt_code = input.getString("DISCNT_CODE");
        String end_date = input.getString("END_DATE");
        return UserDiscntInfoQry.getUserProdDisByUserIdProdIdPkgIdDisIdEndDate(user_id, product_id, package_id, discnt_code, end_date);
    }
    
	public IDataset queryDiscntsCodeByusrid(IData input) throws Exception
	{
		String USER_ID = input.getString("USER_ID","");
		if("".equals(USER_ID)){
			IData users = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
	        if (IDataUtil.isEmpty(users)){
	            CSAppException.apperr(CrmCommException.CRM_COMM_906);
	        }else{
	        	USER_ID = users.getString("USER_ID", "");
	        }
		}
		return UserDiscntInfoQry.queryDiscntsCodeByusrid(USER_ID);
	}

    public IDataset getUserProductDis(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        return UserDiscntInfoQry.getUserProductDis(user_id, user_id_a);
    }

    public IDataset getVpnSpecialChangeDiver(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        String product_id = input.getString("PRODUCT_ID");
        String package_id = input.getString("PACKAGE_ID");
        String diveStartDate = input.getString("DIVE_START_DATE");
        String diveEndDate = input.getString("DIVE_END_DATE");
        return UserDiscntInfoQry.getVpnSpecialChangeDiver(user_id, user_id_a, diveStartDate, diveEndDate);
    }

    public IDataset queryDiscntByUserIdAndDiscntCode(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String discntCode = input.getString("DISCNT_CODE");
        IDataset data = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, discntCode);

        return data;
    }

    public IDataset queryDiscntByUserIdVpmnActive(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        String product_id = input.getString("PRODUCT_ID");
        String package_id = input.getString("PACKAGE_ID");
        String subsys_code = input.getString("SUBSYS_CODE");
        String param_attr = input.getString("PARAM_ATTR");
        String param_code = input.getString("PARAM_CODE");
        String eparchy_code = input.getString("EPARCHY_CODE");
        return UserDiscntInfoQry.queryDiscntByUserIdVpmnActive(user_id, user_id_a, product_id, package_id, subsys_code, param_attr, param_code, eparchy_code);
    }

    public IDataset queryDiscntsByUserIdProdIdPkgId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        String product_id = input.getString("PRODUCT_ID");
        String package_id = input.getString("PACKAGE_ID");
        return UserDiscntInfoQry.queryDiscntsByUserIdProdIdPkgId(user_id, user_id_a, product_id, package_id);
    }
    
    public IDataset queryUserDiscntToCommparaByUID(IData input) throws Exception
    {
        String user_id = input.getString("MEB_USER_ID","");
        IDataset result = UserDiscntInfoQry.queryUserDiscntToCommparaByUID(user_id);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		IData data = result.getData(i);
        		data.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE")));
        	}
        	
        }
        return result;
    }
    /**
     * 查询用户是否办理了1000元海洋通基础套餐
     * @param input
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-8
     */
    public IDataset queryUserHytDisncts(IData input) throws Exception
    {
        return UserDiscntInfoQry.queryUserHytDisncts(input.getString("USER_ID"));
    }

    /**
     * 通过查询用户所有优惠
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserAllDisnctByCode(IData input) throws Exception {
        String userId = input.getString("USER_ID");
        String discntCode = input.getString("DISCNT_CODE");
        return UserDiscntInfoQry.queryUserAllDisnctByCode(userId, discntCode);
    }

    /**
     * 查询有效优惠
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserAllDisnctByCodeNow(IData input) throws Exception {
        String userId = input.getString("USER_ID");
        //String discntCode = input.getString("DISCNT_CODE");
        return UserDiscntInfoQry.getSpecDiscnt(userId);
    }
}
