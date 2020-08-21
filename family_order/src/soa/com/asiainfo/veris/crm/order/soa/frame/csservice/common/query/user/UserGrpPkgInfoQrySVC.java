
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;

public class UserGrpPkgInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据USER_ID查询集团用户定制的付费计划明细
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGrpPayItemInfoByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        IDataset dataset = UserPayItemInfoQry.getGrpPayItemInfoByUserId(user_id);

        return dataset;
    }

    public IDataset getGrpPayPlanByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        IDataset dataset = UserPayPlanInfoQry.getGrpPayPlanByUserId(user_id, user_id_a);

        return dataset;
    }

    public IDataset getUserGrpPackageForGrp(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        IDataset dataset = UserGrpPkgInfoQry.getUserGrpPackageForGrp(user_id);

        return dataset;
    }

    /**
     * 查询企业飞信产品订购关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryECFetionProductOfferId(IData input) throws Exception
    {
        String user_id_b = input.getString("USER_ID_B", "");
        IDataset dataset = UserGrpPkgInfoQry.qryECFetionProductOfferId(user_id_b);

        return dataset;
    }

    /**
     * 查询集团定制资费信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryGrpCustomizeDiscntByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        
        IDataset result = UserGrpPkgInfoQry.qryGrpCustomizeDiscntByUserId(userId);
        
        if(IDataUtil.isEmpty(result))
        {
        	return result;
        }
        
        for(int i=0; i<result.size(); i++)
        {
        	String elementId = result.getData(i).getString("ELEMENT_ID");
        	result.getData(i).put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(elementId));
        }

        return result;
    }

}
