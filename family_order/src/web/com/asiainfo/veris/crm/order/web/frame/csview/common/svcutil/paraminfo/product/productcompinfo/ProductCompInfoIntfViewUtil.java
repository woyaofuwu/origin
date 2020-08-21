
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productcompinfo.ProductCompInfoIntf;

public class ProductCompInfoIntfViewUtil
{
    /**
     * 通过PRODUCTID查询组合产品信息 返回Data类型
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryProductCompInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        return qryProductCompInfoByProductId(bc, productId, true);
    }

    /**
     * 通过PRODUCTID查询组合产品信息返回Data类型
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     *            true查询不到数据抛异常 ，false 查询不到数据返回null
     * @return
     * @throws Exception
     */
    public static IData qryProductCompInfoByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        IData resultData = null;
        IDataset infosDataset = ProductCompInfoIntf.qryProductCompInfosByProductId(bc, productId);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            resultData = infosDataset.getData(0);
        }
        if (IDataUtil.isEmpty(resultData))
        {
            if (isThrowException)
            {
                CSViewException.apperr(ProductException.CRM_PRODUCT_23, productId);
                return null;
            }
        }
        return resultData;
    }

    /**
     * 通过PRODUCTID查询组合产品信息,返回dataset
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProductCompInfosByProductId(IBizCommon bc, String productId) throws Exception
    {
        IDataset infosDataset = ProductCompInfoIntf.qryProductCompInfosByProductId(bc, productId);

        return infosDataset;
    }

    /**
     * 通过产品ID查询组合产品表中的关系编码
     * 
     * @param bc
     * @param productIdqryRaltionTypeCodeByProductId
     * @return
     * @throws Exception
     */
    public static String qryRelationTypeCodeStrByProductId(IBizCommon bc, String productId) throws Exception
    {
        return qryRelationTypeCodeStrByProductId(bc, productId, true);
    }

    /**
     * 通过产品ID查询组合产品表中的关系编码
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static String qryRelationTypeCodeStrByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        String relationTypeCodeString = "";
        IData compInfoData = qryProductCompInfoByProductId(bc, productId, isThrowException);

        if (IDataUtil.isNotEmpty(compInfoData))
        {
            relationTypeCodeString = compInfoData.getString("RELATION_TYPE_CODE", "");
            return relationTypeCodeString;
        }
        // 没查询到定制信息时
        if (isThrowException)
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_23, productId);
            return null;
        }
        return relationTypeCodeString;
    }

    /**
     * 通过产品ID查询组合产品表中的定制信息标志 0 非定制 ， 1定制
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static String qryUseTagStrByProductId(IBizCommon bc, String productId) throws Exception
    {
        return qryUseTagStrByProductId(bc, productId, true);
    }

    /**
     * 通过产品ID查询组合产品表中的定制信息标志 0 非定制 ， 1定制
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static String qryUseTagStrByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        IData compInfoData = qryProductCompInfoByProductId(bc, productId, isThrowException);
        if (IDataUtil.isNotEmpty(compInfoData))
        {
            return compInfoData.getString("USE_TAG", "0");
        }
        if (isThrowException)
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_23, productId);
            return null;
        }
        return "0";
    }

}
