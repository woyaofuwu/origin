
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PoRatePolicysQry
{
    /*
     * @descrption 获取资费计划信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoRatePolicysInfoByPK(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_PORATEPOLICYS", inparam, Route.CONN_CRM_CEN);
    }
}
