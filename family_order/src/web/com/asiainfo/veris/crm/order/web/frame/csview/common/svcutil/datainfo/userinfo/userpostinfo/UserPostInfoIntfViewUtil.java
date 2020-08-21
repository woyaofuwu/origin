
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpostinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userpostinfo.UserPostInfoIntf;

public class UserPostInfoIntfViewUtil
{
    /**
     * 通过账户ID查询邮寄信息列表
     * 
     * @param bc
     * @param acctId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctPostInfosByCustId(IBizCommon bc, String acctId, String routeId) throws Exception
    {
        IDataset infosDataset = qryPostInfosByIdAndIdType(bc, acctId, "2", routeId);
        return infosDataset;
    }

    /**
     * 通过客户ID查询邮寄信息列表
     * 
     * @param bc
     * @param custId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryCustPostInfosByCustId(IBizCommon bc, String custId, String routeId) throws Exception
    {
        IDataset infosDataset = qryPostInfosByIdAndIdType(bc, custId, "0", routeId);
        return infosDataset;
    }

    /**
     * 通过集团用户ID查询邮寄信息列表
     * 
     * @param bc
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserPostInfosByUserId(IBizCommon bc, String grpUserId) throws Exception
    {
        IDataset infosDataset = qryUserPostInfosByUserId(bc, grpUserId, Route.CONN_CRM_CG);

        return infosDataset;
    }

    /**
     * 通过ID和ID_TYPE查询邮寄信息
     * 
     * @param bc
     * @param id
     * @param idType
     *            0客户 1用户 2账户
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryPostInfosByIdAndIdType(IBizCommon bc, String id, String idType, String routeId) throws Exception
    {
        IDataset infosDataset = UserPostInfoIntf.qryPostInfosByIdAndIdType(bc, id, idType, routeId);

        return infosDataset;
    }

    /**
     * 通过用户ID查询邮寄信息列表
     * 
     * @param bc
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserPostInfosByUserId(IBizCommon bc, String userId, String routeId) throws Exception
    {
        IDataset infosDataset = qryPostInfosByIdAndIdType(bc, userId, "1", routeId);

        return infosDataset;
    }

}
