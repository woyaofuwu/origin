
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationuuinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class RelationUUInfoIntf
{

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

        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.qryCountByUserIdAAndRelationTypeCodeAllCrm", param);
    }

    /**
     * 通过SERIAL_NUMBER_B查询UU订购关系
     * 
     * @param bc
     * @param serialNumberB
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosBySerialNumberB(IBizCommon bc, String serialNumberB, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER_B", serialNumberB);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getRelatsBySNB", inparam);
    }

    /**
     * 通过SERIAL_NUMBER_B,RELATION_TYPE_CODE查询订购的UU关系
     * 
     * @param bc
     * @param serialNumberB
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosBySerialNumberBAndRelationTypeCode(IBizCommon bc, String serialNumberB, String relationTypeCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER_B", serialNumberB);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.qryRelaBySerialNumberB", inparam);
    }

    /**
     * 通过集团用户ID 和业务类型查询订购关系列表
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdAAndRelationTypeCode(IBizCommon bc, String grpUserId, String relationTypeCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpUserId);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIda", inparam);
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
    public static IDataset qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", grpUserId);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getAllMebByUSERIDA", inparam);
    }

    /**
     * 通过USER_ID_A,RELATION_TYPE_CODE,ROLE_CODE_B查询UU数据
     * 
     * @param bc
     * @param userIdA
     * @param relationTypeCode
     * @param roleCodeB
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(IBizCommon bc, String userIdA, String relationTypeCode, String roleCodeB, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", userIdA);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put("ROLE_CODE_B", roleCodeB);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getRelaUUByUserRoleA", inparam);
    }

    /**
     * 通过USER_ID_A ,RELATION_TYPE_CODE ,SHORT_CODE 查询全地州库订购关系数据，用来验证短号是否存在
     * 
     * @param bc
     * @param userIdA
     * @param shortCode
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdAAndShortCodeRelationTypeCodeAllCrm(IBizCommon bc, String userIdA, String shortCode, String relationTypeCode) throws Exception
    {
        IData inparam = new DataMap();
        if (StringUtils.isNotBlank(userIdA))
        {
            inparam.put("USER_ID_A", userIdA);
        }
        if (StringUtils.isNotBlank(relationTypeCode))
        {
            inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        }
        if (StringUtils.isNotBlank(shortCode))
        {
            inparam.put("SHORT_CODE", shortCode);
        }
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.chkShortCodeByUserIdAAndShortCodeAllCrm", inparam);
    }

    /**
     * 查询号码订购的所有UU订购关系列表
     * 
     * @param bc
     * @param mebUserId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdB(IBizCommon bc, String mebUserId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", mebUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUseridB", inparam);
    }

    /**
     * 通过成员用户ID和业务类型查询订购关系
     * 
     * @param bc
     * @param userIdB
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdBAndRelationTypeCode(IBizCommon bc, String userIdB, String relationTypeCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", userIdB);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIdBAndRelaTypeCode", inparam);
    }

    /**
     * 通过集团用户ID和成员用户ID查询订购关系
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdBAndUserIdA(IBizCommon bc, String userIdB, String userIdA, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", userIdB);
        inparam.put("USER_ID_A", userIdA);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.getUUInfoByUserIdAB", inparam);
    }

    /**
     * 通过成员用户ID,集团用户ID,关系类型查询订购UU数据
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(IBizCommon bc, String userIdB, String userIdA, String relationTypeCode, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", userIdB);
        inparam.put("USER_ID_A", userIdA);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.qryUU", inparam);
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
    public static IDataset qryCountKDMemForAllCrm(IBizCommon bc, String userIdA, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        return CSViewCall.call(bc, "CS.RelaUUInfoQrySVC.qryCountKDMemForAllCrm", param);
    }

}
