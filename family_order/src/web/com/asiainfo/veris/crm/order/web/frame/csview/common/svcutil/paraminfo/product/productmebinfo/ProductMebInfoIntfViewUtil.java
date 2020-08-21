
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productmebinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productmebinfo.ProductMebInfoIntf;

public class ProductMebInfoIntfViewUtil
{
    /**
     * 查询成员附加基本产品
     */
    public static String getMemberMainProductByProductId(IBizCommon bc, String productId) throws Exception
    {
        IDataset infosDataset = ProductMebInfoIntf.getMemberMainProductByProductId(bc, productId);
        String productIdB = infosDataset.getData(0).getString("PRODUCT_ID_B");
        return productIdB;
    }

    /**
     * 通过集团产品ID查询成员产品列表
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMebInfosByProductId(IBizCommon bc, String productId) throws Exception
    {
        IDataset infosDataset = ProductMebInfoIntf.qryProductMebInfosByProductId(bc, productId);

        return infosDataset;
    }

    /**
     * 通过集团产品ID查询成员产品列表,权限过滤
     * 
     * @param page
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMebInfosByProductIdPriv(CSBasePage page, String productId) throws Exception
    {
        String staffIdString = page.getVisit().getStaffId();
        IDataset infosDataset = ProductMebInfoIntf.qryProductMebInfosByProductId(page, productId);
        ProductPrivUtil.filterProductListByPriv(staffIdString, infosDataset);

        return infosDataset;
    }

    /**
     * 通过集团产品ID查询成员产品列表,权限过滤
     * 
     * @param bc
     * @param productId
     * @param staffId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductMebInfosByProductIdStaffIdPriv(IBizCommon bc, String productId, String staffId) throws Exception
    {
        IDataset infosDataset = ProductMebInfoIntf.qryProductMebInfosByProductId(bc, productId);
        ProductPrivUtil.filterProductListByPriv(staffId, infosDataset);

        return infosDataset;
    }

}
