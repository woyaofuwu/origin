
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductCompInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据product_id查询TD_B_PRODUCT_COMP中的产品
     * 
     * @author xunyl
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductFromComp(IData data) throws Exception
    {
        IData idata = new DataMap();
        IDataset idataSet = new DatasetList("[]");
        String productId = data.getString("PRODUCT_ID");
        idata = ProductCompInfoQry.getProductFromComp(productId);
        idataSet.add(idata);
        return idataSet;
    }

    /**
     * 根据product_id查询关系类型
     * 
     * @author fengsl
     * @date 2013-03-21
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryRelationTypeByProductId(IData data) throws Exception
    {
        IData idata = new DataMap();
        IDataset idataSet = new DatasetList("[]");
        String productId = data.getString("PRODUCT_ID");
        idata = ProductCompInfoQry.queryRelationTypeByProductId(productId);
        idataSet.add(idata);
        return idataSet;
    }

    public IDataset getCompProductInfoByID(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset data = ProductCompInfoQry.getCompProductInfoByID(productId);

        return data;
    }

    /**
     * 查询关系类型编码
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelaTypeCodeByProductId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");

        String value = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

        IData map = new DataMap();
        map.put("RELATION_TYPE_CODE", value);

        return IDataUtil.idToIds(map);
    }

}
