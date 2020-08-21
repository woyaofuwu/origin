
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.err;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ErrItfCodeInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 作用：转换ADCMAS产品反向错误码
     * 
     * @param appCode
     *            ，inModeCode
     * @return
     * @throws Exception
     */
    public IDataset getErrItfCode(IData input) throws Exception
    {
        String appCode = input.getString("APP_RSCODE");
        String inModeCode = input.getString("IN_MODE_CODE");
        IDataset data = ErrItfCodeInfoQry.getErrItfCode(appCode, inModeCode);
        return data;
    }
}
