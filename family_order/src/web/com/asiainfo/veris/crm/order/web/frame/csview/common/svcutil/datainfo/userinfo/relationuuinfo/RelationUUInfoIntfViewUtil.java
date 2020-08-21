
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationuuinfo.RelationUUInfoIntf;

public class RelationUUInfoIntfViewUtil
{
    /**
     * 根据USER_ID_A和RELATION_TYPE_CODE统计成员数量
     * 
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static String qryCountByUserIdAAndRelationTypeCodeAllCrm(IBizCommon bc, String userIdA, String relationTypeCode) throws Exception
    {
        IDataset countList = RelationUUInfoIntf.qryCountByUserIdAAndRelationTypeCodeAllCrm(bc, userIdA, relationTypeCode);

        if (IDataUtil.isNotEmpty(countList))
        {
            return countList.getData(0).getString("RECORDCOUNT");
        }

        return "0";
    }

    /**
     * 通过集团用户ID和业务类型查询cg库的订购关系,集团用户ID匹配表中的USER_ID_B返回data类型
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IData qryGrpRelaUUInfoByUserIdBAndRelationTypeCode(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        IData result = null;
        IDataset relationInfosDataset = qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(bc, grpUserId, relationTypeCode);
        if (IDataUtil.isNotEmpty(relationInfosDataset))
        {
            result = relationInfosDataset.getData(0);
        }
        return result;

    }

    /**
     * 通过集团用户ID和业务类型查询cg库的订购关系,集团用户ID匹配表中的USER_ID_B返回data类型
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpRelaUUInfoByUserIdBAndRelationTypeCode(IBizCommon bc, String grpUserId, String relationTypeCode, boolean isThrowException) throws Exception
    {
        IData result = null;
        IDataset relationInfosDataset = qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(bc, grpUserId, relationTypeCode, isThrowException);
        if (IDataUtil.isNotEmpty(relationInfosDataset))
        {
            result = relationInfosDataset.getData(0);
            return result;
        }
        if (isThrowException)
        {
            CSViewException.apperr(UUException.CRM_UU_103, grpUserId, relationTypeCode);
            return null;
        }

        return null;

    }

    /**
     * 过USER_ID_B 和 USER_ID_A 和 RELATION_TYPE_CODE 查询cg库UU订购关系,返回DATA类型
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IData qryGrpRelaUUInfoByUserIdBAndUserIdARelationTypeCode(IBizCommon bc, String userIdB, String userIdA, String relationTypeCode) throws Exception
    {
        return qryGrpRelaUUInfoByUserIdBAndUserIdARelationTypeCode(bc, userIdB, userIdA, relationTypeCode, true);

    }

    /**
     * 过USER_ID_B 和 USER_ID_A 和 RELATION_TYPE_CODE 查询cg库UU订购关系,返回DATA类型
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param relationTypeCode
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryGrpRelaUUInfoByUserIdBAndUserIdARelationTypeCode(IBizCommon bc, String userIdB, String userIdA, String relationTypeCode, boolean isThrowException) throws Exception
    {
        return qryRelaUUInfoByUserIdBAndUserIdARelationTypeCode(bc, userIdB, userIdA, relationTypeCode, Route.CONN_CRM_CG, isThrowException);

    }

    /**
     * 查询cg库：通过集团用户ID 和业务类型查询订购UU关系列表
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdAAndRelationTypeCode(bc, grpUserId, relationTypeCode, Route.CONN_CRM_CG);
        return infosDataset;

    }

    /**
     * 通过USER_ID_A,RELATION_TYPE_CODE,ROLE_CODE_B查询CG库的UU数据
     * 
     * @param bc
     * @param userIdA
     * @param relationTypeCode
     * @param roleCodeB
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(IBizCommon bc, String userIdA, String relationTypeCode, String roleCodeB) throws Exception
    {
        IDataset infosDataset = qryRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(bc, userIdA, relationTypeCode, roleCodeB, Route.CONN_CRM_CG);
        return infosDataset;

    }

    /**
     * 通过集团用户ID和业务类型查询cg库的订购关系，集团用户ID匹配表中的USER_ID_B
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        return qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(bc, grpUserId, relationTypeCode, true);

    }

    /**
     * 通过集团用户ID和业务类型查询cg库的订购关系，集团用户ID匹配表中的USER_ID_B
     * 
     * @param bc
     * @param userIdB
     * @param relationTypeCode
     * @param isThrowException
     *            true 查询不到数据抛出异常
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(IBizCommon bc, String userIdB, String relationTypeCode, boolean isThrowException) throws Exception
    {
        return qryRelaUUInfosByUserIdBAndRelationTypeCode(bc, userIdB, relationTypeCode, Route.CONN_CRM_CG, isThrowException);

    }

    /**
     * 通过USER_ID_B,USER_ID_A,RELATION_TYPE_CODE查询集团库的UU信息
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpRelaUUInfosByUserIdBAndUserIdARelationTypeCode(IBizCommon bc, String userIdB, String userIdA, String relationTypeCode) throws Exception
    {
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(bc, userIdB, userIdA, relationTypeCode, Route.CONN_CRM_CG);
        return infosDataset;

    }

    /**
     * 通过成员用户ID和业务类型查询订购关系，返回IData类型数据
     * 
     * @param bc
     * @param mebUserId
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryRelaUUInfoByUserIdBAndRelationTypeCode(IBizCommon bc, String mebUserId, String relationTypeCode, String routeId) throws Exception
    {
        IDataset infosDataset = qryRelaUUInfosByUserIdBAndRelationTypeCode(bc, mebUserId, relationTypeCode, routeId, true);
        if (IDataUtil.isEmpty(infosDataset))
        {
            CSViewException.apperr(UUException.CRM_UU_103, mebUserId, relationTypeCode);
            return null;
        }
        return infosDataset.getData(0);

    }

    /**
     * 通过成员用户ID和业务类型查询订购关系，返回IData类型数据
     * 
     * @param bc
     * @param mebUserId
     * @param relationTypeCode
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryRelaUUInfoByUserIdBAndRelationTypeCode(IBizCommon bc, String mebUserId, String relationTypeCode, String routeId, boolean isThrowException) throws Exception
    {
        IDataset infosDataset = qryRelaUUInfosByUserIdBAndRelationTypeCode(bc, mebUserId, relationTypeCode, routeId, isThrowException);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }
        if (isThrowException)
        {
            CSViewException.apperr(UUException.CRM_UU_103, mebUserId, relationTypeCode);
            return null;
        }
        return null;

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
    public static IData qryRelaUUInfoByUserIdBAndUserIdA(IBizCommon bc, String userIdB, String userIdA, String routeId) throws Exception
    {
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdBAndUserIdA(bc, userIdB, userIdA, routeId);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            result = infosDataset.getData(0);
        }

        return result;

    }

    /**
     * 通过USER_ID_B 和 USER_ID_A 和 RELATION_TYPE_CODE 查询UU订购关系,返回DATA类型
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryRelaUUInfoByUserIdBAndUserIdARelationTypeCode(IBizCommon bc, String userIdB, String userIdA, String relationTypeCode, String routeId) throws Exception
    {
        IData info = qryRelaUUInfoByUserIdBAndUserIdARelationTypeCode(bc, userIdB, userIdA, relationTypeCode, routeId, true);

        return info;
    }

    /**
     * 通过USER_ID_B 和 USER_ID_A 和 RELATION_TYPE_CODE 查询UU订购关系,返回DATA类型
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param relationTypeCode
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryRelaUUInfoByUserIdBAndUserIdARelationTypeCode(IBizCommon bc, String userIdB, String userIdA, String relationTypeCode, String routeId, boolean isThrowException) throws Exception
    {
        IDataset infosDataset = qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(bc, userIdB, userIdA, relationTypeCode, routeId);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }
        if (isThrowException)
        {
            CSViewException.apperr(UUException.CRM_UU_104, userIdB, userIdA, relationTypeCode);
            return null;
        }
        return null;

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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosBySerialNumberB(bc, serialNumberB, routeId);
        return infosDataset;

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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosBySerialNumberBAndRelationTypeCode(bc, serialNumberB, relationTypeCode, routeId);
        return infosDataset;

    }

    /**
     * 通过集团用户ID 和业务类型查询顶管关系列表
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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdAAndRelationTypeCode(bc, grpUserId, relationTypeCode, routeId);
        return infosDataset;

    }

    /**
     * 通过集团用户ID 和业务类型查询订购该用户的所有地州库上的订购关系列表
     * 
     * @param bc
     * @param grpUserId
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(bc, grpUserId, relationTypeCode);
        return infosDataset;

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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(bc, userIdA, relationTypeCode, roleCodeB, routeId);
        return infosDataset;

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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdAAndShortCodeRelationTypeCodeAllCrm(bc, userIdA, shortCode, relationTypeCode);
        return infosDataset;

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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdB(bc, mebUserId, routeId);
        return infosDataset;

    }

    /**
     * 通过成员用户ID和业务类型查询订购关系
     * 
     * @param bc
     * @param mebUserId
     * @param relationTypeCode
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdBAndRelationTypeCode(IBizCommon bc, String mebUserId, String relationTypeCode, String routeId) throws Exception
    {
        IDataset infosDataset = qryRelaUUInfosByUserIdBAndRelationTypeCode(bc, mebUserId, relationTypeCode, routeId, true);
        return infosDataset;

    }

    /**
     * 通过成员用户ID和业务类型查询订购关系
     * 
     * @param bc
     * @param mebUserId
     * @param relationTypeCode
     * @param routeId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaUUInfosByUserIdBAndRelationTypeCode(IBizCommon bc, String mebUserId, String relationTypeCode, String routeId, boolean isThrowException) throws Exception
    {
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdBAndRelationTypeCode(bc, mebUserId, relationTypeCode, routeId);
        if (IDataUtil.isEmpty(infosDataset) && isThrowException)
        {
            CSViewException.apperr(UUException.CRM_UU_103, mebUserId, relationTypeCode);
        }

        return infosDataset;

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
        IDataset infosDataset = RelationUUInfoIntf.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(bc, userIdB, userIdA, relationTypeCode, routeId);
        return infosDataset;

    }

    /**
     * 通过集团用户ID和成员用户ID查询订购关系的rolecodeb
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static String qryRelaUURoleCodeBByUserIdBAndUserIdA(IBizCommon bc, String userIdB, String userIdA, String routeId) throws Exception
    {
        IData uuInfo = qryRelaUUInfoByUserIdBAndUserIdA(bc, userIdB, userIdA, routeId);
        String roleCodeB = "";
        if (IDataUtil.isNotEmpty(uuInfo))
        {
            roleCodeB = uuInfo.getString("ROLE_CODE_B");
        }

        return roleCodeB;
    }

    /**
     * 根据USER_ID_A和RELATION_TYPE_CODE统计成员数量
     * 
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static String qryCountKDUUForAllCrm(IBizCommon bc, String userIdA, String relationTypeCode) throws Exception
    {
        IDataset countList = RelationUUInfoIntf.qryCountKDMemForAllCrm(bc, userIdA, relationTypeCode);

        if (IDataUtil.isNotEmpty(countList))
        {
            return countList.getData(0).getString("RECORDCOUNT");
        }

        return "0";
    }
}
