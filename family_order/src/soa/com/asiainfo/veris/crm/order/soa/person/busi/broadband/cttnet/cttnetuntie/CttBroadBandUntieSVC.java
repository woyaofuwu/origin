
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetuntie;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class CttBroadBandUntieSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset getOldBroadBandInfo(IData param) throws Exception
    {
        // return BroadBandInfoQry.queryBroadBandAddressInfo(param);
        return WidenetInfoQry.getUserWidenetInfo(param.getString("USER_ID"));
    }
}
