
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUserEasyPromptQry extends CSBizBean
{

    /**
     * 便捷服务意见查询
     * 
     * @param serialNumber1
     * @param serialNumber2
     * @param acceptDate1
     * @param acceptDate2
     * @param smsTypeCode
     * @param routeEparchyCode
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserEasyPrompt(String serialNumber1, String serialNumber2, String acceptDate1, String acceptDate2, String smsTypeCode, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER1", serialNumber1);
        params.put("SERIAL_NUMBER2", serialNumber2);
        params.put("ACCEPT_DATE1", acceptDate1);
        params.put("ACCEPT_DATE2", acceptDate2);
        params.put("SMS_TYPE_CODE", smsTypeCode);
        return Dao.qryByCode("TI_O_PROMPT_SERVICE", "SEL_PROMPT_SRV", params, pagination, routeEparchyCode);

    }

}
