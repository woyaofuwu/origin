package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TpOrderRuleRouteQry {
    public static IData getOrderRoute(IData param) throws Exception{
        String templId = param.getString("TEMPL_ID");
        IData inparam = new DataMap();
        inparam.put("TEMPL_ID", templId);
        IDataset dataset = Dao.qryByCodeParser("TP_ORDER_RULE_ROUTE", "SEL_BY_PKS", inparam);
        if(DataUtils.isNotEmpty(dataset)){
            return dataset.first();
        }
        return null;
    }
}
