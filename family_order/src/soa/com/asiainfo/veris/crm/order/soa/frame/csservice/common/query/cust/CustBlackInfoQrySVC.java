
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;

public class CustBlackInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 判断是否黑名单客户
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryBlackCustInfo(IData input) throws Exception
    {
        String psptTypeCode = input.getString("PSPT_TYPE_CODE");
        String psptId = input.getString("PSPT_ID");

        IDataset dataset = UCustBlackInfoQry.qryBlackCustInfo(psptTypeCode, psptId);

        return dataset;
    }
}
