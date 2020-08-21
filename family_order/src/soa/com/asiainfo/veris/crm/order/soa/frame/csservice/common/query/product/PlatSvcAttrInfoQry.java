
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PlatSvcAttrInfoQry
{

    /**
     * 查询平台服务的绑定属性
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset getPlatAttrByServiceId(String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TD_B_PLATSVC_ATTR", "SEL_BY_SVCID", param);
    }

    public static IDataset queryServiceParam(String user_id, String service_id) throws Exception
    {

        IData qryParam = new DataMap();
        qryParam.put("USER_ID", user_id);
        qryParam.put("SERVICE_ID", service_id);

        return Dao.qryByCode("TD_B_PLATSVC_ATTR", "SEL_BY_SERVICE_ID", qryParam);
    }
}
