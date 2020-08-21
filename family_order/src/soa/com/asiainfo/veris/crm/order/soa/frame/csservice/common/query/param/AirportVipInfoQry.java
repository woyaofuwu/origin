
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AirportVipInfoQry
{

    public static IDataset queryAirportCancel(String QUERY_TYPE, String SERIAL_NUMBER, String START_DATE, String END_DATE, String IS_TRUE, String IS_FREE) throws Exception
    {
        IDataset serviceInfos = new DatasetList();
        IData param = new DataMap();
        param.put("QUERY_TYPE", QUERY_TYPE);
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);
        param.put("START_DATE", START_DATE);
        param.put("END_DATE", END_DATE);
        param.put("IS_RETURN", IS_TRUE);
        param.put("IS_FREE", IS_FREE);

        serviceInfos = Dao.qryByCode("TF_B_VIPAIRDROME_SERVICE", "SEL_SERVICES_2", param);

        return serviceInfos;
    }

    public static IDataset queryServiceLast(String SERIAL_NUMBER, String VIP_NO) throws Exception
    {
        IDataset serviceInfos = new DatasetList();
        IData data = new DataMap();
        if (!"".equals(VIP_NO))
        {
            data.put("SERIAL_NUMBER", SERIAL_NUMBER);
            data.put("VIP_NO", VIP_NO);
            serviceInfos = Dao.qryByCode("TF_B_VIPAIRDROME_SERVICE", "SEL_SERVICE_LAST", data);
        }
        else
        {// 全球通商旅套餐非VIP客户
            data.put("SERIAL_NUMBER", SERIAL_NUMBER);
            serviceInfos = Dao.qryByCode("TF_B_VIPAIRDROME_SERVICE", "SEL_SERVICE_LAST_2", data);
        }
        return serviceInfos;
    }

    public static int queryServiceNum(String SERIAL_NUMBER) throws Exception
    {
        int serviceNum = 0;
        IDataset serviceNuMInfos = new DatasetList();
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", SERIAL_NUMBER);
        serviceNuMInfos = Dao.qryByCode("TF_B_VIPAIRDROME_SERVICE", "SEL_SERVICENUM_2", data);
        String iserviceNum = (String) serviceNuMInfos.get(0, "NUM", "0");
        serviceNum = Integer.parseInt(iserviceNum);
        return serviceNum;
    }

    public static IDataset queryVipAirServer(String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERVICE_ID", tradeId);
        IDataset serviceNuMInfos = Dao.qryByCode("TF_B_VIPAIRDROME_SERVICE", "SEL_ALL_BY_SERVICE_ID", data);
        return serviceNuMInfos;
    }
}
