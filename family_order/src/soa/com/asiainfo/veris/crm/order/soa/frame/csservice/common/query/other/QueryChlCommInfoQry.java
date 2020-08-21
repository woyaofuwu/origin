
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryChlCommInfoQry extends CSBizBean
{
    /**
     * 渠道通讯费补贴查询
     * 
     * @param cityCode
     * @param channelCode
     * @param channelName
     * @param serialNumber
     * @param routeEparchyCode
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryChlCommInfo(String cityCode, String channelCode, String channelName, String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("CHNL_CODE", channelCode);
        params.put("CITY_CODE", cityCode);
        params.put("CHNL_NAME", channelName);
        params.put("SERIAL_NUMBER", serialNumber);
      //  return Dao.qryByCode("TF_F_USER_OTHER", "SEL_CHNLCOMM_BY_CITYID_SN", params, pagination, Route.CONN_CRM_LXF);//crm库超时限定60秒,crmyt没有配置,暂时用crm去查,modify by duhj
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_CHNLCOMM_BY_CITYID_SN", params, pagination);

    }

}
