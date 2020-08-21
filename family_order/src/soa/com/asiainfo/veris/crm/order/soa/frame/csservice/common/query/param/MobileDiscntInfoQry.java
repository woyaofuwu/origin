
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MobileDiscntInfoQry
{

    public static IDataset queryMobileDiscntByPkgId(String packageId, String activeTime, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        cond.put("EPARCHY_CODE", eparchyCode);
        cond.put("ACTIVATE_TIME", activeTime);
        IDataset results = Dao.qryByCode("TD_B_MOBILE_DISCNT", "SEL_BY_PACKAGEID_EPAR_DATE", cond);

        return results;
    }
}
