
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.mutexAndRely;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductLimitInfoQry;

public class ProductLimitInfoQry
{

    /**
     * 根据产品ID查询LIMIT_TAG 即依赖和互斥的关系以及对应的主产品product_id_a modify_liuxx3_20140515_01
     */
    public static IDataset getLimitTagA(String productIdB) throws Exception
    {
    	return UProductLimitInfoQry.getLimitTagA(productIdB);
    }

    /**
     * 根据产品ID查询LIMIT_TAG 即依赖和互斥的关系以及对应的主产品product_id_b modify_liuxx3_20140515_01
     */
    public static IDataset getLimitTagB(String productIdA) throws Exception
    {
    	return UProductLimitInfoQry.getLimitTagB(productIdA);

    }

}
