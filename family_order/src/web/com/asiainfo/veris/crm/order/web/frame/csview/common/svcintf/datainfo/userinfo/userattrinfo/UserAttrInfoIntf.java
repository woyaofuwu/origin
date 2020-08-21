
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userattrinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserAttrInfoIntf
{

    /**
     * 根据user_id attr_code查询资料表参数信息
     * 
     * @author liuxx3
     * @date 2014-07-31
     */
    public static IDataset qryBBossUserAttrInfo(IBizCommon bc, String userId, String attrCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("ATTR_CODE", attrCode);
        return CSViewCall.call(bc, "CS.UserAttrInfoQrySVC.getUserAttrByUserIdc", inparam);
    }

    /**
     * 通过USERID,INST_TYPE查询集团用户订购的产品参数信息
     * 
     * @param bc
     * @param userId
     * @param instType
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductAttrInfosByUserIdAndInstType(IBizCommon bc, String userId, String instType) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("INST_TYPE", instType);
        return CSViewCall.call(bc, "CS.UserAttrInfoQrySVC.getUserProductAttrByUTForGrp", inparam);
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
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("INST_TYPE", instType);
        inparam.put("ATTR_CODE", attrCode);
        return CSViewCall.call(bc, "CS.UserAttrInfoQrySVC.getUserProductAttrValue", inparam);
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
    public static IDataset qryMebProductAttrInfosByUserIdAndUserIdA(IBizCommon bc, String userId, String userIdA, String instType, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);
        inparam.put("INST_TYPE", instType);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserAttrInfoQrySVC.getUserProductAttrByUserIdAndUserIdA", inparam);
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
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("INST_TYPE", instType);
        inparam.put("RELA_INST_ID", relaInstId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserAttrInfoQrySVC.getUserAttrByUserIdInstid", inparam);
    }

}
