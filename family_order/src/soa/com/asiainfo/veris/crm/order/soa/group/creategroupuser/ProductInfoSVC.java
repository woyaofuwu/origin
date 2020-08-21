
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;

public class ProductInfoSVC extends CSBizService
{

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    public IDataset genGrpSn(IData input) throws Exception
    {
        IData data = GrpGenSn.genGrpSn(input);
        IDataset result = new DatasetList();
        result.add(data);
        return result;
    }

    public IDataset getAccountInfoInit(IData input) throws Exception
    {
        IData data = GrpGenSn.genGrpSn(input);
        IDataset result = new DatasetList();
        result.add(data);
        return result;
    }

    public IDataset getGrpSnBySelectParam(IData input) throws Exception
    {
        String groupId = input.getString("GROUP_ID");
        String productId = input.getString("PRODUCT_ID");
        // String preSn = CommparaInfoQry.getGrpSnBySelectParam(productId, groupId);
        String preSn = "";
        IData data = new DataMap();
        data.put("PRE_SN", preSn);
        IDataset result = new DatasetList();
        result.add(data);
        return result;
    }

    public IData getProductByPK(IData inpput) throws Exception
    {
        String product_id = inpput.getString("PRODUCT_ID");
        IData reData = UProductInfoQry.qryProductByPK(product_id);
        return reData;
    }
}
