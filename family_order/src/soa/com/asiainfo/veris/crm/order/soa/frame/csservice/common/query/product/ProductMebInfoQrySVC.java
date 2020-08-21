
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductMebInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset getMebProduct(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset data = ProductMebInfoQry.getMebProduct(productId);

        return data;
    }

    public IDataset getMebProductNoRight(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset data = ProductMebInfoQry.getMebProductNoRight(productId);

        return data;
    }

    /**
     * 查询成员附加基本产品
     */
    public IDataset getMemberMainProductByProductId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);
        IDataset datas = new DatasetList();
        IData data = new DataMap();
        data.put("PRODUCT_ID_B", memProductId);
        datas.add(data);
        return datas;
    }

    public IDataset getProductMebByPidB(IData input) throws Exception
    {
        String productIdB = input.getString("PRODUCT_ID_B");
        IDataset data = ProductMebInfoQry.getProductMebByPidB(productIdB);

        return data;
    }

    /**
     * @author fengsl
     * @date 2014-04-12 根据product_id查询成员附加产品
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getProductMebByPidC(IData input) throws Exception
    {
        String productIdB = input.getString("PRODUCT_ID_B");
        IDataset data = ProductMebInfoQry.getProductMebByPidC(productIdB);

        return data;
    }

}
