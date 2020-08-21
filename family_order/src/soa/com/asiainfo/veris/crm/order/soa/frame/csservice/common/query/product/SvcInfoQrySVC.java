
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;

public class SvcInfoQrySVC extends CSBizService
{

    public IDataset getServInfos(IData input) throws Exception
    {
        String service_id = input.getString("SERVICE_ID");

        return IDataUtil.idToIds(USvcInfoQry.qryServInfoBySvcId(service_id));
    }

    public IDataset getSvcByForcetag(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String trade_staff_id = input.getString("TRADE_STAFF_ID");

        return SvcInfoQry.getSvcByForcetag(productId, trade_staff_id);
    }

    public IDataset getSvcByProudctId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");

        return USvcInfoQry.qryRequireServBySvcId(productId);
    }

    public IDataset qryByPidPkgTypeCode(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String packageTypeCode = input.getString("PACKAGE_TYPE_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");

        return SvcInfoQry.qryByPidPkgTypeCode(productId, packageTypeCode, eparchyCode);
    }
}
