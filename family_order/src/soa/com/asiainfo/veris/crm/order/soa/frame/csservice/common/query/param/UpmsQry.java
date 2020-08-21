
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UpmsQry
{
    /**
     * 获取IBOSS的配送市信息
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static IDataset getIBOSSCity(IData data) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TI_B_UPMS_CITY", "SEL_IBOSS_CITY", data);

        return dataset;
    }

    /**
     * 获取IBOSS的配送区信息
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static IDataset getIBOSSDistrict(IData data) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TI_B_UPMS_CITY", "SEL_IBOSS_DISTRICT", data);

        return dataset;
    }

    /**
     * 获取IBOSS的配送省份信息
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static IDataset getIBOSSProvince() throws Exception
    {
        IData data = new DataMap();
        return Dao.qryByCode("TI_B_UPMS_CITY", "SEL_IBOSS_PROVINCE", data);
    }

    public static IDataset queryUpmsGiftByPK(String itemId) throws Exception
    {
        IData data = new DataMap();
        data.put("ITEM_ID", itemId);
        data.put("STATE", "0");// 查询超时,根据页面查询条件，新增参数  duhj
        data.put("ITEM_STATUS", "1");// 查询超时,根据页面查询条件，新增参数
        data.put("SCORE_STATE", "0");// 查询超时,根据页面查询条件，新增参数
        return Dao.qryByCode("TD_B_UPMS_GIFT", "SEL_GIFT_BY_ID", data, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUpmsGiftInfoAsc(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_B_UPMS_GIFT", "SEL_BY_GIFT_ASC", param, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUpmsGiftInfoByItemId(String itemId) throws Exception
    {
        IData data = new DataMap();
        data.put("ITEM_ID", itemId);

        return Dao.qryByCode("TD_B_UPMS_GIFT", "SEL_GIFT_BY_ID3", data, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUpmsGiftInfoByItemId2(String itemId) throws Exception
    {
        IData data = new DataMap();
        data.put("ITEM_ID", itemId);
        return Dao.qryByCode("TD_B_UPMS_GIFT", "SEL_GIFT_BY_ID2", data, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUpmsGiftInfoDesc(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_B_UPMS_GIFT", "SEL_BY_GIFT_DESC", param, pagination, Route.CONN_CRM_CEN);
    }
}
