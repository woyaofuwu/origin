
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustContractInfoQry
{

    /**
     * 通过custid查询合同信息
     *
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractByCustId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        return Dao.qryByCode("TF_F_CUST_CONTRACT", "SEL_BY_CUSTID_VALID", param);
    }

    /**
     * 通过合同编码查询合同信息
     *
     * @param contractId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractInfoByContractId(String contractId) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);

        return Dao.qryByCode("TF_F_CUST_CONTRACT", "SEL_BY_CONTRACTID", param);
    }

    public static void insert(IData data) throws Exception
    {
        Dao.insert("TF_F_CUST_CONTRACT", data, Route.CONN_CRM_CG);
    }

}
