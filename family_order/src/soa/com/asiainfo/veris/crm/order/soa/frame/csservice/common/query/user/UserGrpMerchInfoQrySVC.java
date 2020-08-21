
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserGrpMerchInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询用户订购商品信息
     * 
     * @param param
     *            参数
     * @param pagination
     *            分页
     * @return 用户订购商品信息
     * @throws Exception
     * @author ft
     */
    public static IDataset qryMerchInfoByUserIdMerchSpecStatus(IData param) throws Exception
    {

        String user_id = param.getString("USER_ID");
        String merch_spec_code = param.getString("MERCH_SPEC_CODE");
        String status = param.getString("STATUS");
        IDataset list=null;
        list= UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(user_id, merch_spec_code, status, null);
        if(list.size()==0 || list==null){
        	//增加集客大厅商品信息查询
        list= UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatusJkdt(user_id, merch_spec_code, status, null);

        }
        return list;
    }

    /*
     * @descption 根据集团用户编号和成员用户编号获取成员用户信息
     * @atuhor xunyl
     * @date 2013-04-28
     */
    public IDataset getSEL_BY_USERID_USERIDA(IData param) throws Exception
    {
        String memUserId = param.getString("USER_ID");
        String grpUserId = param.getString("GRP_USER_ID");
        String mem_eparchy_code = param.getString("MEM_EPARCHY_CODE");
        return UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(memUserId, grpUserId, mem_eparchy_code);
    }

}
