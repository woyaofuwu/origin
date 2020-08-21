
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserAltsnInfoQry
{

    public static IDataset checkExistsInfoBySn(String serial_number, String alt_interval) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("ALT_INTERVAL", alt_interval);

        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_EXISTS_USER", param);
    }

    /**
     * 根据号码查询出改号资料表中已经激活的信息
     * 
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static int delAltsnBySn(String serial_number) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serial_number);
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "DEL_BY_ALTSN_STATUS", params);
        return dsPreInfo;
    }

    public static int delPreTradeBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_B_PRE_TRADE", "DEL_PRE", param);
        return dsPreInfo;
    }
    
    public static int delPreTradeBySn1(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_B_PRE_TRADE", "DEL_PRE1", param);
        return dsPreInfo;
    }

    public static int delUserAltsnBySn(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "DEL_BY_SN", param);
    }

    public static int delUserAltsnBySnStatus(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "DEL_BY_ALTSN_STATUS", param);
    }

    public static int insAltOpenInfoBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "INS_ALTSN_OPEN_OTHER", param);
        return dsPreInfo;
    }

    public static boolean insAltsnBySn(IData param) throws Exception
    {
        IData params = new DataMap();

        boolean dsPreInfo = Dao.insert("TF_F_USER_ALTSN", param);
        return dsPreInfo;
    }

    public static int insBhPreTradeBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_B_PRE_TRADE", "INS_TO_HIS", param);
        return dsPreInfo;
    }
    
    public static int insBhPreTradeBySn1(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_B_PRE_TRADE", "INS_TO_HIS1", param);
        return dsPreInfo;
    }

    public static IDataset queryAltInfoBySn(String serial_number, String trade_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("TRADE_TYPE_CODE", trade_type_code);
        return Dao.qryByCode("TF_B_PRE_TRADE", "QRY_PRE_STATUS", param);
    }

    public static IDataset queryAltStateInfoBySn(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_F_USER_ALTSN", "QRY_ALTSN_STATUS", param);
    }
    
    public static IDataset queryAllAltStateInfoBySn(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_F_USER_ALTSN", "QRY_ALL_ALTSN_STATUS", param);
    }


    public static IDataset queryReqInfoBySn(String serial_number, String rela_type, String status, String interval) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("RELA_TYPE", rela_type);
        param.put("STATUS", status);
        param.put("INTERVAL", interval);
        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_FOR_QRY_REQ", param);
    }

    public static IDataset queryUserAltsnBySn(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_BY_ALTSN", param);
    }

    public static IDataset queryUserAltsnBySn(String serialNumber, String relaType, String status) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("RELA_TYPE", relaType);
        param.put("STATUS", status);
        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_FOR_CANCEL", param);
    }

    public static IDataset queryUserAltsnBySnLarger(IData params) throws Exception
    {
        IDataset dsPreInfo = Dao.qryByCode("TF_F_USER_ALTSN", "SEL_LAGRGE_OPEN_USER", params);
        return dsPreInfo;
    }

    /**
     * 根据号码查询出改号资料表中已经激活的信息
     * 
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAltsnBySnSelSnCancel(String serial_number) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serial_number);
        IDataset dsPreInfo = Dao.qryByCode("TF_F_USER_ALTSN", "SEL_SN_CANCEL", params);
        return dsPreInfo;
    }

    public static IDataset queryUserAltsnBySnStatus(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_BY_ALTSN_STATUSS", param);
    }

    public static IDataset queryUserAltsnByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_SVC_BY_USERID", param);
    }

    public static IDataset queryUserAnswerBySn(String serial_number, String status, String rela_type, String interval) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("STATUS", status);
        param.put("RELA_TYPE", rela_type);
        param.put("INTERVAL", interval);
        return Dao.qryByCode("TF_F_USER_ALTSN", "SEL_FOR_USER_ANSWER", param);
    }

    public static IDataset selPlatInfoBySn(IData param) throws Exception
    {
        IDataset dsPreInfo = Dao.qryByCode("TF_B_TRADE_ALTSN_PLATMRG", "SEL_NEW_PLATINFO", param,Route.getJourDb(BizRoute.getRouteId()));
        return dsPreInfo;
    }

    public static int updAltOpenInfoBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "UPD_ALTSN_OPEN_DATE", param);
        return dsPreInfo;
    }

    public static int updAltPlatmrgBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_B_TRADE_ALTSN_PLATMRG", "UPD_ACTTIME_BYSN", param,Route.getJourDb(BizRoute.getRouteId()));
        return dsPreInfo;
    }

    public static int updAltsnBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "UPD_CANCELDATE_BY_ID", param);
        return dsPreInfo;
    }

    public static int updRsrvStr1InfoBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "UPD_RSRV1_BY_ID", param);
        return dsPreInfo;
    }

    public static int updStatusInfoBySn(IData param) throws Exception
    {
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "UPD_STATUS_BY_SN", param);
        return dsPreInfo;
    }

}
