
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productinfo.ProductInfoIntf;

public class ProductInfoIntfViewUtil
{
    /**
     * 通过产品ID查询包返回dataset
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPackagesByProductId(IBizCommon bc, String productId, String eparchyCode) throws Exception
    {
        IDataset infosDataset = ProductInfoIntf.getPackagesByProductId(bc, productId, eparchyCode);

        return infosDataset;
    }

    /**
     * 通过产品ID查询产品表中的BRAND_CODE
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static String qryBrandCodeStrByProductId(IBizCommon bc, String productId) throws Exception
    {
        return qryBrandCodeStrByProductId(bc, productId, true);
    }

    /**
     * 通过产品ID查询产品表中的BRAND_CODE
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static String qryBrandCodeStrByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        String productNameStr = "";
        IData productInfo = qryProductInfoByProductId(bc, productId, isThrowException);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            return productInfo.getString("BRAND_CODE", "");
        }
        if (isThrowException)
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_135, productId);
            return null;
        }
        return productNameStr;
    }

    /**
     * 通过产品ID查询产品信息返回data
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryProductInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        return qryProductInfoByProductId(bc, productId, true);
    }

    /**
     * 通过产品ID查询产品信息返回data
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     *            ,true 查询不到数据抛出异常, false 查询不到数据返回null
     * @return
     * @throws Exception
     */
    public static IData qryProductInfoByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        IData resultData = null;
        IDataset infosDataset = qryProductInsfoByProductId(bc, productId);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            resultData = infosDataset.getData(0);
        }
        if (IDataUtil.isEmpty(resultData))
        {
            if (isThrowException)
            {
                CSViewException.apperr(ProductException.CRM_PRODUCT_135, productId);
                return null;
            }
        }
        return resultData;
    }

    /**
     * 通过产品ID查询产品信息返回dataset
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductInsfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IDataset infosDataset = ProductInfoIntf.qryProductInfoByProductId(bc, productId);

        return infosDataset;
    }

    /**
     * 通过产品ID查询产品表中的PRODUCT_NAME
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static String qryProductNameStrByProductId(IBizCommon bc, String productId) throws Exception
    {
        return qryProductNameStrByProductId(bc, productId, true);
    }

    /**
     * 通过产品ID查询产品表中的PRODUCT_NAME
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static String qryProductNameStrByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        String productNameStr = "";
        IData productInfo = qryProductInfoByProductId(bc, productId, isThrowException);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            return productInfo.getString("PRODUCT_NAME", "");
        }
        if (isThrowException)
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_135, productId);
            return null;
        }
        return productNameStr;
    }

    /**
     * 通过活动类型查询营销产品信息返回dataset
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset querySaleActiveProduct(IBizCommon bc, String campnType, String eparchyCode) throws Exception
    {
        IDataset infosDataset = ProductInfoIntf.querySaleActiveProduct(bc, campnType, eparchyCode);

        return infosDataset;
    }
}
