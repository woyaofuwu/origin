
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationbbinfo.RelationBBInfoIntf;

public class RelationBBInfoIntfViewUtil
{
    /**
     * @description 根据userIdA、userIdB、relationTypeCode、roleCodeB查询BB关系信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset getBBByUserIdAB(IBizCommon bc, String userIdA, String userIdB, String roleCodeB, String relationTypeCode, String routeId) throws Exception
    {
        IDataset infosDataset = RelationBBInfoIntf.getBBByUserIdAB(bc, userIdA, userIdB, roleCodeB, relationTypeCode, routeId);
        return infosDataset;
    }

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
        IDataset countList = RelationBBInfoIntf.qryCountByUserIdAAndRelationTypeCodeAllCrm(bc, userIdA, relationTypeCode);

        if (IDataUtil.isNotEmpty(countList))
        {
            return countList.getData(0).getString("RECORDCOUNT");
        }

        return "0";
    }

    /**
     * @description 根据商品用户编号、产品关系编号和角色编号查询用户UU关系
     * @author xunyl
     * @date 2014-07-26
     */
    public static IDataset qryRelaBBInfoByRoleCodeBForGrp(IBizCommon bc, String userIdA, String relationTypeCode, String roleCodeB) throws Exception
    {
        IDataset infosDataset = RelationBBInfoIntf.qryRelaBBInfoByRoleCodeBForGrp(bc, userIdA, relationTypeCode, roleCodeB);
        return infosDataset;
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
    public static IData qryRelaBBInfoByUserIdBAndUserIdA(IBizCommon bc, String userIdB, String userIdA, String routeId) throws Exception
    {
        IDataset relaList = RelationBBInfoIntf.qryRelaBBInfosByUserIdBAndUserIdA(bc, userIdB, userIdA, routeId);

        IData relaData = new DataMap();

        if (IDataUtil.isNotEmpty(relaList))
        {
            relaData = relaList.getData(0);
        }

        return relaData;
    }

    /**
     * @description 根据userIdB和产品关系编号查询BB关系信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBInfoByUserIdBRelaTypeCode(IBizCommon bc, String userIdB, String relationTypeCode, String routeId) throws Exception
    {
        IDataset infosDataset = RelationBBInfoIntf.qryRelaBBInfoByUserIdBRelaTypeCode(bc, userIdB, relationTypeCode, routeId);
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
    public static IDataset qryRelaBBInfosByUserIdAAndRelationTypeCodeAllCrm(IBizCommon bc, String grpUserId, String relationTypeCode) throws Exception
    {
        IDataset infosDataset = RelationBBInfoIntf.qryRelaBBInfosByUserIdAAndRelationTypeCodeAllCrm(bc, grpUserId, relationTypeCode);
        return infosDataset;

    }

    /**
     * 通过集团用户ID和成员用户ID查询订购关系的ROLE_CODE_B
     * 
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static String qryRelaBBRoleCodeBByUserIdBAndUserIdA(IBizCommon bc, String userIdB, String userIdA, String routeId) throws Exception
    {
        IData relaData = qryRelaBBInfoByUserIdBAndUserIdA(bc, userIdB, userIdA, routeId);

        String roleCodeB = "";

        if (IDataUtil.isNotEmpty(relaData))
        {
            roleCodeB = relaData.getString("ROLE_CODE_B");
        }

        return roleCodeB;
    }

}
