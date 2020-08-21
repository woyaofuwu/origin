
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompRelaInfoQry;

public class ProductCompRelaInfoQrySVC extends CSBizService
{
    /**
     * @Description:根据PRODUCT_ID_B、FORCE_TAG、RELATION_TYPE_CODE获取组合产品关系相关信息
     * @author wusf
     * @date 2010-1-19
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getCompRelaInfoByPIDB(IData data) throws Exception
    {
        String productIdB = data.getString("PRODUCT_ID_B");
        String forceTag = data.getString("FORCE_TAG");
        String relationTypeCode = data.getString("RELATION_TYPE_CODE");
        IDataset dataset = ProductCompRelaInfoQry.getCompRelaInfoByPIDB(productIdB, forceTag, relationTypeCode);
        return dataset;
    }

    /**
     * 查询组合产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getCompReleInfo(IData data) throws Exception
    {
        String productIdA = data.getString("PRODUCT_ID_A");
        IDataset dataset = UProductCompRelaInfoQry.getCompReleInfo(productIdA);
        return dataset;
    }

    /**
     * 查询某BBOSS商品的子产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qrySubProductInfos(IData data) throws Exception
    {
        String productId = data.getString("PRODUCT_ID");

        IDataset dataset = ProductCompRelaInfoQry.qrySubProductInfos(productId);
        return dataset;

    }

    /**
     * 查资费包信息
     * 
     * @author shixb
     * @version 创建时间：2009-5-12 下午10:10:25
     */
    public static IDataset queryProductComp(IData data) throws Exception
    {
        String productId = data.getString("PRODUCT_ID");
        String relationTypeCode = data.getString("RELATION_TYPE_CODE");
        IDataset dataset = ProductCompRelaInfoQry.queryProductComp(productId, relationTypeCode);
        return dataset;

    }

    /**
     * 根据product_id_b获得组合产品关系
     * 
     * @author shixb
     * @version 创建时间：2009-11-10 上午10:07:33
     */
    public static IDataset queryProductRealByProductB(IData data) throws Exception
    {
        String productIdB = data.getString("PRODUCT_ID_B");
        IDataset dataset = ProductCompRelaInfoQry.queryProductRealByProductB(productIdB);
        return dataset;
    }
}
