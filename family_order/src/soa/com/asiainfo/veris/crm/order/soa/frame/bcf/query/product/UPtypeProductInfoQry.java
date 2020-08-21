package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UPtypeProductInfoQry
{
    /**
     * 
     * @Title: getproductTypeCodeByProductId
     * @Description: 根据产品id获得上级目录id
     * @param @param productId
     * @param @return
     * @param @throws Exception 设定文件
     * @return String 返回类型
     * @throws
     */
    public static IDataset getProductTypeByProductId(String productId) throws Exception
    {
        IDataset cataRelDatas = null;
        try
        {
            cataRelDatas = UpcCall.qryCatalogByOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (IDataUtil.isNotEmpty(cataRelDatas))
            {
                for (int i = 0, size = cataRelDatas.size(); i < size; i++)
                {
                    cataRelDatas.getData(i).put("PRODUCT_TYPE_CODE", cataRelDatas.getData(i).getString("CATALOG_ID"));
                }
            }
        }
        catch (Exception e)
        {
            if (e.getMessage().indexOf("UPC_ERROR_28") > -1) // 根据offer_code和offer_type查offer_id的错误暂时不抛错
            {
                return cataRelDatas;
            }
            throw e;
        }
            
        return cataRelDatas;
    }
    
    /**
     * 根据PARENT_PTYPE_CODE获取产品类型
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductsTypeByParentTypeCode(String parent_ptype_code) throws Exception
    {
        IDataset catalogs = UpcCall.qryCatalogsByUpCatalogId(parent_ptype_code);
        if (IDataUtil.isNotEmpty(catalogs))
        {
            for (int i = 0, size = catalogs.size(); i < size; i++)
            {
                IData catalog = catalogs.getData(i);
                catalog.put("PRODUCT_TYPE_CODE", catalog.getString("CATALOG_ID"));
                catalog.put("PRODUCT_TYPE_NAME", catalog.getString("CATALOG_NAME"));
            }
        }
        return catalogs;
    }
    
    /**
     * 
     * @Title: checkExisProductIdAndProductTypeCode  
     * @Description: 验证产品ID是否为特定的产品类型 
     * @param @param productId
     * @param @param productTypeCode
     * @param @return
     * @param @throws Exception    设定文件  
     * @return boolean    返回类型  
     * @throws
     */
    public static boolean checkExisProductIdAndProductTypeCode(String productId, String productTypeCode) throws Exception
    {
        IDataset offers = UpcCall.qryOffersByCatalogIdAndOfferId(productTypeCode, productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (IDataUtil.isEmpty(offers))
        {
            return false;
        }
        
        return true;
    }
    
    public static String getParentPTypeByProductTypeCode(String productTypeCode) throws Exception
    {
        IDataset catalogData = UpcCall.qryCatalogByCatalogId(productTypeCode);
        
        if (IDataUtil.isEmpty(catalogData))
        {
            return null;
        }
        
        return catalogData.getData(0).getString("UP_CATALOG_ID");
    }
    
    public static String getProductTypeNameByProductTypeCode(String productTypeCode) throws Exception
    {
        IDataset catalogData = UpcCall.qryCatalogByCatalogId(productTypeCode);
        
        if (IDataUtil.isEmpty(catalogData))
        {
            return null;
        }
        
        return catalogData.getData(0).getString("CATALOG_NAME");
    }
    
    public static IDataset getProductTypeByProductIDAndParentPType(String productId, String parentCode) throws Exception
    {
        IDataset results = UpcCall.qryCatalogByoffertIdAndupCatalogId(parentCode, productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (IDataUtil.isNotEmpty(results))
        {
            for (int i = 0, size = results.size(); i < size; i++)
            {
                IData resultData = results.getData(i);
                resultData.put("PRODUCT_TYPE_CODE", resultData.getString("CATALOG_ID"));
                resultData.put("PRODUCT_TYPE_NAME", resultData.getString("CATALOG_NAME"));
            }
        }
        return results;
    }
}
