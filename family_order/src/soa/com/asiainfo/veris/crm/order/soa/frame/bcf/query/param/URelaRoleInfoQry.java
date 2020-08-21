
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class URelaRoleInfoQry
{
    /**
     * 根据角色编码查询角色名称(集团)
     * 
     * @param relaTypeCode
     * @param roleCodeA
     * @return
     * @throws Exception
     */
    public static String getRoleANameByRelaTypeCodeRoleCodeA(String relaTypeCode, String roleCodeA) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("RELATION_TYPE_CODE", relaTypeCode);

        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_RELATION_ROLE", new String[]
        { "RELATION_TYPE_CODE", "ROLE_CODE_A" }, "ROLE_A", new String[]
        { relaTypeCode, roleCodeA });
    }

    /**
     * 根据角色编码查询角色名称(成员)
     * 
     * @param relaTypeCode
     * @param roleCodeB
     * @return
     * @throws Exception
     */
    public static String getRoleBNameByRelaTypeCodeRoleCodeB(String relaTypeCode, String roleCodeB) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("RELATION_TYPE_CODE", relaTypeCode);

        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_RELATION_ROLE", new String[]
        { "RELATION_TYPE_CODE", "ROLE_CODE_B" }, "ROLE_B", new String[]
        { relaTypeCode, roleCodeB });
    }

    /**
     * 根据关系类型编码得到所有角色编码
     * 
     * @param relaTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaRoleInfoByRelaTypeCode(String relaTypeCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("RELATION_TYPE_CODE", relaTypeCode);

        IDataset dataset = Dao.qryByCode("TD_S_RELATION_ROLE", "SEL_BY_RELATIONTYPE", inparams, Route.CONN_CRM_CEN);

        return dataset;
    }

}
