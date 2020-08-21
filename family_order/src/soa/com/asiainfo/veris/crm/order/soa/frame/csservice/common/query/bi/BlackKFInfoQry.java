
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bi;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BlackKFInfoQry
{

    public static IDataset queryAllBySnDT(String serialNumber, String eparchyCode, String dealTag, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("DEAL_TAG", dealTag);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TI_BI_BLACK_KF", "SEL_ALL_BY_SN_O_DT", param, pagination);

    }

}
