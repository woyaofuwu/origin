
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PoRatePlanQry
{

    /*
     * @descrption 根据条件获取商品套餐信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoRatePalnInfoByParams(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_PORATEPLAN", inparam, new String[]
        { "POSPECNUMBER", "PORATENUMBER", "RATETYPE", "RATEPLANID" }, Route.CONN_CRM_CEN);
    }

    /*
     * @descrption 获取商品套餐信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoRatePlanInfoByPK(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_PORATEPLAN", inparam, Route.CONN_CRM_CEN);
    }

    /**
     * @Function: getRateplanBySpecDiscntCode
     * @Description: 从集团库 查询 优惠 对应的集团编码
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: liming2
     */
    public static IDataset getRateplanBySpecDiscntCode(String merch_spec_code, String product_spec_code, String product_discnt_code) throws Exception
    {
    	return UpcCall.queryPoratePlanBySpecRatePlanId(merch_spec_code, product_spec_code, product_discnt_code);
    	
    }
    
    //daidl    
    public static IDataset getRateplanByNumberAndType(String merch_spec_code, String ratetype, String product_discnt_code) throws Exception
    {
        return UpcCall.getRateplanByNumberAndType(merch_spec_code,ratetype,product_discnt_code);
    } 
}
