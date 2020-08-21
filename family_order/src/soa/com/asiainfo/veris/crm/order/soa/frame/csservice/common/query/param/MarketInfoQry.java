/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @CREATED by gongp@2014-7-25 修改历史 Revision 2014-7-25 下午07:41:48
 */
public class MarketInfoQry
{

    /**
     * 查询移动商城营销活动
     * 
     * @param province
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-25
     */
    public static IDataset getMarketActive(String province) throws Exception
    {

        IData param = new DataMap();
        param.put("PROVINCE", province);

        return Dao.qryByCode("TD_B_MARKET_ACTIVE", "SELECT_MARKET_ACTIVE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询移动商城套餐
     * 
     * @param province
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-25
     */
    public static IDataset getMarketPackage(String province) throws Exception
    {

        IData param = new DataMap();
        param.put("PROVINCE", province);

        return Dao.qryByCode("TD_B_MARKET_PACKAGE", "SELECT_MARKET_PACKAGE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询所有产品
     * 
     * @param province
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-25
     */
    public static IDataset getMarketProduct(String province) throws Exception
    {

        IData param = new DataMap();
        param.put("PROVINCE", province);

        return Dao.qryByCode("TD_B_MARKET_PRODUCT", "SELECT_MARKET_PRODUCT", param, Route.CONN_CRM_CEN);
    }
}
