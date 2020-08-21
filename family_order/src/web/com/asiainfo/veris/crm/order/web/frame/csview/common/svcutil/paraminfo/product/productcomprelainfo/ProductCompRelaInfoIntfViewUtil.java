
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntf;

public class ProductCompRelaInfoIntfViewUtil
{
    /**
     * 通过产品ID和关系类型查询关系表信息
     * 
     * @param bc
     * @param productId
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryProductCompRelaInfosByProductIdARelationTypeCode(IBizCommon bc, String productId, String relationTypeCode) throws Exception
    {
        IDataset infosDataset = ProductCompRelaInfoIntf.qryProductCompRelaInfosByProductIdARelationTypeCode(bc, productId, relationTypeCode);

        return infosDataset;
    }

    /**
     * 通过PRODUCT_ID_A,RELATION_TYPE_CODE,FORCE_TAG查询组合产品关系表信息
     * 
     * @param bc
     * @param productIdA
     *            父产品ID
     * @param relationTypeCode
     *            关系类型
     * @param forceTag
     *            必选标志 0 不必选 1必选
     * @return
     * @throws Exception
     */
    public static IDataset qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(IBizCommon bc, String productIdA, String relationTypeCode, String forceTag) throws Exception
    {
        IDataset infosDataset = ProductCompRelaInfoIntf.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(bc, productIdA, relationTypeCode, forceTag);

        return infosDataset;
    }

    /**
     * 通过PRODUCT_ID_B,RELATION_TYPE_CODE,FORCE_TAG查询组合产品关系表信息
     * 
     * @param bc
     * @param productIdB
     * @param relationTypeCode
     * @param forceTag
     * @return
     * @throws Exception
     */
    public static IDataset qryProductCompRelaInfosByProductIdBRelationTypeCodeAndForceTag(IBizCommon bc, String productIdB, String relationTypeCode, String forceTag) throws Exception
    {
        IDataset infosDataset = ProductCompRelaInfoIntf.qryProductCompRelaInfosByProductIdBRelationTypeCodeAndForceTag(bc, productIdB, relationTypeCode, forceTag);

        return infosDataset;
    }
}
