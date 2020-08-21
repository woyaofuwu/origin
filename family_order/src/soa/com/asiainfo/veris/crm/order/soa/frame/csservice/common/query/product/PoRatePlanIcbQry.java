
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PoRatePlanIcbQry
{

    /**
     * 根据，查ICB参数
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getIcbsByParameterNumber(String parameterNumber) throws Exception
    {
        //IData param = new DataMap();
        //param.put("PARAMETERNUMBER", parameterNumber);

        //return Dao.qryByCode("TD_F_PORATEPLANICB", "SEL_BY_PARAMETERNUMBER", param, Route.CONN_CRM_CEN);
        return UpcCall.queryPoratePlanIcbByParameterNumber(parameterNumber);
    }

    /*
     * @descrption 获取资费计划的ICB参数信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoRatePlanIcbInfoByPK(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_PORATEPLANICB", inparam, Route.CONN_CRM_CEN);
    }
}
