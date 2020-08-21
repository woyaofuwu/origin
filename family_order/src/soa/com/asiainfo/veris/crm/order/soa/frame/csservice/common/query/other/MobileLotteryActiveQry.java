
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MobileLotteryActiveQry extends CSBizBean
{
    public static IDataset qryDetailNum(String startDate, String endDate, String cityCode) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("CITY_CODE", cityCode);
        return Dao.qryByCode("TM_O_LOTTERY", "SEL_DETAIL_NUM", param);
    }

    public static IDataset queryCountMonth(String startDate, String endDate, String month) throws Exception
    {
        IData params = new DataMap();
        params.put("MONTH", month);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);

        return Dao.qryByCode("TF_F_USER_LOTTERY", "SEL_COUNT_MONTH", params);
    }

    public static IDataset queryLotteryInfo(String serialNumber, String user_id, String month, String deal_tag) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("USER_ID", user_id);
        params.put("MONTH", month);
        params.put("DEAL_TAG", deal_tag);

        return Dao.qryByCode("TF_F_USER_LOTTERY", "SEL_LOT_USR", params);
    }

    // 手机缴费通抽奖
    public static IDataset queryLotteryInfo(String serialNumber, String user_id, String month, String prize_type_code, String deal_tag, String startDate, String EndDate, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("MONTH", month);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("USER_ID", user_id);
        params.put("PRIZE_TYPE_CODE", prize_type_code);
        params.put("START_DATE", startDate);
        params.put("END_DATE", EndDate);
        params.put("DEAL_TAG", deal_tag);

        return Dao.qryByCode("TF_F_USER_LOTTERY", "SEL_LOT_USR", params, pagination);
    }

    // 手机月月通抽奖
    public static IDataset queryLotteryInfoByUid(String user_id, String month) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", user_id);
        params.put("MONTH", month);
        return Dao.qryByCode("TF_F_USER_LOTTERY", "SEL_BY_UID", params);
    }

    public static IDataset queryUserCity(String user_id) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER", "SEL_BY_USER_ENDDATE", params);
    }

    public static int updateLotteryInfo(String staff_id, String depart_id, String month, String user_id) throws Exception
    {

        IData param = new DataMap();
        param.put("STAFF_ID", staff_id);
        param.put("DEPART_ID", depart_id);
        param.put("MONTH", month);
        param.put("USER_ID", user_id);
        return Dao.executeUpdateByCodeCode("TF_F_USER_LOTTERY", "UPD_LOT_USR", param);
    }
}
