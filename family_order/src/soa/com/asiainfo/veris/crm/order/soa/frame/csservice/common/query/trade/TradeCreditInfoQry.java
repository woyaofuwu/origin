
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeCreditInfoQry
{
    /**
     * 查询信誉度子台帐
     * 
     * @author chenzm
     * @param trade_id
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getTradeCreditByPK(String trade_id, String modify_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("MODIFY_TAG", modify_tag);
        return Dao.qryByCode("TF_B_TRADE_CREDIT", "SEL_BY_TRADEID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
}
