
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PoProvQry
{

    /*
     * @descrption 获取商品开放省信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoProvInfoByPK(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_POPRIV", inparam, new String[]
        { "POSPECNUMBER", "ENABLECOMPANYID" }, Route.CONN_CRM_CEN);
    }
}
