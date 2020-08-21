
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FeeRegQry
{
    public static IDataset qryBankFeeRegCTT(String state, String departId, String staffId, String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("STATE", state);
        if (staffId != null)
        {
            paramData.put("REG_STAFF_ID", staffId);
        }
        if (departId != null)
        {
            paramData.put("REG_DEPART_ID", departId);
        }
        paramData.put("START_REG_DATE", startDate);
        paramData.put("END_REG_DATE", endDate);

        return Dao.qryByCodeParser("TF_F_FEEREG", "SEL_BANKFEEREG_BY_DATE", paramData, pagination);
    }

    public static IDataset qryFeeRegCTT(String state, String departId, String staffId, String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("STATE", state);
        if (staffId != null)
        {
            paramData.put("REG_STAFF_ID", staffId);
        }
        if (departId != null)
        {
            paramData.put("REG_DEPART_ID", departId);
        }
        paramData.put("START_REG_DATE", startDate);
        paramData.put("END_REG_DATE", endDate);

        return Dao.qryByCodeParser("TF_F_FEEREG", "SEL_FEEREG_BY_DATE", paramData, pagination);
    }

    public static IDataset queryBankFeeRegs() throws Exception
    {
        IData paramData = new DataMap();
        return Dao.qryByCode("TF_F_FEEREG", "SEL_BANKFEEREG", paramData, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBankFeeRegTotle(String logId) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("LOG_ID", logId);
        IDataset dataset = Dao.qryByCodeParser("TF_F_FEEREG", "SEL_BANKFEEREG2", paramData);
        if (IDataUtil.isNotEmpty(dataset))
        {
            dataset.getData(0).put("BANK", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK_CTT", "BANK_CODE", "BANK", dataset.getData(0).getString("RSRV_STR2")));
        }
        return dataset;
    }

    public static IDataset queryFeeRegs() throws Exception
    {
        IData paramData = new DataMap();
        return Dao.qryByCode("TF_F_FEEREG", "SEL_FEEREG", paramData, Route.CONN_CRM_CEN);
    }

    public static IDataset queryFeeRegsByLogId(String logId) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("LOG_ID", logId);
        return Dao.qryByCode("TF_F_FEEREG", "SEL_FEEREG_BY_LOGID", paramData);
    }

    public static IDataset queryFeeRegTotle(String logId) throws Exception
    {
        IData param = new DataMap();
        param.put("LOG_ID", logId);
        return Dao.qryByCode("TF_F_FEEREG", "SEL_FEEREG2", param);
    }
}
