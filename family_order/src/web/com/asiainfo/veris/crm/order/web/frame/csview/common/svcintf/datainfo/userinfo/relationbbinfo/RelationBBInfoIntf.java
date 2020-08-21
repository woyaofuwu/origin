
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationbbinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class RelationBBInfoIntf
{

    /**
     * @description 根据userIdA、userIdB、relationTypeCode、roleCodeB查询BB关系信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset getBBByUserIdAB(IBizCommon bc, String userIdA, String userIdB, String roleCodeB, String relationTypeCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", userIdA);
        inparam.put("USER_ID_B", userIdB);
        inparam.put("ROLE_CODE_B", roleCodeB);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaBBInfoQrySVC.getBBByUserIdAB", inparam);
    }

    /**
     * 根据USER_ID_A和RELATION_TYPE_CODE统计成员数量
     * 
     * @param bc
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCountByUserIdAAndRelationTypeCodeAllCrm(IBizCommon bc, String userIdA, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        return CSViewCall.call(bc, "CS.RelaBBInfoQrySVC.qryCountByUserIdAAndRelationTypeCodeAllCrm", param);
    }

    /**
     * @description 根据商品用户编号、产品关系编号和角色编号查询用户UU关系
     * @author xunyl
     * @date 2014-07-26
     */
    public static IDataset qryRelaBBInfoByRoleCodeBForGrp(IBizCommon bc, String userIdA, String relationTypeCode, String roleCodeB) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", userIdA);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put("ROLE_CODE_B", roleCodeB);
        return CSViewCall.call(bc, "CS.RelaBBInfoQrySVC.qryRelaBBInfoByRoleCodeBForGrp", inparam);
    }

    /**
     * @description 根据userIdB和产品关系编号查询BB关系信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBInfoByUserIdBRelaTypeCode(IBizCommon bc, String userIdB, String relationTypeCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", userIdB);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);

        return CSViewCall.call(bc, "CS.RelaBBInfoQrySVC.qryRelaBBInfoByUserIdBRelaTypeCode", inparam);
    }

    /**
     * 通过集团用户ID 和业务类型查询全地州订购关系列表
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaBBInfosByUserIdAAndRelationTypeCodeAllCrm(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpUserId);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        return CSViewCall.call(bc, "CS.RelaBBInfoQrySVC.getAllMebByUSERIDA", inparam);
    }

    /**
     * 通过集团用户ID和成员用户ID查询订购关系
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaBBInfosByUserIdBAndUserIdA(IBizCommon bc, String userIdB, String userIdA, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", userIdB);
        inparam.put("USER_ID_A", userIdA);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaBBInfoQrySVC.qryBBInfoByUserIdAB", inparam);
    }
}
