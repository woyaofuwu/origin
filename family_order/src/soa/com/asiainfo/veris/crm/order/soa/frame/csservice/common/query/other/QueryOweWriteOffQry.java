
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryOweWriteOffQry extends CSBizBean
{
    public static IDataset queryOweWriteOff(String areaCode, String startDate, String endDate, String startSerialNumber, String endSerialNumber, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("REMOVE_TAG", "4");
        params.put("AREA_CODE", areaCode);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);
        params.put("START_SERIALNUMBER", startSerialNumber);
        params.put("END_SERIALNUMBER", endSerialNumber);
        // 用户服务状态
        params.put("USER_SVC_STATE", "9");
        return Dao.qryByCode("TF_F_USER", "SEL_OWEFEE_DESTROYINFO", params, pagination);

    }

}
