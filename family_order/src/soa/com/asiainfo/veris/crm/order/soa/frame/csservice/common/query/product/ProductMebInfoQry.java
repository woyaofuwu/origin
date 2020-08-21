
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;

public class ProductMebInfoQry extends CSBizBean
{
    /**
     * 根据集团成员product_id查询，集团产品名称
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String getGrpProductNameByPidB(String productIdB) throws Exception
    {
        IDataset products = UProductMebInfoQry.queryGrpProductInfosByMebProductId(productIdB);
        if(IDataUtil.isNotEmpty(products))
        {
            return products.getData(0).getString("PRODUCT_NAME");
        }
        else
        {
            return "";
        }
    }

    /**
     * 根据product_id查询成员可订购产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMebProduct(String productId) throws Exception
    {
        return UProductMebInfoQry.getMemberProductByGrpProductId(productId);
    }

    /**
     * 根据product_id查询成员附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMebProductNoRight(String productId) throws Exception
    {
        return UProductMebInfoQry.getMemberProductByGrpProductId(productId);
    }

    /**
     * 根据product_id查询成员可订购的营销产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMebSaleProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PRODUCT_MEB", "SEL_BY_PID_SALE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据product_id查询成员营销产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMebSaleProductNoRight(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_B_PRODUCT_MEB", "SEL_BY_PID_SALE_NORIGHT", data);
    }

    /**
     * 查询成员附加基本产品
     */
    public static String getMemberMainProductByProductId(String productId) throws Exception
    {
        return UProductMebInfoQry.getMemberMainProductByProductId(productId);
    }

    /**
     * 根据集团产品id和操作员工号查询成员产品id TODO
     * 
     * @param staffId
     *            String 操作员工号
     * @param productId
     *            String 产品ID
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String getMemberProductId(String staffId, String productId) throws Exception
    {

        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        if(StringUtils.isEmpty(mebProductId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_248, productId, staffId);
            return null;
        }
        else
        {
            return mebProductId;
        }
        
    }

    public static String getMemberProductIdByProductId(String productId) throws Exception
    {
        return UProductMebInfoQry.getMemberMainProductByProductId(productId);
    }

    /**
     * 通过成员服务查询主产品ID
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getProByMemSerId(String serviceId) throws Exception
    {
        //这个是个错误接口 ，订单中心没做修改  lim
//        IData param = new DataMap();
//        param.put("SERVICE_ID param", serviceId);
//
//        return Dao.qryByCode("TD_B_PRODUCT_MEB", "SEL_BY_MEM_SERID", param, Route.CONN_CRM_CEN);
        return null;
    }

    /**
     * 根据product_id查询成员附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductMebByPidB(String productIdB) throws Exception
    {
        return UProductMebInfoQry.queryGrpProductInfosByMebProductId(productIdB);
    }

    /**
     * 根据product_id查询成员附加产品
     * 
     * @author fensl
     * @date 2013-04-12 *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductMebByPidC(String productIdB) throws Exception
    {
        return UProductMebInfoQry.queryGrpProductInfosByMebProductId(productIdB);
        
    }

    /**
     * 根据product_id查询产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryProduct(String productId) throws Exception
    {
        return UProductMebInfoQry.qryProduct(productId);

    }
}
