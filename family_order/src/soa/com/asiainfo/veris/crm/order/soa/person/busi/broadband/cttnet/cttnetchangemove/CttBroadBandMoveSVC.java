
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class CttBroadBandMoveSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset getOldBroadBandInfo(IData param) throws Exception
    {
        IDataset resultSet = WidenetInfoQry.getUserWidenetInfo(param.getString("USER_ID"));
        // IDataset resultSet = BroadBandInfoQry.queryBroadBandAddressInfo(param);
        if (IDataUtil.isEmpty(resultSet))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_4);
        }
        return resultSet;
    }
}
