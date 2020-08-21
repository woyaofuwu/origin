
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PoInfoQry
{

    /**
     * 通过商品订购状态编码查询订购名称
     * 
     * @param specNumber
     * @return
     * @throws Exception
     */
    public static String getMerchStateNameByStateCode(String stateCode) throws Exception
    {

        return StaticUtil.getStaticValue("TD_S_STAT_MECH", stateCode);
    }

    /*
     * @descrption 获取商品信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoInfoByPK(IData inparam) throws Exception
    {
        return UPoInfoQry.queryPoDataBySpecNumber(inparam.getString("POSPECNUMBER"));
    }

    /**
     * 通过商品编号查询商品名称
     * 
     * @param specNumber
     * @return
     * @throws Exception
     */
    public static String getPOSpecNameByPoSpecNumber(String specNumber) throws Exception
    {
		return UPoInfoQry.queryPospecNameBySpecNumber(specNumber);   		
    	 
    }

    /**
     * 通过商品编码 查询商品信息
     * 
     * @author liuxx3
     * @date 2014-07-08
     */

    public static IDataset getProductInfosByPoSpecNumber(String pospecnumber) throws Exception
    {
        IData param = new DataMap();
        param.put("POSPECNUMBER", pospecnumber);

        return Dao.qryByCode("TD_F_PO", "SEL_BY_POSPECNUMBER", param, Route.CONN_CRM_CEN);
    }
}
