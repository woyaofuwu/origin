
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RelaBBInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 依据user_id_a/relation_type_code 查询所有成员
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getAllMebByUSERIDA(IData inparam) throws Exception
    {
        IDataset data = RelaBBInfoQry.qryRelationBBAll(inparam.getString("USER_ID_A", ""), inparam.getString("RELATION_TYPE_CODE", ""));
        return data;
    }

    /**
     * @description 根据userIdA、userIdB、relationTypeCode、roleCodeB查询BB关系信息
     * @author xunyl
     * @date 2014-07-26
     */
    public static IDataset getBBByUserIdAB(IData inparam) throws Exception
    {
        String userIdA = inparam.getString("USER_ID_A");
        String userIdB = inparam.getString("USER_ID_B");
        String roleCodeB = inparam.getString("ROLE_CODE_B");
        String relationTypeCode = inparam.getString("RELATION_TYPE_CODE");
        return RelaBBInfoQry.getBBByUserIdAB(userIdA, userIdB, roleCodeB, relationTypeCode);
    }

    /**
     * @description 根据商品用户编号、产品关系编号和角色编号查询用户UU关系
     * @author xunyl
     * @date 2014-07-26
     */
    public static IDataset qryRelaBBInfoByRoleCodeBForGrp(IData inparam) throws Exception
    {
        String userIdA = inparam.getString("USER_ID_A");
        String relationTypeCode = inparam.getString("RELATION_TYPE_CODE");
        String roleCodeB = inparam.getString("ROLE_CODE_B");
        return RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userIdA, relationTypeCode, roleCodeB);
    }

    /**
     * @description 根据userIdB和产品关系编号查询BB关系信息
     * @author xunyl
     * @date 2014-07-26
     */
    public static IDataset qryRelaBBInfoByUserIdBRelaTypeCode(IData inparam) throws Exception
    {
        String userIdB = inparam.getString("USER_ID_B");
        String relationTypeCode = inparam.getString("RELATION_TYPE_CODE");
        String routeId = BizRoute.getRouteId();
        return RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(userIdB, relationTypeCode, routeId);
    }

    public IDataset qryBBInfoByUserIdAB(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A");
        String userIdB = input.getString("USER_ID_B");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");

        return RelaBBInfoQry.qryBB(userIdA, userIdB, relationTypeCode, getPagination());
    }

    public IDataset qryCountByUserIdAAndRelationTypeCodeAllCrm(IData iData) throws Exception
    {
        String userIdA = iData.getString("USER_ID_A");

        String relationTypeCode = iData.getString("RELATION_TYPE_CODE");

        return RelaBBInfoQry.qryCountByUserIdAAndRelationTypeCodeAllCrm(userIdA, relationTypeCode);
    }
}
