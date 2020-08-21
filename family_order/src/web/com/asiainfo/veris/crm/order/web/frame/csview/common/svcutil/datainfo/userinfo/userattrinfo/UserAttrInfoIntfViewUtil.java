
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userattrinfo.UserAttrInfoIntf;

public class UserAttrInfoIntfViewUtil
{
    /**
     * 根据user_id attr_code查询资料表参数信息
     * 
     * @author liuxx3
     * @date 2014-07-31
     */
    public static IDataset qryBBossUserAttrInfo(IBizCommon bc, String userId, String attrCode) throws Exception
    {
        IDataset infosDataset = UserAttrInfoIntf.qryBBossUserAttrInfo(bc, userId, attrCode);
        return infosDataset;
    }

    /**
     * 通过USERID查询集团用户订购的产品参数信息
     * 
     * @param bc
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductAttrInfosByUserId(IBizCommon bc, String userId) throws Exception
    {
        IDataset infosDataset = UserAttrInfoIntf.qryGrpProductAttrInfosByUserIdAndInstType(bc, userId, "P");
        return infosDataset;
    }

    /**
     * 根据用户编号、参数类型、参数编号查找参数用户信息
     * 
     * @param bc
     * @param userId
     * @param instType
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductAttrInfosByUserIdAndInstTypeAndAttrCode(IBizCommon bc, String userId, String instType, String attrCode) throws Exception
    {
        IDataset productParamInfoList = UserAttrInfoIntf.qryGrpProductAttrInfosByUserIdAndInstTypeAndAttrCode(bc, userId, instType, attrCode);
        return productParamInfoList;
    }

    /**
     * 通过用户id，属性类型(S,D,P),关联的实例ID查询集团库的属性信息
     * 
     * @param bc
     * @param grpUserId
     * @param instType
     * @param relaInstId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserAttrInfosByUserIdAndInstTypeRelaInstId(IBizCommon bc, String grpUserId, String instType, String relaInstId) throws Exception
    {
        IDataset infosDataset = qryUserAttrInfosByUserIdAndInstTypeRelaInstId(bc, grpUserId, instType, relaInstId, Route.CONN_CRM_CG);

        return infosDataset;
    }

    /**
     * 通过USER_ID,USER_ID_A查询成员用户订购的特定集团产品的产品参数
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryMebProductAttrInfosByUserIdAndUserIdA(IBizCommon bc, String userId, String userIdA, String routeId) throws Exception
    {
        IDataset infosDataset = UserAttrInfoIntf.qryMebProductAttrInfosByUserIdAndUserIdA(bc, userId, userIdA, "P", routeId);
        return infosDataset;
    }

    /**
     * 通过用户id，属性类型(S,D,P),关联的实例ID查询属性信息
     * 
     * @param bc
     * @param userId
     * @param instType
     * @param relaInstId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserAttrInfosByUserIdAndInstTypeRelaInstId(IBizCommon bc, String userId, String instType, String relaInstId, String routeId) throws Exception
    {
        IDataset infosDataset = UserAttrInfoIntf.qryUserAttrInfosByUserIdAndInstTypeRelaInstId(bc, userId, instType, relaInstId, routeId);

        return infosDataset;
    }
}
