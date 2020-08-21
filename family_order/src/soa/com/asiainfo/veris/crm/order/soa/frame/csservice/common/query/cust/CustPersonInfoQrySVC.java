
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class CustPersonInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getPerInfoByCustId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        IDataset data = IDataUtil.idToIds(UcaInfoQry.qryPerInfoByCustId(cust_id));

        return data;
    }

    /**
     * 根据custId查询TF_F_CUST_PERSON_OTHER表
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryCustPersonOtherInfoByCustId(IData inParam) throws Exception {
        String custId = inParam.getString("CUST_ID", "");
        return CustPersonInfoQry.qryPerInfoByPsptId_2(custId);
    }
}
