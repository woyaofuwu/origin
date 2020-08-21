
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ResCorpInfoQry
{

    /**
     * 获取指定条件的TD_M_RES_CORP参数
     * 
     * @param resTypeCode
     * @param factoryCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getResCorpInfos(String resTypeCode, String factoryCode, String eparchyCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("EPARCHY_CODE", eparchyCode);
        inparams.put("RES_TYPE_CODE", resTypeCode);
        inparams.put("FACTORY_CODE", factoryCode);
        return Dao.qryByCode("TD_M_RES_CORP", "SEL_BY_RES_TYPE", inparams, Route.CONN_RES);
    }

    /**
     * 查询某个CHNL_ID的费用数据
     * 
     * @param chnlId
     * @param factoryCode
     * @param status
     * @return
     * @throws Exception
     */
    public static IDataset queryChnlFeeInfo(String chnlId, String factoryCode, String status) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CHNL_ID", chnlId);
        inparams.put("STATUS", status);
        inparams.put("FACTORY_CODE", factoryCode);
        return Dao.qryByCode("TD_M_RES_CORP", "SEL_BY_CHNL_FOR_FEE", inparams);
    }
}
