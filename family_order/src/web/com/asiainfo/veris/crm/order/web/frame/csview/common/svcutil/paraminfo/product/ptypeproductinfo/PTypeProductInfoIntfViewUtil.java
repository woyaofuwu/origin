
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.ptypeproductinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.ptypeproductinfo.PTypeProductInfoIntf;

public class PTypeProductInfoIntfViewUtil
{
    /**
     * 判断产品ID是否是集团产品树下的产品
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean ifGrpProductTypeBooByProductId(IBizCommon bc, String productId) throws Exception
    {
        boolean resultB = false;
        IData productTypeInfoData = qryProductTypeInfoByProductIdAndParentPtypeCode(bc, productId, "1000", false);
        if (IDataUtil.isNotEmpty(productTypeInfoData))
        {
            resultB = true;
        }

        return resultB;
    }

    /**
     * 通过产品类型编码，地州编码查询产品类型下包含的产品列表
     * 
     * @param bc
     * @param productTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryProductInfosByProductTypeCodeAndEparchyCode(IBizCommon bc, String productTypeCode, String eparchyCode) throws Exception
    {
        IDataset infosDataset = PTypeProductInfoIntf.qryProductInfosByProductTypeCodeAndEparchyCode(bc, productTypeCode, eparchyCode);

        return infosDataset;
    }

    /**
     * 通过产品编码和父产品类型查询产品类型信息
     * 
     * @param bc
     * @param productId
     * @param parentPtypeCode
     * @return
     * @throws Exception
     */
    public static IData qryProductTypeInfoByProductIdAndParentPtypeCode(IBizCommon bc, String productId, String parentPtypeCode) throws Exception
    {
        return qryProductTypeInfoByProductIdAndParentPtypeCode(bc, productId, parentPtypeCode);
    }

    /**
     * 通过产品编码和父产品类型查询产品类型信息
     * 
     * @param bc
     * @param productId
     * @param parentPtypeCode
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryProductTypeInfoByProductIdAndParentPtypeCode(IBizCommon bc, String productId, String parentPtypeCode, boolean isThrowException) throws Exception
    {
        IDataset infosDataset = PTypeProductInfoIntf.qryProductTypeInfosByProductIdAndParentPtypeCode(bc, productId, parentPtypeCode);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            return infosDataset.getData(0);
        }
        if (isThrowException)
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_241, productId, parentPtypeCode);
            return null;
        }

        return null;
    }

}
