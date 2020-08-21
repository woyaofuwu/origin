
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.wlan;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WlanFeeCardLogQry
{
    /**
     * 查询wlan日志
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getWlanFeeLogBySeqId(String strSeqId) throws Exception
    {

        IData param = new DataMap();
        param.put("SEQ_ID", strSeqId);
        return Dao.qryByCode("TF_B_WLAN_FEE_CARD_LOG", "SEL_WLAN_FEECARD_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getWlanFeeLogBySnState(String serialNumber, String state) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("STATE", state);
        return Dao.qryByCode("TF_B_WLAN_FEE_CARD_LOG", "SEL_WLAN_EFFECT_CARD_BY_OPR_DATE", param, Route.CONN_CRM_CEN);
    }

}
