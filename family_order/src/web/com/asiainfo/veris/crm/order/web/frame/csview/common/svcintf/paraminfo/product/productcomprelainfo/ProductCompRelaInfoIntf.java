
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productcomprelainfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ProductCompRelaInfoIntf
{

    /**
     * 通过产品ID和关系类型查询关系表信息
     * 
     * @param bc
     * @param productIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryProductCompRelaInfosByProductIdARelationTypeCode(IBizCommon bc, String productIdA, String relationTypeCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productIdA);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        return CSViewCall.call(bc, "CS.ProductCompRelaInfoQrySVC.queryProductComp", inparam);
    }

    /**
     * 查询组合产品关系表信息
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
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productIdA);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put("FORCE_TAG", forceTag);
        return CSViewCall.call(bc, "CS.ProductInfoQrySVC.queryProductByComp", inparam);
    }

    /**
     * 查询组合产品关系表信息
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
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID_B", productIdB);
        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
        inparam.put("FORCE_TAG", forceTag);
        return CSViewCall.call(bc, "CS.ProductCompRelaInfoQrySVC.getCompRelaInfoByPIDB", inparam);
    }

}
