
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.err;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ErrItfCodeInfoQry
{
    /**
     * 作用：转换ADCMAS产品反向错误码
     * 
     * @param appCode
     *            ，inModeCode
     * @return
     * @throws Exception
     */
    public static IDataset getErrItfCode(String appCode, String inModeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("APP_RSCODE", appCode);
        param.put("IN_MODE_CODE", inModeCode);
        return Dao.qryByCode("TD_S_ERRITFCODE", "SEL_BY_APPCODE_INMODECODE", param, Route.CONN_CRM_CEN);
    }

}
