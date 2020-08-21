
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WhiteUserInfoQry
{

    public static IDataset queryBlackInfoBySerialNumber(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_BLACK_SER", data);
    }

    public static IDataset querySuspectInfosByNormal(String startSn, String endSn, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startSn);
        data.put("PARA_CODE2", endSn);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_SUSPECTINFONORMAL", data, page);
    }

    public static IDataset querySuspectInfosByReport(String startDate, String endDate, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startDate);
        data.put("PARA_CODE2", endDate);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_SUSPECTINFOREPORT", data, page);
    }

    public static IDataset queryVerifySuspectSmsNormal(String startSn, String endSn, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startSn);
        data.put("PARA_CODE2", endSn);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_VERIFYSMSNORMAL", data, page);
    }

    public static IDataset queryVerifySuspectSmsReport(String startDate, String endDate, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startDate);
        data.put("PARA_CODE2", endDate);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_VERIFYSMSREPORT", data, page);
    }

    public static IDataset queryWhiteInfoBySerialNumber(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_WHITE_SER", data);
    }

    public static IDataset queryWhiteInfosByNormal(String startSn, String endSn, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startSn);
        data.put("PARA_CODE2", endSn);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_WHITEINFONORMAL", data, page);
    }

    public static IDataset queryWhiteInfosByReport(String startDate, String endDate, String provCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE1", startDate);
        data.put("PARA_CODE2", endDate);
        data.put("PARA_CODE3", provCode);
        return Dao.qryByCode("TL_F_WHITEUSER", "SEL_BY_WHITEINFOREPORT", data, page);
    }
}
