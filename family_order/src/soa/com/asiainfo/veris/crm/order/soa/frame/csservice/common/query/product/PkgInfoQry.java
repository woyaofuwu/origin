
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;

public class PkgInfoQry extends CSBizBean
{
    /**
     * 根据集团product_id查询成员产品下的包
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMemPackageByGrpProduct(String product_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_MEMTREE", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getPackageByPackage(String packageId, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_PACKAGE_EXT", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据package_id查询包信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getPackageByPK(String package_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        IDataset dataset = Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /**
     * 根据product_id查询产品下的包
     * 
     * @param data
     * @return
     * @throws Exception
     * @author fanwenhui
     */
    public static IDataset getPackageByProduct(String product_id, String user_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据product_id查询产品下的包
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProduct_staff(String product_id, String user_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE", data, eparchy_code);
    }

    /**
     * 查询TF_F_USER_GRP_PACKAGE中定义的用户可订购的产品包
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProductFromGrpPck(String productId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("USER_ID", userId);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_FROM_GRPPCK", data, Route.CONN_CRM_CG);
    }

    /**
     * 查询TF_F_USER_GRP_PACKAGE中定义的用户可订购的产品包
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProductFromGrpPckNoPriv(String productId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_FROM_GRPPCK_NO_PRIV", data, Route.CONN_CRM_CG);
    }

    /**
     * 查询TF_F_USER_GRP_PACKAGE中定义的用户可订购的产品包
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProductFromGrpPckWithParentGroup(String productId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("USER_ID", userId);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_FROM_GRPPCK_WITH_PARNET_GROUP", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据product_id查询产品下的包
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProductNoPriv(String product_id, String user_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_NO_PRIV", data);
    }

    /**
     * 查询包名称
     */
    public static String getPackageNameByPackageId(String packageId) throws Exception
    {

        return UPackageInfoQry.getNameByPackageId(packageId);
    }

    /**
     * 查询包类型
     */
    public static String getPackageTypeByPackageId(String packageId) throws Exception
    {

        return StaticUtil.getStaticValue(getVisit(), "TD_B_PACKAGE", "PACKAGE_ID", "PACKAGE_TYPE_CODE", packageId);
    }

    public static IDataset getPackageUnlimit(String userId, String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PACKAGE_ID", packageId);

        return Dao.qryByCode("TD_B_PACKAGE", "SEL_PACKAGE_UNLIMIT", param);
    }

    /**
     * @Description: 待权限查询产品下的可办理包
     * @param product_id
     * @param tradeStaffId
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 23, 2014 5:13:17 PM
     */
    public static IDataset queryGiftSalePackageByPRoductId(String product_id, String tradeStaffId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        IDataset packages = Dao.qryByCodeParser("TD_B_PACKAGE", "SEL_GIFT_SALEPACKAGE_BY_PID_FOR_TREE", data, Route.CONN_CRM_CEN);
        PackagePrivUtil.filterPackageListByPriv(tradeStaffId, packages);
        return packages;
    }

    public static IDataset queryPackageByProductAndStaff(String product_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("EPARCHY_CODE", eparchy_code);
        // data.put("TRADE_STAFF_ID", staff_id);
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_HNAN2_NEW", data, eparchy_code);
    }

    /**
     * 根据产品查询购机类的营销包
     * 
     * @param data
     * @return
     * @throws Exception
     * @author awx
     * @date 2009-9-22
     */
    public static IDataset queryPhoneSalePackageByPRoductId(String product_id, String rsrv_str2, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("RSRV_STR2", rsrv_str2);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            data.put("EPARCHY_CODE", eparchy_code);
            return Dao.qryByCodeParser("TD_B_PACKAGE", "SEL_PHONE_SALEPACKAGE_BY_PID_FOR_TREE1", data, Route.CONN_CRM_CEN);
        }
        else
        {
            return Dao.qryByCodeParser("TD_B_PACKAGE", "SEL_PHONE_SALEPACKAGE_BY_PID_FOR_TREE", data, Route.CONN_CRM_CEN);
        }
    }

    /**
     * 根据PRODUCT_ID查询营销活动的产品下的包
     * 
     * @author luojh
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset querySaleProductPackage(String product_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        return Dao.qryByCodeParser("TD_B_PACKAGE", "SEL_SALEPACKAGE_BY_PID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryPackageById(String packageId)throws Exception{
    	 IData data = new DataMap();
         data.put("PACKAGE_ID", packageId);
         return Dao.qryByCode("TD_B_PACKAGE", "QRY_PACKAGE_BY_ID", data,Route.CONN_CRM_CEN);
    }
}
