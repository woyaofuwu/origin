
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MphoneMatchingQry extends CSBizBean
{

    public static int UpdateByCode(String serialNumber, String custName, String brand, String viptag, String yearValue, String avgArpu, String custValue) throws Exception
    {
        // TODO Auto-generated method stub
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("CUST_NAME", custName);
        params.put("BRAND", brand);
        params.put("VIP_TAG", viptag);
        params.put("YEAR_VALUE", yearValue);
        params.put("AVG_ARPU", avgArpu);
        params.put("CUST_VALUE", custValue);

        return Dao.executeUpdateByCodeCode("TF_F_RELATION_MM", "INS_ALL", params);
    }
}
