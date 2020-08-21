
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class GrpUserProductQryIntf
{

    /**
     * 根据PRODUCT_ID，USER_ID查询产品参数信息（提供给esop使用）
     * 
     * @param data
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset getProductInfoForEsop(IData data, Pagination pg) throws Exception
    {
        String productId = IDataUtil.chkParam(data, "PRODUCT_ID");
        String userId = IDataUtil.chkParam(data, "USER_ID");
        IDataset ids = new DatasetList();

        String brandTag = UProductInfoQry.getBrandCodeByProductId(productId);

        if ("N001".equals(brandTag))
        {// 专线类产品处理
            IData param = new DataMap();
            param.put("RSRV_VALUE_CODE", brandTag);
            param.put("USER_ID", userId);
            ids = getUserOtherInfoByCode(pg, param);
        }

        return ids;
    }

    public static IDataset getUserOtherInfoByCode(Pagination pg, IData data) throws Exception
    {

        String userId = data.getString("USER_ID");
        String rsrvValueCode = data.getString("RSRV_VALUE_CODE");
        IDataset datasetOther = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, rsrvValueCode);

        return datasetOther;
    }

}
