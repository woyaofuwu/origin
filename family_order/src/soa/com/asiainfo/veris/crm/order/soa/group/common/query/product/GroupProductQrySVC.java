
package com.asiainfo.veris.crm.order.soa.group.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class GroupProductQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static String className = GroupProductInfoQry.class.getName();

    static IData qryData = new DataMap();

    static
    {
        qryData.put("QRY_PRODUCT_TYPE", "qryProductType");
        qryData.put("QRY_PRODUCT_LIST", "qryProductList");
    }

    public IDataset qryGrpProductCommon(IData inParam) throws Exception
    {
        String qryType = inParam.getString("QRY_TYPE");

        String qryMethod = qryData.getString(qryType);

        return (IDataset) GrpInvoker.invoker(className, qryMethod, new Object[]
        { inParam }, new Class[]
        { IData.class });
    }

}
