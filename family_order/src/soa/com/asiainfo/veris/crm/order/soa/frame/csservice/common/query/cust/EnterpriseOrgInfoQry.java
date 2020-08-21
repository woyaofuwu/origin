package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

public class EnterpriseOrgInfoQry
{

    public static IDataset getEnterpriseByPsptID(String psptid) throws Exception
    {
        IData param = new DataMap();
        param.put("REGITNO", psptid);
        return Dao.qryByCode("TF_F_ENTERPRISE", "SEL_ENTERPRISE_BY_PSPTID", param);
    }

    public static IDataset getEnterpriseByPsptIDOther(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("REGITNO", data.getString("REGITNO"));
        param.put("ENTERPRISENAME", data.getString("ENTERPRISENAME"));
        param.put("LEGALPERSON", data.getString("LEGALPERSON"));
        param.put("STARTDATE", data.getString("STARTDATE"));
        param.put("TERMSTARTDATE", data.getString("TERMSTARTDATE"));
        param.put("TERMENDDATE", data.getString("TERMENDDATE"));
        return Dao.qryByCode("TF_F_ENTERPRISE", "SEL_ENTERPRISE_BY_PSPTIDOTHER", param);
    }
    
    public static IDataset getOrgByPsptID(String psptid) throws Exception
    {
        IData param = new DataMap();
        param.put("ORGCODE", psptid);
        return Dao.qryByCode("TF_F_ORG", "SEL_ORG_BY_PSPTID", param);
    }
}
