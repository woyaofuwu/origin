
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BlackUserInfoQry
{

    public static IDataset qryBlackUserByAll(String serialNumber, String startDate, String endDate, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("START_TIME", startDate);
        param.put("END_TIME", endDate);

        return Dao.qryByCodeParser("TL_B_BLACKUSER", "SEL_BY_COND", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset qryBlackUserByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TL_B_BLACKUSER", "SEL_ALL_BY_NUMBER", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryBlackUserByValid(String serialNumber, String startDate, String endDate, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("START_TIME", startDate);
        param.put("END_TIME", endDate);
        return Dao.qryByCodeParser("TL_B_BLACKUSER", "SEL_BY_EFFECT", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBlacjInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TL_B_BLACKUSER", "SEL_BY_SN_EFFECT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBlackInfosByNormal(String startSn, String endSn, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startSn);
        data.put("PARA_CODE2", endSn);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_BLACKINFONORMAL", data, page);
    }

    public static IDataset queryBlackInfosByReport(String startDate, String endDate, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startDate);
        data.put("PARA_CODE2", endDate);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_BLACKINFOREPORT", data, page);
    }

    public static IDataset queryBlackUserInfo(String userId, String provCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PROV_CODE", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_USER_INFO", data);
    }
}
