
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GrpProductManagerQry extends CSBizBean
{
    /**
     * 根据集团编码和联系人信息删除产品管理员信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int deleteGrpProdMgrByGrpIdGrpMgrSN(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_GRP_PRODUCT_MANAGER", "UPD_ALL_MGR_SN", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编码、联系人信息和产品管理员号码删除产品管理员信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int deleteGrpProdMgrByGrpIdGrpMgrSNProdMgrSN(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_GRP_PRODUCT_MANAGER", "UPD_BY_PRO_MGR_SN", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团客户编码和产品管理员号码查询已经删除的产品管理员信息
     * 
     * @param groupId
     *            集团客户编码
     * @param prodMgrSN
     *            联系人号码
     * @return
     * @throws Exception
     */
    public static IDataset qryDelProductMgrByGrpIdProdMgrSNGrpMgrSN(String groupId, String prodMgrSN) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_MGR_SN", prodMgrSN);
        return Dao.qryByCode("TF_F_GRP_PRODUCT_MANAGER", "SEL_BY_PK_NO_VALID", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团客户编码和联系人号码查询产品管理员信息
     * 
     * @param groupId
     * @param groupMgrSN
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMgrByGrpIdGrpMgrSN(String groupId, String groupMgrSN) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("GROUP_MGR_SN", groupMgrSN);
        return Dao.qryByCode("TF_F_GRP_PRODUCT_MANAGER", "SEL_BY_MGR_SN", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团客户编码和产品管理员手机号码查询产品管理员信息
     * 
     * @param groupId
     * @param productMgrSN
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMgrByGrpIdProdMgrSN(String groupId, String productMgrSN) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_MGR_SN", productMgrSN);
        return Dao.qryByCode("TF_F_GRP_PRODUCT_MANAGER", "SEL_MGR_PRO_ID", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团客户编码和产品管理员号码查询产品管理员信息
     * 
     * @param groupId
     * @param productMgrSN
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMgrByGrpIdProductMgrSN(String groupId, String productMgrSN) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_MGR_SN", productMgrSN);
        return Dao.qryByCode("TF_F_GRP_PRODUCT_MANAGER", "SEL_GRP_BY_MGR_SN", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据管理员手机号码查询集团产品管理员信息
     * 
     * @param productMgrSN
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMgrByMgrSN(String productMgrSN) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_MGR_SN", productMgrSN);
        return Dao.qryByCode("TF_F_GRP_PRODUCT_MANAGER", "SEL_BY_MGR_SN_ONLY", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团客户编码和产品编码查询产品管理员信息
     * 
     * @param groupId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMgrInfoByGrpIdProdId(String groupId, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_F_GRP_PRODUCT_MANAGER", "SEL_BY_PRO_MGR_SN", param, Route.CONN_CRM_CG);
    }

    /**
     * 保存集团产品管理员信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean saveGroupProductMgr(IData param) throws Exception
    {
        String groupId = param.getString("GROUP_ID"); // 集团编码
        String prodMgrSN = param.getString("PRODUCT_MGR_SN"); // 产品管理员号码
        IDataset delProdMgrDataset = qryDelProductMgrByGrpIdProdMgrSNGrpMgrSN(groupId, prodMgrSN);
        if (IDataUtil.isNotEmpty(delProdMgrDataset))
        { // 如果存在已删除的记录，则将删除记录update为有效
            return updateGrpProMgrByPKNoValid(param) > 0 ? true : false;
        }
        return Dao.insert("TF_F_GRP_PRODUCT_MANAGER", param, Route.CONN_CRM_CG);
    }

    /**
     * 更新产品管理员信息(VALID_TAG = '0')
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int updateGrpProMgrByPK(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_GRP_PRODUCT_MANAGER", "UPDATE_BY_PK", param, Route.CONN_CRM_CG);
    }

    /**
     * 更新产品管理员信息(VALID_TAG = '1')
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int updateGrpProMgrByPKNoValid(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_GRP_PRODUCT_MANAGER", "UPDATE_BY_PK_NO_VALID", param, Route.CONN_CRM_CG);
    }
}
