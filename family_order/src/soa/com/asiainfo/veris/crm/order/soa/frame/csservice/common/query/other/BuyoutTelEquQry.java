
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BuyoutTelEquQry
{
    public static IDataset qryBuyoutTelEqu(String departId, String chnlId, String regStartDate, String regEndDate, Pagination pagination) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("DEPART_ID", departId);
        paramData.put("CHNL_ID", chnlId);
        paramData.put("START_REG_DATE", regStartDate);
        paramData.put("END_REG_DATE", regEndDate);
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_BUYOUTTELEQU", paramData, pagination);
    }

    public static IDataset qryBuyoutTelEqu2(String chnlId, String regStartDate, String regEndDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CHNL_ID", chnlId);
        param.put("START_REG_DATE", regStartDate);
        param.put("END_REG_DATE", regEndDate);
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_BUYOUTTELEQU2", param, pagination);
    }

    public static IDataset qryChl(String departId, String chnlCode, String chnlName, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("DEPART_ID", departId);
        param.put("CHNL_CODE", chnlCode);
        param.put("CHNL_NAME", chnlName);
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_CHL", param, pagination);
    }

    public static IDataset qryChl2(String chnlCode, String chnlName, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CHNL_CODE", chnlCode);
        param.put("CHNL_NAME", chnlName);
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_CHL2", param, pagination);
    }

    public static IDataset qryDepart(String departId) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("DEPART_ID", departId);
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_DEPART", paramData);
    }

    public static IDataset qryDeparts() throws Exception
    {
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_DEPARTS", null);
    }

    public static IDataset queryBuyoutInfos(String logId) throws Exception
    {
        IData param = new DataMap();
        param.put("LOG_ID", logId);
        return Dao.qryByCodeParser("TF_F_BUYOUT_TELEQU", "SEL_BY_LOG_ID", param);
    }
}
