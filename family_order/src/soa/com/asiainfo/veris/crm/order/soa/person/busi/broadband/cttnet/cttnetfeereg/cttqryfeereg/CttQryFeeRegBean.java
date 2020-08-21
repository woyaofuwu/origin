
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetfeereg.cttqryfeereg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee.FeeRegQry;

public class CttQryFeeRegBean extends CSBizBean
{
    public IDataset qryFeeRegCTT(IData param, Pagination pagination) throws Exception
    {
        return FeeRegQry.qryFeeRegCTT(param.getString("STATE"), param.getString("REG_DEPART_ID"), param.getString("REG_STAFF_ID"), param.getString("START_DATE"), param.getString("END_DATE"), pagination);
    }
}
