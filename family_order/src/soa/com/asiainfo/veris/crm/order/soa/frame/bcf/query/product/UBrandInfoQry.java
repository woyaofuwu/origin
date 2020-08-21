
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UBrandInfoQry
{
    /**
     * 根据品牌编码查询品牌名称
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static String getBrandNameByBrandCode(String brandCode) throws Exception
    {
        return UpcCall.queryBrandNameByChaVal(brandCode);
    }
    /**
     * 获取品牌编码和品牌名称列表
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static IDataset getBrandList() throws Exception
    {
        return UpcCall.queryBrandList("BRAND_CODE");

    }
}
